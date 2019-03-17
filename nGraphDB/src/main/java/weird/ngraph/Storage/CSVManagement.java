package weird.ngraph.Storage;

import weird.ngraph.Analysis.Constants;
import com.opencsv.CSVWriter;
import weird.ngraph.Domain.Entity;
import weird.ngraph.Domain.Relation;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CSVManagement {

    private Map<String, CSVWriter > map = new HashMap<String, CSVWriter >();

    public CSVManagement() {
        for ( String eName : Constants.ENTITIES.keySet() ) {
            try {
                Path p = Paths.get(Constants.CSV_OUTPUT,  eName + ".csv");
                FileWriter outputfile = new FileWriter( p.toFile() );
                this.map.put(eName, new CSVWriter(outputfile));
                this.map.get(eName).writeNext(Constants.ENTITIES.get(eName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean addRow(Entity object) {
        String[] temp = object.getClass().getCanonicalName().split("\\.");
        String cName = temp[temp.length - 1];
        this.map.get(cName).writeNext(object.fields());
        return true;
    }
    public boolean addRow(Relation object) {
        String[] temp = object.getClass().getCanonicalName().split("\\.");
        String cName = temp[temp.length - 1];
        this.map.get(cName).writeNext(object.fields());
        return true;
    }

    public boolean close() {
        for ( String eName : Constants.ENTITIES.keySet() ) {
            try {
                this.map.get(eName).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public Map<String, Integer> removeDuplicates() {
        Map<String, Integer> stats = new HashMap<String, Integer>();
        stats = new HashMap<String, Integer>();
        for ( String eName : Constants.ENTITIES.keySet() ) {
            stats.put(eName, 0);
            try {
                Path pS = Paths.get(Constants.CSV_OUTPUT,  eName + ".csv");
                BufferedReader reader = new BufferedReader(new FileReader( pS.toString() ));
                Set<String> lines = new LinkedHashSet<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                reader.close();
                BufferedWriter writer = new BufferedWriter(new FileWriter( pS.toString() ));
                for (String unique : lines) {
                    writer.write(unique);
                    writer.newLine();
                    stats.put(eName, stats.get(eName) + 1 );
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stats;
    }

}
