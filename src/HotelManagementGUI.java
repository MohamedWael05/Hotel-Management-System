
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HotelManagementGUI extends JFrame {
    private HotelManagementSystem hotelSystem;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color ACCENT_COLOR = new Color(46, 204, 113);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    private final Color WARNING_COLOR = new Color(230, 126, 34);
    private final Color DANGER_COLOR = new Color(231, 76, 60);

    private DefaultTableModel guestTableModel, reservationTableModel, roomTableModel, serviceTableModel, billingTableModel;
    private String currentUser;

    public HotelManagementGUI() {
        this("admin"); // Default user
    }

    public HotelManagementGUI(String username) {
        this.currentUser = username;
        hotelSystem = new HotelManagementSystem();
        initializeGUI();


        SwingUtilities.invokeLater(() -> {
            refreshDashboard();
        });
    }

    private void initializeGUI() {
        setTitle("Hotel Management System - Database Edition");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        JPanel sidebar = createSidebar();
        container.add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BACKGROUND_COLOR);

        mainPanel.add(createDashboardPanel(), "Dashboard");
        mainPanel.add(createGuestPanel(), "Guests");
        mainPanel.add(createReservationPanel(), "Reservations");
        mainPanel.add(createRoomPanel(), "Rooms");
        mainPanel.add(createServicePanel(), "Services");
        mainPanel.add(createBillingPanel(), "Billing");
        mainPanel.add(createReportsPanel(), "Reports");

        container.add(mainPanel, BorderLayout.CENTER);


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                hotelSystem.getDatabase().closeConnection();
                System.out.println("Application closed. Database connection closed.");
            }
        });

        setVisible(true);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(PRIMARY_COLOR);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));


        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setBackground(PRIMARY_COLOR);
        userPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel userIcon = new JLabel("👨‍💼");
        userIcon.setFont(new Font("Arial", Font.PLAIN, 40));
        userIcon.setForeground(Color.WHITE);
        userIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userName = new JLabel(currentUser);
        userName.setFont(new Font("Arial", Font.BOLD, 16));
        userName.setForeground(Color.WHITE);
        userName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userRole = new JLabel("Administrator");
        userRole.setFont(new Font("Arial", Font.PLAIN, 12));
        userRole.setForeground(new Color(200, 200, 200));
        userRole.setAlignmentX(Component.CENTER_ALIGNMENT);

        userPanel.add(userIcon, BorderLayout.NORTH);
        userPanel.add(userName, BorderLayout.CENTER);
        userPanel.add(userRole, BorderLayout.SOUTH);

        sidebar.add(userPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));


        String[] menuItems = {"Dashboard", "Guests", "Reservations", "Rooms", "Services", "Billing", "Reports"};
        String[] icons = {"📊", "👥", "📅", "🏠", "🛎️", "💰", "📈"};

        for (int i = 0; i < menuItems.length; i++) {
            JButton btn = createMenuButton(icons[i] + " " + menuItems[i], menuItems[i]);
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        sidebar.add(Box.createVerticalGlue());


        JButton logoutBtn = new JButton("🚪 Logout");
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(DANGER_COLOR);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setMaximumSize(new Dimension(220, 45));
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?", "Logout",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                hotelSystem.getDatabase().closeConnection();
                this.dispose();
                SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
            }
        });

        logoutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutBtn.setBackground(DANGER_COLOR.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutBtn.setBackground(DANGER_COLOR);
            }
        });

        sidebar.add(logoutBtn);

        return sidebar;
    }

    private JButton createMenuButton(String text, String panelName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.PLAIN, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(SECONDARY_COLOR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            cardLayout.show(mainPanel, panelName);

            switch (panelName) {
                case "Dashboard":
                    refreshDashboard();
                    break;
                case "Guests":
                    refreshGuestTable();
                    break;
                case "Reservations":

                    break;
                case "Rooms":
                    refreshRoomTable();
                    break;
                case "Services":
                    refreshServiceTable();
                    break;
                case "Billing":
                    refreshBillingTable();
                    break;
            }
        });

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(SECONDARY_COLOR.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(SECONDARY_COLOR);
            }
        });

        return btn;
    }
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));


        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);

        JLabel title = new JLabel("Dashboard Overview");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(TEXT_COLOR);

        JButton refreshBtn = createStyledButton("🔄 Refresh Dashboard", SECONDARY_COLOR);
        refreshBtn.addActionListener(e -> refreshDashboard());

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        statsPanel.setBackground(BACKGROUND_COLOR);
        statsPanel.setName("statsPanel"); // Set name for identification


        statsPanel.add(createStatCard("Total Guests", "0", new Color(52, 152, 219), "👥"));
        statsPanel.add(createStatCard("Available Rooms", "0/0", new Color(46, 204, 113), "🏠"));
        statsPanel.add(createStatCard("Active Reservations", "0", new Color(230, 126, 34), "📅"));
        statsPanel.add(createStatCard("Total Reservations", "0", new Color(155, 89, 182), "📋"));
        statsPanel.add(createStatCard("Total Revenue", "$0.00", new Color(241, 196, 15), "💰"));
        statsPanel.add(createStatCard("Service Requests", "0", new Color(231, 76, 60), "🛎️"));

        panel.add(statsPanel, BorderLayout.CENTER);

        refreshDashboard();

        return panel;
    }

    private void refreshDashboard() {
        Component[] components = mainPanel.getComponents();
        for (Component panel : components) {
            if (panel instanceof JPanel) {
                Component[] children = ((JPanel) panel).getComponents();
                for (Component child : children) {
                    if (child instanceof JPanel && "statsPanel".equals(child.getName())) {
                        JPanel statsPanel = (JPanel) child;
                        statsPanel.removeAll();

                        int totalGuests = hotelSystem.getTotalGuests();
                        int availableRooms = hotelSystem.getAvailableRooms();
                        int totalRooms = hotelSystem.getTotalRooms();
                        int activeReservations = hotelSystem.getActiveReservations();
                        int totalReservations = hotelSystem.getTotalReservations();
                        double totalRevenue = hotelSystem.getTotalRevenue();
                        int serviceRequests = hotelSystem.getTotalServiceRequests();
                        double collectedRevenue = hotelSystem.getCollectedRevenue();

                        double occupancyRate = totalRooms > 0 ?
                                ((double)(totalRooms - availableRooms) / totalRooms) * 100 : 0;

                        statsPanel.add(createStatCard("Total Guests",
                                String.valueOf(totalGuests),
                                new Color(52, 152, 219), "👥"));

                        statsPanel.add(createStatCard("Available Rooms",
                                availableRooms + "/" + totalRooms,
                                availableRooms > 0 ? new Color(46, 204, 113) : DANGER_COLOR,
                                "🏠"));

                        statsPanel.add(createStatCard("Active Reservations",
                                String.valueOf(activeReservations),
                                activeReservations > 0 ? WARNING_COLOR : SECONDARY_COLOR,
                                "📅"));

                        statsPanel.add(createStatCard("Total Reservations",
                                String.valueOf(totalReservations),
                                new Color(155, 89, 182), "📋"));

                        statsPanel.add(createStatCard("Total Revenue",
                                "$" + String.format("%.2f", totalRevenue),
                                totalRevenue > 0 ? new Color(241, 196, 15) : SECONDARY_COLOR,
                                "💰"));

                        statsPanel.add(createStatCard("Service Requests",
                                String.valueOf(serviceRequests),
                                serviceRequests > 0 ? new Color(231, 76, 60) : SECONDARY_COLOR,
                                "🛎️"));

                        statsPanel.revalidate();
                        statsPanel.repaint();
                        return;
                    }
                }
            }
        }


        System.err.println("Warning: statsPanel not found!");
    }

    private JPanel createStatCard(String title, String value, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 5, 1, 1, color),
                new EmptyBorder(20, 15, 20, 15)
        ));


        JPanel iconPanel = new JPanel();
        iconPanel.setBackground(Color.WHITE);
        iconPanel.setLayout(new BorderLayout());

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setForeground(color);
        iconPanel.add(iconLabel, BorderLayout.CENTER);


        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setBackground(Color.WHITE);
        textPanel.setBorder(new EmptyBorder(0, 15, 0, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.GRAY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(color);

        textPanel.add(titleLabel);
        textPanel.add(valueLabel);

        card.add(iconPanel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(2, 7, 2, 2, color.brighter()),
                        new EmptyBorder(18, 13, 18, 13)
                ));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 5, 1, 1, color),
                        new EmptyBorder(20, 15, 20, 15)
                ));
            }
        });

        return card;
    }

    private JPanel createGuestPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));


        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BACKGROUND_COLOR);

        JLabel title = new JLabel("Guest Management");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(TEXT_COLOR);
        titlePanel.add(title, BorderLayout.WEST);

        JButton refreshButton = createStyledButton("🔄 Refresh", SECONDARY_COLOR);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setPreferredSize(new Dimension(120, 35));
        titlePanel.add(refreshButton, BorderLayout.EAST);

        panel.add(titlePanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Add New Guest"),
                new EmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField addressField = new JTextField();

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        nameField.setPreferredSize(new Dimension(250, 30));
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Contact:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        contactField.setPreferredSize(new Dimension(250, 30));
        contactField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(contactField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Address:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        addressField.setPreferredSize(new Dimension(250, 30));
        addressField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(addressField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0;

        JButton addButton = createStyledButton("➕ Add Guest", ACCENT_COLOR);
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setPreferredSize(new Dimension(150, 40));
        formPanel.add(addButton, gbc);

        formPanel.setPreferredSize(new Dimension(panel.getWidth(), 220));
        panel.add(formPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(BACKGROUND_COLOR);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        String[] columns = {"Guest ID", "Name", "Contact", "Address"};
        guestTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(guestTableModel);
        styleTable(table);

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Registered Guests"));

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(tablePanel, BorderLayout.CENTER);

        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            String address = addressField.getText().trim();

            if (name.isEmpty() || contact.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Guest guest = hotelSystem.addGuest(name, contact, address);
            if (guest != null) {
                guestTableModel.addRow(new Object[]{
                        guest.getGuestID(),
                        guest.getName(),
                        guest.getContactInfo(),
                        guest.getAddress()
                });

                nameField.setText("");
                contactField.setText("");
                addressField.setText("");

                JOptionPane.showMessageDialog(this,
                        "<html><div style='width: 250px;'>" +
                                "<h3 style='color: green;'>✅ Guest Added!</h3>" +
                                "<b>Guest ID:</b> " + guest.getGuestID() + "<br>" +
                                "<b>Name:</b> " + guest.getName() +
                                "</div></html>",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                refreshDashboard();
                refreshButton.doClick(); // Refresh the combo boxes in reservation panel
            }
        });

        refreshButton.addActionListener(e -> refreshGuestTable());

        refreshGuestTable();
        return panel;
    }

    private void refreshGuestTable() {
        guestTableModel.setRowCount(0);
        for (Guest guest : hotelSystem.getGuestList().getAllGuests()) {
            guestTableModel.addRow(new Object[]{
                    guest.getGuestID(),
                    guest.getName(),
                    guest.getContactInfo(),
                    guest.getAddress()
            });
        }
    }

    private JPanel createReservationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));


        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BACKGROUND_COLOR);

        JLabel title = new JLabel("Reservation Management");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(TEXT_COLOR);
        titlePanel.add(title, BorderLayout.WEST);

        JButton refreshButton = createStyledButton("🔄 Refresh", SECONDARY_COLOR);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setPreferredSize(new Dimension(120, 35));
        titlePanel.add(refreshButton, BorderLayout.EAST);

        panel.add(titlePanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Create New Reservation"),
                new EmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> guestCombo = new JComboBox<>();
        JComboBox<String> roomCombo = new JComboBox<>();
        JTextField checkInField = new JTextField(LocalDate.now().toString());
        JTextField checkOutField = new JTextField(LocalDate.now().plusDays(1).toString());

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel guestLabel = new JLabel("Guest:");
        guestLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(guestLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        guestCombo.setPreferredSize(new Dimension(250, 30));
        guestCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(guestCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        JLabel roomLabel = new JLabel("Room:");
        roomLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(roomLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        roomCombo.setPreferredSize(new Dimension(250, 30));
        roomCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(roomCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        JLabel checkInLabel = new JLabel("Check-In Date:");
        checkInLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(checkInLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        checkInField.setPreferredSize(new Dimension(150, 30));
        checkInField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(checkInField, gbc);

        gbc.gridx = 2; gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        JButton todayBtn = new JButton("Today");
        todayBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        todayBtn.setBackground(SECONDARY_COLOR);
        todayBtn.setForeground(Color.WHITE);
        todayBtn.setFocusPainted(false);
        todayBtn.setPreferredSize(new Dimension(80, 25));
        todayBtn.addActionListener(e -> checkInField.setText(LocalDate.now().toString()));
        formPanel.add(todayBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 1;
        JLabel checkOutLabel = new JLabel("Check-Out Date:");
        checkOutLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(checkOutLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        checkOutField.setPreferredSize(new Dimension(150, 30));
        checkOutField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(checkOutField, gbc);

        gbc.gridx = 2; gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        JButton tomorrowBtn = new JButton("Tomorrow");
        tomorrowBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        tomorrowBtn.setBackground(SECONDARY_COLOR);
        tomorrowBtn.setForeground(Color.WHITE);
        tomorrowBtn.setFocusPainted(false);
        tomorrowBtn.setPreferredSize(new Dimension(80, 25));
        tomorrowBtn.addActionListener(e -> checkOutField.setText(LocalDate.now().plusDays(1).toString()));
        formPanel.add(tomorrowBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0;

        JButton bookButton = createStyledButton("📅 Book Reservation", ACCENT_COLOR);
        bookButton.setFont(new Font("Arial", Font.BOLD, 14));
        bookButton.setPreferredSize(new Dimension(200, 40));
        formPanel.add(bookButton, gbc);

        formPanel.setPreferredSize(new Dimension(panel.getWidth(), 250));
        panel.add(formPanel, BorderLayout.NORTH);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(BACKGROUND_COLOR);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        String[] columns = {"ID", "Guest", "Room", "Check-In", "Check-Out", "Nights", "Status", "Actions"};
        reservationTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only Actions column is editable
            }
        };

        JTable table = new JTable(reservationTableModel);
        styleTable(table);

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(70);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);
        table.getColumnModel().getColumn(7).setPreferredWidth(80);

        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ReservationButtonEditor(new JCheckBox(), table));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("All Reservations"));

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(tablePanel, BorderLayout.CENTER);

        refreshButton.addActionListener(e -> {
            guestCombo.removeAllItems();
            roomCombo.removeAllItems();

            for (Guest guest : hotelSystem.getGuestList().getAllGuests()) {
                guestCombo.addItem(guest.getGuestID() + " - " + guest.getName());
            }

            for (Room room : hotelSystem.getRoomList()) {
                if (room.isAvailable()) {
                    roomCombo.addItem(room.getRoomID() + " - " + room.getRoomType() +
                            " ($" + room.getPricePerNight() + "/night)");
                }
            }

            if (guestCombo.getItemCount() == 0) {
                guestCombo.addItem("No guests available");
                guestCombo.setEnabled(false);
            } else {
                guestCombo.setEnabled(true);
            }

            if (roomCombo.getItemCount() == 0) {
                roomCombo.addItem("No available rooms");
                roomCombo.setEnabled(false);
            } else {
                roomCombo.setEnabled(true);
            }

            refreshReservationTable();
        });

        bookButton.addActionListener(e -> {
            if (guestCombo.getSelectedItem() == null ||
                    guestCombo.getSelectedItem().toString().contains("No guests")) {
                JOptionPane.showMessageDialog(this,
                        "Please add guests first!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (roomCombo.getSelectedItem() == null ||
                    roomCombo.getSelectedItem().toString().contains("No available")) {
                JOptionPane.showMessageDialog(this,
                        "No rooms available!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!guestCombo.isEnabled() || !roomCombo.isEnabled()) {
                JOptionPane.showMessageDialog(this,
                        "Please refresh the data first!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String guestID = guestCombo.getSelectedItem().toString().split(" - ")[0];
                String roomID = roomCombo.getSelectedItem().toString().split(" - ")[0];
                LocalDate checkIn = LocalDate.parse(checkInField.getText());
                LocalDate checkOut = LocalDate.parse(checkOutField.getText());

                Reservation res = hotelSystem.makeReservation(guestID, roomID, checkIn, checkOut);
                if (res != null) {
                    Room room = findRoomByID(roomID);
                    double totalCost = room.getPricePerNight() * res.getNumberOfNights();

                    JOptionPane.showMessageDialog(this,
                            "<html><div style='width: 300px;'>" +
                                    "<h3 style='color: green;'>✅ Reservation Created!</h3>" +
                                    "<b>Reservation ID:</b> " + res.getReservationID() + "<br>" +
                                    "<b>Guest:</b> " + guestID + "<br>" +
                                    "<b>Room:</b> " + roomID + "<br>" +
                                    "<b>Check-In:</b> " + checkIn + "<br>" +
                                    "<b>Check-Out:</b> " + checkOut + "<br>" +
                                    "<b>Nights:</b> " + res.getNumberOfNights() + "<br>" +
                                    "<b>Total Cost:</b> $" + String.format("%.2f", totalCost) +
                                    "</div></html>",
                            "Success", JOptionPane.INFORMATION_MESSAGE);

                    refreshButton.doClick();
                    refreshDashboard();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "❌ Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshButton.doClick();

        return panel;
    }

    private void refreshReservationTable() {
        reservationTableModel.setRowCount(0);
        for (Reservation res : hotelSystem.getReservationList()) {
            reservationTableModel.addRow(new Object[]{
                    res.getReservationID(),
                    res.getGuestID(),
                    res.getRoomID(),
                    res.getCheckInDate(),
                    res.getCheckOutDate(),
                    res.getNumberOfNights(),
                    res.getStatus(),
                    "Edit"
            });
        }
    }

    private JPanel createRoomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));


        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BACKGROUND_COLOR);

        JLabel title = new JLabel("Habiba Osama");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(TEXT_COLOR);
        titlePanel.add(title, BorderLayout.WEST);

        JButton refreshButton = createStyledButton("🔄 Refresh", SECONDARY_COLOR);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setPreferredSize(new Dimension(120, 35));
        refreshButton.addActionListener(e -> refreshRoomTable());
        titlePanel.add(refreshButton, BorderLayout.EAST);

        panel.add(titlePanel, BorderLayout.NORTH);


        String[] columns = {"Room ID", "Type", "Capacity", "Price/Night", "Status"};
        roomTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(roomTableModel);
        styleTable(table);


        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("All Rooms"));

        panel.add(scrollPane, BorderLayout.CENTER);

        refreshRoomTable();
        return panel;
    }

    private void refreshRoomTable() {
        roomTableModel.setRowCount(0);
        for (Room room : hotelSystem.getRoomList()) {
            String status = room.isAvailable() ? "✅ Available" : "❌ Occupied";
            String availability = room.isAvailable() ? "🟢" : "🔴";

            roomTableModel.addRow(new Object[]{
                    room.getRoomID(),
                    room.getRoomType(),
                    room.getCapacity(),
                    "$" + String.format("%.2f", room.getPricePerNight()),
                    status,
                    availability
            });
        }
    }

    private JPanel createServicePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Service Requests");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        panel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JComboBox<String> guestCombo = new JComboBox<>();
        JComboBox<String> serviceCombo = new JComboBox<>(new String[]{
                "Room Service", "Laundry", "Maintenance",
                "Housekeeping", "Wake-up Call", "Extra Towels",
                "Mini-bar Refill", "Transportation", "Spa"
        });

        formPanel.add(new JLabel("Guest:"));
        formPanel.add(guestCombo);
        formPanel.add(new JLabel("Service:"));
        formPanel.add(serviceCombo);

        JButton addButton = createStyledButton("➕ Add Request", ACCENT_COLOR);
        JButton refreshButton = createStyledButton("🔄 Refresh", SECONDARY_COLOR);

        formPanel.add(addButton);
        formPanel.add(refreshButton);
        panel.add(formPanel, BorderLayout.NORTH);


        String[] columns = {"ID", "Guest", "Service", "Date", "Status", "Actions"};
        serviceTableModel = new DefaultTableModel(columns, 0);

        JTable table = new JTable(serviceTableModel);
        styleTable(table);
        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ServiceButtonEditor(new JCheckBox(), table));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Service Requests"));

        panel.add(scrollPane, BorderLayout.CENTER);


        refreshButton.addActionListener(e -> {
            guestCombo.removeAllItems();
            for (Guest guest : hotelSystem.getGuestList().getAllGuests()) {
                guestCombo.addItem(guest.getGuestID() + " - " + guest.getName());
            }
            refreshServiceTable();
        });

        addButton.addActionListener(e -> {
            if (guestCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Please select a guest!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String guestID = guestCombo.getSelectedItem().toString().split(" - ")[0];
            ServiceRequest req = hotelSystem.addServiceRequest(guestID, serviceCombo.getSelectedItem().toString());

            if (req != null) {
                JOptionPane.showMessageDialog(this,
                        "Service request added!\nRequest ID: " + req.getRequestID(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                refreshServiceTable();
                refreshDashboard();
            }
        });

        refreshButton.doClick();
        return panel;
    }

    private void refreshServiceTable() {
        serviceTableModel.setRowCount(0);
        for (ServiceRequest req : hotelSystem.getServiceRequests()) {
            String statusIcon = "⏳";
            if (req.getStatus().equals("In Progress")) statusIcon = "🔄";
            else if (req.getStatus().equals("Completed")) statusIcon = "✅";

            serviceTableModel.addRow(new Object[]{
                    req.getRequestID(),
                    req.getGuestID(),
                    req.getServiceType(),
                    req.getRequestDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    statusIcon + " " + req.getStatus(),
                    "Edit"
            });
        }
    }

    private JPanel createBillingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Billing & Payments");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        panel.add(title, BorderLayout.NORTH);

        String[] columns = {"Bill ID", "Guest", "Total", "Paid", "Remaining", "Status", "Actions"};
        billingTableModel = new DefaultTableModel(columns, 0);

        JTable table = new JTable(billingTableModel);
        styleTable(table);
        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(new BillingButtonEditor(new JCheckBox(), table));

        JButton refreshButton = createStyledButton("🔄 Refresh", SECONDARY_COLOR);
        refreshButton.addActionListener(e -> refreshBillingTable());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(refreshButton);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Billing Records"));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(buttonPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(centerPanel, BorderLayout.CENTER);

        refreshBillingTable();
        return panel;
    }

    private void refreshBillingTable() {
        billingTableModel.setRowCount(0);
        for (Billing bill : hotelSystem.getBillingRecords()) {
            double totalPaid = bill.getPaymentHistory().stream().mapToDouble(Double::doubleValue).sum();
            double remaining = bill.getRemainingBalance();

            String status = bill.getPaymentStatus();
            String statusIcon = "💳";
            if (status.contains("Fully Paid")) statusIcon = "✅";
            else if (status.contains("Partially Paid")) statusIcon = "⚠️";
            else if (status.contains("Unpaid")) statusIcon = "❌";

            billingTableModel.addRow(new Object[]{
                    bill.getBillID(),
                    bill.getGuestID(),
                    "$" + String.format("%.2f", bill.getTotalAmount()),
                    "$" + String.format("%.2f", totalPaid),
                    "$" + String.format("%.2f", remaining),
                    statusIcon + " " + status,
                    "Pay"
            });
        }
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Reports");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        panel.add(title, BorderLayout.NORTH);

        JPanel reportPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        reportPanel.setBackground(Color.WHITE);
        reportPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton reservationReportBtn = createStyledButton("📋 Reservation Report", SECONDARY_COLOR);
        JButton revenueReportBtn = createStyledButton("💰 Revenue Report", new Color(39, 174, 96));
        JButton occupancyReportBtn = createStyledButton("🏠 Occupancy Report", WARNING_COLOR);

        JTextArea reportArea = new JTextArea(20, 50);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportArea.setEditable(false);
        reportArea.setBorder(BorderFactory.createTitledBorder("Report Output"));

        JScrollPane scrollPane = new JScrollPane(reportArea);

        reservationReportBtn.addActionListener(e -> {
            String report = hotelSystem.generateReports("reservation");
            reportArea.setText(report);
        });

        revenueReportBtn.addActionListener(e -> {
            String report = hotelSystem.generateReports("revenue");
            reportArea.setText(report);
        });

        occupancyReportBtn.addActionListener(e -> {
            String report = hotelSystem.generateReports("occupancy");
            reportArea.setText(report);
        });

        reportPanel.add(reservationReportBtn);
        reportPanel.add(revenueReportBtn);
        reportPanel.add(occupancyReportBtn);

        panel.add(reportPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(color);
            }
        });

        return btn;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(245, 245, 245));
                    }
                }

                if (column == 0 || column == 2 || column == 5 || column == 7) {
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                } else if (column == 3 || column == 4) {
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                } else if (column == 6) {
                    String status = value.toString();
                    if (status.contains("Active")) {
                        c.setForeground(new Color(39, 174, 96));
                    } else if (status.contains("Completed")) {
                        c.setForeground(new Color(52, 152, 219));
                    } else if (status.contains("Cancelled")) {
                        c.setForeground(DANGER_COLOR);
                    }
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                }

                return c;
            }
        });

        table.setSelectionBackground(SECONDARY_COLOR.brighter());
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(220, 220, 220));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
    }
    private Room findRoomByID(String roomID) {
        for (Room room : hotelSystem.getRoomList()) {
            if (room.getRoomID().equals(roomID)) {
                return room;
            }
        }
        return null;
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            setBackground(SECONDARY_COLOR);
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 12));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            return this;
        }
    }

    class ReservationButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private JTable table;

        public ReservationButtonEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);
            this.table = table;
            button = new JButton();
            button.addActionListener(e -> {
                int row = table.getSelectedRow();
                String resID = table.getValueAt(row, 0).toString();
                String currentStatus = table.getValueAt(row, 6).toString().replaceAll("[^\\w\\s]", "").trim();

                String[] options = {"Update Status", "Cancel Reservation", "View Details"};
                int choice = JOptionPane.showOptionDialog(null,
                        "Reservation: " + resID + "\nCurrent Status: " + currentStatus,
                        "Reservation Actions",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, options, options[0]);

                if (choice == 0) {
                    String[] statuses = {"Active", "Completed", "Cancelled"};
                    String newStatus = (String) JOptionPane.showInputDialog(null,
                            "Select new status:", "Update Status",
                            JOptionPane.QUESTION_MESSAGE, null, statuses, currentStatus);

                    if (newStatus != null && !newStatus.equals(currentStatus)) {
                        hotelSystem.updateReservationStatus(resID, newStatus);
                        refreshReservationTable();
                        refreshDashboard();
                        JOptionPane.showMessageDialog(null,
                                "Status updated to: " + newStatus, "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } else if (choice == 1) {
                    int confirm = JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to cancel this reservation?",
                            "Confirm Cancellation", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        hotelSystem.cancelReservation(resID);
                        refreshReservationTable();
                        refreshDashboard();
                    }
                } else if (choice == 2) {
                    Reservation res = hotelSystem.getReservationList().stream()
                            .filter(r -> r.getReservationID().equals(resID))
                            .findFirst()
                            .orElse(null);

                    if (res != null) {
                        JOptionPane.showMessageDialog(null,
                                "Reservation Details:\n" +
                                        "ID: " + res.getReservationID() + "\n" +
                                        "Guest: " + res.getGuestID() + "\n" +
                                        "Room: " + res.getRoomID() + "\n" +
                                        "Check-In: " + res.getCheckInDate() + "\n" +
                                        "Check-Out: " + res.getCheckOutDate() + "\n" +
                                        "Nights: " + res.getNumberOfNights() + "\n" +
                                        "Status: " + res.getStatus(),
                                "Reservation Details", JOptionPane.INFORMATION_MESSAGE);
                    }
                }

                fireEditingStopped();
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            button.setText(value.toString());
            return button;
        }

        public Object getCellEditorValue() {
            return button.getText();
        }
    }

    class ServiceButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private JTable table;

        public ServiceButtonEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);
            this.table = table;
            button = new JButton();
            button.addActionListener(e -> {
                int row = table.getSelectedRow();
                String reqID = table.getValueAt(row, 0).toString();
                String currentStatus = table.getValueAt(row, 4).toString().replaceAll("[^\\w\\s]", "").trim();

                String[] statuses = {"Pending", "In Progress", "Completed"};
                String newStatus = (String) JOptionPane.showInputDialog(null,
                        "Update status for request: " + reqID, "Update Status",
                        JOptionPane.QUESTION_MESSAGE, null, statuses, currentStatus);

                if (newStatus != null && !newStatus.equals(currentStatus)) {
                    hotelSystem.updateServiceStatus(reqID, newStatus);
                    refreshServiceTable();
                    JOptionPane.showMessageDialog(null,
                            "Status updated to: " + newStatus, "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }

                fireEditingStopped();
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            button.setText(value.toString());
            return button;
        }

        public Object getCellEditorValue() {
            return button.getText();
        }
    }

    class BillingButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private JTable table;

        public BillingButtonEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);
            this.table = table;
            button = new JButton();
            button.addActionListener(e -> {
                int row = table.getSelectedRow();
                String billID = table.getValueAt(row, 0).toString();
                String guestID = table.getValueAt(row, 1).toString();
                String total = table.getValueAt(row, 2).toString().replace("$", "");
                String paid = table.getValueAt(row, 3).toString().replace("$", "");
                String remaining = table.getValueAt(row, 4).toString().replace("$", "");

                String message = "Bill ID: " + billID + "\n" +
                        "Guest: " + guestID + "\n" +
                        "Total Amount: $" + total + "\n" +
                        "Paid: $" + paid + "\n" +
                        "Remaining: $" + remaining + "\n\n" +
                        "Enter payment amount:";

                String amountStr = JOptionPane.showInputDialog(null, message, "Process Payment", JOptionPane.QUESTION_MESSAGE);

                if (amountStr != null && !amountStr.isEmpty()) {
                    try {
                        double amount = Double.parseDouble(amountStr);
                        double remainingDbl = Double.parseDouble(remaining);

                        if (amount <= 0) {
                            JOptionPane.showMessageDialog(null, "Amount must be positive!", "Error", JOptionPane.ERROR_MESSAGE);
                        } else if (amount > remainingDbl) {
                            int confirm = JOptionPane.showConfirmDialog(null,
                                    "Payment amount ($" + amount + ") exceeds remaining balance ($" + remaining + ").\n" +
                                            "Do you want to process an overpayment?", "Confirm Overpayment",
                                    JOptionPane.YES_NO_OPTION);
                            if (confirm == JOptionPane.YES_OPTION) {
                                hotelSystem.processPayment(billID, amount);
                                refreshBillingTable();
                                JOptionPane.showMessageDialog(null, "Payment processed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } else {
                            hotelSystem.processPayment(billID, amount);
                            refreshBillingTable();
                            JOptionPane.showMessageDialog(null, "Payment of $" + amount + " processed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid amount! Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

                fireEditingStopped();
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            button.setText(value.toString());
            return button;
        }

        public Object getCellEditorValue() {
            return button.getText();
        }
    }
}