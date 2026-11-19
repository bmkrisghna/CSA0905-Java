@echo off
REM Usage (Windows):  run.bat <Folder> <MainClass> [arguments]
REM Example:          run.bat B1 LineCounters 1 10
REM The MySQL driver jar (mysql-connector-j-*.jar) must be in the folder lib next to this file.
if not exist lib mkdir lib
set LIB=%CD%\lib\*
pushd %1
javac -cp ".;%LIB%" *.java
if errorlevel 1 (popd & exit /b 1)
set FOLDER=%1
set MAIN=%2
shift
shift
java -cp ".;%LIB%" %MAIN% %1 %2 %3 %4
popd
