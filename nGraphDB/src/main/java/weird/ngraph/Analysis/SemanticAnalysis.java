package weird.ngraph.Analysis;

import weird.ngraph.Utils.Generator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SemanticAnalysis {

    // Find name from class source file or call instruction
    public static String findClassName(String code, boolean... from_source) {
        Pattern reg = null;
        Matcher matcher = null;
        String result = null;
        if (from_source.length == 1) {
            reg = Pattern.compile("^\\.class\\s+(.*)$", Pattern.MULTILINE);
            matcher = reg.matcher(code);
            if (matcher.find()) {
                String[] temp = matcher.group(1).split(" ");
                result = temp[temp.length - 1];
            }
        } else {
            reg = Pattern.compile("^(L.*?);->.*$", Pattern.MULTILINE);
            matcher = reg.matcher(code);
            if (matcher.find()) {
                result = matcher.group(0);
            }
        }
        return result;
    }

    // Find parent class name from class source file
    public static String findClassInheritance(String code) {
        String result = null;
        Pattern reg = Pattern.compile("\\.super\\s+(.*)$", Pattern.MULTILINE);
        Matcher matcher = reg.matcher(code);
        if (matcher.find()) {
            result = matcher.group(1);
        }
        return result;
    }

    // Find Function code block from class source file
    public static Generator<String> findClassFunctions(String code) {
        String result = null;
        Pattern reg = Pattern.compile("(\\.method\\s+.*\\.end\\s*method)", Pattern.DOTALL);
        Matcher matcher = reg.matcher(code);
        return new Generator<String>() {
            @Override
            protected void run() throws InterruptedException {
                while (matcher.find()) {
                    yield(matcher.group(1));
                }
            }
        };
    }

    // Find Function definition from Function block code
    public static String findFunctionDefinition(String class_name, String code) {
        String result = null;
        Pattern reg = Pattern.compile("^(\\.method\\s+.*)$", Pattern.MULTILINE);
        Matcher matcher = reg.matcher(code);
        if (matcher.find()) {
            String[] temp = matcher.group(1).split(" ");
            result = String.format("%1$s->%2$s", class_name, temp[temp.length - 1]);
        }
        return result;
    }

    // Find Function calls from Function block code
    public static Generator<String> findFunctionCalls(String code) {
        String result = null;
        Pattern reg = Pattern.compile("\\s+(invoke-[a-z]+\\s+.*)\\s+");
        Matcher matcher = reg.matcher(code);
        return new Generator<String>() {
            @Override
            protected void run() throws InterruptedException {
                while (matcher.find()) {
                    String[] temp = matcher.group(1).split(" ");
                    yield(temp[temp.length - 1]);
                }
            }
        };
    }

    // Find Function class from Function definition
    public static String findFunctionClass(String definition) {
        return definition.split("->")[0];
    }

    // Find Function parameters from Function definition
    public static String findFunctionOutputParameter(String definition) {
        String output = definition.split("\\)")[1];
        if (output.equals("V")) output = null;
        return output;
    }

    public static List<String> findFunctionInputParameters(String definition) {
        String result = null;
        String basic_types = "VZBSCIJFD";
        List<String> inputs = new ArrayList<String>();
        StringBuilder param = new StringBuilder();

        char[] description = definition.split("\\(")[1].split("\\)")[0].toCharArray();
        int index = 0;
        while (index < description.length ) {
            // Array
            if (description[index] == '[') {
                param.append(description[index]);
                index += 1;
            // Object
            } else if (description[index] == 'L') {
                char[] entry = Arrays.copyOfRange(description, index, description.length);
                param.append(entry);
                index += entry.length;
                inputs.add(param.toString());
                param = new StringBuilder();
                // Native
            } else if (basic_types.indexOf(description[index]) >= 0) {
                param.append(description[index]);
                index += 1;
                inputs.add(param.toString());
                param = new StringBuilder();
            }
        }
        return inputs;
    }

    // Find Strings from Function block code
    public static Generator<String> findFunctionConstants(String code) {
        String result = null;
        Pattern reg = Pattern.compile( "\\s+const-string.*\\\"(.*)\\\"" );
        Matcher matcher = reg.matcher(code);
        return new Generator<String>() {
            @Override
            protected void run() throws InterruptedException {
                while (matcher.find()) {
                    yield(matcher.group(1));
                }
            }
        };
    }

    // Find Fields from Function block code
    public static Generator<String> findFunctionFields(String code, String operation) {
        String result = null;
        Pattern reg;

        if (operation.equals("read")) {
            reg = Pattern.compile( "([\\sb-z]get[\\s-].*)\\s+" );
        } else {
            reg = Pattern.compile( "([\\sb-z]put[\\s-].*)\\s+" );
        }
        Matcher matcher = reg.matcher(code);
        return new Generator<String>() {
            @Override
            protected void run() throws InterruptedException {
                while (matcher.find()) {
                    String[] temp = matcher.group(1).split(" ");
                    yield( temp[temp.length - 1] );
                }
            }
        };
    }

    /*     public static String findClassFields() {}     */
}
