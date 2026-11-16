import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MessMateTest {
    public static void main(String[] args) {
        // A is processed first and greedily takes 11:30; B can only use 11:30.
        // Maximum matching reassigns A to 11:35 so both students are served.
        List<MessMate.Student> data = Arrays.asList(
            new MessMate.Student("A", 690, 705),
            new MessMate.Student("B", 690, 700)
        );
        if (MessMate.optimize(data).served != 2)
            throw new AssertionError("Maximum matching should serve both students");
        if (MessMate.fcfs(data) != 1)
            throw new AssertionError("FCFS baseline should serve one student");
        if (MessMate.optimize(Collections.<MessMate.Student>emptyList()).served != 0)
            throw new AssertionError("Empty input should serve zero students");
        System.out.println("MessMate tests passed.");
    }
}