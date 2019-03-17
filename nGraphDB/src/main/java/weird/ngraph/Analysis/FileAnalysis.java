package weird.ngraph.Analysis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.*;
import weird.ngraph.Domain.*;
import weird.ngraph.Storage.APKManagement;
import weird.ngraph.Storage.CSVManagement;
import weird.ngraph.Storage.JAVAManagement;
import weird.ngraph.Storage.NEOManagement;
import weird.ngraph.Utils.FunctionWrapper;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class FileAnalysis {

    private  Map<String, Integer> stats = new HashMap<String, Integer>();
    private final CSVManagement storage_csv;

    public FileAnalysis() {
        this.storage_csv = new CSVManagement();
    }

    private Map<String, String[]> manifestAnalysis() throws Exception {
        try {
            File inputFile = Paths.get(Constants.SMALI_OUTPUT, "AndroidManifest.xml").toFile();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();
            String pkg = root.getAttributeNode("package").getValue().replace(".", "/");
            Map<String, String[]> manifest_entries = new HashMap<String, String[] >();
            for (String component : new String[] {"activity", "service", "provider", "receiver"}) {
                NodeList neles = root.getElementsByTagName(component);
                ArrayList<String> list = new ArrayList<String>();
                for (int i = 0; i < neles.getLength(); i++) {
                    Element e = (Element)neles.item(i);
                    String entry_point = e.getAttribute( "android:name").replace(".", "/");
                    String[] temp = entry_point.split("/");
                    String constructor = temp[temp.length - 1];
                    String entry;
                    if (entry_point.startsWith("/")) {
                        entry = String.format("L%1$s%2$s;->~%3$s", pkg, entry_point, constructor);
                    } else {
                        entry = String.format("L%1$s;->~%2$s", entry_point, constructor);
                    }
                    list.add(entry);
                }
                manifest_entries.put(component, list.toArray(new String[0]) );
            }
            return manifest_entries;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Corrupt AndroidManifest file.");
        }
    }

    public void applicationAnalyzer(boolean quiet, boolean garbage, boolean decomp) {
        try{

            APKManagement apk = new APKManagement();
            FunctionWrapper.timmed(quiet,"Unpacking APK", () -> {
                try {apk.unpack();} catch (Exception e) {}
            });
            FunctionWrapper.timmed(quiet,"Semantic Analysis", () -> {
                try {this.codeAnalyzer();} catch (Exception e) {e.printStackTrace();}
            });
            if (!quiet) {
                System.out.println("\tStatistics");
                for (String k : this.stats.keySet() ) {
                    String sDesc = String.format("\t\t%1$11s\t%2$d", k.toUpperCase(), this.stats.get(k));
                    System.out.println(sDesc);
                }
            }
            NEOManagement neo = new NEOManagement();
            FunctionWrapper.timmed(quiet,"Neo4j Domain Import", () -> {
                try {neo.importData();} catch (Exception e) {}
            });
            if (decomp) {
                JAVAManagement java = new JAVAManagement();
                FunctionWrapper.timmed(quiet,"Java decompilation", () -> {
                    try {java.unpack();} catch (Exception e) { }
                });
            }

        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }
    }

    // Core function (traverse and analyze smali files)
    public void codeAnalyzer() throws Exception {
        String base = Constants.SMALI_OUTPUT;
        Map<String, String[]> ENTRY_POINTS = this.manifestAnalysis();
        try (Stream<Path> walk = Files.walk(Paths.get(base))) {
            List<String> smali_files = walk.map(x -> x.toString())
                    .filter(f -> f.endsWith(".smali")).collect(Collectors.toList());

            for (String smali_file: smali_files) {
                String extension = FilenameUtils.getExtension(smali_file);
                if (!extension.equals("smali")) continue;
                try {
                    byte[] encoded = Files.readAllBytes(Paths.get(smali_file));
                    String class_file_content = new String(encoded);

                    // Main class definition from smali file .class directive
                    String class_name = SemanticAnalysis.findClassName(class_file_content, true);
                    if (class_name == null) continue;
                    Klass source_class_entity = new Klass(class_name);
                    storage_csv.addRow(source_class_entity);

                    // Main class inheritance from smali class file .super directive class->class
                    String parent_class_name = SemanticAnalysis.findClassInheritance(class_file_content);
                    Klass target_class_entity = new Klass(parent_class_name);
                    Extends extends_relation = new Extends(source_class_entity.id, target_class_entity.id);
                    storage_csv.addRow(target_class_entity);
                    storage_csv.addRow(extends_relation);

                    // Methods code blocks from smali class file from .method directive
                    for (String class_function_code : SemanticAnalysis.findClassFunctions(class_file_content)) {

                        // Method definition from method code block
                        String function_definition = SemanticAnalysis.findFunctionDefinition(class_name, class_file_content);
                        Function function_entity = new Function(function_definition, ENTRY_POINTS);
                        storage_csv.addRow(function_entity);

                        // Main class implements relation with method definition class->method
                        Implements implements_relation = new Implements(source_class_entity.id, function_entity.id);
                        storage_csv.addRow(implements_relation);

                        // Method calls (invoke-*) from code block
                        for (String call : SemanticAnalysis.findFunctionCalls(class_function_code)) {

                            // Method Call class entity
                            String call_class = SemanticAnalysis.findFunctionClass(call);
                            Klass call_class_entity = new Klass(call_class);
                            storage_csv.addRow(call_class_entity);

                            // Method call self entity
                            Function call_entity = new Function(call, ENTRY_POINTS);
                            storage_csv.addRow(call_entity);

                            // Main class invokes relation to method call method->method
                            Calls invokes_relation = new Calls(function_entity.id, call_entity.id);
                            storage_csv.addRow(invokes_relation);

                            // Method call return parameter
                            String output = SemanticAnalysis.findFunctionOutputParameter(call);
                            if (output != null) {
                                // Parameter entity
                                Parameter returns_entity = new Parameter(output);
                                storage_csv.addRow(returns_entity);
                                // Returns relation method->parameter
                                Returns returns_relation = new Returns(function_entity.id, returns_entity.id);
                                storage_csv.addRow(returns_relation);
                            }

                            // Method call inputs parameters
                            List<String> inputs = SemanticAnalysis.findFunctionInputParameters(call);
                            for (String input: inputs) {
                                // parameter entity
                                Parameter input_entity = new Parameter(input);
                                storage_csv.addRow(input_entity);
                                // Input relation method->parameter
                                Input input_relation = new Input(function_entity.id, input_entity.id);
                                storage_csv.addRow(input_relation);
                            }
                        }

                        // Method Literals operations
                        for (String literal : SemanticAnalysis.findFunctionConstants(class_function_code)) {
                            // Literal entity
                            Literal literal_entity = new Literal(literal);
                            storage_csv.addRow(literal_entity);
                            // Method literal usage relation method->literal
                            Uses uses_relation = new Uses(function_entity.id, literal_entity.id);
                            storage_csv.addRow(uses_relation);
                        }

                        // Fields read/write operations
                        for (String field : SemanticAnalysis.findFunctionFields(class_function_code, "read")) {
                            // Field entity
                            Field field_entity = new Field(field);
                            if (!field_entity.error) {
                                // Field read relation method->field
                                Gets gets_relation = new Gets(function_entity.id, field_entity.id);
                                storage_csv.addRow(field_entity);
                                storage_csv.addRow(gets_relation);
                            }
                        }
                        for (String field : SemanticAnalysis.findFunctionFields(class_function_code, "write")) {
                            // Field entity
                            Field field_entity = new Field(field);
                            if (!field_entity.error) {
                                // Field write relation method->field
                                Puts puts_relation = new Puts(function_entity.id, field_entity.id);
                                storage_csv.addRow(field_entity);
                                storage_csv.addRow(puts_relation);
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new Exception("Semantic smali file analysis error.");
                }
            }
            storage_csv.close();
            this.stats = storage_csv.removeDuplicates();
        } catch (IOException e) {
            throw new Exception("Recursive smali files walk error.");
        }
    }
}
