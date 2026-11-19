# CSA09-<RegisterNumber>-Assignment2 – Strings, Collections and Generics (Java, CO2)

**Name:** ______________  **Register No:** ______________  **Java version:** 21 (works on Java 17+)

Every program is one file. Compile and run inside its folder:
`javac FileName.java` then `java FileName`

| Folder | Files | What it shows |
| --- | --- | --- |
| Q1 | StringBenchmark.java | String `+` vs StringBuilder vs StringBuffer (3 workloads, 5-run average) |
| Q2 | ListBenchmark.java | ArrayList vs LinkedList vs ArrayDeque at 1,000 / 10,000 / 100,000 |
| Q3 | AttendanceNoHash.java (run first, prints 3), Attendance.java (TODO 1 done, prints 2), HashSetTests.java | hashCode–equals contract |
| Q4 | MapBenchmark.java, BinarySearchAlternative.java | HashMap vs TreeMap vs LinkedHashMap, sorted array alternative |
| Q5 | LeaderboardDemo.java (Complete the Code), ComparatorTests.java | generic Leaderboard, Comparator chain, faulty comparators |
| Q6 | Triage.java (Complete the Code), TriageGrowth.java, AgingTriage.java | PriorityQueue, growth, starvation fix |
| Q7 | TicketRedesign.java | Design challenge: Q1 ticket builder, original vs redesign |

## Notes
- Timings change on every computer. Run each benchmark several times and keep your own numbers in the report.
- Q3: write your prediction before running each test. `HashSetTests` Test B has two scans of the same student,
  so without hashCode the size is 2 (it should be 1).
- Q7 chosen problem: Q1 (ticket generation). Redesign = one reused, pre-sized StringBuilder.

## Git commands (one commit per question)
```
git init
git add Q1 && git commit -m "Q1: String vs StringBuilder vs StringBuffer"
git add Q2 && git commit -m "Q2: ArrayList vs LinkedList vs ArrayDeque"
git add Q3 && git commit -m "Q3: hashCode-equals contract"
git add Q4 && git commit -m "Q4: HashMap vs TreeMap vs LinkedHashMap"
git add Q5 && git commit -m "Q5: generic leaderboard"
git add Q6 && git commit -m "Q6: priority queue triage"
git add Q7 && git commit -m "Q7: design challenge redesign"
git add README.md && git commit -m "Add README"
git branch -M main
git remote add origin https://github.com/<username>/CSA09-<RegisterNumber>-Assignment2.git
git push -u origin main
```
Also add your output screenshots into each folder.
