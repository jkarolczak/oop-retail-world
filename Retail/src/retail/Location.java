package retail;

/**
 * @author Jacek Karolczak
 */
public class Location {
    
    // fields
    private int x;
    private int y;
    
    // constructor
    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    // setters
    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    
    // getters
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    
    // methods
    /**
     * Compare if given location points to the same place on map
     * @param foreign location to be checked
     * @return boolean value, if given location points to the same  place
     */
    public boolean compare(Location foreign) {
        return (this.x == foreign.getX() && this.y == foreign.getY());
    }
    
    /**
     * Find relative location
     * @param x relative location's x coordinate
     * @param y relative location's y coordinate
     * @return relative location
     */
    public Location relativeLocation(int x, int y) {
        Location relativeLocation = new Location(this.x + x, this.y + y);
        return relativeLocation;
    }
}
