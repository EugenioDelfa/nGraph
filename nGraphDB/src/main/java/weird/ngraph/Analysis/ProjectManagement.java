package weird.ngraph.Analysis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.FileUtils;

public class ProjectManagement {

    public static void createDirectoryStructure(String apkPath) throws Exception {

        try {

            // User provided sample path cheks
            File f = new File(apkPath);
            Constants.APK_LOCATION = f.getCanonicalPath();
            if(!f.exists() || f.isDirectory()) {
                throw new Exception("Source APK File not found.");
            }

            String basename = FilenameUtils.getBaseName(apkPath);
            Constants.PROJECT_NAME = basename;

            // Neo4J checks
            String neo4jHomeEnv = System.getenv("NEO4J_HOME");

            if (neo4jHomeEnv == null) {
                throw new Exception("NEO4J_HOME environment variable not found.");
            }
            f = new File(neo4jHomeEnv);
            if(!f.exists() || !f.isDirectory()) {
                throw new Exception("NEO4J_HOME environment variable location not exists.");
            }
            Path path = Paths.get(neo4jHomeEnv, "bin", "neo4j-admin");
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                path = path.resolveSibling(path.getFileName() + ".bat");
            }
            Constants.NEO4J_ADMIN = path.toString();

            // Basic User home project path check
            String userHome = System.getProperty("user.home");
            f = new File(userHome);
            if(!f.exists() || !f.isDirectory()) {
                String exMEssage = String.format("User home directory (%1$s) not found.", userHome);
                throw new Exception(exMEssage);
            }
            // base projects dir.
            Path basePath = Paths.get(userHome, ".nGraph");
            if (!Files.exists(basePath)) {
                Files.createDirectories(basePath);
            }
            // Specified project dir.
            Path prjPath = Paths.get(userHome, ".nGraph", basename);
            if (!Files.exists(prjPath)) {
                Files.createDirectories(prjPath);
            }
            // CSV's dir (nodes/edges).
            Path auxPath = Paths.get(userHome, ".nGraph", basename, ".csv");
            if (!Files.exists(auxPath)) {
                Files.createDirectories(auxPath);
            }
            Constants.CSV_OUTPUT = auxPath.toString();
            // apktool output dir.
            auxPath = Paths.get(userHome, ".nGraph", basename, ".apk");
            if (!Files.exists(auxPath)) {
                Files.createDirectories(auxPath);
            }
            Constants.SMALI_OUTPUT = auxPath.toString();
            // procyon output dir.
            auxPath = Paths.get(userHome, ".nGraph", basename, ".java");
            if (!Files.exists(auxPath)) {
                Files.createDirectories(auxPath);
            }
            Constants.JAVA_OUTPUT = auxPath.toString();
        } catch (IOException ex) {
            throw new Exception("Project structure creation error.");
        }

    }

    // Delete not needed dirs after Neo4J indexation (csv, smali)
    public static void cleanDirectoryStructure()throws Exception {
        try {
            FileUtils.deleteDirectory( new File(Constants.CSV_OUTPUT) );
            FileUtils.deleteDirectory( new File(Constants.SMALI_OUTPUT) );
        } catch (IOException ex) {
            throw new Exception("Project structure clean error.");
        }
    }
}
