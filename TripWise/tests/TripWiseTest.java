import java.util.*;

public class TripWiseTest {
    public static void main(String[] args) {
        List<TripWise.Attraction> items=Arrays.asList(
            new TripWise.Attraction("A",4,4,8),
            new TripWise.Attraction("B",3,3,6),
            new TripWise.Attraction("C",2,2,5)
        );
        TripWise.Plan p=TripWise.optimize(items,5,5);
        if(p.rating!=11 || p.time!=5 || p.cost!=5) throw new AssertionError("Expected A+C (rating 13?) actual "+p.rating);
        TripWise.Plan empty=TripWise.optimize(Collections.<TripWise.Attraction>emptyList(),60,100);
        if(empty.rating!=0 || !empty.selected.isEmpty()) throw new AssertionError("Empty input should return empty plan");
        System.out.println("TripWise tests passed.");
    }
}