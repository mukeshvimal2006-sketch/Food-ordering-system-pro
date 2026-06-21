package foodorder.model;

public class CartItem {
    private int foodId;
    private String foodName;
    private double price;
    private int quantity;

    public CartItem(int foodId, String foodName, double price, int quantity) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.price = price;
        this.quantity = quantity;
    }

    public int getFoodId() { return foodId; }
    public String getFoodName() { return foodName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getSubtotal() { return price * quantity; }
}
