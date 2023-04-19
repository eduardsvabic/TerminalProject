@echo off
javac src/app/*.java src/kernel/*.java src/truck/*.java -d bin
java -cp bin app.Mainer