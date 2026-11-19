# CSA09-<RegisterNumber>-Assignment1 – Campus Smart-Parking System (Java, CO1)

**Name:** ______________  **Register No:** ______________  **Java version:** 21 (works on Java 17+)

## Folder guide

| Folder | What it contains | Run (inside the folder) | Result |
| --- | --- | --- | --- |
| Task01 | Report only (problem analysis) | – | – |
| Task02 | Casting / integer-division demo | `javac CastingDemo.java` then `java CastingDemo` | wrong fee 30.0, right fee 60.0 |
| Task03 | ParkingFacility (Complete the Code) | `javac ParkingFacility.java` then `java ParkingFacility` | matches expected output |
| Task04_05 | Constructors, copy constructor, static counter | `javac TicketDemo.java` then `java TicketDemo` | tickets 1, 2 and a duplicate of 1 |
| Task06_09 | Vehicle hierarchy, overriding, polymorphism, interface (FeeDemo) | `javac *.java` then `java FeeDemo` | 30.0, 90.0, 210.0, true |
| Task10 | Wrapper class `==` risk + validating setter | `javac WrapperDemo.java` then `java WrapperDemo` | 1000 == 1000 is false |
| Task11 | Full system in packages + test table + lookup benchmark | see below | 13 tests passed |
| Task12 | Design challenge: e-bike, van, one-day pass, 5 floors | see below | 6 tests passed |

### Task11 and Task12 (packages)
```
javac -d out @sources.txt
java -cp out parking.app.Main        # Task11
java -cp out parking.app.Main12      # Task12
```
Task11 extra (alternative concept – array vs ArrayList vs HashMap):
```
cd bench
javac TicketLookupBench.java
java TicketLookupBench
```

## Design notes
- Packages: `parking.model` (data classes), `parking.service` (ParkingFacility), `parking.app` (Main).
- Task12: **MODIFY** decision. New classes were only *added* (EBike, DeliveryVan, OneDayPass). Only
  `VehicleFactory` was edited (two new `case` lines). Vehicle, Car, TwoWheeler, ElectricCar and
  ParkingFacility were not changed.
- Assumption: the van rate depends on the hour the parking *starts* (9 AM to 5 PM = Rs 60/hour, otherwise Rs 30/hour).

## Git commands (one commit per task)
```
git init
git add Task02 && git commit -m "Task 2: casting demo"
git add Task03 && git commit -m "Task 3: allocate and release"
git add Task04_05 && git commit -m "Task 4-5: constructors and static"
git add Task06_09 && git commit -m "Task 6-9: inheritance, polymorphism, abstraction"
git add Task10 && git commit -m "Task 10: encapsulation and wrapper classes"
git add Task11 && git commit -m "Task 11: full system and tests"
git add Task12 && git commit -m "Task 12: design challenge"
git add README.md && git commit -m "Add README"
git branch -M main
git remote add origin https://github.com/<username>/CSA09-<RegisterNumber>-Assignment1.git
git push -u origin main
```
Also add your output screenshots into each folder.
