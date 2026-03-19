package kiemtra;

public class DigitalProduct extends Product{
    private double size;

    public DigitalProduct(String id, String name, double price, double size) {
        super(id, name, price);
        this.size = size;
    }

    public double getSize() {
        return this.size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    @Override
    public void displayInfo() {
        System.out.println("Digital Product:");
        System.out.println("ID: " + this.getId());
        System.out.println("Name: " + this.getName());
        System.out.println("Price: $" + this.getPrice());
        System.out.println("Size: " + this.size + " MB");
    }
}
