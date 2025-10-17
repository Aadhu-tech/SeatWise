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
 - mysql-connector-j jar in lib/

Quick start (Windows PowerShell):
1. Create DB and tables:
   ```bash
   cd seatwise-code
   mysql -u root -p < schema.sql
   ```
2. Configure DB connection in `src/db.properties`.
3. Build and run:
   ```bash
   ./build.ps1
   ```

Manual compile/run (Windows):
```bash
cd seatwise-code
# Compile
javac -cp ".;lib/mysql-connector-j-9.4.0/mysql-connector-j-9.4.0.jar" -d out (Get-ChildItem -Recurse src/*.java).FullName
# Copy resources
Copy-Item src/db.properties out/
# Run
java -cp "out;lib/mysql-connector-j-9.4.0/mysql-connector-j-9.4.0.jar" src.App
```

Notes:
 - Admin login is handled in UI (type `admin` as username).
 - CSV upload formats:
    - students.csv: studentId,name,branch
    - rooms.csv: roomId,capacity,isBackup (true/false)
    - exams.csv: examSlotId,subject,date(YYYY-MM-DD)
