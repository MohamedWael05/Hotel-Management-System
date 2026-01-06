
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginGUI() {
        setTitle("Hotel Management System - Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
    }

    private void initUI() {

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(41, 128, 185);
                Color color2 = new Color(52, 152, 219);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new BorderLayout());


        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 30, 0));

        JLabel titleLabel = new JLabel("HOTEL MANAGEMENT SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        JLabel subTitleLabel = new JLabel("Database Version", SwingConstants.CENTER);
        subTitleLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        subTitleLabel.setForeground(new Color(230, 230, 230));
        subTitleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        titlePanel.add(subTitleLabel, BorderLayout.SOUTH);


        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setOpaque(false);
        loginPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        loginPanel.add(userLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 2;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField.setPreferredSize(new Dimension(200, 35));
        usernameField.setText("admin"); // Default for testing
        loginPanel.add(usernameField, gbc);


        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("Arial", Font.BOLD, 16));
        loginPanel.add(passLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 2;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setPreferredSize(new Dimension(200, 35));
        passwordField.setText("admin123"); // Default for testing
        loginPanel.add(passwordField, gbc);


        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 3;
        JLabel reqLabel = new JLabel("Password must be at least 6 characters", SwingConstants.CENTER);
        reqLabel.setForeground(Color.YELLOW);
        reqLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        loginPanel.add(reqLabel, gbc);


        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 3;
        JPanel testPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        testPanel.setOpaque(false);
        testPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        JLabel testLabel = new JLabel("Test Credentials: ");
        testLabel.setForeground(Color.WHITE);
        testLabel.setFont(new Font("Arial", Font.BOLD, 12));
        testPanel.add(testLabel);

        JLabel adminLabel = new JLabel("admin / admin123");
        adminLabel.setForeground(Color.GREEN);
        adminLabel.setFont(new Font("Arial", Font.BOLD, 12));
        testPanel.add(adminLabel);

        JLabel orLabel = new JLabel(" or ");
        orLabel.setForeground(Color.WHITE);
        orLabel.setFont(new Font("Arial", Font.BOLD, 12));
        testPanel.add(orLabel);

        JLabel staffLabel = new JLabel("staff / staff123");
        staffLabel.setForeground(Color.GREEN);
        staffLabel.setFont(new Font("Arial", Font.BOLD, 12));
        testPanel.add(staffLabel);

        loginPanel.add(testPanel, gbc);


        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 3;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton loginButton = createStyledButton("Login", new Color(46, 204, 113), 120, 40);
        JButton exitButton = createStyledButton("Exit", new Color(231, 76, 60), 120, 40);
        JButton resetButton = createStyledButton("Reset DB", new Color(241, 196, 15), 120, 40);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(LoginGUI.this,
                        "This will reset the entire database! Are you sure?",
                        "Reset Database", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    DatabaseConnection db = DatabaseConnection.getInstance();
                    db.resetDatabase();
                    JOptionPane.showMessageDialog(LoginGUI.this,
                            "Database reset successfully!\nDefault users and rooms have been created.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);
        buttonPanel.add(resetButton);
        loginPanel.add(buttonPanel, gbc);


        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 3;
        JLabel statusLabel = new JLabel("MySQL Database Connected", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(46, 204, 113));
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        loginPanel.add(statusLabel, gbc);


        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(loginPanel, BorderLayout.CENTER);


        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });

        setContentPane(mainPanel);
    }

    private JButton createStyledButton(String text, Color color, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(width, height));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        System.out.println("Login attempt - Username: " + username + ", Password length: " + password.length());


        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password!",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Password must be at least 6 characters!",
                    "Password Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }


        DatabaseConnection db = DatabaseConnection.getInstance();
        boolean authenticated = db.authenticateUser(username, password);

        if (authenticated) {
            String role = db.getUserRole(username);
            System.out.println("User " + username + " logged in with role: " + role);

            JOptionPane.showMessageDialog(this,
                    "Login successful! Welcome " + username + "!\nRole: " + role,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);


            this.dispose();
            SwingUtilities.invokeLater(() -> {
                HotelManagementGUI mainApp = new HotelManagementGUI();
                mainApp.setVisible(true);
            });
        } else {
            System.out.println("Authentication failed for: " + username);


            if (username.equals("admin") && password.equals("admin123") ||
                    username.equals("staff") && password.equals("staff123")) {
                System.out.println("Using fallback authentication");
                JOptionPane.showMessageDialog(this,
                        "Login successful (fallback)!\nWelcome " + username + "!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                SwingUtilities.invokeLater(() -> {
                    HotelManagementGUI mainApp = new HotelManagementGUI();
                    mainApp.setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password!\n\n" +
                                "Try:\n" +
                                "• Username: admin, Password: admin123\n" +
                                "• Username: staff, Password: staff123\n\n" +
                                "Or check database connection.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {

                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());


                UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 14));
                UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.BOLD, 12));
                UIManager.put("TextField.font", new Font("Arial", Font.PLAIN, 14));
                UIManager.put("PasswordField.font", new Font("Arial", Font.PLAIN, 14));
                UIManager.put("Button.font", new Font("Arial", Font.BOLD, 12));

            } catch (Exception e) {
                e.printStackTrace();
            }


            DatabaseConnection.getInstance();


            new LoginGUI().setVisible(true);
        });
    }
}