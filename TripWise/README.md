# TripWise — Tourist Attraction Planner

A Java Swing DAA mini-project that selects attractions to maximize total rating under both a time limit and a budget limit.

## Algorithm
Two-constraint 0/1 Knapsack using dynamic programming. Each attraction can be selected at most once. Both total visit duration and total cost must remain within the user's limits.

## Model assumptions
Ratings are additive integer values; attractions are independent; travel time, opening hours, travel route and visit order are not modelled. Time is entered in minutes and cost in whole rupees.

## Run
Requires JDK 8 or later.

```bash
cd TripWise
javac -d out src/TripWise.java
java -cp out TripWise
```

## Test
```bash
javac -d out src/TripWise.java tests/TripWiseTest.java
java -cp out TripWiseTest
```

No external libraries are required.