package cycling;

public class Checkpoint extends Entity {
    protected final CheckpointType type;
    protected final double location;
    public Checkpoint(int id, String name, CheckpointType type, double location) {
        super(id, name);
        this.type = type;
        this.location = location;
    }
    public String toString() { return "Checkpoint[id="+id+", type="+type+", location="+location+"]"; }

    public CheckpointType getType() { return type; }
    public double getLocation() { return location; }
}
