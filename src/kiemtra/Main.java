package kiemtra;

import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final ProductDatabase productDatabase = ProductDatabase.getInstance();
    public static void main(String[] args) {
        int choice;
        do {
            System.out.println("""
                    ---------------------- QUẢN LÝ SẢN PHẨM ----------------------
                    1. Thêm mới sản phẩm
                    2. Xem danh sách sản phẩm
                    3. Cập nhật thông tin sản phẩm
                    4. Xoá sản phẩm
                    5. Thoát
                    --------------------------------------------------------------
                    Lựa chọn của bạn:
                    """);
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    addProduct();
                    break;
                case 2:
                    displayProducts();
                    break;
                case 3:
                    updateProduct();
                    break;
                case 4:
                    deleteProduct();
                    break;
                case 5:
                    System.out.println("Thoát...");
                    return;
                default:
                    System.out.println("Vui lòng nhập lại... !");
            }
        } while (choice != 5);
    }
    static void addProduct() {
        System.out.print("Chọn (1 : Physical, 2 : Digital): ");
        int type = sc.nextInt();
        sc.nextLine();

        System.out.print("Nhập ID: ");
        String id = sc.nextLine();
        System.out.print("Nhập name: ");
        String name = sc.nextLine();
        System.out.print("Nhập price: ");
        double price = sc.nextDouble();

        if (type == 1) {
            System.out.print("Nhập weight: ");
            double weight = sc.nextDouble();
            productDatabase.addProduct(new PhysicalProduct(id, name, price, weight));
        } else {
            System.out.print("Nhập size: ");
            double size = sc.nextDouble();
            productDatabase.addProduct(new DigitalProduct(id, name, price, size));
        }
    }

    static void updateProduct() {
        System.out.print("Nhập ID sản phẩm cần cập nhật: ");
        String id = sc.nextLine();
        System.out.print("Chọn (1 : Physical, 2 : Digital): ");
        int type = sc.nextInt();
        sc.nextLine();

        System.out.print("Nhập name: ");
        String name = sc.nextLine();
        System.out.print("Nhập price: ");
        double price = sc.nextDouble();

        if (type == 1) {
            System.out.print("Nhập weight: ");
            double weight = sc.nextDouble();
            productDatabase.updateProduct(id, new PhysicalProduct(id, name, price, weight));
        } else {
            System.out.print("Nhập size: ");
            double size = sc.nextDouble();
            productDatabase.updateProduct(id, new DigitalProduct(id, name, price, size));
        }
    }

    static void deleteProduct() {
        System.out.print("Nhập ID sản phẩm cần xóa: ");
        String id = sc.nextLine();
        productDatabase.deleteProduct(id);
    }

    static void displayProducts() {
        for (Product product : productDatabase.getProducts()) {
            product.displayInfo();
            System.out.println();
        }
    }
}
