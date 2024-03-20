package cycling;

public class Climb extends Checkpoint {
    private final double averageGradient;
    private final double length;
    public Climb(int id, String name, CheckpointType type, double location, double averageGradient, double length) {
        super(id, name, type, location);
        this.averageGradient = averageGradient;
        this.length = length;
    }
    public String toString() {
        return "Checkpoint(Climb)[id="+id+", type="+type+", location="+location+
                ", averageGradient="+averageGradient+", length="+length+"]";
    }

    public double getAverageGradient() { return averageGradient; }
    public double getLength() { return length; }
}
