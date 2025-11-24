package tutorial.modtutorial.controller.moderator;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorial.modtutorial.domain.dto.general.request.NameDTO;
import tutorial.modtutorial.domain.dto.general.response.SubjectDTO;
import tutorial.modtutorial.service.SubjectService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/protected/subjects")
public class SubjectProtectedController {
    private final SubjectService subjectService;

    public SubjectProtectedController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<SubjectDTO> getSubject(@PathVariable String id) {
        return new ResponseEntity<>(subjectService.getSubject(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<SubjectDTO>> getSubjects() {
        return new ResponseEntity<>(subjectService.getSubjects(), HttpStatus.OK);
    }

    @PostMapping("/{subjectId}/tags")
    public void addTag(@PathVariable String subjectId, @Valid @RequestBody NameDTO dto) {
        subjectService.addTag(subjectId, dto);
    }

    @PutMapping("/{id}/tags/{tagId}")
    public void updateTag(@PathVariable String id, @PathVariable String tagId, @Valid @RequestBody NameDTO dto) {
        subjectService.updateTag(id, tagId, dto);
    }

    @DeleteMapping("/{id}/tags/{tagId}")
    public void deleteTag(@PathVariable String id, @PathVariable String tagId) {
        subjectService.deleteTag(id, tagId);
    }
}
