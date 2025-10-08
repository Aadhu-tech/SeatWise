package model;
public class ExamSlot {
    private String examSlotId;
    private String subject;
    private String date;
    public ExamSlot(String examSlotId, String subject, String date) {
        this.examSlotId = examSlotId;
        this.subject = subject;
        this.date = date;
    }
    public String getExamSlotId() { return examSlotId; }
    public String getSubject() { return subject; }
    public String getDate() { return date; }
    @Override public String toString() { return examSlotId+" | "+subject+" | "+date; }
}
