package cycling;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Stage extends Entity implements HasChildren {
    private final String description;
    private final double length;
    private final LocalDateTime startTime;
    private final StageType type;
    private final ArrayList<Checkpoint> checkpoints = new ArrayList<>();
    private final Map<Rider, LocalTime[]> results = new HashMap<>();
    public Stage(int id, String name, String description, double length, LocalDateTime startTime, StageType type) {
        super(id, name);
        this.description = description;
        this.length = length;
        this.startTime = startTime;
        this.type = type;
    }
    @Override
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
    @Override
    public ArrayList<Checkpoint> getChildren() { return checkpoints; }
    public Map<Rider, LocalTime[]> getResults() { return results; }

    public int numCriticalPoints() { return checkpoints.size() + 2; }
    public void addResult(Rider rider, LocalTime[] criticalTimes) throws DuplicatedResultException {
        if (results.containsKey(rider)) throw new DuplicatedResultException();
        results.put(rider, criticalTimes);
    }
}
