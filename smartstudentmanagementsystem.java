import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class SmartStudentManagementSystem extends JFrame {

    
    JTextField idField, nameField, courseField, semField;
    JTextField m1Field, m2Field, m3Field;
    JTextArea outputArea;
    JButton reportBtn;

    
    public SmartStudentManagementSystem() {

        setTitle("Smart Student Record & Performance Management System");
        setSize(420, 520);
        setLayout(null);

        JLabel l1 = new JLabel("Student ID:");
        l1.setBounds(30, 30, 120, 25);
        add(l1);

        idField = new JTextField();
        idField.setBounds(160, 30, 180, 25);
        add(idField);

        JLabel l2 = new JLabel("Student Name:");
        l2.setBounds(30, 70, 120, 25);
        add(l2);

        nameField = new JTextField();
        nameField.setBounds(160, 70, 180, 25);
        add(nameField);

        JLabel l3 = new JLabel("Course:");
        l3.setBounds(30, 110, 120, 25);
        add(l3);

        courseField = new JTextField();
        courseField.setBounds(160, 110, 180, 25);
        add(courseField);

        JLabel l4 = new JLabel("Semester:");
        l4.setBounds(30, 150, 120, 25);
        add(l4);

        semField = new JTextField();
        semField.setBounds(160, 150, 180, 25);
        add(semField);

        JLabel l5 = new JLabel("Marks (3 Subjects):");
        l5.setBounds(30, 190, 150, 25);
        add(l5);

        m1Field = new JTextField();
        m1Field.setBounds(160, 190, 50, 25);
        add(m1Field);

        m2Field = new JTextField();
        m2Field.setBounds(220, 190, 50, 25);
        add(m2Field);

        m3Field = new JTextField();
        m3Field.setBounds(280, 190, 50, 25);
        add(m3Field);

        reportBtn = new JButton("Generate Report");
        reportBtn.setBounds(120, 240, 170, 30);
        add(reportBtn);

        outputArea = new JTextArea();
        outputArea.setBounds(40, 290, 330, 160);
        outputArea.setEditable(false);
        add(outputArea);

        reportBtn.addActionListener(e -> generateReport());

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    
    private void generateReport() {
        try {
            // Creating Student object 
            Student student = new Student(
                    Integer.parseInt(idField.getText()),
                    nameField.getText(),
                    courseField.getText(),
                    Integer.parseInt(semField.getText())
            );

            // Marks object
            Marks marks = new Marks(
                    Integer.parseInt(m1Field.getText()),
                    Integer.parseInt(m2Field.getText()),
                    Integer.parseInt(m3Field.getText())
            );

            // Performance logic
            PerformanceReport report = new PerformanceReport();
            int total = marks.getTotal();
            double avg = marks.getAverage();
            String result = report.getResult(avg);

            outputArea.setText(
                    "Student Name : " + student.name +
                    "\nCourse       : " + student.course +
                    "\nTotal Marks  : " + total +
                    "\nAverage      : " + avg +
                    "\nResult       : " + result
            );

            // Multithreading
            new Thread(() -> Database.insertStudent(student)).start();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid Input! Please check values.");
        }
    }

    // Student Class
    class Student {
        private int id;
        private String name;
        private String course;
        private int semester;

        Student(int id, String name, String course, int semester) {
            this.id = id;
            this.name = name;
            this.course = course;
            this.semester = semester;
        }
    }

    // Marks Class
    class Marks {
        private int m1, m2, m3;

        Marks(int m1, int m2, int m3) {
            this.m1 = m1;
            this.m2 = m2;
            this.m3 = m3;
        }

        int getTotal() {
            return m1 + m2 + m3;
        }

        double getAverage() {
            return getTotal() / 3.0;
        }
    }

    // Performance Report Class
    class PerformanceReport {
        String getResult(double avg) {
            if (avg >= 75)
                return "Distinction";
            else if (avg >= 50)
                return "Pass";
            else
                return "Fail";
        }
    }

    // Database Class
    static class Database {
        static void insertStudent(Student s) {
            try {
                Connection con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/studentdb",
                        "root",
                        "password"
                );

                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO student VALUES (?,?,?,?)"
                );

                ps.setInt(1, s.id);
                ps.setString(2, s.name);
                ps.setString(3, s.course);
                ps.setInt(4, s.semester);

                ps.executeUpdate();
                con.close();
            } catch (Exception e) {
                System.out.println("Database Error");
            }
        }
    }

    // Main Method
    public static void main(String[] args) {
        new SmartStudentManagementSystem();
    }
}
