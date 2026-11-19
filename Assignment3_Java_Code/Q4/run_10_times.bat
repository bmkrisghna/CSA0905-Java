@echo off
REM Windows: runs StationThreads ten times
javac StationThreads.java
for /L %%i in (1,1,10) do (
  echo ----- Run %%i -----
  java StationThreads | findstr finished
)
