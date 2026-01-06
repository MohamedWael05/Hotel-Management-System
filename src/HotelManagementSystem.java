import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import javax.swing.*;

public class HotelManagementSystem {
    private GuestBST guestList;
    private List<Room> roomList;
    private List<Reservation> reservationList;
    private List<ServiceRequest> serviceRequests;
    private List<Billing> billingRecords;
    private int guestCounter;
    private int reservationCounter;
    private int serviceCounter;
    private int billCounter;
    private DatabaseConnection database;

    public HotelManagementSystem() {
        this.guestList = new GuestBST();
        this.roomList = new ArrayList<>();
        this.reservationList = new ArrayList<>();
        this.serviceRequests = new ArrayList<>();
        this.billingRecords = new ArrayList<>();
        this.guestCounter = 1;
        this.reservationCounter = 1;
        this.serviceCounter = 1;
        this.billCounter = 1;

        this.database = DatabaseConnection.getInstance();

        loadDataFromDatabase();

        System.out.println("Hotel Management System initialized");
        System.out.println("Guests: " + guestList.getAllGuests().size());
        System.out.println("Rooms: " + roomList.size());
        System.out.println("Reservations: " + reservationList.size());
    }

    private void loadDataFromDatabase() {
        System.out.println("Loading data from database...");
        database.loadAllData(this);

        updateCounters();
    }

    private void updateCounters() {
        List<Guest> guests = guestList.getAllGuests();
        if (!guests.isEmpty()) {
            String lastGuestId = guests.get(guests.size() - 1).getGuestID();
            try {
                guestCounter = Integer.parseInt(lastGuestId.substring(1)) + 1;
            } catch (NumberFormatException e) {
                guestCounter = guests.size() + 1;
            }
        }

        if (!reservationList.isEmpty()) {
            String lastResId = reservationList.get(reservationList.size() - 1).getReservationID();
            try {
                reservationCounter = Integer.parseInt(lastResId.substring(3)) + 1;
            } catch (NumberFormatException e) {
                reservationCounter = reservationList.size() + 1;
            }
        }

        if (!serviceRequests.isEmpty()) {
            String lastReqId = serviceRequests.get(serviceRequests.size() - 1).getRequestID();
            try {
                serviceCounter = Integer.parseInt(lastReqId.substring(3)) + 1;
            } catch (NumberFormatException e) {
                serviceCounter = serviceRequests.size() + 1;
            }
        }

        if (!billingRecords.isEmpty()) {
            String lastBillId = billingRecords.get(billingRecords.size() - 1).getBillID();
            try {
                billCounter = Integer.parseInt(lastBillId.substring(1)) + 1;
            } catch (NumberFormatException e) {
                billCounter = billingRecords.size() + 1;
            }
        }

        System.out.println("Counters updated:");
        System.out.println("Guest Counter: " + guestCounter);
        System.out.println("Reservation Counter: " + reservationCounter);
        System.out.println("Service Counter: " + serviceCounter);
        System.out.println("Bill Counter: " + billCounter);
    }

    public Guest addGuest(String name, String contactInfo, String address) {
        if (name == null || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Guest name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        String guestID = "G" + String.format("%03d", guestCounter++);
        Guest guest = new Guest(guestID, name.trim(), contactInfo.trim(), address.trim());

        try {
            guestList.insert(guest);
            guest.registerGuest();

            boolean saved = database.saveGuest(guest);
            if (saved) {
                System.out.println("Guest saved to database: " + guestID);
            } else {
                System.err.println("Failed to save guest to database: " + guestID);
            }

            return guest;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error adding guest: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            guestCounter--;
            return null;
        }
    }

    public Reservation makeReservation(String guestID, String roomID, LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            JOptionPane.showMessageDialog(null, "Dates cannot be null!", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) {
            JOptionPane.showMessageDialog(null, "Check-in date must be before check-out date!", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (checkIn.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(null, "Check-in date cannot be in the past!", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        Guest guest = guestList.searchByID(guestID);
        if (guest == null) {
            JOptionPane.showMessageDialog(null, "Guest not found! ID: " + guestID, "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        Room room = findRoomByID(roomID);
        if (room == null) {
            JOptionPane.showMessageDialog(null, "Room not found! ID: " + roomID, "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (!room.isAvailable()) {
            JOptionPane.showMessageDialog(null, "Room " + roomID + " is not available!", "Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        String resID = "RES" + String.format("%03d", reservationCounter++);
        Reservation reservation = new Reservation(resID, guestID, roomID, checkIn, checkOut);

        try {
            reservation.makeReservation();
            reservationList.add(reservation);

            room.assignRoom();
            database.updateRoomAvailability(roomID, false);

            guest.addBooking(resID);

            double totalCost = room.getPricePerNight() * reservation.getNumberOfNights();
            createBill(guestID, totalCost);


            boolean saved = database.saveReservation(reservation);
            if (saved) {
                System.out.println("Reservation saved to database: " + resID);
            } else {
                System.err.println("Failed to save reservation to database: " + resID);
            }

            return reservation;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error creating reservation: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            reservationCounter--;
            return null;
        }
    }

    public void cancelReservation(String reservationID) {
        Reservation res = findReservationByID(reservationID);
        if (res != null && res.getStatus().equals("Active")) {
            res.cancelReservation();
            Room room = findRoomByID(res.getRoomID());
            if (room != null) {
                room.releaseRoom();
                database.updateRoomAvailability(room.getRoomID(), true);
            }
            database.updateReservationStatus(reservationID, "Cancelled");
            JOptionPane.showMessageDialog(null, "Reservation cancelled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Reservation not found or not active!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateReservationStatus(String reservationID, String newStatus) {
        Reservation res = findReservationByID(reservationID);
        if (res != null) {
            String oldStatus = res.getStatus();
            res.setStatus(newStatus);

            database.updateReservationStatus(reservationID, newStatus);


            if (oldStatus.equals("Active") && !newStatus.equals("Active")) {
                Room room = findRoomByID(res.getRoomID());
                if (room != null) {
                    room.releaseRoom();
                    database.updateRoomAvailability(room.getRoomID(), true);
                }
            } else if (!oldStatus.equals("Active") && newStatus.equals("Active")) {
                Room room = findRoomByID(res.getRoomID());
                if (room != null && room.isAvailable()) {
                    room.assignRoom();
                    database.updateRoomAvailability(room.getRoomID(), false);
                }
            }

            System.out.println("Reservation " + reservationID + " status updated from " + oldStatus + " to " + newStatus);
        } else {
            JOptionPane.showMessageDialog(null, "Reservation not found!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateServiceStatus(String requestID, String newStatus) {
        ServiceRequest req = findServiceRequestByID(requestID);
        if (req != null) {
            req.setStatus(newStatus);


            database.updateServiceRequestStatus(requestID, newStatus);

            System.out.println("Service request " + requestID + " updated to: " + newStatus);
        } else {
            JOptionPane.showMessageDialog(null, "Service request not found!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public ServiceRequest addServiceRequest(String guestID, String serviceType) {
        if (guestList.searchByID(guestID) == null) {
            JOptionPane.showMessageDialog(null, "Guest not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (serviceType == null || serviceType.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Service type cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        String reqID = "SRV" + String.format("%03d", serviceCounter++);
        ServiceRequest request = new ServiceRequest(reqID, guestID, serviceType.trim());

        try {
            request.addRequest();
            serviceRequests.add(request);


            boolean saved = database.saveServiceRequest(request);
            if (saved) {
                System.out.println("Service request saved to database: " + reqID);
            } else {
                System.err.println("Failed to save service request to database: " + reqID);
            }

            return request;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error adding service request: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            serviceCounter--;
            return null;
        }
    }

    public Billing createBill(String guestID, double amount) {
        if (amount <= 0) {
            System.err.println("Invalid bill amount: " + amount);
            return null;
        }

        String billID = "B" + String.format("%03d", billCounter++);
        Billing bill = new Billing(billID, guestID, amount);

        try {
            bill.generateBill(amount);
            billingRecords.add(bill);

            boolean saved = database.saveBill(bill);
            if (saved) {
                System.out.println("Bill saved to database: " + billID);
            } else {
                System.err.println("Failed to save bill to database: " + billID);
            }

            return bill;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error creating bill: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            billCounter--;
            return null;
        }
    }

    public void processPayment(String billID, double payment) {
        if (payment <= 0) {
            JOptionPane.showMessageDialog(null, "Payment amount must be positive!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Billing bill = findBillingByID(billID);
        if (bill != null) {
            bill.addPayment(payment);
            Guest guest = guestList.searchByID(bill.getGuestID());
            if (guest != null) {
                guest.addPayment(payment);
            }

            boolean saved = database.savePayment(billID, payment);
            if (saved) {
                System.out.println("Payment saved to database for bill: " + billID);
            } else {
                System.err.println("Failed to save payment to database for bill: " + billID);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Bill not found!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public String generateReports(String reportType) {
        ReportGenerator generator = new ReportGenerator(reportType);
        switch (reportType.toLowerCase()) {
            case "reservation":
                return generator.generateReservationReport(reservationList);
            case "revenue":
                return generator.generateRevenueReport(billingRecords);
            case "occupancy":
                return generator.generateOccupancyReport(roomList);
            default:
                return "Invalid report type! Available: reservation, revenue, occupancy";
        }
    }
    public int getTotalGuests() {
        return guestList.getAllGuests().size();
    }

    public int getAvailableRooms() {
        int available = 0;
        for (Room room : roomList) {
            if (room.isAvailable())
                available++;
        }
        return available;
    }

    public int getTotalRooms() {
        return roomList.size();
    }

    public int getActiveReservations() {
        int active = 0;
        for (Reservation res : reservationList) {
            if (res.getStatus().equals("Active")) active++;
        }
        return active;
    }

    public int getTotalReservations() {
        return reservationList.size();
    }

    public int getTotalServiceRequests() {
        return serviceRequests.size();
    }

    public double getTotalRevenue() {
        double revenue = 0;
        for (Billing bill : billingRecords) {
            revenue += bill.getTotalAmount();
        }
        return revenue;
    }

    public double getCollectedRevenue() {
        double collected = 0;
        for (Billing bill : billingRecords) {
            collected += bill.getPaymentHistory().stream().mapToDouble(Double::doubleValue).sum();
        }
        return collected;
    }

    private Room findRoomByID(String roomID) {
        for (Room room : roomList) {
            if (room.getRoomID().equals(roomID)) {
                return room;
            }
        }
        return null;
    }

    private Reservation findReservationByID(String reservationID) {
        for (Reservation res : reservationList) {
            if (res.getReservationID().equals(reservationID)) {
                return res;
            }
        }
        return null;
    }

    private ServiceRequest findServiceRequestByID(String requestID) {
        for (ServiceRequest req : serviceRequests) {
            if (req.getRequestID().equals(requestID)) {
                return req;
            }
        }
        return null;
    }

    private Billing findBillingByID(String billID) {
        for (Billing bill : billingRecords) {
            if (bill.getBillID().equals(billID)) {
                return bill;
            }
        }
        return null;
    }


    public GuestBST getGuestList() { return guestList; }
    public List<Room> getRoomList() { return roomList; }
    public List<Reservation> getReservationList() { return reservationList; }
    public List<ServiceRequest> getServiceRequests() { return serviceRequests; }
    public List<Billing> getBillingRecords() { return billingRecords; }
    public DatabaseConnection getDatabase() { return database; }
}