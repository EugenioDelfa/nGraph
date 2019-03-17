package weird.ngraph.Domain;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class Entity {
    protected String clean(String data) {
        return data.replace("\\", "").replace("\"", "");
    }

    protected String identifier(String definition) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(definition.getBytes());
            return DatatypeConverter.printHexBinary(md.digest());
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    public abstract String[] fields();

}
