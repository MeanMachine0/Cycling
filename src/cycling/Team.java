package cycling;

import java.util.ArrayList;

public class Team extends Entity implements HasChildren {
    private final String description;
    private final ArrayList<Rider> riders = new ArrayList<>();

    public Team(int id, String name, String description) {
        super(id, name);
        this.description = description;
    }
    @Override
    public String toString() {
        return "Team[id="+id+", name="+name+", description="+description+"]";
    }


    public String getDescription() {
        return description;
    }
    @Override
    public ArrayList<Rider> getChildren() { return riders; }
}
