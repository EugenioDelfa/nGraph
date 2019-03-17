package weird.ngraph.Domain;

public class Relation {
    public String source;
    public String target;

    public Relation(String source, String target) {
        this.source = source;
        this.target = target;
    }

    public String[] fields() {
        return new String[] {this.source, this.target };
    }
}
