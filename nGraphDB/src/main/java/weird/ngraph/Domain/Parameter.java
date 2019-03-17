package weird.ngraph.Domain;

public class Parameter extends Entity {
    public String id;
    public String definition;

    public Parameter(String definition) {
        this.id = this.identifier(definition);
        this.definition = this.clean(definition);
    }

    public String[] fields() {
        return new String[] {this.id, this.definition };
    }
}
