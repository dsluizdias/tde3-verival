import java.util.Scanner;

public class CalculadoraDescontoTradicional {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("Enter the price");
            double price = Double.parseDouble(scanner.nextLine());

            System.out.println("Are you vip? (yes/no)");
            String answer = scanner.nextLine();

            CustomerCategory vip = CustomerCategory.NO_VIP;
            if (answer.trim().equalsIgnoreCase("yes")) {
                vip = CustomerCategory.VIP;
            }

            System.out.printf("Final price: %.2f%n", calculateFinalPrice(price, vip));
        }
    }

    public static double calculateFinalPrice(double price, CustomerCategory vip) {

        if (price < 0) {
            throw new IllegalArgumentException("price cannot be negative");
        }

        int discountPercent = 0;

        if (price >= 100 && price <= 500) {
            discountPercent = 10;
        } else if (price > 500 && price <= 1000) {
            discountPercent = 20;
        }

        if (vip == CustomerCategory.VIP) {
            discountPercent += 5;
        }

        double finalPrice = price - price * discountPercent / 100.0;

        if (price < 100) {
            finalPrice += 15;
        }

        return finalPrice;
    }

}
