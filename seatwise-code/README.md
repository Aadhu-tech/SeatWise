SmartExamSeat - refactored project (Swing + MySQL)

Structure (src):
 - gui/
 - logic/
 - model/
 - dao/
 - App.java

Requirements:
 - Java JDK 11+
 - MySQL server
 - mysql-connector-java jar in lib/

Steps:
1. Create database: mysql -u root -p < schema.sql
2. Edit src/db.properties with DB credentials
3. Put mysql-connector-java-x.jar in lib/
4. Compile:
   javac -cp ".:lib/mysql-connector-java-8.0.34.jar" -d out $(find src -name "*.java")
5. Run:
   java -cp "out:lib/mysql-connector-java-8.0.34.jar" App

Notes:
 - Admin username: admin (password: admin) by default in schema
 - Upload CSV formats:
    students.csv: studentId,name,branch
    rooms.csv: roomId,capacity,isBackup (true/false)
    exams.csv: examSlotId,subject,date(YYYY-MM-DD)
