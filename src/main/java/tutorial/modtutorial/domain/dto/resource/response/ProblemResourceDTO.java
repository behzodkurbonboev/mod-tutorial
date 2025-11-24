package tutorial.modtutorial.domain.dto.resource.response;

import lombok.Getter;
import lombok.Setter;
import tutorial.modtutorial.domain.dto.forum.response.ForumPublicDTO;
import tutorial.modtutorial.domain.dto.forum.response.ProblemPublicDTO;
import tutorial.modtutorial.domain.dto.forum.response.SolutionPublicDTO;

import java.util.List;


@Getter
@Setter
public class ProblemResourceDTO {
    private ForumPublicDTO forum;
    private ProblemPublicDTO problem;
    private List<SolutionPublicDTO> solutions;

    public ProblemResourceDTO(ForumPublicDTO forum, ProblemPublicDTO problem, List<SolutionPublicDTO> solutions) {
        this.forum = forum;
        this.problem = problem;
        this.solutions = solutions;
    }
}
