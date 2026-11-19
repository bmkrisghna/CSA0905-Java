#!/bin/bash
# Compiles and runs every program of Assignment 3 and saves the output in output_all.txt
# Usage (Linux / Mac / Git Bash):  bash run_all.sh
run() {            # run <folder> <MainClass> [args]
  folder=$1; shift
  echo ""; echo "################ $folder / $1 ################"
  (cd "$folder" && java "$@") 2>&1 || true
}
for d in Q1 Q2 Q3 Q4 Q5 Q6 Q7; do (cd $d && javac *.java) ; done
{
  run Q1 AtmWithdrawal
  run Q1 StackTraceDemo
  run Q1 ErrorCodeAlternative
  run Q2 StorageDemo
  run Q2 ExamPortal
  run Q2 StackTraceCost
  run Q3 ReportGenerator
  run Q4 StationThreads
  run Q4 RunVsStart
  run Q4 ThreadStates
  run Q4 ThreadExperiments
  run Q4 ExecutorStations
  run Q5 SeatBooking
  run Q5 SeatBookingUnsafe
  run Q5 SeatBookingUnsafe slow
  run Q5 BookingRace
  run Q6 TokenCounter
  run Q6 TokenCounterIfBug
  run Q6 MessCounterTrace
  run Q6 WalletDeadlock
  run Q6 WalletSafe
  run Q6 AlternativeDesigns
  run Q7 ResultProcessor
} | tee output_all.txt
