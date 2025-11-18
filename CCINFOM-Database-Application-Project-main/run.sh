#!/bin/bash

# Compile the Java file
javac -cp lib/mysql-connector-j-9.5.0.jar src/*.java -d out

# Run the Java program
java -cp "lib/mysql-connector-j-9.5.0.jar;out" Main