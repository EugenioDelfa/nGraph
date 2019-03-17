package weird.ngraph.Serializer;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import weird.ngraph.Analysis.FileAnalysis;
import weird.ngraph.Analysis.ProjectManagement;
import weird.ngraph.Utils.FunctionWrapper;

public class Main {

    private static final Options allOptions = new Options();

    public static void main(String[] args) {
        CommandLineParser parser  = null;
        CommandLine       cmdLine = null;

        _Options();
        try {
            parser  = new DefaultParser();
            cmdLine = parser.parse(allOptions, args, false);
            String apkPath = cmdLine.getOptionValue("path");
            boolean garbage = (cmdLine.hasOption("-g") || cmdLine.hasOption("--garbage"));
            boolean decomp = (cmdLine.hasOption("-j") || cmdLine.hasOption("--java"));
            boolean quiet = (cmdLine.hasOption("-q") || cmdLine.hasOption("--quiet"));

            long start = FunctionWrapper.timeSnapshot();
            ProjectManagement prj = new ProjectManagement();
            prj.createDirectoryStructure(apkPath);

            FileAnalysis fla = new FileAnalysis();
            fla.applicationAnalyzer(quiet, garbage, decomp);
            if (garbage) {
                FunctionWrapper.timmed(quiet,"Temporal files deletion", () -> {
                    try {prj.cleanDirectoryStructure();} catch (Exception e) {}
                });
            }
            long end = FunctionWrapper.timeSnapshot();
            String strTime = FunctionWrapper.timeDiff(start, end);
            if (!quiet) {
                System.out.println("Total analysis time: " + strTime);
            }
        } catch (ParseException ex) {
            System.out.println("Serializer v0.01");
            new HelpFormatter().printHelp(Main.class.getCanonicalName(), allOptions);
        } catch (Exception ex) {
            System.out.println("ERROR: " + ex.getMessage());
        }
    }

    private static void _Options() {
        Option apkPathOption = new Option("p", "path", true, "APK File/Directory path.");
        apkPathOption.setRequired(true);
        Option garbageOption = new Option("g", "garbage", false, "Delete temporal project files (smali & csv).");
        Option javaOption = new Option("j", "java", false, "Decompile Java Code (time consuming task).");
        Option quietOption = new Option("q", "quiet", false, "Stay quiet.");
        allOptions.addOption(apkPathOption);
        allOptions.addOption(garbageOption);
        allOptions.addOption(javaOption);
        allOptions.addOption(quietOption);
    }

}
