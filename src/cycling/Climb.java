package cycling;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class Climb extends Checkpoint {
    private final double averageGradient;
    private final double length;
    public static final EnumMap<CheckpointType, ArrayList<Integer>> MOUNTAIN_POINTS = new EnumMap<>(CheckpointType.class);

    static {
        MOUNTAIN_POINTS.put(CheckpointType.C4, new ArrayList<>(List.of(1)));
        MOUNTAIN_POINTS.put(CheckpointType.C3, new ArrayList<>(List.of(2, 1)));
        MOUNTAIN_POINTS.put(CheckpointType.C2, new ArrayList<>(List.of(5, 3, 2, 1)));
        MOUNTAIN_POINTS.put(CheckpointType.C1, new ArrayList<>(List.of(10, 8, 6, 4, 2, 1)));
        MOUNTAIN_POINTS.put(CheckpointType.HC, new ArrayList<>(List.of(20, 15, 12, 10, 8, 6, 4, 2)));
    }
    public Climb(int id, String name, CheckpointType type, double location, double averageGradient, double length) {
        super(id, name, type, location);
        this.averageGradient = averageGradient;
        this.length = length;
    }
    @Override
    public String toString() {
        return "Checkpoint(Climb)[id="+id+", type="+type+", location="+location+
                ", averageGradient="+averageGradient+", length="+length+"]";
    }

    public double getAverageGradient() { return averageGradient; }
    public double getLength() { return length; }
}
