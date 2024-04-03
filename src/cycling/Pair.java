package cycling;

/**
 * A pair of objects.
 *
 * @param <One> The class of the first object in the pair.
 * @param <Two> The class of the second object in the pair.
 *
 * @author Marcus Carter
 */
public class Pair<One, Two> {
    private One one;
    private Two two;

    public Pair(One one, Two two) {
        this.one = one;
        this.two = two;
    }
    @Override
    public String toString() {
        return "Pair["+one+", "+two+"]";
    }

    public One getOne() {
        return one;
    }
    public void setOne(One one) {
        this.one = one;
    }
    public Two getTwo() {
        return two;
    }
    public void setTwo(Two two) {
        this.two = two;
    }
}
