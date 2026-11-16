import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MessMate extends JFrame {
    private static final int OPEN = 11 * 60 + 30, CLOSE = 14 * 60, SERVICE = 10, STEP = 5;
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"Student", "Available From", "Available Until"}, 0);
    private final JTable table = new JTable(model);
    private final JTextField name = new JTextField(10), from = new JTextField("11:30", 5), until = new JTextField("13:00", 5);
    private final JTextArea output = new JTextArea(10, 48);

    static class Student {
        String name; int from, until;
        Student(String name, int from, int until) { this.name=name; this.from=from; this.until=until; }
    }
    static class Result {
        int served; List<String> assignments = new ArrayList<>();
    }
    private static int minutes(String value) {
        LocalTime t = LocalTime.parse(value.trim(), TIME);
        return t.getHour()*60+t.getMinute();
    }
    static Result optimize(List<Student> students) {
        List<Integer> slots = new ArrayList<>();
        for (int t=OPEN; t+SERVICE<=CLOSE; t+=STEP) slots.add(t);
        int[] owner = new int[slots.size()]; Arrays.fill(owner, -1);
        int matched=0;
        for (int i=0;i<students.size();i++) if (augment(i, students, slots, owner, new boolean[slots.size()])) matched++;
        Result r = new Result(); r.served=matched;
        for (int s=0;s<owner.length;s++) if(owner[s]>=0) {
            Student st=students.get(owner[s]); r.assignments.add(st.name+"  →  "+fmt(slots.get(s))+" - "+fmt(slots.get(s)+SERVICE));
        }
        return r;
    }
    private static boolean augment(int student, List<Student> students, List<Integer> slots, int[] owner, boolean[] seen) {
        Student st=students.get(student);
        for(int s=0;s<slots.size();s++) {
            int start=slots.get(s);
            if(seen[s] || start<st.from || start+SERVICE>st.until) continue;
            seen[s]=true;
            if(owner[s]==-1 || augment(owner[s],students,slots,owner,seen)) { owner[s]=student; return true; }
        }
        return false;
    }
    static int fcfs(List<Student> students) {
        List<Integer> slots=new ArrayList<>();
        for(int t=OPEN;t+SERVICE<=CLOSE;t+=STEP) slots.add(t);
        boolean[] used=new boolean[slots.size()]; int count=0;
        for(Student st:students) for(int s=0;s<slots.size();s++) {
            int t=slots.get(s);
            if(!used[s] && t>=st.from && t+SERVICE<=st.until) { used[s]=true; count++; break; }
        }
        return count;
    }
    private static String fmt(int m) { return String.format("%02d:%02d",m/60,m%60); }

    private MessMate() {
        super("MessMate — College Mess Meal Scheduler");
        setDefaultCloseOperation(EXIT_ON_CLOSE); setSize(850,650); setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));
        JLabel heading=new JLabel("MessMate | College Mess Meal Scheduler",SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif",Font.BOLD,22)); add(heading,BorderLayout.NORTH);
        JPanel top=new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Student")); top.add(name); top.add(new JLabel("From (HH:mm)")); top.add(from);
        top.add(new JLabel("Until (HH:mm)")); top.add(until);
        JButton add=new JButton("Add Student"), sample=new JButton("Load Sample"), remove=new JButton("Remove Last"), run=new JButton("Optimize"), clear=new JButton("Clear");
        top.add(add); top.add(sample); top.add(remove); top.add(run); top.add(clear);
        JPanel center=new JPanel(new BorderLayout(5,5)); center.add(top,BorderLayout.NORTH); center.add(new JScrollPane(table),BorderLayout.CENTER);
        output.setEditable(false); output.setFont(new Font(Font.MONOSPACED,Font.PLAIN,13));
        center.add(new JScrollPane(output),BorderLayout.SOUTH); add(center,BorderLayout.CENTER);
        add.addActionListener(e->addStudent()); remove.addActionListener(e->{if(model.getRowCount()>0)model.removeRow(model.getRowCount()-1);});
        sample.addActionListener(e->{model.setRowCount(0); model.addRow(new Object[]{"Aarav","11:30","12:00"});model.addRow(new Object[]{"Diya","11:30","11:40"});model.addRow(new Object[]{"Kabir","11:40","12:00"});model.addRow(new Object[]{"Meera","12:00","12:30"});});
        clear.addActionListener(e->{model.setRowCount(0);output.setText("");});
        run.addActionListener(e->runOptimization());
    }
    private void addStudent() {
        try {
            String n=name.getText().trim(); int a=minutes(from.getText()), b=minutes(until.getText());
            if(n.isEmpty() || a<OPEN || b>CLOSE || b-a<SERVICE) throw new IllegalArgumentException();
            model.addRow(new Object[]{n,fmt(a),fmt(b)}); name.setText("");
        } catch(Exception ex) { JOptionPane.showMessageDialog(this,"Enter a name and a valid HH:mm window within 11:30–14:00 (at least 10 minutes).","Invalid input",JOptionPane.WARNING_MESSAGE); }
    }
    private void runOptimization() {
        try {
            List<Student> students=new ArrayList<>();
            for(int i=0;i<model.getRowCount();i++) students.add(new Student(model.getValueAt(i,0).toString(),minutes(model.getValueAt(i,1).toString()),minutes(model.getValueAt(i,2).toString())));
            Result r=optimize(students);
            output.setText("MAXIMUM BIPARTITE MATCHING\nStudents: "+students.size()+" | Maximum served: "+r.served+" | FCFS baseline: "+fcfs(students)+"\n\nASSIGNED SERVICE SLOTS\n");
            for(String line:r.assignments) output.append(line+"\n");
            output.append("\nModel: one counter, 10-minute service, 5-minute slot increments, 11:30–14:00.");
        } catch(Exception ex) { JOptionPane.showMessageDialog(this,"Please check the student time values.","Input error",JOptionPane.ERROR_MESSAGE); }
    }
    public static void main(String[] args) { SwingUtilities.invokeLater(()->new MessMate().setVisible(true)); }
}