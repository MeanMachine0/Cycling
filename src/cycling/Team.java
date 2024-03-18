package cycling;

import java.util.ArrayList;

public class Team extends Entity implements HasDescription {
    private final String description;
    private final ArrayList<Rider> riders = new ArrayList<>();

    public Team(int id, String name, String description) {
        super(id, name);
        this.description = description;
    }
    public String toString() {
        return "Team[id="+id+", name="+name+", description="+description+"]";
    }

    @Override
    public String getDescription() {
        return description;
    }
    public ArrayList<Rider> getRiders() { return riders; }
}
