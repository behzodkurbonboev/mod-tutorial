package tutorial.modtutorial.controller.moderator;

import org.springframework.http.HttpStatus;
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
import tutorial.modtutorial.domain.dto.forum.request.ProblemDTO;
import tutorial.modtutorial.domain.dto.forum.request.SolutionDTO;
import tutorial.modtutorial.domain.dto.forum.response.ProblemProtectedDTO;
import tutorial.modtutorial.domain.dto.forum.response.SolutionProtectedDTO;
import tutorial.modtutorial.service.ProblemService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/protected/problems")
public class ProblemProtectedController {
    private final ProblemService problemService;

    public ProblemProtectedController(ProblemService problemService) {
        this.problemService = problemService;
    }


    @GetMapping
    public ResponseEntity<List<ProblemProtectedDTO>> getProblemsProtected(@RequestParam String forumId) {
        return new ResponseEntity<>(problemService.getProblemsProtected(forumId), HttpStatus.OK);
    }

    @PostMapping
    public void createProblem(@RequestBody ProblemDTO dto) {
        problemService.createProblem(dto);
    }

    @PutMapping("/{problemId}")
    public void updateProblem(@PathVariable String problemId, @RequestBody ProblemDTO dto) {
        problemService.updateProblem(problemId, dto);
    }

    @PutMapping("/{problemId}/visibility")
    public void changeProblemVisibility(@PathVariable String problemId) {
        problemService.changeProblemVisibility(problemId);
    }

    @PutMapping("/{problemId}/transfer")
    public void transferProblem(@PathVariable String problemId, @RequestParam String userId) {
        problemService.transferProblem(problemId, userId);
    }

    @DeleteMapping("/{problemId}")
    public void deleteProblem(@PathVariable String problemId) {
        problemService.deleteProblem(problemId);
    }

    @GetMapping("/{problemId}/solutions")
    public ResponseEntity<List<SolutionProtectedDTO>> getSolutionsProtected(@PathVariable String problemId) {
        return new ResponseEntity<>(problemService.getSolutionsProtected(problemId), HttpStatus.OK);
    }

    @PostMapping("/{problemId}/solutions")
    public void createSolution(@PathVariable String problemId, @RequestBody SolutionDTO dto) {
        problemService.createSolution(problemId, dto);
    }

    @PutMapping("/{problemId}/solutions/{solutionId}")
    public void updateSolution(@PathVariable String problemId, @PathVariable String solutionId, @RequestBody SolutionDTO dto) {
        problemService.updateSolution(problemId, solutionId, dto);
    }

    @PutMapping("/{problemId}/solutions/{solutionId}/visibility")
    public void changeSolutionVisibility(@PathVariable String problemId, @PathVariable String solutionId) {
        problemService.changeSolutionVisibility(problemId, solutionId);
    }

    @PutMapping("/{problemId}/solutions/{solutionId}/transfer")
    public void transferSolution(@PathVariable String problemId, @PathVariable String solutionId, @RequestParam String userId) {
        problemService.transferSolution(problemId, solutionId, userId);
    }

    @DeleteMapping("/{problemId}/solutions/{solutionId}")
    public void deleteSolution(@PathVariable String problemId, @PathVariable String solutionId) {
        problemService.deleteSolution(problemId, solutionId);
    }
}
