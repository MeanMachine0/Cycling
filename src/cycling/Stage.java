package cycling;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Contained within a {@link Race}, contains {@link Checkpoint}s.
 *
 * @author Marcus Carter
 */
public class Stage extends Entity implements HasChildren {
    protected final String description;
    protected final double length;
    protected final LocalDateTime start;
    protected final StageType type;
    private String state;
    private final ArrayList<Checkpoint> checkpoints = new ArrayList<>();
    private final Map<Rider, LocalDateTime[]> results = new HashMap<>();
    public static final EnumMap<StageType, ArrayList<Integer>> SPRINTER_POINTS = new EnumMap<>(StageType.class);

    static {
        SPRINTER_POINTS.put(StageType.FLAT,
                new ArrayList<>(List.of(50, 30, 20, 18, 16, 14, 12, 10, 8, 7, 6, 5, 4, 3, 2)));
        SPRINTER_POINTS.put(StageType.MEDIUM_MOUNTAIN,
                new ArrayList<>(List.of(30, 25, 22, 19, 17, 15, 13, 11, 9, 7, 6, 5, 4, 3, 2)));
        SPRINTER_POINTS.put(StageType.HIGH_MOUNTAIN,
                new ArrayList<>(List.of(20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1)));
        SPRINTER_POINTS.put(StageType.TT,
                new ArrayList<>(List.of(20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1)));
    }
    public Stage(int id, String name, String description, double length, LocalDateTime start, StageType type) {
        super(id, name);
        this.description = description;
        this.length = length;
        this.start = start;
        this.type = type;
        state = "preparation";
    }
    @Override
    public String toString() {
        return "Stage[id="+id+", name="+name+", description="+description+", length="+length+
                "km, start="+start+", type="+type+"]";
    }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    @Override
    public ArrayList<Checkpoint> getChildren() { return checkpoints; }
    public Map<Rider, LocalDateTime[]> getResults() { return results; }
    public int numCriticalPoints() { return checkpoints.size() + 2; }
    public void addResult(Rider rider, LocalDateTime[] criticalTimes) throws DuplicatedResultException {
        if (results.containsKey(rider)) throw new DuplicatedResultException();
        results.put(rider, criticalTimes);
    }

    /**
     * Adds a checkpoint to the checkpoints in the stage, then sorts them by their locations, ascending.
     *
     * @param checkpoint the checkpoint to be added to the stage.
     */
    public void addCheckpoint(Checkpoint checkpoint) {
        checkpoints.add(checkpoint);
        checkpoints.sort(Comparator.comparingDouble(Checkpoint::getLocation));
    }

    /**
     * Calculates the elapsed time of a rider in a stage which is not a time trial.
     *
     * @param riderEnd the time the rider finished the stage.
     * @return the rider's elapsed time in this stage.
     */
    public Duration timeElapsed(LocalDateTime riderEnd) {
        return Duration.between(start, riderEnd);
    }

    /**
     * Calculates the elapsed time of a rider in a time trial stage.
     *
     * @param riderStart the time the rider began the stage.
     * @param riderEnd the time the rider finished the stage.
     * @return the rider's elapsed time in this stage.
     */
    public Duration ttTimeElapsed(LocalDateTime riderStart, LocalDateTime riderEnd) {
        return Duration.between(riderStart, riderEnd);
    }
    public boolean isTimeTrial() { return type.equals(StageType.TT); }
    public boolean isInPreparation() { return !state.equals("waiting for results"); }
}
