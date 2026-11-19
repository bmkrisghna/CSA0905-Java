# CSA09-<RegisterNumber>-Assignment5 – Data Persistence: Files and Databases (Java, CO5)

**Name:** ______________  **Register No:** ______________  **Java version:** 21 (works on Java 17+)

Part A (design and pseudocode) goes in the report; the code here supports the "Alternative Concept" parts and Part B.

## Setup for the database programs (B2, B3, B4, A5)
1. Install MySQL and run `mysql -u root -p < schema.sql` (a LOCAL TEST database `busdb`; never use real data).
2. Edit `db.properties` (user and password).
3. Download MySQL Connector/J and copy `mysql-connector-j-x.x.x.jar` into the `lib` folder.
4. Run any program with:  `bash run.sh <Folder> <MainClass> [args]`  (Windows: `run.bat <Folder> <MainClass> [args]`)
Programs that need no database can also be run directly: `cd B1`, `javac *.java`, `java LineCounters`.

## Folder guide

| Folder | Program | Needs DB | What it shows |
| --- | --- | --- | --- |
| A1 | TextRecordCodec | no | text layout with escaping, damaged-line detection |
| A1 | BinaryPassFile | no | DataOutputStream layout |
| A1 | RandomAccessPassFile | no | alternative: fixed-length records, seek = O(1), padding waste |
| A4 | SerializationDemo | no | object graph, transient fields, shared references |
| A4/versioning | STEPS.txt, v1, v2 | no | new field + serialVersionUID experiments |
| A4 | SerializationVsCsv | no | alternative: size and time, serialization vs CSV |
| A5 | TransferDemo | yes | transaction vs two auto-commit updates, failure between them |
| B1 | PassStore (Complete the Code) | no | expected output `[P1003, 192311003, Divya, 7, 3800.0]` then `null` |
| B1 | PassStoreTest | no | 9-case test table (normal, missing id, damaged, empty line, bad amount, missing file, half-written line) |
| B1 | LineCounters | no | B1.3 buffering: `java LineCounters` (1, 10, 50 MB) or `java LineCounters 1 10 50 skip-slow` |
| B1 | FindGrowthBenchmark | no | B1.4 growth 1,000 to 100,000 + HashMap alternative |
| B2 | LoginDemo | yes | unsafe vs PreparedStatement with injection inputs |
| B2 | DamagedFileDemo | no | adversarial pass file (uses PassStore) |
| B2 | RegexAlternative | no | why escaping / regex is weaker, validation cost |
| B3 | PassLoader (Complete the Code) | – | TODO 1-3 completed |
| B3 | PassLoaderMockTest | no | tests PassLoader with a fake Connection (batches, commit, rollback) |
| B3 | VerifySmall | yes | 10 records x 3 approaches + ResultSetMetaData |
| B3 | InsertBenchmark | yes | B3.2 and B3.3 (`1000 10000 100000`, add `--fast` to cap the slow ones) |
| B3 | AlternativeInserts | yes | multi-row INSERT and LOAD DATA |
| B4 | Migration | yes (not with `--dry-run`) | validate + batch + transaction + failure report |
| B4 | GeneratePassFile | no | creates a big pass file (`java GeneratePassFile 100000`) |
| B4 | DbInfo | yes | DatabaseMetaData report |
| B4 | CampusFilesAlternative | no | alternative: 12 serialized files, lost updates with concurrent counters |

## Tips
- InsertBenchmark and AlternativeInserts DELETE the rows of `bus_pass`. Test database only.
- For B3 measurements, run once with the default url and once with `&rewriteBatchedStatements=true` in `db.properties`, and explain the difference.
- Timings differ on every computer. Record your own numbers and interpret them.

## Git commands (one commit per task)
```
git init
git add A1 && git commit -m "A1: record layouts and random access alternative"
git add A4 && git commit -m "A4: serialization experiments"
git add A5 && git commit -m "A5: transaction demo"
git add B1 && git commit -m "B1: PassStore, buffering and growth measurements"
git add B2 && git commit -m "B2: injection and damaged file"
git add B3 && git commit -m "B3: statement vs prepared vs batch"
git add B4 && git commit -m "B4: migration and metadata"
git add README.md schema.sql db.properties run.sh run.bat && git commit -m "Add README and scripts"
git branch -M main
git remote add origin https://github.com/<username>/CSA09-<RegisterNumber>-Assignment5.git
git push -u origin main
```
