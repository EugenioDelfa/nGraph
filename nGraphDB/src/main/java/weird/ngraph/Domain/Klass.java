package weird.ngraph.Domain;

public class Klass extends Entity {
    public String id = null;
    public String definition = null;

    public Klass(String definition) {
        this.id = this.identifier(definition);
        this.definition = this.clean(definition);
    }

    public String[] fields() {
        return new String[] {this.id, this.definition };
    }

}
