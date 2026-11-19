# CSA09-<RegisterNumber>-Assignment3 – Exceptions and Multithreading (Java, CO3)

**Student A:** ______________ (Reg No ________)  **Student B:** ______________ (Reg No ________)
**Java version:** 21 (works on Java 17+)

Both students must run every program in their own repository.

## Quick start
```
bash run_all.sh        # Linux / Mac / Git Bash   (Windows: run_all.bat)
```
This compiles everything and saves all output in `output_all.txt`. Or run one program at a time:
```
cd Q5
javac *.java
java BookingRace
```

## Folder guide

| Folder | Files | What it shows | Main result |
| --- | --- | --- | --- |
| Q1 | AtmWithdrawal.java | try / multi-catch / finally with 7 test inputs | finally runs 7 times |
| Q1 | StackTraceDemo.java | uncaught exception and stack trace | ArithmeticException trace |
| Q1 | CatchOrderError.txt | broader catch before narrower = compile error | – |
| Q1 | ErrorCodeAlternative.java | error codes instead of exceptions + timing | codes are faster for invalid input |
| Q2 | StorageDemo.java | Complete the Code (TODO 1) | matches expected output |
| Q2 | ExamPortal.java | 3 layers, custom exceptions, chaining, lost cause | 6 scenarios |
| Q2 | StackTraceCost.java | cost of capturing stack traces | grows with stack depth |
| Q3 | ReportGenerator.java | 4 versions, files left open, closing order, suppressed exception | leaks counted |
| Q4 | StationThreads.java | extends Thread vs Runnable, start/sleep/join/isAlive | total 180 |
| Q4 | RunVsStart.java, ThreadStates.java | run() vs start(), thread states | – |
| Q4 | ThreadExperiments.java | priorities and daemon thread | – |
| Q4 | ExecutorStations.java | fixed thread pool alternative | about 400 ms |
| Q4 | run_10_times.sh / .bat | runs StationThreads 10 times | finish order record |
| Q5 | SeatBooking.java | Complete the Code (synchronized) | Seats sold: 10 |
| Q5 | SeatBookingUnsafe.java | without synchronized (`java SeatBookingUnsafe slow` shows the race) | more than 10 |
| Q5 | BookingRace.java | reliable race test + 5 synchronization strategies timed | all correct = 100 |
| Q6 | TokenCounter.java | Complete the Code (wait / notifyAll) | 210 |
| Q6 | TokenCounterIfBug.java | `while` vs `if`, 200 runs | `if` gives wrong results |
| Q6 | MessCounterTrace.java | counter size stays within 0..5 | – |
| Q6 | WalletDeadlock.java, WalletSafe.java | deadlock detected, then fixed by lock ordering | total money 5000 |
| Q6 | AlternativeDesigns.java | ArrayBlockingQueue and tryLock | 210, no deadlock |
| Q7 | ResultProcessor.java | ExecutorService + Callable + Future, pools 1/2/4/8, parallelStream, thread-per-file | summary counts match |

## Notes
- Output order and timings change on every run and on every computer. Run several times and record your own results.
- Q5: the given 100-thread program rarely races. Use the `slow` argument or `BookingRace.java` to reproduce the double booking.
- Q6: change `while` to `if` in `TokenCounter.java` yourself, run it several times and record what happens (`TokenCounterIfBug.java` automates this with 4 consumers).
- Q7 simulates 0.1 ms of file-read time per file so that the effect of the thread-pool size can be seen.

## Git commands (one commit per question)
```
git init
git add Q1 && git commit -m "Q1: ATM exceptions"
git add Q2 && git commit -m "Q2: exam portal custom exceptions"
git add Q3 && git commit -m "Q3: try-with-resources"
git add Q4 && git commit -m "Q4: threads and life cycle"
git add Q5 && git commit -m "Q5: race condition and synchronization"
git add Q6 && git commit -m "Q6: deadlock and producer-consumer"
git add Q7 && git commit -m "Q7: concurrent result processor"
git add README.md run_all.sh run_all.bat && git commit -m "Add README and run scripts"
git branch -M main
git remote add origin https://github.com/<username>/CSA09-<RegisterNumber>-Assignment3.git
git push -u origin main
```
Add your output screenshots and `output_all.txt` to the repository.
