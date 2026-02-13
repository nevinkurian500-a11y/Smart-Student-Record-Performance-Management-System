import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class StudentManagementSystem extends JFrame {

    private final JTextField idField;
    private final JTextField nameField;
    private final JTextField courseField;
    private final JTextField semField;

    private final JButton addButton;
    private final JButton viewButton;
    private final JButton clearButton;

    // SQLite Database
    private static final String DB_URL = "jdbc:sqlite:studentdb.db";

    // Constructor
    public StudentManagementSystem() {

        setTitle("Smart Student Management System (SQLite)");
        setSize(450, 400);
        setLayout(new GridLayout(7, 2, 10, 10));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize Fields
        idField = new JTextField();
        nameField = new JTextField();
        courseField = new JTextField();
        semField = new JTextField();

        // Initialize Buttons
        addButton = new JButton("Add Student");
        viewButton = new JButton("View Students");
        clearButton = new JButton("Clear Fields");

        // Add Components
        add(new JLabel("  Student ID:"));
        add(idField);

        add(new JLabel("  Name:"));
        add(nameField);

        add(new JLabel("  Course:"));
        add(courseField);

        add(new JLabel("  Semester:"));
        add(semField);

        add(addButton);
        add(viewButton);
        add(clearButton);
        add(new JLabel(""));

        // Button Actions
        addButton.addActionListener(e -> addStudent());
        viewButton.addActionListener(e -> viewStudents());
        clearButton.addActionListener(e -> clearFields());

        createTable(); // Ensure table exists

        setVisible(true);
    }

    // Create Table
    private void createTable() {
        try (Connection con = DriverManager.getConnection(DB_URL);
             Statement st = con.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS student (" +
                    "id INTEGER PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "course TEXT NOT NULL, " +
                    "semester INTEGER NOT NULL)";
            st.execute(sql);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Database Error: " + e.getMessage());
        }
    }

    // Add Student
    private void addStudent() {

        if (idField.getText().isEmpty() ||
                nameField.getText().isEmpty() ||
                courseField.getText().isEmpty() ||
                semField.getText().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields.");
            return;
        }

        try (Connection con = DriverManager.getConnection(DB_URL)) {

            String sql = "INSERT INTO student (id, name, course, semester) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, Integer.parseInt(idField.getText()));
            ps.setString(2, nameField.getText());
            ps.setString(3, courseField.getText());
            ps.setInt(4, Integer.parseInt(semField.getText()));

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Student Added Successfully!");
            clearFields();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "ID and Semester must be numbers!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Student ID already exists!");
        }
    }

    // View Students
    private void viewStudents() {

        try (Connection con = DriverManager.getConnection(DB_URL);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM student")) {

            StringBuilder data = new StringBuilder();
            data.append("ID\tName\tCourse\tSemester\n");
            data.append("----------------------------------------------\n");

            boolean found = false;

            while (rs.next()) {
                found = true;
                data.append(rs.getInt("id")).append("\t")
                        .append(rs.getString("name")).append("\t")
                        .append(rs.getString("course")).append("\t")
                        .append(rs.getInt("semester")).append("\n");
            }

            if (!found) {
                JOptionPane.showMessageDialog(this,
                        "No records found.");
            } else {
                JTextArea textArea = new JTextArea(data.toString());
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(350, 250));

                JOptionPane.showMessageDialog(this,
                        scrollPane,
                        "Student Records",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage());
        }
    }

    // Clear Fields
    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        courseField.setText("");
        semField.setText("");
    }

    // Main Method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentManagementSystem::new);
    }
}
