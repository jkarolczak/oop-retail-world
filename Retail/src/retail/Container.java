package retail;

import java.time.LocalTime;

/**
 * @author Jacek Karolczak
 */
public class Container {
    
    //fields
    private int size;
    private int pointer;
    private Product products[];

    // constructor
    public Container(int size) {
        this.size = size + 1;
        this.pointer = 0;
        this.products = new Product[size];
    }

    // setters
    
    // getters
    public int getSize() {
        return size - 1;
    }
    public Product[] getProducts() {
        return products;
    }
    public int getPointer() {
        return pointer;
    }
    

    // methods
    /**
     * Checks if container is full
     * @return boolean value describing if this is full
     */
    public boolean isFull() {
        return this.pointer == this.size - 1;
    }
    
    /**
     * Checks if container is empty
     * @return boolean value describing if this is empty
     */
    public boolean isEmpty() {
        return this.pointer == 0;
    }
    
    /**
     * Checks how many products are available in this
     * @return number of available products
     */
    public int availableProducts() {
        return this.pointer;
    }
    
    /**
     * Checks how many free slots are available in this
     * @return number of free slots
     */
    public int availableSlots() {
        return this.size - this.availableProducts();
    }

    /**
     * Remove last product from this
     */
    public void removeProduct() {
        synchronized(this.products){
            if(!this.isEmpty())
                this.products[--pointer] = null;
        }
    }
    
    /**
     * Add product to this
     * @param product product to be added
     */
    public void addProduct(Product product){
        synchronized(this.products){
            if(!this.isFull())
                this.products[pointer++] = product;
        }
    }
    
    /**
     * Get product from this
     * @return product
     */
    public Product getProduct() {
        Product product = null;
        synchronized(this.products){
            if(!this.isEmpty())
                product = products[--pointer];
        }
        return product;
    }
    
    /**
     * Get products from this
     * @param amount number of products to be returned
     * @return products - if amount is greater then number of available products, all available size
     */
    public Product[] getSomeProducts(int amount) {
        amount = Math.min(pointer + 1, amount);
        Product[] newProducts = new Product[amount];
        synchronized(this.products){
            for (int i = 0; i < amount; i++) {
                newProducts[i]= this.getProduct();
            }
        }
        return newProducts;
    }
    /**
     * Add products to this
     * @param newProducts products to be added
     */
    public void addProducts(Product[] newProducts){
        synchronized(this.products){
            for (Product newProduct : newProducts) {
                this.addProduct(newProduct);
            }
        }
    }
    
    /**
     * Checks dates of available products and removes outdated products
     */
    public void checkDates() {
        synchronized(this.products){ 
            for (int i = 0; i < this.products.length; i++) {
                if(null != this.products[i]) {
                    if(this.products[i].getBestBefore().isBefore(LocalTime.now()) && i < this.pointer){
                        for (int j = pointer; j > i; j--) {
                            if(null != this.products[j]) {
                                if(this.products[j].getBestBefore().isBefore(LocalTime.now())){
                                    this.products[i] = this.products[j];
                                    this.pointer--;
                                    break;
                                }else{
                                    this.products[j] = null;
                                    this.pointer--;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
