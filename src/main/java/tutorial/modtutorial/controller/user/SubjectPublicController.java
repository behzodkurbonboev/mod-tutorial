package tutorial.modtutorial.controller.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorial.modtutorial.domain.dto.general.response.SubjectDTO;
import tutorial.modtutorial.domain.dto.general.response.TagDTO;
import tutorial.modtutorial.service.SubjectService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/public/subjects")
public class SubjectPublicController {
    private final SubjectService subjectService;

    public SubjectPublicController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }


    @GetMapping
    public ResponseEntity<List<SubjectDTO>> getSubjectsForArticlesFilter() {
        return new ResponseEntity<>(subjectService.getSubjectsForArticlesFilter(), HttpStatus.OK);
    }

    @GetMapping("/special-blocks")
    public ResponseEntity<List<SubjectDTO>> getSubjectsForSpecialBlocksFilter() {
        return new ResponseEntity<>(subjectService.getSubjectsForSpecialBlocksFilter(), HttpStatus.OK);
    }

    @GetMapping("/{subjectId}")
    public ResponseEntity<List<TagDTO>> getTagsForForumFilter(@PathVariable String subjectId, @RequestParam String forumId) {
        return new ResponseEntity<>(subjectService.getTagsForForumFilter(subjectId, forumId), HttpStatus.OK);
    }
}
