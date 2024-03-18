package cycling;

public class Rider {
    private final int id;
    private final String name;
    private final int yearOfBirth;

    public Rider(int id, String name, int yearOfBirth) {
        this.id = id;
        this.name = name;
        this.yearOfBirth = yearOfBirth;
    }
    public String toString() {
        return "Rider[id="+id+", name=" + name + ", yearOfBirth=" + yearOfBirth + "]";
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getYearOfBirth() { return yearOfBirth; }
}
