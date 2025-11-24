package tutorial.modtutorial.service;

import tutorial.modtutorial.domain.dto.test.request.SpecialTestDTO;
import tutorial.modtutorial.domain.dto.test.response.SpecialTestProtectedDTO;
import tutorial.modtutorial.domain.entity.SpecialTest;

import java.util.List;


public interface SpecialTestService {
    // ====================== SYSTEM ZONE =======================
    String createIfNotExistsByIdAndSubjectId(String testId, String subjectId, int number);
    void updateUsageCount(String testId, int change);
    SpecialTest findById(String testId);


    // ===================== MODERATOR ZONE =====================
    List<SpecialTestProtectedDTO> getSpecialTestsProtected(String subjectId, int maxUsageCount);
    void createSpecialTest(SpecialTestDTO dto);
    void updateSpecialTest(String testId, SpecialTestDTO dto);
    void deleteSpecialTest(String testId);
}
