package tutorial.modtutorial.service;

import tutorial.modtutorial.domain.dto.forum.request.ProblemDTO;
import tutorial.modtutorial.domain.dto.forum.request.ProblemFilter;
import tutorial.modtutorial.domain.dto.forum.request.SolutionDTO;
import tutorial.modtutorial.domain.dto.forum.response.ProblemProtectedDTO;
import tutorial.modtutorial.domain.dto.forum.response.ProblemPublicDTO;
import tutorial.modtutorial.domain.dto.forum.response.SolutionProtectedDTO;
import tutorial.modtutorial.domain.dto.forum.response.SolutionPublicDTO;
import tutorial.modtutorial.domain.dto.general.response.Slice;

import java.util.List;


public interface ProblemService {
    // ===================== MODERATOR ZONE =====================
    List<ProblemProtectedDTO> getProblemsProtected(String forumId);
    void createProblem( ProblemDTO dto);
    void updateProblem(String problemId, ProblemDTO dto);
    void changeProblemVisibility(String problemId);
    void transferProblem(String problemId, String userId);
    void deleteProblem(String problemId);

    List<SolutionProtectedDTO> getSolutionsProtected(String problemId);
    void createSolution(String problemId, SolutionDTO dto);
    void updateSolution(String problemId, String solutionId, SolutionDTO dto);
    void changeSolutionVisibility(String problemId, String solutionId);
    void transferSolution(String problemId, String solutionId, String userId);
    void deleteSolution(String problemId, String solutionId);


    // ======================== USER ZONE ========================
    Slice<ProblemPublicDTO> getProblemsPublic(ProblemFilter filter);
    Slice<ProblemPublicDTO> getProblemsSavedByUser(ProblemFilter filter);
    Slice<ProblemPublicDTO> getProblemsCreatedByUser(ProblemFilter filter);
    void handleProblemLike(String problemId);
    void handleProblemSave(String problemId);
    void handleProblemSee(String problemId);
    void handleProblemShare(String problemId);
    List<SolutionPublicDTO> getSolutionsPublic(String problemId);
    void handleSolutionLike(String problemId, String solutionId);
}
