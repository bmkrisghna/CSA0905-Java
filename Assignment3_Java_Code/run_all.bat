@echo off
REM Compiles and runs every program of Assignment 3 (Windows). Output is saved in output_all.txt
for %%d in (Q1 Q2 Q3 Q4 Q5 Q6 Q7) do ( pushd %%d & javac *.java & popd )
(
call :run Q1 AtmWithdrawal
call :run Q1 StackTraceDemo
call :run Q1 ErrorCodeAlternative
call :run Q2 StorageDemo
call :run Q2 ExamPortal
call :run Q2 StackTraceCost
call :run Q3 ReportGenerator
call :run Q4 StationThreads
call :run Q4 RunVsStart
call :run Q4 ThreadStates
call :run Q4 ThreadExperiments
call :run Q4 ExecutorStations
call :run Q5 SeatBooking
call :run Q5 SeatBookingUnsafe
call :run Q5 SeatBookingUnsafe slow
call :run Q5 BookingRace
call :run Q6 TokenCounter
call :run Q6 TokenCounterIfBug
call :run Q6 MessCounterTrace
call :run Q6 WalletDeadlock
call :run Q6 WalletSafe
call :run Q6 AlternativeDesigns
call :run Q7 ResultProcessor
) > output_all.txt 2>&1
type output_all.txt
goto :eof

:run
echo.
echo ################ %1 / %2 ################
pushd %1
java %2 %3
popd
goto :eof
