package weird.ngraph.Domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Function extends Entity {
    public String id;
    public String klass;
    public String name;
    public String descriptor;
    public String[] labels;

    public Function(String definition, Map<String, String[]> entry_points) {
        this.id = this.identifier(definition);
        this.klass = this.clean(definition.split("->")[0]);
        this.name = this.clean(definition.split("->")[1].split("\\(")[0]);
        this.descriptor = this.clean("(" + definition.split("\\(")[1]);
        String[] p1 = this.klass.split("/");
        if (this.name.startsWith("<cl")) {
            this.name = "~c" + p1[p1.length - 1].substring(0, p1[p1.length - 1].length() - 1);
        } else if (this.name.startsWith("<i")) {
            this.name = "~" + p1[p1.length - 1].substring(0, p1[p1.length - 1].length() - 1);
        }

        String x = String.format("%1$s->%2$s", this.klass, this.name);
        ArrayList<String> list = new ArrayList<String>();
        list.add("Method");
        for (String k : entry_points.keySet()) {
            if (Arrays.asList(entry_points.get(k)).contains(x)) {
                list.add("EntryPoint");
                list.add(k.substring(0, 1).toUpperCase() + k.substring(1));
            }
        }
        this.labels = list.toArray(new String[0]);
    }

    public String[] fields() {
        return new String[] {this.id, this.klass, this.name, this.descriptor, String.join(";", this.labels) };
    }

}
