package weird.ngraph.Domain;

public class Field extends Entity {
    public boolean error;
    public String id;
    public String klass;
    public String name;
    public String field_type;

    public Field(String definition) {
        try {
            this.id = this.identifier(definition);
            this.klass = this.clean(definition.split("->")[0]);
            this.name = this.clean(definition.split("->")[1].split(":")[0]);
            this.field_type = this.clean(definition.split(":")[1]);
            this.error = false;
            if (this.id.equals("B15835F133FF2E27C7CB28117BFAE8F4")) {
                System.out.println(definition);
            }
        } catch (Exception e) {
            this.error = true;
        }
    }

    public String[] fields() {
        if (this.error) {
            return new String[] {};
        } else {
            return new String[] {this.id, this.klass, this.name, this.field_type };
        }
    }

}
