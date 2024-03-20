package cycling;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Stage extends Entity implements HasDescription {
    private final String description;
    private final double length;
    private final LocalDateTime startTime;
    private final StageType type;
    private final ArrayList<Checkpoint> checkpoints = new ArrayList<>();
    public Stage(int id, String name, String description, double length, LocalDateTime startTime, StageType type) {
        super(id, name);
        this.description = description;
        this.length = length;
        this.startTime = startTime;
        this.type = type;
    }
    public String toString() {
        return "Stage[id="+id+", name="+name+", description="+description+", length="+length+"km, startTime="+startTime+", type="+type+"]";
    }

    public String getDescription() {
        return description;
    }
    public double getLength() {
        return length;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public StageType getType() {
        return type;
    }
    public ArrayList<Checkpoint> getCheckpoints() { return checkpoints; }
}
