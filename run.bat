dir /s /b src\*.java > sources.txt

javac -cp lib\mysql-connector-j-9.5.0.jar -d out @sources.txt

java -cp out;lib\mysql-connector-j-9.5.0.jar com.App