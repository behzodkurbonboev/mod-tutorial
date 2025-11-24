package tutorial.modtutorial.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorial.modtutorial.constant.Date;
import tutorial.modtutorial.domain.dto.forum.response.ForumPublicDTO;
import tutorial.modtutorial.domain.dto.forum.response.ProblemPublicDTO;
import tutorial.modtutorial.domain.dto.forum.response.SolutionPublicDTO;
import tutorial.modtutorial.domain.dto.resource.response.ProblemResourceDTO;
import tutorial.modtutorial.domain.entity.Forum;
import tutorial.modtutorial.domain.entity.Problem;
import tutorial.modtutorial.domain.entity.Solution;
import tutorial.modtutorial.repository.ForumRepository;
import tutorial.modtutorial.repository.ProblemRepository;
import tutorial.modtutorial.repository.SolutionRepository;
import tutorial.modtutorial.service.ResourceService;
import tutorial.modtutorial.service.UserService;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ResourceServiceImpl implements ResourceService {
    private final ForumRepository forumRepository;
    private final ProblemRepository problemRepository;
    private final SolutionRepository solutionRepository;
    private final UserService userService;
    private final TagMapper tagMapper;

    public ResourceServiceImpl(
            ForumRepository forumRepository, ProblemRepository problemRepository,
            SolutionRepository solutionRepository, UserService userService,
            TagMapper tagMapper
    ) {
        this.forumRepository = forumRepository;
        this.problemRepository = problemRepository;
        this.solutionRepository = solutionRepository;
        this.userService = userService;
        this.tagMapper = tagMapper;
    }


    @Override
    @Transactional(readOnly = true)
    public ProblemResourceDTO get(String resourceId, String resourceType) {
        if (!"PROBLEM".equalsIgnoreCase(resourceType)) {
            throw new IllegalArgumentException("Specified type is not valid: type = '" + resourceType + "'");
        }

        Problem problem = problemRepository.findById(resourceId).orElseThrow(() -> new EntityNotFoundException("Problem with 'id'=[" + resourceId + "] not found."));
        Forum forum = forumRepository.findById(problem.getForumId()).orElseThrow(() -> new EntityNotFoundException("Forum with 'id'=[" + problem.getForumId() + "] not found."));
        List<Solution> solutions = solutionRepository.findAllByProblemIdAndVisibleTrueOrderByCreatedDateAsc(problem.getId());

        return new ProblemResourceDTO(
                toForumResource(forum),
                toProblemResource(problem),
                solutions.stream()
                        .map(this::toSolutionResource)
                        .collect(Collectors.toList())
        );
    }

    private ForumPublicDTO toForumResource(Forum forum) {
        ForumPublicDTO dto = new ForumPublicDTO();

        dto.setName(forum.getName());

        return dto;
    }

    private ProblemPublicDTO toProblemResource(Problem problem) {
        ProblemPublicDTO dto = new ProblemPublicDTO();

        dto.setId(problem.getId());
        dto.setTitle(problem.getTitle());
        dto.setSource(problem.getSource());
        dto.setContent(problem.getContent());
        dto.setShareUrl(problem.getShareUrl());
        dto.setTags(tagMapper.toNameList(problem.getTags()));
        dto.setDate(problem.getCreatedDate().format(Date.FORMAT));
        dto.setAuthor(userService.toAuthor(problem.getCreatorId()));
        dto.setLikeCount(problem.getLikeCount());
        dto.setSaveCount(problem.getSaveCount());
        dto.setSeenCount(problem.getSeenCount());
        dto.setShareCount(problem.getShareCount());
        dto.setSolutionCount(problem.getSolutionCount());
        dto.setAccessType(problem.getAccessType());

        return dto;
    }

    private SolutionPublicDTO toSolutionResource(Solution solution) {
        SolutionPublicDTO dto = new SolutionPublicDTO();

        dto.setId(solution.getId());
        dto.setDate(solution.getCreatedDate().format(Date.FORMAT));
        dto.setContent(solution.getContent());
        dto.setLikeCount(solution.getLikeCount());
        dto.setAuthor(userService.toAuthor(solution.getCreatorId()));

        return dto;
    }
}
