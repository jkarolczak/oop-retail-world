package retail;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * @author Jacek Karolczak
 */
public class WholesaleShop extends Shop {
    
    // fields
    private int processingCapacity;

    // constructor
    public WholesaleShop(int processingCapacity, String name, String address, int capacity, int x, int y, JPanel mapPanel) {
        super(name, address, capacity, x, y);
        this.processingCapacity = processingCapacity;
        this.createButton(mapPanel);
    }
    
    // setters
    public void setProcessingCapacity(int processingCapacity) {
        this.processingCapacity = processingCapacity;
    }
    
    // getters
    public int getProcessingCapacity() {
        return processingCapacity;
    }
    
    // methods
    private Product getNewProduct(World world) {
        try{
            world.getNextProductIdSemaphore().acquire();
        }catch(InterruptedException e){
            return null;
        }
        int id = world.getNextProductId();
        world.setNextProductId(world.getNextProductId() + 1);
        world.getNextProductIdSemaphore().release();
        String availableNames[] = {"Bond paper", "Gloss coated paper", "Matt coated paper", "Recycled paper", "Silk coated paper", "Uncoated paper", "Watermarked paper"};
        String name = availableNames[(int)(Math.random() * availableNames.length)];
        String brand = this.getName();
        int bestBefore = 1 + (int)(Math.random() * 5);
        Product newProduct = new Product(id, name, brand, bestBefore);
        return newProduct;
    }
    
    private void createProducts(World world) {
        for(int i = 0; i < this.getProcessingCapacity(); i++){
            if(!this.getStorage().isFull())
                this.getStorage().addProduct(this.getNewProduct(world));
            else
                break;
        }
    }
    
    /**
     * Handle supply process on wholesale side
     * @param amount amount of products which should be delivered to the supplier
     * @return products products delivered to the supplier
     */
    public Product[] supply(int amount) {
        Product products[] = new Product[amount];
        for (int i = 0; i < amount; i++) {
            products[i] = this.getStorage().getProduct();
        }
        return products;
    }
    private void createButton(JPanel mapPanel){
        ImageIcon img = new ImageIcon(getClass().getResource("/images/wholesale.png"));
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
        jpanel.setLayout(new GridLayout(7, 2, 10, 10));
        jpanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        jpanel.add(new JLabel("Name:"));
        jpanel.add(new JLabel(this.getName()));
        jpanel.add(new JLabel("Address:"));
        jpanel.add(new JLabel(this.getAddress()));
        jpanel.add(new JLabel("Products in storage:"));
        jpanel.add(new JLabel(String.valueOf(this.getStorage().availableProducts())));
        jpanel.add(new JLabel("Processing capacity:"));
        jpanel.add(new JLabel(String.valueOf(this.getProcessingCapacity())));
        jpanel.add(new JLabel("Product name:"));
        JTextField productName = new JTextField();
        jpanel.add(productName);
        jpanel.add(new JLabel("Product brand:"));
        JTextField productBrand = new JTextField();
        jpanel.add(productBrand);
        jpanel.add(new JLabel());
        JButton jbutton = new JButton("Create product");
        jbutton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                addProduct(productName.getText(), productBrand.getText());
                jframe.dispose();
            }
        });
        jpanel.add(jbutton);
        jframe.add(jpanel);
        jframe.pack();
        jframe.setVisible(true);
    }
    
    private void addProduct(String name, String brand){
        this.getStorage().addProduct(new Product(this.getWorld().getNextProductId(), name, brand, 3));
    }
    
    /**
     * Provide running routine of a shop
     */
    public synchronized void run() {
        this.createProducts(this.getWorld());
        this.checkDates();
    }
    
}