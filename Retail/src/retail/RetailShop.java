package retail;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * @author Jacek Karolczak
 */
public class RetailShop extends Shop {
   
    // fields
    private int clientCapacity;
    private int clientInside;
    private Semaphore clientCapacitySemaphore;
    
    // constructor
    public RetailShop(int clientCapacity, String name, String address, int capacity, int x, int y, JPanel mapPanel) {
        super(name, address, capacity, x, y);
        this.clientCapacity = clientCapacity;
        this.clientInside = 0;
        this.clientCapacitySemaphore = new Semaphore(1);
        this.createButton(mapPanel);
    }
    
    // setters
    public void setClientCapacity(int clientCapacity) {
        this.clientCapacity = clientCapacity;
    }
    public void setClientInside(int clientInside) {
        this.clientInside = clientInside;
    } 
    
    // getters
    public int getClientCapacity() {
        return clientCapacity;
    }

    public int getClientInside() {
        return clientInside;
    }
    public Semaphore getClientCapacitySemaphore() {
        return clientCapacitySemaphore;
    }
       
    // methods
    /**
     * Handle supply process on shop side
     * @param products products delivered by a supplier
     */
    public void receiveSupply(Product[] products){
        this.getStorage().addProducts(products);
    }
    
    /**
     * Handle shopping process on shop side
     * @param amount amount of products which should be sold
     * @return sold products
     */
    public Product[] sale(int amount) {
        amount = Math.min(amount, this.getStorage().availableProducts());
        return this.getStorage().getSomeProducts(amount);
    }
    
    /**
     * Provide running routine of a shop
     */
    public void run() {
        this.checkDates();
    }
    
    private void createButton(JPanel mapPanel){
        ImageIcon img = new ImageIcon(getClass().getResource("/images/retail.png"));
        JButton tempButton = new JButton(img);
        tempButton.setBounds(this.getLocation().getX(), this.getLocation().getY(),30,30);
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
    
    private void createMenu(){
        JFrame jframe = new JFrame(this.getName());
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new GridLayout(5, 2, 10, 10));
        jpanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        jpanel.add(new JLabel("Name:"));
        jpanel.add(new JLabel(this.getName()));
        jpanel.add(new JLabel("Address:"));
        jpanel.add(new JLabel(this.getAddress()));
        jpanel.add(new JLabel("Storage capacity:"));
        jpanel.add(new JLabel(String.valueOf(this.getStorage().getSize())));
        jpanel.add(new JLabel("Products in storage:"));
        jpanel.add(new JLabel(String.valueOf(this.getStorage().availableProducts())));
        jpanel.add(new JLabel("Clients capacity:"));
        jpanel.add(new JLabel(String.valueOf(this.clientCapacity)));        
        jframe.add(jpanel);
        jframe.pack();
        jframe.setVisible(true);
    }
}
