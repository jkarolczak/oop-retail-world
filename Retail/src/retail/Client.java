package retail;

import java.awt.Component;
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
public class Client extends Human {
    
    // fields
    private String clientId;
    private String firstName;
    private String lastName;
    private Container cart;
    
    // constructor
    public Client(String id, String firstName, String lastName, int cartCapacity, int x, int y, JPanel mapPanel) {
        super(x, y);
        this.clientId = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.cart = new Container(cartCapacity);
        this.createButton(mapPanel);
    }
    
    // setters
    public void setClientId(String id) {
        this.clientId = id;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setCart(Container cart) {
        this.cart = cart;
    }
    
    // getters
    public String getClientId() {
        return clientId;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    
    public Container getCart() {
        return cart;
    }
    
    // methods
    private void buy(RetailShop retail) {
        Product[] newProducts;
        int productsToBuy = 3 + (int) (Math.random() * (this.getCart().getSize() - 3));
        newProducts = retail.sale(productsToBuy);
        this.consume(newProducts.length - this.getCart().availableSlots() + 1);
        this.cart.addProducts(newProducts);
    }
    
    private void consume(int toConsume) {
        for(int i = 0; i < toConsume; i++) {
            this.cart.removeProduct();
        }
    }
    
    /**
     * Controls client behavior
     */
    @Override
    public synchronized void run() {
        World world = this.getWorld();
        this.setDestination(world.getRandomRetail().getLocation().relativeLocation(-10, 40));
        while(this.isLive()){
            switch(this.getStatus()){
                case 0:
                    this.travelToShop();
                    break;
                case 1:
                    this.enterRetail();
                    break;
                case 2:
                    this.doShopping();
                    break;
                case 3:                
                    this.leaveRetail();
                    break;
            }
            this.infect();
            this.setStatus(this.getStatus() % 4);
            try {
                wait(world.getSimulationSpeed());
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
       
    private void enterRetail() {
        World world = this.getWorld();
        RetailShop retail = world.retailAt(this.getCurrentLocation().relativeLocation(10, -40));
        synchronized(retail){
            if((world.lockdown() && (int)Math.ceil(0.25 * retail.getClientCapacity()) > retail.getClientInside()) || (!world.lockdown() && retail.getClientCapacity() > retail.getClientInside())){
                retail.setClientInside(retail.getClientInside() + 1);
                
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
    }
    
    private void doShopping() {
        World world = this.getWorld();
        Location retailLocation = this.getCurrentLocation().relativeLocation(-10, -20);
        this.buy(world.retailAt(retailLocation));
        try {
            wait(world.getSimulationSpeed() * 10);
        } catch (InterruptedException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.setStatus(this.getStatus() + 1);
    }
    
    private void leaveRetail() {
        World world = this.getWorld();
        RetailShop retail = world.retailAt(this.getCurrentLocation().relativeLocation(-10, -20));
        synchronized(retail){
            retail.setClientInside(retail.getClientInside() - 1);
        }
        this.moveDown();
        this.moveDown();
        if(this.isSick()){
            this.setVisitedShops(this.getVisitedShops() + 1);
        }
        this.setDestination(world.getRandomRetail().getLocation().relativeLocation(-10, 40));
        this.setStatus(this.getStatus() + 1);
    }
    
    private void createButton(JPanel mapPanel){
        ImageIcon img = new ImageIcon(getClass().getResource("/images/client.png"));
        JButton tempButton = new JButton(img);
        tempButton.setBounds(this.getCurrentLocation().getX() - 16, this.getCurrentLocation().getY() - 16,10,10);
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
        JFrame jframe = new JFrame(this.firstName + " " + this.lastName);
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new GridLayout(6, 2, 40, 10));
        jpanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        jpanel.add(new JLabel("ID:"));
        jpanel.add(new JLabel(this.clientId));
        jpanel.add(new JLabel("First name:"));
        jpanel.add(new JLabel(this.firstName));
        jpanel.add(new JLabel("Last name:"));
        jpanel.add(new JLabel(this.lastName));
        jpanel.add(new JLabel("Status:"));
        if(this.isSick())
            jpanel.add(new JLabel("Sick"));
        else
            jpanel.add(new JLabel("health"));  
        jpanel.add(new JLabel("Mask:"));
        if(this.isMaskOn())
            jpanel.add(new JLabel("on face"));
        else
            jpanel.add(new JLabel("in pocket"));
        
        JButton jbuttonSick = new JButton("Make sick");
        jbuttonSick.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                makeSick();
                jframe.dispose();
            }
        });
        jpanel.add(jbuttonSick);
        
        JButton jbuttonDelete = new JButton("Delete");
        jbuttonDelete.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                delete();
                jframe.dispose();
            }
        });
        jpanel.add(jbuttonDelete);
        jframe.add(jpanel);
        jframe.pack();
        jframe.setVisible(true);
    }
    private void makeSick() {
        this.setSick(true);
    }
    private void delete() {
        this.getWorld().getMapPanel().remove(this.getButton());
        if(this.getStatus() == 2)
            this.leaveRetail();
        this.setLive(false);
        this.getWorld().deleteClient(this.getClientId());
    }
}
