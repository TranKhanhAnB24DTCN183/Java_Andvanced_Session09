package kiemtra;

public class ProductFactory {
    public static Product createProduct(String type, String id, String name, double price, double extra) {
        if (type.equalsIgnoreCase("physical")) {
            return new PhysicalProduct(id, name, price, extra);
        } else if (type.equalsIgnoreCase("digital")) {
            return new DigitalProduct(id, name, price, extra);
        } else {
            throw new IllegalArgumentException("Invalid product type: " + type);
        }
    }
}
