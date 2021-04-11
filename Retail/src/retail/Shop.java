package retail;

import java.awt.Component;
import javax.swing.JButton;

/**
 * @author Jacek Karolczak
 */
public abstract class Shop {
    
    //fields
    private Location location;
    private String name;
    private String address;
    private Container storage;
    private JButton button;
    private Component component;
    private World world;

    // constructor
    public Shop(String name, String address, int capacity, int x, int y) {
        this.location = new Location(x, y);
        this.name = name;
        this.address = address;
        this.storage = new Container(capacity); 
    }
    
    // setters
    public void setLocation(int x, int y){
        this.location = new Location(x, y);
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setStorage(Container storage) {
        this.storage = storage;
    }
    public void setButton(JButton button) {
        this.button = button;
    }
    public void setComponent(Component component) {
        this.component = component;
    }
    public void setWorld(World world) {
        this.world = world;
    }  
    
    // getters
    public Location getLocation() {
        return location;
    }
    
    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }
    public Container getStorage() {
        return storage;
    }

    public JButton getButton() {
        return button;
    }
    public Component getComponent() {
        return component;
    }
    public World getWorld() {
        return world;
    }
    
    
    
    // methods
    /**
     * Check best before date and remove outdated products from a storage
     */
    public void checkDates() {
        this.storage.checkDates();
    }
}
