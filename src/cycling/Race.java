package cycling;

import java.io.Serializable;

public class Race implements CyclingEntity {
    private final int id;
    private final String name;
    private final String description;

    public Race(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    public String toString() {
        return "Race[id="+id+", name="+name+", description="+description+"]";
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
}
