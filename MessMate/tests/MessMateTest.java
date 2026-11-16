import java.util.*;

public class MessMateTest {
    public static void main(String[] args) {
        List<MessMate.Student> students = Arrays.asList(
            new MessMate.Student("A", 690, 700),
            new MessMate.Student("B", 690, 710)
        );
        MessMate.Result result = MessMate.optimize(students);
        if (result.served != 2) throw new AssertionError("Expected matching to serve 2, got " + result.served);
        if (MessMate.fcfs(students) != 1) throw new AssertionError("Expected FCFS baseline to serve 1");
        if (MessMate.optimize(Collections.<MessMate.Student>emptyList()).served != 0)
            throw new AssertionError("Empty input should serve 0");
        System.out.println("MessMate tests passed.");
    }
}