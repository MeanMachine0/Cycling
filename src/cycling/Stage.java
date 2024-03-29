package cycling;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class Stage extends Entity implements HasChildren {
    private final String description;
    private final double length;
    private final LocalDateTime startTime;
    private final StageType type;
    private String state;
    private final ArrayList<Checkpoint> checkpoints = new ArrayList<>();
    private final Map<Rider, LocalTime[]> results = new HashMap<>();
    public static final EnumMap<StageType, ArrayList<Integer>> SPRINTER_POINTS = new EnumMap<>(StageType.class);

    static {
        SPRINTER_POINTS.put(StageType.FLAT,
                (ArrayList<Integer>) List.of(50, 30, 20, 18, 16, 14, 12, 10, 8, 7, 6, 5, 4, 3, 2));
        SPRINTER_POINTS.put(StageType.MEDIUM_MOUNTAIN,
                (ArrayList<Integer>) List.of(30, 25, 22, 19, 17, 15, 13, 11, 9, 7, 6, 5, 4, 3, 2));
        SPRINTER_POINTS.put(StageType.HIGH_MOUNTAIN,
                (ArrayList<Integer>) List.of(20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1));
        SPRINTER_POINTS.put(StageType.TT,
                (ArrayList<Integer>) List.of(20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1));
    }
    public Stage(int id, String name, String description, double length, LocalDateTime startTime, StageType type) {
        super(id, name);
        this.description = description;
        this.length = length;
        this.startTime = startTime;
        this.type = type;
        state = "preparation";
    }
    @Override
    public String toString() {
        return "Stage[id="+id+", name="+name+", description="+description+", length="+length+
                "km, startTime="+startTime+", type="+type+"]";
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
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    @Override
    public ArrayList<Checkpoint> getChildren() { return checkpoints; }
    public Map<Rider, LocalTime[]> getResults() { return results; }

    public int numCriticalPoints() { return checkpoints.size() + 2; }
    public void addResult(Rider rider, LocalTime[] criticalTimes) throws DuplicatedResultException {
        if (results.containsKey(rider)) throw new DuplicatedResultException();
        results.put(rider, criticalTimes);
    }
}
