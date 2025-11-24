package tutorial.modtutorial.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorial.modtutorial.constant.Date;
import tutorial.modtutorial.constant.FilesUrls;
import tutorial.modtutorial.constant.SpecialUsers;
import tutorial.modtutorial.domain.dto.forum.request.ProblemDTO;
import tutorial.modtutorial.domain.dto.forum.request.ProblemFilter;
import tutorial.modtutorial.domain.dto.forum.request.SolutionDTO;
import tutorial.modtutorial.domain.dto.forum.response.ForumProtectedDTO;
import tutorial.modtutorial.domain.dto.forum.response.ProblemProtectedDTO;
import tutorial.modtutorial.domain.dto.forum.response.SolutionProtectedDTO;
import tutorial.modtutorial.domain.dto.forum.response.ProblemPublicDTO;
import tutorial.modtutorial.domain.dto.forum.response.SolutionPublicDTO;
import tutorial.modtutorial.domain.dto.general.response.Slice;
import tutorial.modtutorial.domain.entity.Problem;
import tutorial.modtutorial.domain.entity.Solution;
import tutorial.modtutorial.repository.ProblemRepository;
import tutorial.modtutorial.repository.SolutionRepository;
import tutorial.modtutorial.service.FileService;
import tutorial.modtutorial.service.ForumService;
import tutorial.modtutorial.service.ProblemService;
import tutorial.modtutorial.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static tutorial.modtutorial.utils.SecurityUtils.getCurrentUserId;
import static tutorial.modtutorial.utils.TextFieldUtils.isNotEmpty;
import static tutorial.modtutorial.utils.TextFieldUtils.validateTextFields;


@Service
public class ProblemServiceImpl implements ProblemService {
    @PersistenceContext
    private EntityManager entityManager;
    private final ProblemRepository problemRepository;
    private final SolutionRepository solutionRepository;
    private final ForumService forumService;
    private final FileService fileService;
    private final UserService userService;
    private final TagMapper tagMapper;

    private static final int LIMIT = 10;

    public ProblemServiceImpl(
            ProblemRepository problemRepository, ForumService forumService,
            FileService fileService, UserService userService,
            SolutionRepository solutionRepository, TagMapper tagMapper
    ) {
        this.problemRepository = problemRepository;
        this.fileService = fileService;
        this.userService = userService;
        this.solutionRepository = solutionRepository;
        this.forumService = forumService;
        this.tagMapper = tagMapper;
    }


    // ===================== MODERATOR ZONE =====================
    @Override
    @Transactional(readOnly = true)
    public List<ProblemProtectedDTO> getProblemsProtected(String forumId) {
        return problemRepository.findAllByForumIdOrderByUpdatedDateDesc(forumId).stream()
                .map(this::toProblemProtectedDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createProblem(ProblemDTO dto) {
        ForumProtectedDTO forum = forumService.getForum(dto.getForumId());
        validateTextFields(dto.getTitle(), 10);
        validateTextFields(dto.getContent(), 20);
        FilesUrls.validateUrl(dto.getShareUrl());

        Problem problem = new Problem();

        problem.setTitle(dto.getTitle());
        problem.setSource(dto.getSource());
        problem.setForumId(forum.getId());
        problem.setTags(tagMapper.toEntitiesSet(dto.getTagsId(), forum.getSubject().getId()));
        problem.setContent(dto.getContent());
        problem.setShareUrl(dto.getShareUrl());
        problem.setVisible(false);
        problem.setSortDate(LocalDateTime.now());

        problemRepository.save(problem);

        forumService.updateVisibleProblemsCount(forum.getId());
    }

    @Override
    @Transactional
    public void updateProblem(String problemId, ProblemDTO dto) {
        Problem problem = getProblemIfAllowed(problemId);
        String oldShareUrl = problem.getShareUrl();

        if (isNotEmpty(dto.getTitle())) {
            validateTextFields(dto.getTitle(), 10);
            problem.setTitle(dto.getTitle());
        }

        problem.setSource(dto.getSource());

        if (notEmpty(dto.getTagsId())) {
            ForumProtectedDTO forum = forumService.getForum(problem.getForumId());
            problem.setTags(tagMapper.toEntitiesSet(dto.getTagsId(), forum.getSubject().getId()));
        }

        if (isNotEmpty(dto.getContent())) {
            validateTextFields(dto.getContent(), 20);
            FilesUrls.validateUrl(dto.getShareUrl());

            problem.setShareUrl(dto.getShareUrl());
            problem.setContent(dto.getContent());
        }

        problemRepository.save(problem);

        // if everything is ok till this point, then delete old image
        if (dto.getShareUrl() != null && !Objects.equals(oldShareUrl, dto.getShareUrl())) {
            fileService.deleteByUrl(oldShareUrl);
        }
    }

    @Override
    @Transactional
    public void changeProblemVisibility(String problemId) {
        Problem problem = getProblemIfAllowed(problemId);

        problem.setVisible(!problem.isVisible());

        forumService.updateVisibleProblemsCount(problem.getForumId());

        // update posts (problems + solutions) count for problem author
        int count = problemRepository.countVisiblePostsByAuthorId(problem.getCreatorId());
        userService.updateResourceCount(problem.getCreatorId(), "posts", count);

        // update posts (problems + solutions) count for solutions' authors
        solutionRepository.findAllByProblemIdOrderByCreatedDateAsc(problemId).forEach(sol -> {
            String authorId = sol.getCreatorId();

            int postsCount = problemRepository.countVisiblePostsByAuthorId(authorId);
            userService.updateResourceCount(authorId, "posts", postsCount);
        });
    }

    @Override
    @Transactional
    public void transferProblem(String problemId, String userId) {
        userService.validate(userId);

        Problem problem = getProblemIfAllowed(problemId);
        if (Objects.equals(problem.getCreatorId(), userId)) {
            return;
        }

        if (problem.isVisible()) {
            changeProblemVisibility(problemId);
        }

        problem.setCreatorId(userId);
    }

    @Override
    @Transactional
    public void deleteProblem(String problemId) {
        Problem problem = getProblemIfAllowed(problemId);

        problem.setVisible(false);
        problem.setDeleted(true);

        forumService.updateVisibleProblemsCount(problem.getForumId());

        // update posts (problems + solutions) count for problem author
        int count = problemRepository.countVisiblePostsByAuthorId(problem.getCreatorId());
        userService.updateResourceCount(problem.getCreatorId(), "posts", count);

        // update posts (problems + solutions) count for solutions' authors
        solutionRepository.findAllByProblemIdOrderByCreatedDateAsc(problemId).forEach(sol -> {
            String authorId = sol.getCreatorId();

            int postsCount = problemRepository.countVisiblePostsByAuthorId(authorId);
            userService.updateResourceCount(authorId, "posts", postsCount);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolutionProtectedDTO> getSolutionsProtected(String problemId) {
        return solutionRepository.findAllByProblemIdOrderByCreatedDateAsc(problemId).stream()
                .map(this::toSolutionProtectedDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createSolution(String problemId, SolutionDTO dto) {
        // do validation
        Problem problem = getProblemOrElseThrow(problemId);
        validateTextFields(dto.getContent(), 20);

        Solution solution = new Solution();

        solution.setProblemId(problemId);
        solution.setContent(dto.getContent());
        solution.setVisible(false);

        solutionRepository.save(solution);
    }

    @Override
    @Transactional
    public void updateSolution(String problemId, String solutionId, SolutionDTO dto) {
        // do validation
        verifyProblemExistsOrElseThrow(problemId);

        Solution solution = getSolutionIfAllowed(solutionId);

        if (dto.getContent() != null) {
            validateTextFields(dto.getContent(), 20);
            solution.setContent(dto.getContent());
        }
    }

    @Override
    @Transactional
    public void changeSolutionVisibility(String problemId, String solutionId) {
        Solution solution = getSolutionIfAllowed(solutionId);

        solution.setVisible(!solution.isVisible());

        // update problems' solution count and date:
        Problem problem = getProblemOrElseThrow(solution.getProblemId());
        updateVisibleSolutionsCount(problem.getId());
        updateProblemsSortDate(problem);

        int count = problemRepository.countVisiblePostsByAuthorId(solution.getCreatorId());
        userService.updateResourceCount(solution.getCreatorId(), "posts", count);
    }

    @Override
    @Transactional
    public void transferSolution(String problemId, String solutionId, String userId) {
        userService.validate(userId);

        Solution solution = getSolutionIfAllowed(solutionId);
        if (Objects.equals(solution.getCreatorId(), userId)) {
            return;
        }

        if (solution.isVisible()) {
            changeSolutionVisibility(problemId, solutionId);
        }

        solution.setCreatorId(userId);
    }

    @Override
    @Transactional
    public void deleteSolution(String problemId, String solutionId) {
        Solution solution = getSolutionIfAllowed(solutionId);

        solution.setVisible(false);
        solution.setDeleted(true);

        Problem problem = getProblemOrElseThrow(solution.getProblemId());
        updateVisibleSolutionsCount(problem.getId());
        updateProblemsSortDate(problem);

        int count = problemRepository.countVisiblePostsByAuthorId(solution.getCreatorId());
        userService.updateResourceCount(solution.getCreatorId(), "posts", count);
    }


    // ======================== USER ZONE ========================
    @Override
    @Transactional(readOnly = true)
    public Slice<ProblemPublicDTO> getProblemsPublic(ProblemFilter filter) {
        String userId = getCurrentUserId();

        // construct sql query:
        String sql = "SELECT * FROM problem WHERE forum_id = :forumId";
        String tagsFilter = " AND id IN (SELECT p.problem_id FROM problems_tags AS p WHERE tag_id IN :tagsId)";

        if (notEmpty(filter.getTagsId())) {
            sql += tagsFilter;
        }

        sql += " AND sort_date < :minDate AND visible = true ORDER BY sort_date DESC LIMIT :limit";

        Query query = entityManager.createNativeQuery(sql, Problem.class);
        setParams(query, filter);

        // get result and construct response
        List<Problem> problems = query.getResultList();
        LocalDateTime minDate = problems.isEmpty() ? Date.MIN_DATE : problems.getLast().getSortDate();

        return Slice.of(
                problems.stream()
                        .map(article -> toProblemPublicDTO(article, userId))
                        .collect(Collectors.toList()),
                minDate,
                LIMIT
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<ProblemPublicDTO> getProblemsSavedByUser(ProblemFilter filter) {
        String userId = getCurrentUserId();

        List<Problem> problems = problemRepository.findProblemsSavedByUser(userId, filter.getMinDate(), LIMIT);
        LocalDateTime minTime = problems.isEmpty() ? Date.MIN_DATE : problems.getLast().getSortDate();

        return Slice.of(
                problems.stream()
                        .map(problem -> toProblemPublicDTO(problem, userId))
                        .collect(Collectors.toList()),
                minTime,
                LIMIT
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<ProblemPublicDTO> getProblemsCreatedByUser(ProblemFilter filter) {
        String userId = getCurrentUserId();

        List<Problem> problems = problemRepository.findProblemsCreatedByUser(filter.getAuthorId(), filter.getMinDate(), LIMIT);
        LocalDateTime minTime = problems.isEmpty() ? Date.MIN_DATE : problems.getLast().getSortDate();

        return Slice.of(
                problems.stream()
                        .map(problem -> toProblemPublicDTO(problem, userId))
                        .collect(Collectors.toList()),
                minTime,
                LIMIT
        );
    }

    @Override
    @Transactional
    public void handleProblemLike(String problemId) {
        verifyProblemExistsOrElseThrow(problemId);
        problemRepository.addOrDeleteLiked(problemId, getCurrentUserId());
    }

    @Override
    @Transactional
    public void handleProblemSave(String problemId) {
        verifyProblemExistsOrElseThrow(problemId);
        problemRepository.addOrDeleteSaved(problemId, getCurrentUserId());
    }

    @Override
    @Transactional
    public void handleProblemSee(String problemId) {
        verifyProblemExistsOrElseThrow(problemId);
        problemRepository.addOrNothingSeen(problemId, getCurrentUserId());
    }

    @Override
    @Transactional
    public void handleProblemShare(String problemId) {
        verifyProblemExistsOrElseThrow(problemId);
        problemRepository.incrementShareCount(problemId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolutionPublicDTO> getSolutionsPublic(String problemId) {
        String userId = getCurrentUserId();

        return solutionRepository.findAllByProblemIdAndVisibleTrueOrderByCreatedDateAsc(problemId).stream()
                .map(solution -> toSolutionPublicDTO(solution, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void handleSolutionLike(String problemId, String solutionId) {
        if (!solutionRepository.existsById(solutionId)) {
            throw new EntityNotFoundException("404");
        }

        solutionRepository.addOrDeleteLiked(solutionId, getCurrentUserId());
    }


    private void setParams(Query query, ProblemFilter filter) {
        if (notEmpty(filter.getTagsId())) {
            query.setParameter("tagsId", filter.getTagsId());
        }

        query.setParameter("minDate", filter.getMinDate());
        query.setParameter("limit", LIMIT);

        query.setParameter("forumId", filter.getForumId());
    }

    private void verifyProblemExistsOrElseThrow(String problemId) {
        if (!problemRepository.existsById(problemId)) {
            throw new EntityNotFoundException("Problem with 'id'=[" + problemId + "] not found.");
        }
    }

    private Problem getProblemOrElseThrow(String problemId) {
        return problemRepository.findById(problemId).orElseThrow(() -> new EntityNotFoundException("Problem with 'id'=[" + problemId + "] not found."));
    }

    private Problem getProblemIfAllowed(String problemId) {
        Optional<Problem> optionalProblem;

        if (SpecialUsers.contains(getCurrentUserId())) {
            optionalProblem = problemRepository.findById(problemId);
        } else {
            optionalProblem = problemRepository.findByCreatorIdAndId(getCurrentUserId(), problemId);
        }

        if (optionalProblem.isEmpty() || optionalProblem.get().isDeleted()) {
            throw new EntityNotFoundException("Problem with 'id'=[" + problemId + "] not found.");
        }

        return optionalProblem.get();
    }

    private void updateVisibleSolutionsCount(String problemId) {
        int count = solutionRepository.countVisibleSolutionsByProblemId(problemId);

        problemRepository.updateVisibleSolutionsCount(problemId, count);
    }

    private void updateProblemsSortDate(Problem problem) {
        List<Solution> solutions = solutionRepository.findAllByProblemIdAndVisibleTrueOrderByCreatedDateAsc(problem.getId());

        LocalDateTime time;
        if (solutions.isEmpty()) {
            time = problem.getCreatedDate();
        } else {
            time = solutions.getLast().getCreatedDate();
        }

        problemRepository.updateSortDate(problem.getId(), time);
    }

    private ProblemPublicDTO toProblemPublicDTO(Problem problem, String userId) {
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
        dto.setLikedByUser(problemRepository.isLikedByUser(problem.getId(), userId));
        dto.setSavedByUser(problemRepository.isSavedByUser(problem.getId(), userId));
        dto.setSeenByUser(problemRepository.isSeenByUser(problem.getId(), userId));
        dto.setAccessType(problem.getAccessType());

        return dto;
    }

    private ProblemProtectedDTO toProblemProtectedDTO(Problem problem) {
        ProblemProtectedDTO dto = new ProblemProtectedDTO();

        dto.setId(problem.getId());
        dto.setTitle(problem.getTitle());
        dto.setForumId(problem.getForumId());
        dto.setTags(tagMapper.toDTOList(problem.getTags()));
        dto.setSource(problem.getSource());
        dto.setAccessType(problem.getAccessType());
        dto.setShareUrl(problem.getShareUrl());
        dto.setLikeCount(problem.getLikeCount());
        dto.setSaveCount(problem.getSaveCount());
        dto.setSeenCount(problem.getSeenCount());
        dto.setShareCount(problem.getShareCount());
        dto.setContent(problem.getContent());
        dto.setVisible(problem.isVisible());
        dto.setSolutionCount(problem.getSolutionCount());
        dto.setCreatedDate(problem.getCreatedDate().format(Date.FORMAT));
        dto.setUpdatedDate(problem.getUpdatedDate().format(Date.FORMAT));
        dto.setCreatedBy(userService.toAuthor(problem.getCreatorId()));
        dto.setUpdatedBy(userService.toAuthor(problem.getUpdaterId()));
        dto.setLikedByUser(problemRepository.isLikedByUser(problem.getId(), getCurrentUserId()));
        dto.setSavedByUser(problemRepository.isSavedByUser(problem.getId(), getCurrentUserId()));
        dto.setSeenByUser(problemRepository.isSeenByUser(problem.getId(), getCurrentUserId()));

        return dto;
    }

    private Solution getSolutionIfAllowed(String solutionId) {
        Optional<Solution> solutionOptional;

        if (SpecialUsers.contains(getCurrentUserId())) {
            solutionOptional = solutionRepository.findById(solutionId);
        } else {
            solutionOptional = solutionRepository.findByCreatorIdAndId(getCurrentUserId(), solutionId);
        }

        if (solutionOptional.isEmpty() || solutionOptional.get().isDeleted()) {
            throw new EntityNotFoundException("Solution with 'id'=[" + solutionId + "] not found.");
        }

        return solutionOptional.get();
    }

    private SolutionPublicDTO toSolutionPublicDTO(Solution solution, String userId) {
        SolutionPublicDTO dto = new SolutionPublicDTO();

        dto.setId(solution.getId());
        dto.setDate(solution.getCreatedDate().format(Date.FORMAT));
        dto.setContent(solution.getContent());
        dto.setLikeCount(solution.getLikeCount());
        dto.setLikedByUser(solutionRepository.isLikedByUser(solution.getId(), userId));
        dto.setAuthor(userService.toAuthor(solution.getCreatorId()));

        return dto;
    }

    private SolutionProtectedDTO toSolutionProtectedDTO(Solution solution) {
        SolutionProtectedDTO dto=new SolutionProtectedDTO();
        dto.setId(solution.getId());
        dto.setProblemId(solution.getProblemId());
        dto.setContent(solution.getContent());
        dto.setVisible(solution.isVisible());
        dto.setCreatedDate(solution.getCreatedDate().format(Date.FORMAT));
        dto.setUpdatedDate(solution.getUpdatedDate().format(Date.FORMAT));
        dto.setCreatedBy(userService.toAuthor(solution.getCreatorId()));
        dto.setUpdatedBy(userService.toAuthor(solution.getUpdaterId()));

        return dto;
    }

    private static boolean notEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }
}
