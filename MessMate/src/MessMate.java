import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class MessMate extends JFrame {
    private static final int OPEN = 11 * 60 + 30, CLOSE = 14 * 60, SERVICE = 10, STEP = 5;
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Student", "Available From", "Available Until"}, 0);
    private final JTable table = new JTable(model);
    private final JTextField name = new JTextField(10);
    private final JTextField from = new JTextField("11:30", 5);
    private final JTextField until = new JTextField("13:00", 5);
    private final JTextArea output = new JTextArea(8, 48);

    static class Student {
        String name;
        int from, until;

        Student(String name, int from, int until) {
            this.name = name;
            this.from = from;
            this.until = until;
        }
    }

    static class Result {
        int served;
        List<String> assignments = new ArrayList<>();
    }

    private static int minutes(String value) {
        LocalTime t = LocalTime.parse(value.trim(), TIME);
        return t.getHour() * 60 + t.getMinute();
    }

    static Result optimize(List<Student> students) {
        List<Integer> slots = new ArrayList<>();
        for (int t = OPEN; t + SERVICE <= CLOSE; t += STEP) slots.add(t);

        int[] owner = new int[slots.size()];
        Arrays.fill(owner, -1);
        int matched = 0;

        for (int i = 0; i < students.size(); i++) {
            if (augment(i, students, slots, owner, new boolean[slots.size()])) matched++;
        }

        Result result = new Result();
        result.served = matched;
        for (int s = 0; s < owner.length; s++) {
            if (owner[s] >= 0) {
                Student student = students.get(owner[s]);
                result.assignments.add(student.name + "  →  " + fmt(slots.get(s))
                        + " - " + fmt(slots.get(s) + SERVICE));
            }
        }
        return result;
    }

    private static boolean augment(int student, List<Student> students, List<Integer> slots,
                                   int[] owner, boolean[] seen) {
        Student current = students.get(student);
        for (int s = 0; s < slots.size(); s++) {
            int start = slots.get(s);
            if (seen[s] || start < current.from || start + SERVICE > current.until) continue;
            seen[s] = true;
            if (owner[s] == -1 || augment(owner[s], students, slots, owner, seen)) {
                owner[s] = student;
                return true;
            }
        }
        return false;
    }

    static int fcfs(List<Student> students) {
        List<Integer> slots = new ArrayList<>();
        for (int t = OPEN; t + SERVICE <= CLOSE; t += STEP) slots.add(t);
        boolean[] used = new boolean[slots.size()];
        int count = 0;

        for (Student student : students) {
            for (int s = 0; s < slots.size(); s++) {
                int start = slots.get(s);
                if (!used[s] && start >= student.from && start + SERVICE <= student.until) {
                    used[s] = true;
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    private static String fmt(int minutes) {
        return String.format("%02d:%02d", minutes / 60, minutes % 60);
    }

    private MessMate() {
        super("MessMate — College Mess Meal Scheduler");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(920, 680);
        setMinimumSize(new Dimension(780, 560));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        JLabel heading = new JLabel("MessMate | College Mess Meal Scheduler", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));

        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        inputRow.add(new JLabel("Student"));
        inputRow.add(name);
        inputRow.add(new JLabel("From (HH:mm)"));
        inputRow.add(from);
        inputRow.add(new JLabel("Until (HH:mm)"));
        inputRow.add(until);
        JButton add = new JButton("Add Student");
        inputRow.add(add);

        JButton sample = new JButton("Load Sample");
        JButton remove = new JButton("Remove Last");
        JButton run = new JButton("Optimize");
        JButton clear = new JButton("Clear");
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        actionRow.add(sample);
        actionRow.add(remove);
        actionRow.add(run);
        actionRow.add(clear);

        JPanel toolbar = new JPanel(new BorderLayout(4, 4));
        toolbar.add(heading, BorderLayout.NORTH);
        JPanel rows = new JPanel(new GridLayout(2, 1, 2, 2));
        rows.add(inputRow);
        rows.add(actionRow);
        toolbar.add(rows, BorderLayout.CENTER);
        toolbar.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));
        add(toolbar, BorderLayout.NORTH);

        table.setFillsViewportHeight(true);
        table.setRowHeight(24);
        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        output.setText("Welcome to MessMate. Add students or click Load Sample, then click Optimize.");

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(table), new JScrollPane(output));
        split.setResizeWeight(0.72);
        split.setContinuousLayout(true);
        split.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        add(split, BorderLayout.CENTER);

        add.addActionListener(e -> addStudent());
        remove.addActionListener(e -> {
            if (model.getRowCount() > 0) model.removeRow(model.getRowCount() - 1);
        });
        sample.addActionListener(e -> {
            model.setRowCount(0);
            model.addRow(new Object[]{"Aarav", "11:30", "12:00"});
            model.addRow(new Object[]{"Diya", "11:30", "11:40"});
            model.addRow(new Object[]{"Kabir", "11:40", "12:00"});
            model.addRow(new Object[]{"Meera", "12:00", "12:30"});
            output.setText("Sample students loaded. Click Optimize to calculate the schedule.");
        });
        clear.addActionListener(e -> {
            model.setRowCount(0);
            output.setText("Add students or click Load Sample, then click Optimize.");
        });
        run.addActionListener(e -> runOptimization());
    }

    private void addStudent() {
        try {
            String studentName = name.getText().trim();
            int start = minutes(from.getText());
            int end = minutes(until.getText());
            if (studentName.isEmpty() || start < OPEN || end > CLOSE || end - start < SERVICE) {
                throw new IllegalArgumentException();
            }
            model.addRow(new Object[]{studentName, fmt(start), fmt(end)});
            name.setText("");
            name.requestFocusInWindow();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Enter a name and a valid HH:mm window within 11:30–14:00 (at least 10 minutes).",
                    "Invalid input", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void runOptimization() {
        try {
            List<Student> students = new ArrayList<>();
            for (int i = 0; i < model.getRowCount(); i++) {
                students.add(new Student(
                        model.getValueAt(i, 0).toString(),
                        minutes(model.getValueAt(i, 1).toString()),
                        minutes(model.getValueAt(i, 2).toString())));
            }

            Result result = optimize(students);
            output.setText("MAXIMUM BIPARTITE MATCHING\n"
                    + "Students: " + students.size()
                    + " | Maximum served: " + result.served
                    + " | FCFS baseline: " + fcfs(students)
                    + "\n\nASSIGNED SERVICE SLOTS\n");
            for (String line : result.assignments) output.append(line + "\n");
            output.append("\nModel: one counter, 10-minute service, 5-minute slot increments, 11:30–14:00.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please check the student time values.",
                    "Input error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MessMate().setVisible(true));
    }
}
