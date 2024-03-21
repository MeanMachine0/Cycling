package cycling;

import java.io.Serializable;
import java.util.ArrayList;

public interface HasChildren extends Serializable {
    ArrayList<? extends Entity> getChildren();
}
