package weird.ngraph.Utils;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ResManagement {
    public String getResourceFromPath(String path) throws Exception {
        Map<String, File> mExtracted = new HashMap();
        File binary = (File) mExtracted.get(path);
        if (binary == null) {
            Class klass = this.getClass();
            try {
                InputStream in = klass.getResourceAsStream(path);
                if (in == null) {
                    throw new FileNotFoundException(path);
                }
                File fileOut = File.createTempFile("nGraph_res_", null);
                fileOut.deleteOnExit();
                OutputStream out = new FileOutputStream(fileOut);
                IOUtils.copy(in, out);
                in.close();
                out.close();
                binary = fileOut;
            } catch (IOException ex) {
                String exMessage = String.format("Prebuilt Resource not found (%1$s).", path);
                throw new Exception(exMessage);
            }
            mExtracted.put(path, binary);
        }
        return binary.toString();
    }
}