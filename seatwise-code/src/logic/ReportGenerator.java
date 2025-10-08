package logic;
import model.AllocationRecord;
import java.util.List;
import dao.AllocationDAO;
public class ReportGenerator {
    public AllocationRecord getStudentAllocation(String studentId) {
        return dao.AllocationDAO.fetchAllocationForStudent(studentId);
    }
    public List<AllocationRecord> getAllAllocations() {
        return dao.AllocationDAO.fetchAllAllocations();
    }
}
