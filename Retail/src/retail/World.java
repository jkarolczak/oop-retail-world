package retail;

import java.awt.Color;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 * @author Jacek Karolczak
 */
public class World extends Thread{
    
    // fields
    private int simulationSpeed;
    private int visitedStoreBeforeRecovery;
    private int nextSupplierId;
    private int nextProductId;
    private final int initialX;
    private final int initialY;
    private float transmissionRate;
    private float maskTransmissionRate;
    private float vaccinedTransmissionRate;
    private float vaccinedPeople;
    private float lockdownThreshold;
    private float peopleWithMask;
    private List<Client> clients;
    private List<Supplier> suppliers;
    private RetailShop retails[];
    private WholesaleShop wholesales[];
    private Semaphore[][] intersections;
    private Semaphore nextProductIdSemaphore;
    private JPanel mapPanel;
    private JLabel sickValue;
    private JLabel vaccinatedValue;
    private JLabel maskOnValue;
    private JLabel clientsValue;
    private JLabel suppliersValue;
    private JLabel lockdownValue;
    
    // constructor
    public World(JPanel mapPanel, JLabel sickValue, JLabel vaccinatedValue, JLabel maskOnValue, JLabel clientsValue, JLabel suppliersValue, JLabel lockdownValue) {
        this.simulationSpeed = 100;
        this.visitedStoreBeforeRecovery = 10;
        this.nextSupplierId = 1;
        this.nextProductId = 1;
        this.nextProductIdSemaphore = new Semaphore(1);
        this.initialX = 50;
        this.initialY = 50;
        this.transmissionRate = (float)0.45;
        this.maskTransmissionRate = (float)0.2;
        this.vaccinedTransmissionRate = (float)0.2;
        this.vaccinedPeople = 0;
        this.lockdownThreshold = (float)0.5;
        this.peopleWithMask = 0;
        this.mapPanel = mapPanel;
        this.sickValue = sickValue;
        this.vaccinatedValue = vaccinatedValue;
        this.maskOnValue = maskOnValue;
        this.clientsValue = clientsValue;
        this.suppliersValue = suppliersValue;
        this.lockdownValue = lockdownValue;
        this.retails = new RetailShop[10];
        this.wholesales = new WholesaleShop[3];
        this.createWholesales();
        this.createRetails();
        this.intersections = new Semaphore[5][5];

        this.clients = Collections.synchronizedList(new ArrayList<>(15));
        this.suppliers = Collections.synchronizedList(new ArrayList<>(5));
        
        for (int i = 0; i < 15; i++)
            this.clients.add(this.createClient());
        
        for (int i = 0; i < 5; i++) 
            this.suppliers.add(this.createSupplier());
                         
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                intersections[i][j] = new Semaphore(1);
            }
        }
    }    
    
    // setters
    public void setSimulationSpeed(int simulationSpeed) {
        this.simulationSpeed = simulationSpeed;
    }
    public void setRetails(RetailShop[] retails) {
        this.retails = retails;
    }
    public void setWholesales(WholesaleShop[] wholesales) {    
        this.wholesales = wholesales;
    }
    public void setVisitedStoreBeforeRecovery(int visitedStoreBeforeRecovery) {
        this.visitedStoreBeforeRecovery = visitedStoreBeforeRecovery;
    }
    public void setNextProductId(int nextProductId) {
        this.nextProductId = nextProductId;
    } 
    public void setTransmissionRate(float transmissionRate) {
        this.transmissionRate = transmissionRate;
    }
    public void setMaskTransmissionRate(float maskTransmissionRate) {
        this.maskTransmissionRate = maskTransmissionRate;
    }  
    public void setVaccinedTransmissionRate(float vaccinedTransmissionRate) {
        this.vaccinedTransmissionRate = vaccinedTransmissionRate;
    }
    public void setVaccinedPeople(float vaccinedPeople) {
        this.vaccinedPeople = vaccinedPeople;
    } 
    public void setLockdownThreshold(float lockdownThreshold) {
        this.lockdownThreshold = lockdownThreshold;
    }
    public void setPeopleWithMask(float peopleWithMask) {
        this.peopleWithMask = peopleWithMask;
    }
    
    // getters
    public int getSimulationSpeed() {
        return simulationSpeed;
    }
    public int getInitialX() {
        return initialX;
    }
    public int getInitialY() {
        return initialY;
    }
    public int getNextProductId() {
        return nextProductId;
    }
    public int getVisitedStoreBeforeRecovery() {
        return visitedStoreBeforeRecovery;
    }
    public float getTransmissionRate() {
        return transmissionRate;
    }
    public float getMaskTransmissionRate() {
        return maskTransmissionRate;
    }
    public float getVaccinedTransmissionRate() {
        return vaccinedTransmissionRate;
    }
    public float getVaccinedPeople() {
        return vaccinedPeople;
    }
    public float getLockdownThreshold() {
        return lockdownThreshold;
    }   
    public float getPeopleWithMask() {
        return peopleWithMask;
    }
    public List<Client> getClients() {
        return clients;
    }
    public List<Supplier> getSuppliers() {
        return suppliers;
    }
    public RetailShop[] getRetails() {
        return retails;
    }
    public WholesaleShop[] getWholesales() {
        return wholesales;
    }
    public Semaphore getNextProductIdSemaphore() {
        return nextProductIdSemaphore;
    }
    public Semaphore[][] getIntersections() {
        return intersections;
    }
    public JPanel getMapPanel() {
        return mapPanel;
    }
    
    // methods
    /**
     * Choose random wholesale shop
     * @return randomly chosen wholesale shop
     */
    public WholesaleShop getRandomWholesale() {
        return this.wholesales[(int)(Math.random() * (this.wholesales.length - 1))];
    }
    
    /**
     * Choose random retail shop
     * @return randomly chosen retail shop
     */
    public RetailShop getRandomRetail() {
        return this.retails[(int)(Math.random() * (this.retails.length - 1))];
    }
    
    /**
     * Adds to the world new client
     * @param id identifier describing new client
     * @param firstName new client's first name
     * @param lastName new client's last name
     * @param cartCapacity capacity of a cart
     * @param x initial position x coordinate
     * @param y initial position y coordinate
     */
    public void addClient(String id, String firstName, String lastName, int cartCapacity, int x, int y) {
        Client newClient = new Client(id, firstName, lastName, cartCapacity, x, y, mapPanel);
        this.clients.add(newClient);
        newClient.setWorld(this);
        newClient.start();
    }
    /**
     * Creates new, randomly generated client
     * @return new, randomly generated client
     */
    private Client createClient() {
        Random random = new Random();
        String[][] names = {
            {"Jim", "Halpert"},
            {"Michael", "Scott"},
            {"Pam", "Beesly"},
            {"Erin", "Hannon"},
            {"Dwight", "Schrute"},
            {"Karen", "Filipelli"},
            {"Andy", "Bernard"},
            {"Jan", "Levinson"},
            {"Angela", "Martin"},
            {"Meredith", "Palmer"},
            {"Creed", "Bratton"},
            {"Oscar", "Martinez"},
            {"Phyllis", "Vance"},
            {"Stanley", "Hudson"},
            {"Ryan", "Howard"}
        };
        
        Integer temp = 1000 + random.nextInt(8999);
        String id = "ABC" + temp.toString();
        temp = random.nextInt(names.length);
        String firstName = names[temp][0];
        String lastName = names[temp][1];
        temp = 5 + random.nextInt(5);
        int cartCapacity = temp;
        int x = this.initialX;
        int y = this.initialY;
        
        Client newClient = new Client(id, firstName, lastName, cartCapacity, x, y, mapPanel);
        return newClient;
    }
    
    /**
     * Adds to the world new supplier
     * @param company name of company describing the supplier
     * @param car car brand describing the supplier
     * @param trunkCapacity
     * @param x initial position's x of the supplier
     * @param y initial position's y of the supplier
     */
    public void addSupplier(String company, String car, int trunkCapacity, int x, int y) {
        int id = this.nextSupplierId++;
        Supplier newSupplier = new Supplier(id, company, car, trunkCapacity, x, y, mapPanel);
        this.suppliers.add(newSupplier);
        newSupplier.setWorld(this);
        newSupplier.start();
    }
    
    /**
     * Creates new, randomly generated supplier
     * @return new, randomly generated supplier
     */   
    private Supplier createSupplier() {
        Random random = new Random();
        String[] companies = {
            "UPS",
            "FedEx",
            "DHL",
            "GLS",
            "InPost"
        };
        String[] cars = {
            "Fiat Ducato",
            "IVECO Daily",
            "Mercedes Sprinter",
            "Volswagen Crafter",
            "Renaul Master"
        };
        
        int id = this.nextSupplierId++;
        int temp = random.nextInt(companies.length);
        String company = companies[temp];
        temp = random.nextInt(cars.length);
        String car = cars[temp];
        temp = 30 + random.nextInt(40);
        int trunkCapacity = temp;
        int x = this.initialX;
        int y = this.initialY;
        float infectionProbability = this.transmissionRate;
        
        Supplier newSupplier = new Supplier(id, company, car, trunkCapacity, x, y, mapPanel);
        return newSupplier;        
    }
    
    private void createWholesales() {
        this.wholesales[0] = new WholesaleShop(2, "Dunder Mifflin", "1725 Slough Avenue, Scranton PA", 500, 60, 10, this.mapPanel);
        this.wholesales[1] = new WholesaleShop(1, "Schrute Farms", "Main Street, Honesdale PA", 500, 660, 210, this.mapPanel);
        this.wholesales[2] = new WholesaleShop(1, "Sabre", "Tallahassee, FL", 500, 460, 610, this.mapPanel);
        for(WholesaleShop wholesale: wholesales){
            wholesale.setWorld(this);
        }
    }

    private void createRetails() {
        this.retails[0] = new RetailShop(2, "W. B. Jones", "Scranton PA", 80, 460, 10, this.mapPanel);
        this.retails[1] = new RetailShop(4, "Bob Vance Vance Refrigeration", "1Scranton PA", 80, 860, 10, this.mapPanel);
        this.retails[2] = new RetailShop(1, "The Michael Scott Paper Company", "42 Kellum Court, Scranton PA", 80, 260, 210, this.mapPanel);
        this.retails[3] = new RetailShop(2, "Serenity by Jan", "Scranton PA", 80, 60, 410, this.mapPanel);
        this.retails[4] = new RetailShop(1, "Big Red Paper Company", "Scranton PA", 80, 660, 410, this.mapPanel);
        this.retails[5] = new RetailShop(8, "Athlead", "Austin TX", 80, 260, 610, this.mapPanel);
        this.retails[6] = new RetailShop(2, "Prince Paper", "Scranton PA", 80, 860, 610, this.mapPanel);
        this.retails[7] = new RetailShop(2, "Osprey Paper", "Throop PA", 80, 60, 810, this.mapPanel);
        this.retails[8] = new RetailShop(2, "Cress Tool & Die", "Scranton PA", 80, 460, 810, this.mapPanel);
        this.retails[9] = new RetailShop(2, "Agrotourism", "Scranton PA", 80, 860, 810, this.mapPanel);
        for(RetailShop retail: retails){
            retail.setWorld(this);
        }
    }
     
    /**
     * Forces humans to inoculate of according to this field <code>vaccinedPeople</code>
     */
    public void inoculateHumans() {
        synchronized(clients) {
            Collections.shuffle(clients);
            int inoculateClients = (int)(this.getVaccinedPeople() * clients.size());
            for (int i = 0; i < clients.size(); i++) {
                if (i < inoculateClients)
                    clients.get(i).setVaccinated(true);
                else
                    clients.get(i).setVaccinated(false);
            }
        }
        synchronized(suppliers) {
            Collections.shuffle(suppliers);
            int inoculateSuppliers = (int)(this.getVaccinedPeople() * suppliers.size());
            for (int i = 0; i < suppliers.size(); i++) {
                if (i < inoculateSuppliers)
                    suppliers.get(i).setVaccinated(true);
                else
                    suppliers.get(i).setVaccinated(false);
            }
        }
    }
    
    /**
     * Forces humans to put masks on/take masks of according to this field <code>peopleWithMask</code>
     */
    public void putMasksHumans() {
        synchronized(clients) {
            Collections.shuffle(clients);
            int maskClients = (int)(this.getPeopleWithMask() * clients.size());
            for (int i = 0; i < clients.size(); i++) {
                if (i < maskClients)
                    clients.get(i).putMaskOn();
                else
                    clients.get(i).takeMaskOff();
            }
        }
        synchronized(suppliers) {
            Collections.shuffle(suppliers);
            int maskSuppliers = (int)(this.getPeopleWithMask() * suppliers.size());
            for (int i = 0; i < suppliers.size(); i++) {
                if (i < maskSuppliers)
                    suppliers.get(i).putMaskOn();
                else
                    suppliers.get(i).takeMaskOff();
            }
        }
    }  
    
    /**
     * Computes percentage of sick humans
     * @return percentage of sick humans 
     */
    public float sickHumans() {
        int sick = 0;
        for (Supplier supplier: this.suppliers) {
            if(supplier.isSick())
                sick++;
        }
        for (Client client: this.clients) {
            if(client.isSick())
                sick++;
        }
        int total = (this.clients.size() - 1) + (this.suppliers.size() - 1);
        return (float)sick / (float)total;
    } 
    
    /**
     * Checks if there is lockdown
     * @return boolean value of lockdown status
     */
    public boolean lockdown() {
        return this.lockdownThreshold < this.sickHumans();
    }
    
    /**
     * Infect all humans next to given location
     * @param location location where infection should be spread
     */
    public void infectHumansNextTo(Location location) {
        for (Supplier supplier: this.suppliers) {
            if(supplier.getCurrentLocation().compare(location))
                supplier.sick();
        }
        for (Client client: this.clients) {
            if(client.getCurrentLocation().compare(location))
                client.sick();
        }
    }
    
    /**
     * Deletes client with given id
     * @param id identifier of client which should be deleted
     */
    public void deleteClient(String id) {
        for (int i = 0; i < clients.size(); i++) {
            if(clients.get(i).getClientId().equals(id)){
                clients.remove(i);
                break;
            }
        }
    }
    /**
     * Deletes supplier with given id
     * @param id identifier of supplier which should be deleted
     */
    public void deleteSupplier(int id) {
        for (int i = 0; i < suppliers.size(); i++) {
            if(suppliers.get(i).getSupplierId() == id){
                suppliers.remove(i);
                break;
            }
        }
    }  
    
    /**
     * This method runs whole simulation and updates data on map
     */
    @Override
    public synchronized void run() {
        for (Client client: this.clients) {
            client.setWorld(this);
            client.start();
        }

        for (Supplier supplier: this.suppliers) {
            supplier.setWorld(this);
            supplier.start();
        }   
        while(true) {             
            for (WholesaleShop wholesale: this.wholesales){
                wholesale.run();
            }
            for (RetailShop retail: this.retails){
                retail.run();
            }
            this.draw();
            this.updateDashboard();
            try {
                wait(this.simulationSpeed);
            } catch (InterruptedException ex) {
                Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }
    
    private void updateDashboard(){
        int sick = 0;
        int maskOn = 0;
        int vaccinated = 0;
        for(Client c: this.clients){
            if(c.isSick())
                sick++;
            if(c.isMaskOn())
                maskOn++;
            if(c.isVaccinated())
                vaccinated++;
        }
        for(Supplier s: this.suppliers){
            if(s.isSick())
                sick++;
            if(s.isMaskOn())
                maskOn++;
            if(s.isVaccinated())
                vaccinated++;
        }
        this.sickValue.setText(String.valueOf(sick));
        this.maskOnValue.setText(String.valueOf(maskOn));
        this.vaccinatedValue.setText(String.valueOf(vaccinated));
        this.clientsValue.setText(String.valueOf(this.clients.size()));
        this.suppliersValue.setText(String.valueOf(this.suppliers.size()));
        this.lockdownValue.setText(String.valueOf(this.lockdown()));
    }

    private void draw() {
        this.drawClients();
        this.drawSuppliers();
        SwingUtilities.updateComponentTreeUI(this.mapPanel);
    }
    
    private void drawClients() {
        for (Client client: this.clients) {
            client.getButton().setLocation(client.getCurrentLocation().getX() - 15, client.getCurrentLocation().getY() - 15);
            if(client.getStatus() == 2)
                client.getButton().setLocation(client.getCurrentLocation().getX() - 5, client.getCurrentLocation().getY() - 5);
            if(client.isSick() == true)
                client.getButton().setBackground(Color.red);
            else
                client.getButton().setBackground(Color.white);
        }
    }
    
    private void drawSuppliers() {
        for (Supplier supplier: this.suppliers) {
            supplier.getButton().setLocation(supplier.getCurrentLocation().getX() - 5, supplier.getCurrentLocation().getY() - 5); 
            if(supplier.isSick() == true)
                supplier.getButton().setBackground(Color.red);
            else
                supplier.getButton().setBackground(Color.white);
        }
    }
    
    /** 
     * Finds retail shop at given location 
     * @param location  location where retail is expected
     * @return retail shop at given location, if many first one, if none null
     */
    public RetailShop retailAt(Location location) {
        for (RetailShop retail : this.retails) {
            if(retail.getLocation().compare(location))
                return retail;
        }
        return null;
    }
    
    /** 
     * Finds wholesale shop at given location 
     * @param location  location where wholesale is expected
     * @return wholesale shop at given location, if many first one, if none null
     */
    public WholesaleShop wholesaleAt(Location location) {
        for (WholesaleShop wholesale: this.wholesales){
            if(wholesale.getLocation().compare(location))
                return wholesale;
        }
        return null;
    }
}