package weird.ngraph.Storage;

import weird.ngraph.Analysis.Constants;
import weird.ngraph.Utils.ProcessBuilderWrapper;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class APKManagement {

    public void unpack() throws Exception {
        String cwd = new File(APKManagement.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        Path p = Paths.get(cwd, "resources/prebuilt/apktool.jar");
        try {
            String[] options = {
                    "java", "-jar", "-Xms1024m", "-Xmx2G",
                    p.toString(),
                    "d", "-r", "-f", "--no-assets", "--force-manifest", "-o",
                    Constants.SMALI_OUTPUT, Constants.APK_LOCATION,
            };
            ProcessBuilderWrapper pbd = null;
            pbd = new ProcessBuilderWrapper(new File(System.getProperty("java.io.tmpdir")) , options);
            //if (pbd.getErrors().length() > 0) {
            //    throw new Exception("External process execution error.");
            //}
        } catch (Exception e) {
            throw new Exception("External process execution error.");
        }
    }
}
