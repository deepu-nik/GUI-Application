# MessMate — Mini Project Report

## 1. Problem Statement
Students have different availability windows during a fixed college meal break. A single mess counter serves one student at a time. The objective is to assign feasible service slots so that the maximum number of students can be served.

## 2. Objectives
- Accept student availability windows.
- Generate feasible discrete service slots.
- Maximize the number of students assigned.
- Compare the result with a simple FCFS baseline.

## 3. Selected DAA Strategy
Graph Algorithms — Maximum Bipartite Matching.

Construct a bipartite graph with students on the left and service slots on the right. Connect a student to a slot when the full service interval fits within the student's availability window. Use augmenting-path DFS to find a maximum matching.

## 4. Algorithm (Pseudocode)
1. Generate all service slots in the mess period.
2. Mark every slot as unassigned.
3. For each student, start a DFS augmenting-path search with all slots unvisited.
4. For each feasible unvisited slot, mark it visited.
5. If the slot is free, assign it; otherwise recursively try to move its current student to another feasible slot.
6. Count successful augmentations and display the matching.

## 5. Complexity
Let V be the number of students and S the number of service slots. The augmenting-path implementation runs in O(V·E) time in the standard bound, where E is the number of feasible student-slot edges, and uses O(V+S) auxiliary space (excluding graph storage). The displayed FCFS baseline is O(V·S).

## 6. Implementation
Java Swing provides the interface for entering students, loading sample data, removing entries, clearing the table and displaying the optimized schedule. The algorithm is implemented in plain Java.

## 7. Assumptions and Limitations
One counter; fixed 10-minute service; slots every 5 minutes; operating window 11:30–14:00. Travel, queue abandonment, different service durations and continuous-time starts are not modelled.

## 8. Test Cases
- Two students where FCFS assigns one but augmenting-path matching can assign both.
- Empty student list.
- No feasible slot (can be tested by supplying a window outside the operating period through the algorithm test harness).

## 9. Conclusion and Future Scope
The project demonstrates how bipartite matching can improve allocation compared with a greedy FCFS baseline. Future work may include multiple counters, variable service durations and live queue information.

## 10. References
- Cormen, Leiserson, Rivest and Stein, *Introduction to Algorithms*, Bipartite Matching / Graph Algorithms.
- Java Platform documentation: Swing.
