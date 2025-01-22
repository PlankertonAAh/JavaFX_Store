package;

public class Product {
    private int id;
    private String name;
    private int amount;

    // Constructor for adding a new product (without ID)
    public Product(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }
    public Product() {

    }

    // Constructor for creating a product with an ID (e.g., when fetching from the database)
    public Product(int id, String name, int amount) {
        this.id = id;
        this.name = name;
        this.amount = amount;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
