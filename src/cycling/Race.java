package cycling;

import java.util.ArrayList;

public class Race extends Entity implements HasDescription {
    private final String description;
    private final ArrayList<Stage> stages = new ArrayList<>();

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
    public ArrayList<Stage> getStages() {
        return stages;
    }
}
