package retail;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.awt.Component;

/**
 * @author Jacek Karolczak
 */
public abstract class Human extends Thread {
    
    // fields
    private Location currentLocation;
    private Location destination;
    private boolean maskOn;
    private boolean sick;
    private boolean vaccinated;
    private boolean live;
    private int visitedShops;
    private int status;
    private JButton button;
    private Component component;
    private World world;
    
    // constructor
    public Human(int x, int y) {
        this.currentLocation = new Location(x, y);
        this.destination = null;
        this.maskOn = false;
        this.sick = false;
        this.vaccinated = false;
        this.visitedShops = 0;
        this.status = 0;
        this.live = true;
    }
    
    // setters
    public void setDestination(Location destination) {
        this.destination = destination;
    }
    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
    public void setMaskOn(boolean maskOn) {
        this.maskOn = maskOn;
    }
    public void setSick(boolean sick) {
        this.sick = sick;
    }
    public void setVaccinated(boolean vaccinated) {
        this.vaccinated = vaccinated;
    }
    public void setVisitedShops(int visitedShops) {
        this.visitedShops = visitedShops;
    }
    public void setStatus(int status) {
        this.status = status;
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
    public void setLive(boolean live) {
        this.live = live;
    }
    
    // getters
    public Location getDestination() {
        return destination;
    }
    public Location getCurrentLocation() {
        return currentLocation;
    }
    public boolean isMaskOn() {
        return maskOn;
    } 
    public boolean isSick() {
        return sick;
    }
    public boolean isVaccinated() {
        return vaccinated;
    }
    public int getVisitedShops() {
        return visitedShops;
    }
    public int getStatus() {
        return status;
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
    public boolean isLive() {
        return live;
    }
    
    // methods
    private boolean destinationArrived() {
        return (this.currentLocation.getX() == this.destination.getX()) && (this.currentLocation.getY() == this.destination.getY());
    }
    
    /**
     * Forces human to put mask on
     */
    public void putMaskOn() {
        this.setMaskOn(true);
    }
    
    /**
     * Forces human to take mask off
     */
    public void takeMaskOff() {
        this.setMaskOn(false);
    }
    
    /**
     * Tries to make human sick, according to infection probability of this
     */
    public void sick(){
        float probability = this.getWorld().getTransmissionRate();
        if(this.isMaskOn())
            probability *= this.getWorld().getMaskTransmissionRate();
        if(this.isVaccinated())
            probability *= this.getWorld().getVaccinedTransmissionRate();
        if(Math.random() < probability)
            this.setSick(true);
    }
    
    protected synchronized void travelToShop() {
        World world = this.getWorld();
        while(!this.destinationArrived() && this.isLive()){
            this.travel();
            this.infect();
            try {
                wait(world.getSimulationSpeed());
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.setStatus(this.getStatus() + 1);
    }
    
    /**
     * Controls human behavior
     */
    @Override
    public abstract void run();
    
    protected void travel() {
        if(this.currentLocation.getX() < this.destination.getX()){
            this.moveRight();
        }
        else if(this.currentLocation.getX() > this.destination.getX()){
            this.moveLeft();
        }
        else if(this.currentLocation.getY() < this.destination.getY()){
            this.moveDown();
        }
        else if(this.currentLocation.getY() > this.destination.getY()){
            this.moveUp();
        }
    }
    
    protected void moveRight() {
        int travelDistance = 10;
        this.move(travelDistance, 0);   
    }
    protected void moveLeft() {
        int travelDistance = 10;
        this.move(-1 * travelDistance, 0);   
    }
    
    protected void moveUp() {
        int travelDistance = 10;
        this.move(0, -1 * travelDistance);   
    }
    
    protected void moveDown() {
        int travelDistance = 10;
        this.move(0, travelDistance);   
    }
    
    protected void move(int x, int y) {
        Location oldLocation = this.getCurrentLocation();
        Location newLocation = this.getCurrentLocation().relativeLocation(x, y);
        
        if((newLocation.getX() - 50) % 200 == 0 && (newLocation.getY() - 50) % 200 == 0) {
            int intersectionX = ((newLocation.getX() - 50) / 200);
            int intersectionY = ((newLocation.getY() - 50) / 200);
            try {
                world.getIntersections()[intersectionX][intersectionY].acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(Human.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.setCurrentLocation(newLocation);
        if((oldLocation.getX() - 50) % 200 == 0 && (oldLocation.getY() - 50) % 200 == 0) {
            int intersectionX = ((oldLocation.getX() - 50) / 200);
            int intersectionY = ((oldLocation.getY() - 50) / 200);
            world.getIntersections()[intersectionX][intersectionY].release();
        }
    }

    protected void infect() {
        World world = this.getWorld();
        if(this.isSick() && this.getVisitedShops() >= this.getWorld().getVisitedStoreBeforeRecovery()){
            this.setVisitedShops(0);
            this.setSick(false);
        }
        if (this.isSick()){
            world.infectHumansNextTo(this.currentLocation);
        }
    }
}
