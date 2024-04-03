package cycling;

/**
 * Contained within a {@link Stage}.
 *
 * @author Marcus Carter
 */
public class Checkpoint extends Entity {
    protected final CheckpointType type;
    protected final double location;
    public static final int[] INTERMEDIATE_SPRINT_POINTS = { 20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };
    public Checkpoint(int id, String name, CheckpointType type, double location) {
        super(id, name);
        this.type = type;
        this.location = location;
    }
    @Override
    public String toString() { return "Checkpoint[id="+id+", type="+type+", location="+location+"]"; }

    public double getLocation() { return location; }
}
