import java.util.*;

public class Guest implements Comparable<Guest> {
    private String guestID;
    private String name;
    private String contactInfo;
    private String address;
    private List<String> bookingHistory;
    private List<Double> paymentHistory;

    public Guest(String guestID, String name, String contactInfo, String address) {
        this.guestID = guestID;
        this.name = name;
        this.contactInfo = contactInfo;
        this.address = address;
        this.bookingHistory = new ArrayList<>();
        this.paymentHistory = new ArrayList<>();
    }

    public void registerGuest() {
        System.out.println("Guest " + name + " registered successfully with ID: " + guestID);
    }

    public void updateContactInfo(String newContact, String newAddress) {
        this.contactInfo = newContact;
        this.address = newAddress;
    }

    public String getGuestDetails() {
        return String.format("ID: %s | Name: %s | Contact: %s | Address: %s",
                guestID, name, contactInfo, address);
    }

    public void addBooking(String reservationID) {
        bookingHistory.add(reservationID);
    }

    public void addPayment(double amount) {
        paymentHistory.add(amount);
    }

    public String getGuestID() { return guestID; }
    public String getName() { return name; }
    public String getContactInfo() { return contactInfo; }
    public String getAddress() { return address; }
    public List<String> getBookingHistory() { return bookingHistory; }
    public List<Double> getPaymentHistory() { return paymentHistory; }

    public void setName(String name) { this.name = name; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public int compareTo(Guest other) {
        return this.name.compareToIgnoreCase(other.name);
    }

    @Override
    public String toString() {
        return name + " (" + guestID + ")";
    }
}