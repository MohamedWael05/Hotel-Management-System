import java.util.*;

public class Billing {
    private String billID;
    private String guestID;
    private double totalAmount;
    private List<Double> paymentHistory;
    private boolean isPaid;

    public Billing(String billID, String guestID, double totalAmount) {
        this.billID = billID;
        this.guestID = guestID;
        this.totalAmount = totalAmount;
        this.paymentHistory = new ArrayList<>();
        this.isPaid = false;
    }

    public void generateBill(double amount) {
        this.totalAmount = amount;
        System.out.println("Bill generated for Guest " + guestID + ": $" + amount);
    }

    public void addPayment(double payment) {
        paymentHistory.add(payment);
        double totalPaid = getTotalPaid();

        if (totalPaid >= totalAmount) {
            isPaid = true;
            System.out.println("Bill fully paid. Total: $" + totalAmount);
        } else {
            System.out.println("Payment of $" + payment + " received. Remaining: $" +
                    (totalAmount - totalPaid));
        }
    }

    public String getPaymentStatus() {
        double totalPaid = getTotalPaid();
        if (isPaid) {
            return "Fully Paid";
        } else if (totalPaid > 0) {
            return "Partially Paid ($" + totalPaid + " of $" + totalAmount + ")";
        } else {
            return "Unpaid";
        }
    }

    private double getTotalPaid() {
        return paymentHistory.stream().mapToDouble(Double::doubleValue).sum();
    }

    public double getRemainingBalance() {
        return Math.max(0, totalAmount - getTotalPaid());
    }

    public String getBillID() { return billID; }
    public String getGuestID() { return guestID; }
    public double getTotalAmount() { return totalAmount; }
    public List<Double> getPaymentHistory() { return paymentHistory; }
    public boolean isPaid() { return isPaid; }

    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setPaid(boolean paid) { isPaid = paid; }

    @Override
    public String toString() {
        return "Bill " + billID + " - Guest: " + guestID + " | Total: $" +
                totalAmount + " | Status: " + getPaymentStatus();
    }
}