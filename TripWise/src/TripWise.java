import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TripWise extends JFrame {
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Attraction", "Duration (min)", "Cost (₹)", "Rating"}, 0);
    private final JTable table = new JTable(model);
    private final JTextField name = new JTextField(10);
    private final JTextField duration = new JTextField("60", 5);
    private final JTextField cost = new JTextField("200", 6);
    private final JTextField rating = new JTextField("5", 4);
    private final JTextField timeLimit = new JTextField("480", 5);
    private final JTextField budget = new JTextField("3000", 7);
    private final JTextArea output = new JTextArea(8, 50);

    static class Attraction {
        String name;
        int time, cost, rating;

        Attraction(String name, int time, int cost, int rating) {
            this.name = name;
            this.time = time;
            this.cost = cost;
            this.rating = rating;
        }
    }

    static class Plan {
        int rating, time, cost;
        List<String> selected = new ArrayList<>();
    }

    static Plan optimize(List<Attraction> items, int totalTime, int totalBudget) {
        if (totalTime < 0 || totalBudget < 0 || totalTime > 2000 || totalBudget > 200000) {
            throw new IllegalArgumentException("Limits: time 0–2000 minutes, budget ₹0–200000.");
        }
        long cells = (long) (totalTime + 1) * (totalBudget + 1);
        if (cells > 2_000_000L) {
            throw new IllegalArgumentException("Time/budget table is too large. Reduce one or both limits.");
        }

        int n = items.size();
        if ((long) n * cells > 4_000_000L) {
            throw new IllegalArgumentException("Too many attractions for these limits. Reduce the number of attractions or the limits.");
        }

        int[][] dp = new int[totalTime + 1][totalBudget + 1];
        boolean[][][] take = new boolean[n][totalTime + 1][totalBudget + 1];

        for (int i = 0; i < n; i++) {
            Attraction attraction = items.get(i);
            if (attraction.time <= 0 || attraction.cost < 0 || attraction.rating < 0) {
                throw new IllegalArgumentException("Duration must be positive; cost and rating cannot be negative.");
            }
            for (int t = totalTime; t >= attraction.time; t--) {
                for (int b = totalBudget; b >= attraction.cost; b--) {
                    int candidate = dp[t - attraction.time][b - attraction.cost] + attraction.rating;
                    if (candidate > dp[t][b]) {
                        dp[t][b] = candidate;
                        take[i][t][b] = true;
                    }
                }
            }
        }

        Plan plan = new Plan();
        plan.rating = dp[totalTime][totalBudget];
        int t = totalTime, b = totalBudget;
        for (int i = n - 1; i >= 0; i--) {
            if (take[i][t][b]) {
                Attraction attraction = items.get(i);
                plan.selected.add(attraction.name);
                plan.time += attraction.time;
                plan.cost += attraction.cost;
                t -= attraction.time;
                b -= attraction.cost;
            }
        }
        Collections.reverse(plan.selected);
        return plan;
    }

    private TripWise() {
        super("TripWise — Tourist Attraction Planner");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(940, 690);
        setMinimumSize(new Dimension(800, 580));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        JLabel heading = new JLabel("TripWise | Tourist Attraction Planner", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));

        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        inputRow.add(new JLabel("Attraction"));
        inputRow.add(name);
        inputRow.add(new JLabel("Minutes"));
        inputRow.add(duration);
        inputRow.add(new JLabel("Cost ₹"));
        inputRow.add(cost);
        inputRow.add(new JLabel("Rating"));
        inputRow.add(rating);
        JButton add = new JButton("Add Attraction");
        inputRow.add(add);

        JPanel settingsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        settingsRow.add(new JLabel("Available time (minutes)"));
        settingsRow.add(timeLimit);
        settingsRow.add(new JLabel("Budget (₹)"));
        settingsRow.add(budget);
        JButton sample = new JButton("Load Sample");
        JButton remove = new JButton("Remove Last");
        JButton optimize = new JButton("Optimize");
        JButton clear = new JButton("Clear");
        settingsRow.add(sample);
        settingsRow.add(remove);
        settingsRow.add(optimize);
        settingsRow.add(clear);

        JPanel toolbar = new JPanel(new BorderLayout(4, 4));
        toolbar.add(heading, BorderLayout.NORTH);
        JPanel rows = new JPanel(new GridLayout(2, 1, 2, 2));
        rows.add(inputRow);
        rows.add(settingsRow);
        toolbar.add(rows, BorderLayout.CENTER);
        toolbar.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));
        add(toolbar, BorderLayout.NORTH);

        table.setFillsViewportHeight(true);
        table.setRowHeight(24);
        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        output.setText("Welcome to TripWise. Add attractions or click Load Sample, set your time and budget, then click Optimize.");

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(table), new JScrollPane(output));
        split.setResizeWeight(0.72);
        split.setContinuousLayout(true);
        split.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        add(split, BorderLayout.CENTER);

        add.addActionListener(e -> addItem());
        remove.addActionListener(e -> {
            if (model.getRowCount() > 0) model.removeRow(model.getRowCount() - 1);
        });
        sample.addActionListener(e -> {
            model.setRowCount(0);
            model.addRow(new Object[]{"Museum", 120, 300, 8});
            model.addRow(new Object[]{"Fort", 90, 100, 7});
            model.addRow(new Object[]{"Theme Park", 240, 1200, 10});
            model.addRow(new Object[]{"Lake", 60, 0, 5});
            model.addRow(new Object[]{"Gallery", 75, 250, 6});
            output.setText("Sample attractions loaded. Set your time and budget, then click Optimize.");
        });
        clear.addActionListener(e -> {
            model.setRowCount(0);
            output.setText("Add attractions or click Load Sample, set your time and budget, then click Optimize.");
        });
        optimize.addActionListener(e -> runOptimization());
    }

    private void addItem() {
        try {
            String attractionName = name.getText().trim();
            int minutes = Integer.parseInt(duration.getText().trim());
            int amount = Integer.parseInt(cost.getText().trim());
            int score = Integer.parseInt(rating.getText().trim());
            if (attractionName.isEmpty() || minutes <= 0 || amount < 0 || score < 0
                    || minutes > 2000 || amount > 200000) {
                throw new IllegalArgumentException();
            }
            model.addRow(new Object[]{attractionName, minutes, amount, score});
            name.setText("");
            name.requestFocusInWindow();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Enter a name, positive duration, and non-negative cost/rating.",
                    "Invalid input", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void runOptimization() {
        try {
            List<Attraction> items = new ArrayList<>();
            for (int i = 0; i < model.getRowCount(); i++) {
                items.add(new Attraction(
                        model.getValueAt(i, 0).toString(),
                        Integer.parseInt(model.getValueAt(i, 1).toString()),
                        Integer.parseInt(model.getValueAt(i, 2).toString()),
                        Integer.parseInt(model.getValueAt(i, 3).toString())));
            }

            Plan plan = optimize(items,
                    Integer.parseInt(timeLimit.getText().trim()),
                    Integer.parseInt(budget.getText().trim()));

            output.setText("TWO-CONSTRAINT 0/1 KNAPSACK\n"
                    + "Maximum total rating: " + plan.rating
                    + "\nTotal time: " + plan.time + " / " + timeLimit.getText() + " minutes"
                    + "\nTotal cost: ₹" + plan.cost + " / ₹" + budget.getText()
                    + "\n\nSELECTED ATTRACTIONS\n");
            for (String selected : plan.selected) output.append("• " + selected + "\n");
            if (plan.selected.isEmpty()) output.append("(No attractions selected)\n");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage() == null ? "Please check all values." : ex.getMessage(),
                    "Optimization error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TripWise().setVisible(true));
    }
}
