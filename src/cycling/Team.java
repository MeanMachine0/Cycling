package cycling;

import java.io.Serializable;

public class Team implements Serializable {
    private final int id;
    private final String name;
    private final String description;

    public Team(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    public String toString() {
        return "Team[id="+id+", name="+name+", description="+description+"]";
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
