# MessMate — College Mess Meal Scheduler

A small DAA mini-project built with Java Swing. It assigns students to service slots during a college mess break while maximizing the number served.

## Algorithm
Maximum Bipartite Matching using the augmenting-path DFS (Kuhn) method:
- Left vertices: students.
- Right vertices: discrete 10-minute service slots, starting every 5 minutes.
- Edge: the complete service interval fits inside that student's availability window.

The application also reports a simple First-Come-First-Served (FCFS) baseline for comparison.

## Model assumptions
One counter; 10-minute service duration; 5-minute slot increments; mess window 11:30–14:00. This solves the discretized model, not arbitrary continuous-time scheduling or multiple-counter scheduling.

## Run
Requires JDK 8 or later.

```bash
cd MessMate
javac -d out src/MessMate.java
java -cp out MessMate
```

## Test
```bash
javac -d out src/MessMate.java tests/MessMateTest.java
java -cp out MessMateTest
```

No external libraries are required.