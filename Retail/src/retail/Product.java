package retail;

import java.time.LocalTime;

/**
 * @author Jacek Karolczak
 */
public class Product {
    
    // fields
    private int id;
    private String name;
    private String brand;
    private LocalTime bestBefore;

    // constructor
    public Product(int id, String name, String brand, int bestBefore) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.bestBefore = LocalTime.now().plusMinutes(20);
        //this.bestBefore = LocalTime.now().plusMinutes(bestBefore);
    }
    
    // setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setBestBefore(LocalTime bestBefore) {
        this.bestBefore = bestBefore;
    }
    
    // getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public LocalTime getBestBefore() {
        return bestBefore;
    }
    
}
