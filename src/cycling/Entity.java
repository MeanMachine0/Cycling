package cycling;

import java.io.Serializable;

public class Entity implements Serializable {
    protected final int id;
    protected final String name;

    public Entity(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public String toString() {
        return "Entity[id="+id+", name="+name+"]";
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Entity entity = (Entity) object;
        return this.id == entity.id;
    }
}
