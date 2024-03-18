package cycling;

import java.io.Serializable;
import java.util.ArrayList;

public class Team implements CyclingEntity {
    private final int id;
    private final String name;
    private final String description;
    private final ArrayList<Rider> riders = new ArrayList<>();

    public Team(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    public String toString() {
        return "Team[id="+id+", name="+name+", description="+description+"]";
    }

    @Override
    public int getId() {
        return id;
    }
    @Override
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public ArrayList<Rider> getRiders() { return riders; }
}
