package cycling;

public class Rider extends Entity {
    private final int yearOfBirth;

    public Rider(int id, String name, int yearOfBirth) {
        super(id, name);
        this.yearOfBirth = yearOfBirth;
    }
    public String toString() {
        return "Rider[id="+id+", name=" + name + ", yearOfBirth=" + yearOfBirth + "]";
    }

    public int getYearOfBirth() { return yearOfBirth; }
}
