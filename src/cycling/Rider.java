package cycling;

/**
 * Contained within a {@link Team}.
 */
public class Rider extends Entity {
    protected final int yearOfBirth;
    public Rider(int id, String name, int yearOfBirth) {
        super(id, name);
        this.yearOfBirth = yearOfBirth;
    }
    @Override
    public String toString() {
        return "Rider[id="+id+", name=" + name + ", yearOfBirth=" + yearOfBirth + "]";
    }
}
