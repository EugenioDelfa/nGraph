package weird.ngraph;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

public class App {
    private static final Options allOptions = new Options();
    public static void main(String[] args) throws Throwable
    {
        CommandLineParser parser  = null;
        CommandLine       cmdLine = null;
        _Options();
        try {
            parser  = new DefaultParser();
            cmdLine = parser.parse(allOptions, args, false);
            String projectPath = cmdLine.getOptionValue("project");
            boolean prjList = (cmdLine.hasOption("--list") || cmdLine.hasOption("-l"));
            boolean quiet = (cmdLine.hasOption("-q") || cmdLine.hasOption("--quiet"));
            if (prjList) {
                projectList();
            } else if (projectPath != null){
                String userHome = System.getProperty("user.home");
                Path basePath = Paths.get(userHome, ".nGraph", projectPath, ".java");
                if (basePath.toFile().isDirectory()) {
                    Constants.PROJECT_NAME = basePath.toString();
                    new App().run();
                } else {
                    System.out.print( String.format("Project <%1$s> not found, ", projectPath) );
                    projectList();
                }
            } else {
                System.out.println("Serializer v0.01");
                new HelpFormatter().printHelp(App.class.getCanonicalName(), allOptions);
            }
        } catch (ParseException ex) {
            System.out.println("Serializer v0.01");
            new HelpFormatter().printHelp(App.class.getCanonicalName(), allOptions);
        }
    }

    public void run() throws Exception
    {
        Server server = new Server(8080);
        URL webRootLocation = this.getClass().getResource("/www/index.html");
        if (webRootLocation == null)
        {
            throw new IllegalStateException("Unable to determine webroot URL location");
        }
        URI webRootUri = URI.create(webRootLocation.toURI().toASCIIString().replaceFirst("/index.html$","/"));
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.setBaseResource(Resource.newResource(webRootUri));
        context.setWelcomeFiles(new String[] { "index.html" });
        context.getMimeTypes().addMimeMapping("txt","text/plain;charset=utf-8");
        server.setHandler(context);
        context.addServlet(NeoServlet.class,"/graph/*");
        context.addServlet(DefaultServlet.class,"/");
        server.start();
        server.join();
    }

    public static void projectList() {
        String userHome = System.getProperty("user.home");
        Path basePath = Paths.get(userHome, ".nGraph");
        String[] directories = basePath.toFile().list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        System.out.println( "Available project names:" );
        for (String prj : directories) {
            System.out.println( "\t - " + prj );
        }
    }

    private static void _Options() {
        Option projectOption = new Option("p", "project", true, "Project name (%HOME%\\.nGraph\\{PROJECT_NAME}).");
        //projectOption.setRequired(true);
        Option listProjectsOption = new Option("l", "list", false, "List available project names");
        Option quietOption = new Option("q", "quiet", false, "Stay quiet.");
        allOptions.addOption(projectOption);
        allOptions.addOption(listProjectsOption);
        allOptions.addOption(quietOption);

    }
}