package cycling;

public class Race extends Entity implements HasDescription {
    private final String description;

    public Race(int id, String name, String description) {
        super(id, name);
        this.description = description;
    }
    public String toString() {
        return "Race[id="+id+", name="+name+", description="+description+"]";
    }

    public String getDescription() {
        return description;
    }
}
