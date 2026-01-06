import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reservation {
    private String reservationID;
    private String guestID;
    private String roomID;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status;

    public Reservation(String reservationID, String guestID, String roomID,
                       LocalDate checkInDate, LocalDate checkOutDate) {
        this.reservationID = reservationID;
        this.guestID = guestID;
        this.roomID = roomID;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = "Active";
    }

    public void makeReservation() {
        this.status = "Active";
        System.out.println("Reservation " + reservationID + " created successfully.");
    }

    public void cancelReservation() {
        this.status = "Cancelled";
        System.out.println("Reservation " + reservationID + " has been cancelled.");
    }

    public void updateReservation(LocalDate newCheckIn, LocalDate newCheckOut) {
        this.checkInDate = newCheckIn;
        this.checkOutDate = newCheckOut;
        System.out.println("Reservation " + reservationID + " updated.");
    }

    public long getNumberOfNights() {
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }


    public String getReservationID() { return reservationID; }
    public String getGuestID() { return guestID; }
    public String getRoomID() { return roomID; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public String getStatus() { return status; }

    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return reservationID + " - Guest: " + guestID + " | Room: " + roomID +
                " | " + checkInDate + " to " + checkOutDate + " | " + status;
    }
}