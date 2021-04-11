package retail;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * @author Jacek Karolczak
 */
public class Supplier extends Human {
    
    // fields
    private int supplierId;
    private float tank;
    private Container trunk;
    private String companyName;
    private String carBrand;
    private Location[] path;

    // constructor
    public Supplier(int id, String companyName, String carBrand, int trunkCapacity, int x, int y, JPanel mapPanel) {
        super(x, y);
        this.supplierId = id;
        this.tank = (float)1.0;
        this.trunk = new Container(trunkCapacity);
        this.companyName = companyName;
        this.carBrand = carBrand;
        this.path = new Location[3];
        this.createButton(mapPanel);
    }
    
    // setters
    public void setSupplierId(int id) {
        this.supplierId = id;
    }
    public void setTank(float tank) {
        this.tank = tank;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    // getters
    public int getSupplierId() {
        return this.supplierId;
    }
    public float getTank() {
        return tank;
    }
    public Container getTrunk() {
        return trunk;
    }
    public String getCompanyName() {
        return companyName;
    }
    public String getCarBrand() {    
        return carBrand;
    }
    
    // methods 
    /**
     * Controls supplier behavior
     */
    @Override
    public synchronized void run() {
        World world = this.getWorld();
        this.addInitialPath(world);
        while(true){
            switch(this.getStatus()){
                case 0:
                    this.travelToShop();
                    break;
                case 1:
                    this.enterShop();
                    break;
                case 2:
                    this.loadArticles();
                    this.refillTank();
                    break;
                case 3:
                    this.leaveShop();
                    this.addToPath(world.getRandomRetail().getLocation());
                    break;
                case 4:
                    this.travelToShop();
                    break;
                case 5:
                    this.enterShop();
                    break;
                case 6:
                    this.unloadArticles();
                    this.refillTank();
                    break;
                case 7:
                    this.leaveShop();
                    this.addToPath(world.getRandomWholesale().getLocation());
                    break;
            }
            this.infect();
            try {
                wait(world.getSimulationSpeed());
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.setStatus(this.getStatus() % 8);
        }
    }
    
    private void loadArticles() {
        World world = this.getWorld();
        if(this.isLive()){
            WholesaleShop wholesale = world.wholesaleAt(this.getCurrentLocation().relativeLocation(-10, -20));
            this.load(wholesale);
            try {
                wait(world.getSimulationSpeed() * 10);
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.setStatus(this.getStatus() + 1);
        }
    }
    
    private void unloadArticles() {
        World world = this.getWorld();
        if(this.isLive()){
            RetailShop retail = world.retailAt(this.getCurrentLocation().relativeLocation(-10, -20));
            this.unload(retail);
            try {
                wait(world.getSimulationSpeed() * 10);
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.setStatus(this.getStatus() + 1);
        }
    }
    
    private void enterShop() {
        World world = this.getWorld();
        if(this.isLive()){
            try {
                this.moveRight();
                wait(world.getSimulationSpeed());
                this.moveRight();
                wait(world.getSimulationSpeed());
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.moveUp();
            this.moveUp();
            this.setStatus(this.getStatus() + 1);
        }
    }
    
    private void leaveShop() {
        World world = this.getWorld();
        this.moveDown();
        this.moveDown();
        if(this.isSick()){
            this.setVisitedShops(this.getVisitedShops() + 1);
        }
        this.setStatus(this.getStatus() + 1);
    }
        
    private void load(WholesaleShop shop) {
        int demand = Math.min(this.getTrunk().getSize() - this.getTrunk().getPointer(), shop.getStorage().availableProducts());
        Product products[];
        products = shop.supply(demand);
        for (Product product : products) {
            if (product != null) {
                this.trunk.addProduct(product);
            }
        }
    }
    
    private synchronized void unload(RetailShop shop) {
        World world = this.getWorld();
        while(!this.trunk.isEmpty() && this.isLive()){
            if(!shop.getStorage().isFull())
                shop.getStorage().addProduct(this.trunk.getProduct());
            else{
                try {
                    wait(world.getSimulationSpeed());
                } catch (InterruptedException ex) {
                    Logger.getLogger(Supplier.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void refillTank() {
        this.tank = (float)1;
    }
    
    private void addInitialPath(World world) {
        Location tempLocation = world.getRandomWholesale().getLocation().relativeLocation(-10, 40);
        this.path[0] = new Location(tempLocation.getX(), tempLocation.getY());
        tempLocation = world.getRandomRetail().getLocation().relativeLocation(-10, 40);
        this.path[1] = new Location(tempLocation.getX(), tempLocation.getY());
        tempLocation = world.getRandomWholesale().getLocation().relativeLocation(-10, 40);
        this.path[2] = new Location(tempLocation.getX(), tempLocation.getY());
        this.setDestination(this.path[0]);
    }
    
    private void addToPath(Location location) {
        this.path[0] = this.path[1];
        this.path[1] = this.path[2];
        this.path[2] = new Location(location.relativeLocation(-10, 40).getX(), location.relativeLocation(-10, 40).getY());
        this.setDestination(this.path[0]);
    }
    
    private void createButton(JPanel mapPanel){
        ImageIcon img = new ImageIcon(getClass().getResource("/images/supplier.png"));
        JButton tempButton = new JButton("",img);
        tempButton.setBounds(this.getCurrentLocation().getX() - 6, this.getCurrentLocation().getY() - 6,12,12);
        this.setButton(tempButton);
        Component comp = mapPanel.add(tempButton);
        mapPanel.setComponentZOrder(comp, 0);
        this.setComponent(comp);
        tempButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                createMenu();
            }
        });
    }
    
    private void createMenu() {
        JFrame jframe = new JFrame(this.companyName);
        jframe.setLayout(new FlowLayout());
        // left jpanel
        JPanel jpanel1 = new JPanel();
        jpanel1.setLayout(new GridLayout(8, 2, 40, 10));
        jpanel1.setBorder(new EmptyBorder(20, 20, 20, 20));
        jpanel1.add(new JLabel("Status:"));
        jpanel1.add(new JLabel(String.valueOf(this.getStatus())));
        jpanel1.add(new JLabel("ID:"));
        jpanel1.add(new JLabel(String.valueOf(this.supplierId)));
        jpanel1.add(new JLabel("Company:"));
        jpanel1.add(new JLabel(this.companyName));
        jpanel1.add(new JLabel("Car:"));
        jpanel1.add(new JLabel(this.carBrand));
        jpanel1.add(new JLabel("Products in trunk:"));
        jpanel1.add(new JLabel(String.valueOf(this.getTrunk().availableProducts())));
        jpanel1.add(new JLabel("Status:"));
        if(this.isSick())
            jpanel1.add(new JLabel("Sick"));
        else
            jpanel1.add(new JLabel("health"));  
        jpanel1.add(new JLabel("Mask:"));
        if(this.isMaskOn())
            jpanel1.add(new JLabel("on face"));
        else
            jpanel1.add(new JLabel("in pocket"));
        JButton jbuttonSick = new JButton("Make sick");
        jbuttonSick.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                makeSick();
                jframe.dispose();
            }
        });
        jpanel1.add(jbuttonSick); 
        JButton jbutton = new JButton("Delete");
        jbutton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                delete();
                jframe.dispose();
            }      
        });
        jpanel1.add(jbutton);
        jframe.add(jpanel1);
        // right jpanel
        JPanel jpanel2 = new JPanel();
        jpanel2.setLayout(new GridLayout(4, 2, 10, 10));
        jpanel2.setBorder(new EmptyBorder(20, 20, 20, 20));
        int variant;
        if(this.getWorld().retailAt(this.path[0].relativeLocation(10, -40)) != null)
            variant = 0;
        else
            variant = 1;
        jpanel2.add(new JLabel("Stop 1:"));
        if(variant == 0)
            jpanel2.add(new JLabel(this.getWorld().retailAt(this.path[0].relativeLocation(10, -40)).getName()));
        else
            jpanel2.add(new JLabel(this.getWorld().wholesaleAt(this.path[0].relativeLocation(10, -40)).getName()));
        jpanel2.add(new JLabel("Stop 2:"));
        if(variant == 0)
            jpanel2.add(new JLabel(this.getWorld().wholesaleAt(this.path[1].relativeLocation(10, -40)).getName()));       
        else
            jpanel2.add(new JLabel(this.getWorld().retailAt(this.path[1].relativeLocation(10, -40)).getName()));
        jpanel2.add(new JLabel("Stop 3:"));
        if(variant == 0)
            jpanel2.add(new JLabel(this.getWorld().retailAt(this.path[2].relativeLocation(10, -40)).getName()));
        else
            jpanel2.add(new JLabel(this.getWorld().wholesaleAt(this.path[2].relativeLocation(10, -40)).getName()));
        jpanel2.add(new JLabel());
        
        JButton jbuttonChangeRoute = new JButton("Change route");
        jbuttonChangeRoute.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                changeRoute();
                jframe.dispose();
                createMenu();
            }
        });
        jpanel2.add(jbuttonChangeRoute); 
        jframe.add(jpanel2);
        jframe.pack();
        jframe.setVisible(true);
    }
    
    private void makeSick() {
        this.setSick(true);
    }
    
    private void delete(){
        this.getWorld().getMapPanel().remove(this.getButton());
        if(this.getStatus() == 2 || this.getStatus() == 6)
            this.leaveShop();
        this.setLive(false);
        this.getWorld().deleteSupplier(this.getSupplierId());
    } 
    
    private void changeRoute(){
        World world = this.getWorld();
        if(this.getWorld().retailAt(this.path[0].relativeLocation(10, -40)) != null){
            Location tempLocation = world.getRandomRetail().getLocation().relativeLocation(-10, 40);
            this.path[0] = new Location(tempLocation.getX(), tempLocation.getY());
            tempLocation = world.getRandomWholesale().getLocation().relativeLocation(-10, 40);
            this.path[1] = new Location(tempLocation.getX(), tempLocation.getY());
            tempLocation = world.getRandomRetail().getLocation().relativeLocation(-10, 40);
            this.path[2] = new Location(tempLocation.getX(), tempLocation.getY());
            this.setDestination(this.path[0]);
        }
        else{
            Location tempLocation = world.getRandomWholesale().getLocation().relativeLocation(-10, 40);
            this.path[0] = new Location(tempLocation.getX(), tempLocation.getY());
            tempLocation = world.getRandomRetail().getLocation().relativeLocation(-10, 40);
            this.path[1] = new Location(tempLocation.getX(), tempLocation.getY());
            tempLocation = world.getRandomWholesale().getLocation().relativeLocation(-10, 40);
            this.path[2] = new Location(tempLocation.getX(), tempLocation.getY());
            this.setDestination(this.path[0]);
        }
        if(this.getWorld().retailAt(this.getDestination().relativeLocation(10, -40)) != null)
            this.setStatus(4);
        else
            this.setStatus(0);
        
        while((this.getCurrentLocation().getY() - 50) % 200 != 0){
                if(this.getCurrentLocation().getY() < this.getDestination().getY())
                    this.moveDown();
                else
                    this.moveUp();       
            }
    }
}   
