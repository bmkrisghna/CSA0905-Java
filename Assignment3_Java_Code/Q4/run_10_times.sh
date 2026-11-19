#!/bin/bash
# Linux / Mac / Git Bash: runs StationThreads ten times and saves the output
javac StationThreads.java
for i in 1 2 3 4 5 6 7 8 9 10; do
  echo "----- Run $i -----"
  java StationThreads | grep finished
done | tee run_10_times_output.txt
