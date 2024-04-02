package cycling;

import java.util.ArrayList;

public class Race extends Entity implements HasChildren {
    protected final String description;
    private final ArrayList<Stage> stages = new ArrayList<>();
    public Race(int id, String name, String description) {
        super(id, name);
        this.description = description;
    }
    @Override
    public String toString() {
        return "Race[id="+id+", name="+name+", description="+description+", numStages="+stages.size()+
                ", totalLength="+stages.stream().mapToDouble(stage -> stage.length).reduce(0, Double::sum)+"km]";
    }
    @Override
    public ArrayList<Stage> getChildren() { return stages; }
}
