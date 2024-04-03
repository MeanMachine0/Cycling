package cycling;

import java.util.ArrayList;

/**
 * Contains {@link Rider}s
 *
 * @author Marcus Carter
 */
public class Team extends Entity implements HasChildren {
    protected final String description;
    private final ArrayList<Rider> riders = new ArrayList<>();
    public Team(int id, String name, String description) {
        super(id, name);
        this.description = description;
    }
    @Override
    public String toString() {
        return "Team[id="+id+", name="+name+", description="+description+"]";
    }

    @Override
    public ArrayList<Rider> getChildren() { return riders; }
}
