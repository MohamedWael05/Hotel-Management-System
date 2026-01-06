
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_management_system";
    private static final String USERNAME = "hotel_app";
    private static final String PASSWORD = "HotelApp123!";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        initializeDatabase();
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    private void initializeDatabase() {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");


            Connection rootConn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/", "root", "1b2961bf8c145");


            Statement stmt = rootConn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS hotel_management_system");
            stmt.close();
            rootConn.close();


            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            createTablesIfNotExist();

            System.out.println("Database connection established successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());

            try {
                connection = DriverManager.getConnection(URL, "root", "your_root_password_here");
                createTablesIfNotExist();
                System.out.println("Connected with root user!");
            } catch (SQLException ex) {
                System.err.println("Failed to connect with root user: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void createTablesIfNotExist() throws SQLException {
        String[] createTableQueries = {

                "CREATE TABLE IF NOT EXISTS users (" +
                        "user_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "username VARCHAR(50) UNIQUE NOT NULL," +
                        "password_hash VARCHAR(255) NOT NULL," +
                        "full_name VARCHAR(100) NOT NULL," +
                        "role ENUM('admin', 'manager', 'staff') DEFAULT 'staff'," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)",


                "CREATE TABLE IF NOT EXISTS guests (" +
                        "guest_id VARCHAR(10) PRIMARY KEY," +
                        "name VARCHAR(100) NOT NULL," +
                        "contact_info VARCHAR(50)," +
                        "address TEXT," +
                        "email VARCHAR(100)," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "INDEX idx_name (name))",


                "CREATE TABLE IF NOT EXISTS rooms (" +
                        "room_id VARCHAR(10) PRIMARY KEY," +
                        "room_type VARCHAR(20) NOT NULL," +
                        "capacity INT NOT NULL," +
                        "price_per_night DECIMAL(10,2) NOT NULL," +
                        "is_available BOOLEAN DEFAULT TRUE," +
                        "description TEXT," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "INDEX idx_type (room_type)," +
                        "INDEX idx_availability (is_available))",


                "CREATE TABLE IF NOT EXISTS reservations (" +
                        "reservation_id VARCHAR(10) PRIMARY KEY," +
                        "guest_id VARCHAR(10) NOT NULL," +
                        "room_id VARCHAR(10) NOT NULL," +
                        "check_in_date DATE NOT NULL," +
                        "check_out_date DATE NOT NULL," +
                        "status ENUM('Active', 'Completed', 'Cancelled') DEFAULT 'Active'," +
                        "total_cost DECIMAL(10,2)," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (guest_id) REFERENCES guests(guest_id) ON DELETE CASCADE," +
                        "FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE," +
                        "INDEX idx_status (status)," +
                        "INDEX idx_dates (check_in_date, check_out_date))",


                "CREATE TABLE IF NOT EXISTS service_requests (" +
                        "request_id VARCHAR(10) PRIMARY KEY," +
                        "guest_id VARCHAR(10) NOT NULL," +
                        "service_type VARCHAR(50) NOT NULL," +
                        "request_date DATETIME NOT NULL," +
                        "status ENUM('Pending', 'In Progress', 'Completed') DEFAULT 'Pending'," +
                        "notes TEXT," +
                        "completed_date DATETIME," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (guest_id) REFERENCES guests(guest_id) ON DELETE CASCADE," +
                        "INDEX idx_status (status))",


                "CREATE TABLE IF NOT EXISTS billing (" +
                        "bill_id VARCHAR(10) PRIMARY KEY," +
                        "guest_id VARCHAR(10) NOT NULL," +
                        "reservation_id VARCHAR(10)," +
                        "total_amount DECIMAL(10,2) NOT NULL," +
                        "paid_amount DECIMAL(10,2) DEFAULT 0," +
                        "status ENUM('Unpaid', 'Partially Paid', 'Fully Paid') DEFAULT 'Unpaid'," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (guest_id) REFERENCES guests(guest_id) ON DELETE CASCADE," +
                        "FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id) ON DELETE SET NULL," +
                        "INDEX idx_status (status))",


                "CREATE TABLE IF NOT EXISTS payments (" +
                        "payment_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "bill_id VARCHAR(10) NOT NULL," +
                        "amount DECIMAL(10,2) NOT NULL," +
                        "payment_method ENUM('Cash', 'Credit Card', 'Debit Card', 'Online') DEFAULT 'Cash'," +
                        "payment_date DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "notes TEXT," +
                        "FOREIGN KEY (bill_id) REFERENCES billing(bill_id) ON DELETE CASCADE)"
        };

        Statement stmt = connection.createStatement();
        for (String query : createTableQueries) {
            try {
                stmt.executeUpdate(query);
            } catch (SQLException e) {
                System.err.println("Error creating table: " + e.getMessage());

            }
        }
        stmt.close();


        insertDefaultData();
    }

    private void insertDefaultData() throws SQLException {

        String[] defaultUsers = {
                "INSERT IGNORE INTO users (username, password_hash, full_name, role) VALUES ('admin', SHA2('admin123', 256), 'System Administrator', 'admin')",
                "INSERT IGNORE INTO users (username, password_hash, full_name, role) VALUES ('staff', SHA2('staff123', 256), 'Hotel Staff', 'staff')"
        };


        String[] defaultRooms = {
                "INSERT IGNORE INTO rooms (room_id, room_type, capacity, price_per_night, is_available, description) VALUES ('R101', 'Single', 1, 80.00, TRUE, 'Standard single room')",
                "INSERT IGNORE INTO rooms (room_id, room_type, capacity, price_per_night, is_available, description) VALUES ('R102', 'Single', 1, 80.00, TRUE, 'Standard single room')",
                "INSERT IGNORE INTO rooms (room_id, room_type, capacity, price_per_night, is_available, description) VALUES ('R201', 'Double', 2, 120.00, TRUE, 'Double bed room')",
                "INSERT IGNORE INTO rooms (room_id, room_type, capacity, price_per_night, is_available, description) VALUES ('R202', 'Double', 2, 120.00, TRUE, 'Double bed room')",
                "INSERT IGNORE INTO rooms (room_id, room_type, capacity, price_per_night, is_available, description) VALUES ('R301', 'Suite', 4, 250.00, TRUE, 'Luxury suite')",
                "INSERT IGNORE INTO rooms (room_id, room_type, capacity, price_per_night, is_available, description) VALUES ('R302', 'Suite', 4, 250.00, TRUE, 'Luxury suite')",
                "INSERT IGNORE INTO rooms (room_id, room_type, capacity, price_per_night, is_available, description) VALUES ('R401', 'Deluxe', 3, 180.00, TRUE, 'Deluxe room')",
                "INSERT IGNORE INTO rooms (room_id, room_type, capacity, price_per_night, is_available, description) VALUES ('R402', 'Deluxe', 3, 180.00, TRUE, 'Deluxe room')"
        };

        Statement stmt = connection.createStatement();
        for (String query : defaultUsers) {
            try {
                stmt.executeUpdate(query);
            } catch (SQLException e) {
                System.err.println("Error inserting default users: " + e.getMessage());
            }
        }

        for (String query : defaultRooms) {
            try {
                stmt.executeUpdate(query);
            } catch (SQLException e) {
                System.err.println("Error inserting default rooms: " + e.getMessage());
            }
        }
        stmt.close();
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Error reconnecting to database: " + e.getMessage());

            try {
                connection = DriverManager.getConnection(URL, "root", "your_root_password_here");
            } catch (SQLException ex) {
                System.err.println("Failed to reconnect: " + ex.getMessage());
            }
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }


    public boolean authenticateUser(String username, String password) {
        String query = "SELECT password_hash FROM users WHERE username = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                String inputHash = hashPassword(password);
                return storedHash.equals(inputHash);
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        return false;
    }

    public String getUserRole(String username) {
        String query = "SELECT role FROM users WHERE username = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            System.err.println("Error getting user role: " + e.getMessage());
        }
        return "staff";
    }

    private String hashPassword(String password) {
        try {

            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return password;
        }
    }


    public boolean saveGuest(Guest guest) {
        String query = "INSERT INTO guests (guest_id, name, contact_info, address) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE name = ?, contact_info = ?, address = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setString(1, guest.getGuestID());
            pstmt.setString(2, guest.getName());
            pstmt.setString(3, guest.getContactInfo());
            pstmt.setString(4, guest.getAddress());
            pstmt.setString(5, guest.getName());
            pstmt.setString(6, guest.getContactInfo());
            pstmt.setString(7, guest.getAddress());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saving guest: " + e.getMessage());
            return false;
        }
    }

    public List<Guest> getAllGuests() {
        List<Guest> guests = new ArrayList<>();
        String query = "SELECT * FROM guests ORDER BY name";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Guest guest = new Guest(
                        rs.getString("guest_id"),
                        rs.getString("name"),
                        rs.getString("contact_info"),
                        rs.getString("address")
                );
                guests.add(guest);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching guests: " + e.getMessage());
        }
        return guests;
    }

    public Guest getGuestById(String guestId) {
        String query = "SELECT * FROM guests WHERE guest_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setString(1, guestId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Guest(
                        rs.getString("guest_id"),
                        rs.getString("name"),
                        rs.getString("contact_info"),
                        rs.getString("address")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching guest by ID: " + e.getMessage());
        }
        return null;
    }


    public boolean saveRoom(Room room) {
        String query = "INSERT INTO rooms (room_id, room_type, capacity, price_per_night, is_available) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE room_type = ?, capacity = ?, price_per_night = ?, is_available = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setString(1, room.getRoomID());
            pstmt.setString(2, room.getRoomType());
            pstmt.setInt(3, room.getCapacity());
            pstmt.setDouble(4, room.getPricePerNight());
            pstmt.setBoolean(5, room.isAvailable());
            pstmt.setString(6, room.getRoomType());
            pstmt.setInt(7, room.getCapacity());
            pstmt.setDouble(8, room.getPricePerNight());
            pstmt.setBoolean(9, room.isAvailable());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saving room: " + e.getMessage());
            return false;
        }
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM rooms ORDER BY room_id";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Room room = new Room(
                        rs.getString("room_id"),
                        rs.getString("room_type"),
                        rs.getInt("capacity"),
                        rs.getDouble("price_per_night")
                );
                room.setAvailable(rs.getBoolean("is_available"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching rooms: " + e.getMessage());
        }
        return rooms;
    }

    public boolean updateRoomAvailability(String roomId, boolean isAvailable) {
        String query = "UPDATE rooms SET is_available = ? WHERE room_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setBoolean(1, isAvailable);
            pstmt.setString(2, roomId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating room availability: " + e.getMessage());
            return false;
        }
    }


    public boolean saveReservation(Reservation reservation) {
        String query = "INSERT INTO reservations (reservation_id, guest_id, room_id, check_in_date, check_out_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE guest_id = ?, room_id = ?, check_in_date = ?, check_out_date = ?, status = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setString(1, reservation.getReservationID());
            pstmt.setString(2, reservation.getGuestID());
            pstmt.setString(3, reservation.getRoomID());
            pstmt.setDate(4, Date.valueOf(reservation.getCheckInDate()));
            pstmt.setDate(5, Date.valueOf(reservation.getCheckOutDate()));
            pstmt.setString(6, reservation.getStatus());
            pstmt.setString(7, reservation.getGuestID());
            pstmt.setString(8, reservation.getRoomID());
            pstmt.setDate(9, Date.valueOf(reservation.getCheckInDate()));
            pstmt.setDate(10, Date.valueOf(reservation.getCheckOutDate()));
            pstmt.setString(11, reservation.getStatus());
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0 && reservation.getStatus().equals("Active")) {
                updateRoomAvailability(reservation.getRoomID(), false);
            }

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saving reservation: " + e.getMessage());
            return false;
        }
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservations ORDER BY check_in_date DESC";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Reservation reservation = new Reservation(
                        rs.getString("reservation_id"),
                        rs.getString("guest_id"),
                        rs.getString("room_id"),
                        rs.getDate("check_in_date").toLocalDate(),
                        rs.getDate("check_out_date").toLocalDate()
                );
                reservation.setStatus(rs.getString("status"));
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reservations: " + e.getMessage());
        }
        return reservations;
    }

    public boolean updateReservationStatus(String reservationId, String status) {
        String query = "UPDATE reservations SET status = ? WHERE reservation_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setString(2, reservationId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating reservation status: " + e.getMessage());
            return false;
        }
    }


    public boolean saveServiceRequest(ServiceRequest serviceRequest) {
        String query = "INSERT INTO service_requests (request_id, guest_id, service_type, request_date, status) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE guest_id = ?, service_type = ?, request_date = ?, status = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setString(1, serviceRequest.getRequestID());
            pstmt.setString(2, serviceRequest.getGuestID());
            pstmt.setString(3, serviceRequest.getServiceType());
            pstmt.setTimestamp(4, Timestamp.valueOf(serviceRequest.getRequestDate()));
            pstmt.setString(5, serviceRequest.getStatus());
            pstmt.setString(6, serviceRequest.getGuestID());
            pstmt.setString(7, serviceRequest.getServiceType());
            pstmt.setTimestamp(8, Timestamp.valueOf(serviceRequest.getRequestDate()));
            pstmt.setString(9, serviceRequest.getStatus());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saving service request: " + e.getMessage());
            return false;
        }
    }

    public List<ServiceRequest> getAllServiceRequests() {
        List<ServiceRequest> requests = new ArrayList<>();
        String query = "SELECT * FROM service_requests ORDER BY request_date DESC";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                ServiceRequest request = new ServiceRequest(
                        rs.getString("request_id"),
                        rs.getString("guest_id"),
                        rs.getString("service_type")
                );
                request.setStatus(rs.getString("status"));
                requests.add(request);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching service requests: " + e.getMessage());
        }
        return requests;
    }

    public boolean updateServiceRequestStatus(String requestId, String status) {
        String query = "UPDATE service_requests SET status = ? WHERE request_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setString(2, requestId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating service request status: " + e.getMessage());
            return false;
        }
    }


    public boolean saveBill(Billing bill) {
        String query = "INSERT INTO billing (bill_id, guest_id, total_amount, paid_amount, status) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE guest_id = ?, total_amount = ?, paid_amount = ?, status = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            double totalPaid = bill.getPaymentHistory().stream().mapToDouble(Double::doubleValue).sum();
            pstmt.setString(1, bill.getBillID());
            pstmt.setString(2, bill.getGuestID());
            pstmt.setDouble(3, bill.getTotalAmount());
            pstmt.setDouble(4, totalPaid);
            pstmt.setString(5, bill.getPaymentStatus());
            pstmt.setString(6, bill.getGuestID());
            pstmt.setDouble(7, bill.getTotalAmount());
            pstmt.setDouble(8, totalPaid);
            pstmt.setString(9, bill.getPaymentStatus());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saving bill: " + e.getMessage());
            return false;
        }
    }

    public boolean savePayment(String billId, double amount) {
        String paymentQuery = "INSERT INTO payments (bill_id, amount) VALUES (?, ?)";
        String billingQuery = "UPDATE billing SET paid_amount = paid_amount + ?, " +
                "status = CASE WHEN paid_amount + ? >= total_amount THEN 'Fully Paid' " +
                "WHEN paid_amount + ? > 0 THEN 'Partially Paid' ELSE 'Unpaid' END " +
                "WHERE bill_id = ?";

        try {

            getConnection().setAutoCommit(false);

            try (PreparedStatement pstmt1 = getConnection().prepareStatement(paymentQuery)) {
                pstmt1.setString(1, billId);
                pstmt1.setDouble(2, amount);
                pstmt1.executeUpdate();
            }

            try (PreparedStatement pstmt2 = getConnection().prepareStatement(billingQuery)) {
                pstmt2.setDouble(1, amount);
                pstmt2.setDouble(2, amount);
                pstmt2.setDouble(3, amount);
                pstmt2.setString(4, billId);
                int rowsAffected = pstmt2.executeUpdate();

                if (rowsAffected > 0) {
                    getConnection().commit();
                    return true;
                } else {
                    getConnection().rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            try {
                getConnection().rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.err.println("Error saving payment: " + e.getMessage());
            return false;
        } finally {
            try {
                getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    public List<Billing> getAllBills() {
        List<Billing> bills = new ArrayList<>();
        String query = "SELECT * FROM billing ORDER BY created_at DESC";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Billing bill = new Billing(
                        rs.getString("bill_id"),
                        rs.getString("guest_id"),
                        rs.getDouble("total_amount")
                );

                List<Double> payments = getPaymentsForBill(bill.getBillID());
                for (Double payment : payments) {
                    bill.addPayment(payment);
                }
                bills.add(bill);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching bills: " + e.getMessage());
        }
        return bills;
    }

    private List<Double> getPaymentsForBill(String billId) {
        List<Double> payments = new ArrayList<>();
        String query = "SELECT amount FROM payments WHERE bill_id = ? ORDER BY payment_date";
        try (PreparedStatement pstmt = getConnection().prepareStatement(query)) {
            pstmt.setString(1, billId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                payments.add(rs.getDouble("amount"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching payments: " + e.getMessage());
        }
        return payments;
    }


    public int getTotalGuests() {
        String query = "SELECT COUNT(*) as count FROM guests";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total guests: " + e.getMessage());
        }
        return 0;
    }

    public int getAvailableRooms() {
        String query = "SELECT COUNT(*) as count FROM rooms WHERE is_available = TRUE";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting available rooms: " + e.getMessage());
        }
        return 0;
    }

    public int getTotalRooms() {
        String query = "SELECT COUNT(*) as count FROM rooms";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total rooms: " + e.getMessage());
        }
        return 0;
    }

    public int getActiveReservations() {
        String query = "SELECT COUNT(*) as count FROM reservations WHERE status = 'Active'";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting active reservations: " + e.getMessage());
        }
        return 0;
    }

    public double getTotalRevenue() {
        String query = "SELECT COALESCE(SUM(paid_amount), 0) as revenue FROM billing";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getDouble("revenue");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total revenue: " + e.getMessage());
        }
        return 0.0;
    }


    public void loadAllData(HotelManagementSystem hotelSystem) {
        try {
            List<Guest> guests = getAllGuests();
            for (Guest guest : guests) {
                hotelSystem.getGuestList().insert(guest);
            }


            List<Room> rooms = getAllRooms();
            hotelSystem.getRoomList().addAll(rooms);


            List<Reservation> reservations = getAllReservations();
            hotelSystem.getReservationList().addAll(reservations);


            List<ServiceRequest> serviceRequests = getAllServiceRequests();
            hotelSystem.getServiceRequests().addAll(serviceRequests);


            List<Billing> bills = getAllBills();
            hotelSystem.getBillingRecords().addAll(bills);

            System.out.println("Data loaded successfully from database!");
            System.out.println("Guests: " + guests.size());
            System.out.println("Rooms: " + rooms.size());
            System.out.println("Reservations: " + reservations.size());
            System.out.println("Service Requests: " + serviceRequests.size());
            System.out.println("Bills: " + bills.size());

        } catch (Exception e) {
            System.err.println("Error loading data from database: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void resetDatabase() {
        try {
            String[] dropTables = {
                    "DROP TABLE IF EXISTS payments",
                    "DROP TABLE IF EXISTS billing",
                    "DROP TABLE IF EXISTS service_requests",
                    "DROP TABLE IF EXISTS reservations",
                    "DROP TABLE IF EXISTS rooms",
                    "DROP TABLE IF EXISTS guests",
                    "DROP TABLE IF EXISTS users"
            };

            Statement stmt = getConnection().createStatement();
            for (String query : dropTables) {
                try {
                    stmt.executeUpdate(query);
                } catch (SQLException e) {
                    System.err.println("Error dropping table: " + e.getMessage());
                }
            }
            stmt.close();


            createTablesIfNotExist();
            System.out.println("Database reset successfully!");
        } catch (SQLException e) {
            System.err.println("Error resetting database: " + e.getMessage());
        }
    }
}