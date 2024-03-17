package cycling;

import java.io.Serializable;

public class Team implements Serializable {
    private final String name;
    private final String description;
    private final int id;

    public Team(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String toString() {
        return "Team[name="+name+",description="+description+"]";
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }
}
