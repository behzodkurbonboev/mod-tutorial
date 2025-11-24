package tutorial.modtutorial.controller.admin;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorial.modtutorial.domain.dto.general.request.NameDTO;
import tutorial.modtutorial.domain.dto.general.response.SubjectDTO;
import tutorial.modtutorial.service.SubjectService;


@Validated
@RestController
@RequestMapping("/api/v1/private/subjects")
public class SubjectPrivateController {
    private final SubjectService subjectService;

    public SubjectPrivateController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }


    @PostMapping
    public ResponseEntity<SubjectDTO> createSubject(@Valid @RequestBody NameDTO dto) {
        return new ResponseEntity<>(subjectService.createSubject(dto), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public void updateSubject(@PathVariable String id, @Valid @RequestBody NameDTO dto) {
        subjectService.updateSubject(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteSubject(@PathVariable String id) {
        subjectService.deleteSubject(id);
    }
}
