package weird.ngraph.Storage;

import weird.ngraph.Analysis.Constants;
import weird.ngraph.Utils.ProcessBuilderWrapper;
import java.io.File;
import java.nio.file.Paths;

public class NEOManagement {
    public NEOManagement() {}
    public boolean importData() {
        String[] options =  new String[]{
            Constants.NEO4J_ADMIN,
                "import",
                "--multiline-fields=true",
                "--database", String.format("%1$s.db", Constants.PROJECT_NAME),
                //"--database", "graph.db",
                "--id-type", "string",
                "--nodes:Class", Paths.get(Constants.CSV_OUTPUT,  "Klass.csv" ).toString(),
                "--nodes:Method", Paths.get(Constants.CSV_OUTPUT,  "Function.csv" ).toString(),
                "--nodes:Parameter", Paths.get(Constants.CSV_OUTPUT,  "Parameter.csv" ).toString(),
                "--nodes:Field", Paths.get(Constants.CSV_OUTPUT,  "Field.csv" ).toString(),
                "--nodes:Literal", Paths.get(Constants.CSV_OUTPUT,  "Literal.csv" ).toString(),
                "--relationships:INPUT", Paths.get(Constants.CSV_OUTPUT,  "Input.csv" ).toString(),
                "--relationships:RETURNS", Paths.get(Constants.CSV_OUTPUT,  "Returns.csv" ).toString(),
                "--relationships:IMPLEMENTS", Paths.get(Constants.CSV_OUTPUT,  "Implements.csv" ).toString(),
                "--relationships:EXTENDS", Paths.get(Constants.CSV_OUTPUT,  "Extends.csv" ).toString(),
                "--relationships:CALLS", Paths.get(Constants.CSV_OUTPUT,  "Calls.csv" ).toString(),
                "--relationships:GETS", Paths.get(Constants.CSV_OUTPUT,  "Gets.csv" ).toString(),
                "--relationships:PUTS", Paths.get(Constants.CSV_OUTPUT,  "Puts.csv" ).toString(),
                "--relationships:USES", Paths.get(Constants.CSV_OUTPUT,  "Uses.csv" ).toString()
        };
        try {
            ProcessBuilderWrapper pbd = null;
            pbd = new ProcessBuilderWrapper(new File(System.getProperty("java.io.tmpdir")), options);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
