package weird.ngraph.Storage;

import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import weird.ngraph.Analysis.Constants;
import weird.ngraph.Utils.ProcessBuilderWrapper;

public class JAVAManagement {

    public boolean unpack() throws Exception {
        String dexDestination = Paths.get(Constants.SMALI_OUTPUT, "dex.jar").toString();
        String cwd = new File(APKManagement.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        Path p = Paths.get(cwd, "resources/prebuilt/d2j/");
        File[] files = p.toFile().listFiles();
        List<String> paths = new ArrayList<String>();
        for (File f : files) {
            paths.add(f.toString());
        }
        String cp = new String();
        if (System.getProperty("os.name").startsWith("Windows")) {
            cp = String.join(";", paths);
        } else {
            cp = String.join(":", paths);
        }

        // dex2jar
        try {
            String[] args2 = new String[] {"java", "-Xms1024m", "-Xmx2G", "-cp", cp, "com.googlecode.dex2jar.tools.Dex2jarCmd", Constants.APK_LOCATION, "-o", dexDestination, "-f"};
            ProcessBuilderWrapper pbd = null;
            pbd = new ProcessBuilderWrapper(new File(System.getProperty("java.io.tmpdir")) , args2);
            if (pbd.getErrors().length() > 0) {
                //throw new Exception("External process (procyon) execution error.");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        // procyon
        p = Paths.get(cwd, "resources/prebuilt/procyon.jar");
        try {
            String[] args2 =  new String[]{"java", "-Xms1024m", "-Xmx2G", "-jar", p.toString(), "-o", Constants.JAVA_OUTPUT, dexDestination };
            ProcessBuilderWrapper pbd = null;
            pbd = new ProcessBuilderWrapper(new File(System.getProperty("java.io.tmpdir")) , args2);
            if (pbd.getErrors().length() > 0) {
                //throw new Exception("External process (procyon) execution error.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        return true;
    }
}
