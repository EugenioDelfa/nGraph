package weird.ngraph.Domain;

public class Literal extends Entity {
    public String id;
    public String value;

    public Literal(java.lang.String definition) {
        this.id = this.identifier(definition);
        this.value = this.clean(definition);
    }

    public java.lang.String[] fields() {
        return new String[] {this.id, this.value };
    }
}
