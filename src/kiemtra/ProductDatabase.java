package kiemtra;

import java.util.ArrayList;
import java.util.List;

public class ProductDatabase {
    private static ProductDatabase instance;
    private final List<Product> products;

    private ProductDatabase() {
        this.products = new ArrayList<>();
    }

    public static synchronized ProductDatabase getInstance() {
        if (instance == null) {
            instance = new ProductDatabase();
        }
        return instance;
    }

    public void addProduct(Product product) {
        this.products.add(product);
    }

    public void updateProduct(String id, Product updatedProduct) {
        for (int i = 0; i < this.products.size(); i++) {
            if (this.products.get(i).getId().equals(id)) {
                this.products.set(i, updatedProduct);
                return;
            }
        }
    }

    public void deleteProduct(String id) {
        this.products.removeIf(product -> product.getId().equals(id));
    }

    public List<Product> getProducts() {
        return new ArrayList<>(this.products);
    }
}
