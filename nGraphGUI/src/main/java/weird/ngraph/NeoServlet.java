package weird.ngraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.HTTP;

public class NeoServlet extends HttpServlet
{
    private static final TimeZone TZ = TimeZone.getDefault();
    private NeoDriver driver;

    public void init() throws ServletException
    {
        this.driver = new NeoDriver();
    }
    public void destroy()
    {
        this.driver.close();
    }

    public void response(HttpServletResponse resp, String message) {
        try {
            JSONObject result = new JSONObject();
            result.put("status", message);
            result.put("nodes", this.driver.nodes);
            result.put("rels", this.driver.edges);
            resp.setContentType("application/json");
            resp.getWriter().println( result.toString() );
            resp.getWriter().close();
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        Locale locale = req.getLocale();
        Calendar cal = Calendar.getInstance(TZ,locale);
        String dateStr = DateFormat.getDateInstance(DateFormat.DEFAULT,locale).format(cal.getTime());
        String timeStr = DateFormat.getTimeInstance(DateFormat.DEFAULT,locale).format(cal.getTime());
        String tzStr = TZ.getDisplayName(false,TimeZone.SHORT,locale);
        String finalStr = String.format("%s %s %s",dateStr,timeStr,tzStr);

        String pathInfo = req.getPathInfo();
        String message = "";
        if (pathInfo.equals("/totals/")) {
            JSONObject result = new JSONObject();
            result.put("status", message);
            result.put("nodes", this.driver.total_nodes);
            result.put("rels", this.driver.total_edges);
            resp.setContentType("application/json");
            resp.getWriter().println( result.toString() );
            resp.getWriter().close();
        } else if (pathInfo.equals("/reset/")) {
            this.driver.dups.clear();
            this.driver.nodes.clear();
            this.driver.edges.clear();
            this.response(resp, message);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        StringBuffer jb = new StringBuffer();
        String line = "";
        try {
            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) {
            resp.setContentType("application/json");
            resp.getWriter().println( new JSONObject().toString() );
            resp.getWriter().close();
        }

        JSONObject parameters = new JSONObject(jb.toString());
        String q;

        int i = 0;
        String message = "";
        String pathInfo = req.getPathInfo();
        if (pathInfo.equals("/code/")) {
            if (parameters.has("class")) {
                q = parameters.get("class").toString();
                String cname = FilenameUtils.separatorsToSystem( q.substring(1, q.length() - 1) );
                File f = new File(Paths.get( Constants.PROJECT_NAME, cname ).toString() + ".java");
                byte[] encoded = Files.readAllBytes(f.toPath());
                String code = new String(encoded, StandardCharsets.UTF_8);
                JSONObject result = new JSONObject();
                result.put("code", code);
                result.put("status", message);
                resp.setContentType("application/json");
                resp.getWriter().println( result.toString() );
                resp.getWriter().close();
            }
        } else if (pathInfo.equals("/query/")) {
            if (parameters.has("query")) {
                q = parameters.get("query").toString();
                this.driver.dups.clear();
                message = this.driver.query(q);
                this.response(resp, message);
            }
        } else if (pathInfo.equals("/expand/")) {
            if (parameters.has("id")) {
                JSONArray jArray = parameters.getJSONArray("id");
                for (int ii = 0; ii < jArray.length(); ii++) {
                    q = jArray.getString(ii);
                    message = this.driver.query(String.format("MATCH p=(n)-[]-() WHERE id(n)=%1$s RETURN nodes(p) as n, relationships(p) as r;", q));
                }
                this.response(resp, message);
            }
        } else {
            JSONObject result = new JSONObject();
            result.put("status", message);
            resp.setContentType("application/json");
            resp.getWriter().println( result.toString() );
            resp.getWriter().close();
        }
    }
}