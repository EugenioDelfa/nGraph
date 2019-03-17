package weird.ngraph.Analysis;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static String PROJECT_NAME = null;
    public static String NEO4J_ADMIN = null;
    public static String SMALI_OUTPUT = null;
    public static String CSV_OUTPUT = null;
    public static String JAVA_OUTPUT = null;
    public static String APK_LOCATION = null;

    public static Map<String, String[]> ENTITIES = new HashMap<String, String[] >() {{
        put("Klass", new String[] {"id:ID(Class)", "name"});
        put("Function", new String[] {"id:ID(Method)", "class", "name", "descriptor", ":LABEL" });
        put("Parameter", new String[] {"id:ID(Parameter)", "class"});
        put("Field", new String[] {"id:ID(Field)", "class", "name", "field_type"});
        put("Literal", new String[] {"id:ID(Literal)", "value"});
        put("Extends", new String[] {":START_ID(Class)", ":END_ID(Class)"});
        put("Implements", new String[] {":START_ID(Class)", ":END_ID(Method)"});
        put("Calls", new String[] {":START_ID(Method)", ":END_ID(Method)"});
        put("Puts", new String[] {":START_ID(Method)", ":END_ID(Field)"});
        put("Gets", new String[] {":START_ID(Method)", ":END_ID(Field)"});
        put("Uses", new String[] {":START_ID(Method)", ":END_ID(Literal)"});
        put("Input", new String[] {":START_ID(Method)", ":END_ID(Parameter)"});
        put("Returns", new String[] {":START_ID(Method)", ":END_ID(Parameter)"});
    }};
}
