package tutorial.modtutorial.controller.user;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorial.modtutorial.domain.dto.forum.request.ProblemFilter;
import tutorial.modtutorial.domain.dto.forum.response.ProblemPublicDTO;
import tutorial.modtutorial.domain.dto.forum.response.SolutionPublicDTO;
import tutorial.modtutorial.domain.dto.general.response.Slice;
import tutorial.modtutorial.service.ProblemService;

import java.util.List;


@Validated
@RestController
@RequestMapping("/api/v1/public/problems")
public class ProblemPublicController {
    private final ProblemService problemService;

    public ProblemPublicController(ProblemService problemService) {
        this.problemService = problemService;
    }


    @PostMapping
    public ResponseEntity<Slice<ProblemPublicDTO>> getProblemsPublic(@Valid @RequestBody ProblemFilter filter) {
        return ResponseEntity.ok(problemService.getProblemsPublic(filter));
    }

    @PostMapping("/saved")
    public ResponseEntity<Slice<ProblemPublicDTO>> getProblemsSavedByUser(@RequestBody ProblemFilter filter) {
        return ResponseEntity.ok(problemService.getProblemsSavedByUser(filter));
    }

    @PostMapping("/by-author")
    public ResponseEntity<Slice<ProblemPublicDTO>> getProblemsCreatedByUser(@RequestBody ProblemFilter filter) {
        return ResponseEntity.ok(problemService.getProblemsCreatedByUser(filter));
    }

    @PutMapping("/{problemId}/like")
    public void handleProblemLike(@PathVariable String problemId) {
        problemService.handleProblemLike(problemId);
    }

    @PutMapping("/{problemId}/save")
    public void handleProblemSave(@PathVariable String problemId) {
        problemService.handleProblemSave(problemId);
    }

    @PutMapping("/{problemId}/see")
    public void handleProblemSee(@PathVariable String problemId) {
        problemService.handleProblemSee(problemId);
    }

    @PutMapping("/{problemId}/share")
    public void handleProblemShare(@PathVariable String problemId) {
        problemService.handleProblemShare(problemId);
    }

    @GetMapping("/{problemId}/solutions")
    public ResponseEntity<List<SolutionPublicDTO>> getSolutionsPublic(@PathVariable String problemId) {
        return ResponseEntity.ok(problemService.getSolutionsPublic(problemId));
    }

    @PutMapping("/{problemId}/solutions/{solutionId}/like")
    public void handleSolutionLike(@PathVariable String problemId, @PathVariable String solutionId) {
        problemService.handleSolutionLike(problemId, solutionId);
    }
}
