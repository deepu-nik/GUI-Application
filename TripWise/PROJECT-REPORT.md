# TripWise — Mini Project Report

## 1. Problem Statement
A tourist has a limited total visit time and a fixed budget. Each attraction has a visit duration, entry cost and rating. Select a subset of attractions that maximizes total rating without exceeding either limit.

## 2. Objectives
- Accept attraction details and both resource limits.
- Select each attraction at most once.
- Maximize total rating under simultaneous time and budget constraints.
- Display the selected attractions and resource totals.

## 3. Selected DAA Strategy
Dynamic Programming — Two-Constraint 0/1 Knapsack.

## 4. Recurrence
Let DP[i][t][b] be the maximum rating using the first i attractions within time t and budget b:
DP[i][t][b] = max(DP[i-1][t][b], rating[i] + DP[i-1][t-duration[i]][b-cost[i]]) when both resources fit; otherwise use DP[i-1][t][b].

## 5. Algorithm (Pseudocode)
1. Initialize all DP values to zero.
2. For each attraction, iterate time and budget capacities in descending order.
3. If adding the attraction improves the rating, update the state and record the choice.
4. Reconstruct the selected attractions from the recorded decisions.
5. Display total rating, time and cost.

## 6. Complexity
Let n be the number of attractions, T the time capacity and B the budget capacity. Time complexity is O(nTB). The implementation stores a 2D value table and a 3D boolean decision table, so space complexity is O(TB+nTB), i.e. O(nTB). Input limits keep the table within a practical size.

## 7. Implementation
Java Swing provides fields for attraction name, duration, cost, rating, total available time and budget. The application displays the optimal selected set.

## 8. Assumptions and Limitations
Attractions are independent, ratings are additive, and each attraction is selected at most once. Travel time, opening hours, route order and changing prices are outside the model.

## 9. Conclusion and Future Scope
The project demonstrates dynamic programming for a selection problem with two simultaneous constraints. Future scope includes travel-time estimation, route planning and time-dependent attraction availability.

## 10. References
- Cormen, Leiserson, Rivest and Stein, *Introduction to Algorithms*, Dynamic Programming / Knapsack.
- Java Platform documentation: Swing.
