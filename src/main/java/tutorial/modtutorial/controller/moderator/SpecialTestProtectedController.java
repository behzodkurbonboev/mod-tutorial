package tutorial.modtutorial.controller.moderator;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorial.modtutorial.domain.dto.test.request.SpecialTestDTO;
import tutorial.modtutorial.domain.dto.test.response.SpecialTestProtectedDTO;
import tutorial.modtutorial.service.SpecialTestService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/protected/special-tests")
public class SpecialTestProtectedController {
    private final SpecialTestService testService;

    public SpecialTestProtectedController(SpecialTestService testService) {
        this.testService = testService;
    }


    @GetMapping
    public ResponseEntity<List<SpecialTestProtectedDTO>> getSpecialTestsProtected(@RequestParam String subjectId, @RequestParam(required = false, defaultValue = "0") int maxUsageCount) {
        return ResponseEntity.ok(testService.getSpecialTestsProtected(subjectId, maxUsageCount));
    }

    @PostMapping
    public void createSpecialTest(@RequestBody SpecialTestDTO dto) {
        testService.createSpecialTest(dto);
    }

    @PutMapping("/{testId}")
    public void updateSpecialTest(@PathVariable String testId, @RequestBody SpecialTestDTO dto) {
        testService.updateSpecialTest(testId, dto);
    }

    @DeleteMapping("/{testId}")
    public void deleteSpecialTest(@PathVariable String testId) {
        testService.deleteSpecialTest(testId);
    }
}
