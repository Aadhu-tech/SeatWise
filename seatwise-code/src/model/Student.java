package model;
public class Student {
    private String studentId;
    private String name;
    private String branch;
    public Student(String studentId, String name, String branch) {
        this.studentId = studentId;
        this.name = name;
        this.branch = branch;
    }
    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getBranch() { return branch; }
    @Override public String toString() { return studentId+" | "+name+" | "+branch; }
}
