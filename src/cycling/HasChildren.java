package cycling;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Implemented in container classes.
 *
 * @author Marcus Carter
 */
public interface HasChildren extends Serializable {
    /**
     * @return an ArrayList of the contained class.
     */
    ArrayList<? extends Entity> getChildren();
}
