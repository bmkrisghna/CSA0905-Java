# CSA09-<RegisterNumber>-Assignment4 – Event-Driven GUI Design (Java, CO4)

**Name:** ______________  **Register No:** ______________  **Java version:** 21 (works on Java 17+)

Programs that open a window need a computer with a screen. Compile and run inside each folder.

## Folder guide

| Folder | Files | Run | What to record |
| --- | --- | --- | --- |
| Task01 | BadFeeWindow.java.txt | – (original code, for the diagnosis) | 5+ deficiencies |
| Task01 | FeeWindow.java + FeeService.java, FeeException, Receipt, PaymentStore, InMemoryPaymentStore, JdbcPaymentStore | `javac *.java` then `java FeeWindow` (`java FeeWindow jdbc` for MySQL) | screenshot of valid and invalid payment |
| Task01 | FeeServiceTest.java | `java FeeServiceTest` | 8 tests PASS (no window needed) |
| Task01 | ListenerDispatchBench.java | `java ListenerDispatchBench` | if-else chain vs one listener per button |
| Task02 | AppointmentDesk.java | `javac AppointmentDesk.java` then `java AppointmentDesk` | console prints the window events (Task 2.4); try closing during a booking |
| Task02 | DoctorLookupBench.java | `java DoctorLookupBench` | Map vs database fetch |
| Task03 | LibrarySearch.java | `java LibrarySearch` or `java LibrarySearch --shots` | `--shots` saves 3 resize screenshots |
| Task03 | LibrarySearchGridBag.java, LayoutBench.java | `java LibrarySearchGridBag`, `java LayoutBench` | alternative layouts + timing |
| Task04 | DrawPadBroken.java | `java DrawPadBroken` | click: nothing happens (the bug) |
| Task04 | DrawPad.java | `java DrawPad` | Complete the Code; minimise and restore |
| Task04 | DrawPadMisspelled.txt | follow the steps in the file | compiler message for Task 4.4 |
| Task04 | DrawPadBuffered.java, RepaintBench.java | `java DrawPadBuffered`, `java RepaintBench` | BufferedImage alternative |
| Task05 | ConverterStyleA.java .. ConverterStyleD.java | `java ConverterStyleA` etc. | lines marked `[5.2]` are what you add for the changed requirement |
| Task05 | Converter.java | `java Converter` | Complete the Code (lambda version) |
| Task05 | ConverterSharedAction.java | `java ConverterSharedAction` | shared Action alternative |
| Task06 | eventreg/... (layered), schema.sql, db.properties | see below | demonstration + SQL script |

## Task 6 – Event registration (Swing + JDBC + MySQL)
1. Install MySQL, then run `mysql -u root -p < schema.sql`.
2. Edit `db.properties` (user and password).
3. Download the MySQL driver (Connector/J, the "Platform Independent" zip) and copy `mysql-connector-j-x.x.x.jar` into `Task06/lib/`.
4. Start: `bash run.sh` (Windows: `run.bat`). Without a database the window opens in **demo mode** (title shows it) - use MySQL for your submission.
5. Console demonstration of normal / invalid / duplicate cases: `bash run.sh ServiceDemo` (add `jdbc` to use MySQL).
6. Alternative-concept measurement: `bash run.sh LoadBenchmark jdbc 10000 100000` (this adds rows to your table; clear it afterwards with `DELETE FROM registration;`).

Layers: `ui` (Swing only) -> `service` (validation, duplicate rule, CSV export) -> `dao` (JDBC only). Database work runs in `SwingWorker`, never on the event-dispatch thread. Duplicates are stopped twice: by a check in the service and by the `UNIQUE (student_id, event_name)` key in the table.

## Notes
- Task 4: `DrawPadBroken` compiles but does nothing, which is exactly the defect to explain.
- Take every screenshot and link it to the requirement or test it shows (a screenshot alone is not enough evidence).

## Git commands (one commit per task)
```
git init
git add Task01 && git commit -m "Task 1: fee window redesign"
git add Task02 && git commit -m "Task 2: appointment desk"
git add Task03 && git commit -m "Task 3: library search layouts"
git add Task04 && git commit -m "Task 4: draw pad event forensics"
git add Task05 && git commit -m "Task 5: event-handling styles"
git add Task06 && git commit -m "Task 6: event registration app"
git add README.md compile_all.sh compile_all.bat && git commit -m "Add README"
git branch -M main
git remote add origin https://github.com/<username>/CSA09-<RegisterNumber>-Assignment4.git
git push -u origin main
```
