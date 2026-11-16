import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

public class TripWise extends JFrame {
    private final DefaultTableModel model=new DefaultTableModel(new String[]{"Attraction","Duration (min)","Cost (₹)","Rating"},0);
    private final JTable table=new JTable(model);
    private final JTextField name=new JTextField(10), duration=new JTextField("60",5), cost=new JTextField("200",6), rating=new JTextField("5",4);
    private final JTextField timeLimit=new JTextField("480",5), budget=new JTextField("3000",7);
    private final JTextArea output=new JTextArea(12,50);
    static class Attraction { String name; int time,cost,rating; Attraction(String n,int t,int c,int r){name=n;time=t;cost=c;rating=r;} }
    static class Plan { int rating,time,cost; List<String> selected=new ArrayList<>(); }
    static Plan optimize(List<Attraction> items,int T,int B) {
        if(T<0||B<0||T>2000||B>200000) throw new IllegalArgumentException("Limits: time 0–2000 minutes, budget ₹0–200000.");
        if((long)(T+1)*(B+1)>2_000_000L) throw new IllegalArgumentException("Time/budget table is too large. Reduce one or both limits.");
        int n=items.size(); int[][] dp=new int[T+1][B+1]; boolean[][][] take=new boolean[n][T+1][B+1];
        for(int i=0;i<n;i++) {
            Attraction a=items.get(i);
            if(a.time<=0||a.cost<0||a.rating<0) throw new IllegalArgumentException("Duration must be positive; cost and rating cannot be negative.");
            for(int t=T;t>=a.time;t--) for(int b=B;b>=a.cost;b--) {
                int candidate=dp[t-a.time][b-a.cost]+a.rating;
                if(candidate>dp[t][b]) { dp[t][b]=candidate; take[i][t][b]=true; }
            }
        }
        Plan p=new Plan(); p.rating=dp[T][B]; int t=T,b=B;
        for(int i=n-1;i>=0;i--) if(take[i][t][b]) { Attraction a=items.get(i); p.selected.add(a.name); p.time+=a.time; p.cost+=a.cost; t-=a.time; b-=a.cost; }
        Collections.reverse(p.selected); return p;
    }
    private TripWise() {
        super("TripWise — Tourist Attraction Planner"); setDefaultCloseOperation(EXIT_ON_CLOSE); setSize(900,680); setLocationRelativeTo(null);
        setLayout(new BorderLayout(8,8));
        JLabel heading=new JLabel("TripWise | Tourist Attraction Planner",SwingConstants.CENTER); heading.setFont(new Font("SansSerif",Font.BOLD,22)); add(heading,BorderLayout.NORTH);
        JPanel controls=new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("Attraction"));controls.add(name);controls.add(new JLabel("Minutes"));controls.add(duration);controls.add(new JLabel("Cost ₹"));controls.add(cost);controls.add(new JLabel("Rating"));controls.add(rating);
        JButton add=new JButton("Add"), sample=new JButton("Load Sample"), remove=new JButton("Remove Last"), optimize=new JButton("Optimize"), clear=new JButton("Clear");
        controls.add(add);controls.add(sample);controls.add(remove);controls.add(optimize);controls.add(clear);
        JPanel limits=new JPanel(new FlowLayout(FlowLayout.LEFT)); limits.add(new JLabel("Available time (minutes)"));limits.add(timeLimit);limits.add(new JLabel("Budget (₹)"));limits.add(budget);
        JPanel north=new JPanel(new BorderLayout());north.add(controls,BorderLayout.NORTH);north.add(limits,BorderLayout.SOUTH);
        JPanel center=new JPanel(new BorderLayout(5,5));center.add(north,BorderLayout.NORTH);center.add(new JScrollPane(table),BorderLayout.CENTER);
        output.setEditable(false);output.setFont(new Font(Font.MONOSPACED,Font.PLAIN,13));center.add(new JScrollPane(output),BorderLayout.SOUTH);add(center,BorderLayout.CENTER);
        add.addActionListener(e->addItem());remove.addActionListener(e->{if(model.getRowCount()>0)model.removeRow(model.getRowCount()-1);});
        sample.addActionListener(e->{model.setRowCount(0);model.addRow(new Object[]{"Museum",120,300,8});model.addRow(new Object[]{"Fort",90,100,7});model.addRow(new Object[]{"Theme Park",240,1200,10});model.addRow(new Object[]{"Lake",60,0,5});model.addRow(new Object[]{"Gallery",75,250,6});});
        clear.addActionListener(e->{model.setRowCount(0);output.setText("");});
        optimize.addActionListener(e->runOptimization());
    }
    private void addItem() {
        try { String n=name.getText().trim();int t=Integer.parseInt(duration.getText().trim()),c=Integer.parseInt(cost.getText().trim()),r=Integer.parseInt(rating.getText().trim());
            if(n.isEmpty()||t<=0||c<0||r<0||t>2000||c>200000)throw new IllegalArgumentException();
            model.addRow(new Object[]{n,t,c,r});name.setText("");
        } catch(Exception ex){JOptionPane.showMessageDialog(this,"Enter a name, positive duration, and non-negative cost/rating.","Invalid input",JOptionPane.WARNING_MESSAGE);}
    }
    private void runOptimization() {
        try { List<Attraction> items=new ArrayList<>();for(int i=0;i<model.getRowCount();i++)items.add(new Attraction(model.getValueAt(i,0).toString(),Integer.parseInt(model.getValueAt(i,1).toString()),Integer.parseInt(model.getValueAt(i,2).toString()),Integer.parseInt(model.getValueAt(i,3).toString())));
            Plan p=optimize(items,Integer.parseInt(timeLimit.getText().trim()),Integer.parseInt(budget.getText().trim()));
            output.setText("TWO-CONSTRAINT 0/1 KNAPSACK\nMaximum total rating: "+p.rating+"\nTotal time: "+p.time+" / "+timeLimit.getText()+" minutes\nTotal cost: ₹"+p.cost+" / ₹"+budget.getText()+"\n\nSELECTED ATTRACTIONS\n");
            for(String s:p.selected)output.append("• "+s+"\n");
            if(p.selected.isEmpty())output.append("(No attractions selected)\n");
        } catch(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage()==null?"Please check all values.":ex.getMessage(),"Optimization error",JOptionPane.ERROR_MESSAGE);}
    }
    public static void main(String[] args){SwingUtilities.invokeLater(()->new TripWise().setVisible(true));}
}