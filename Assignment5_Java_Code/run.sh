#!/bin/bash
# Usage (Linux / Mac / Git Bash):   bash run.sh <Folder> <MainClass> [arguments]
# Example:                          bash run.sh B1 LineCounters 1 10
#                                   bash run.sh B3 InsertBenchmark 1000 10000
# The MySQL driver jar (mysql-connector-j-*.jar) must be in the folder  lib/  next to this file.
folder=$1; main=$2; shift 2
mkdir -p lib
LIB="$(pwd)/lib/*"
(cd "$folder" && javac -cp ".:$LIB" *.java && java -cp ".:$LIB" "$main" "$@")
