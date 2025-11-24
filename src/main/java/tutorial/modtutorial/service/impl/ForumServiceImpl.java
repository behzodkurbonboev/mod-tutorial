package tutorial.modtutorial.service.impl;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorial.modtutorial.constant.Date;
import tutorial.modtutorial.constant.FilesUrls;
import tutorial.modtutorial.domain.dto.forum.request.ForumDTO;
import tutorial.modtutorial.domain.dto.forum.response.ForumProtectedDTO;
import tutorial.modtutorial.domain.dto.forum.response.ForumPublicDTO;
import tutorial.modtutorial.domain.entity.Forum;
import tutorial.modtutorial.repository.ForumRepository;
import tutorial.modtutorial.repository.ProblemRepository;
import tutorial.modtutorial.service.ForumService;
import tutorial.modtutorial.service.SubjectService;
import tutorial.modtutorial.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

import static tutorial.modtutorial.utils.SecurityUtils.getCurrentUserId;


@Service
public class ForumServiceImpl implements ForumService {
    private final ForumRepository forumRepository;
    private final ProblemRepository problemRepository;
    private final UserService userService;
    private final SubjectService subjectService;

    public ForumServiceImpl(
            ForumRepository forumRepository, ProblemRepository problemRepository,
            UserService userService, SubjectService subjectService
    ) {
        this.forumRepository = forumRepository;
        this.problemRepository = problemRepository;
        this.userService = userService;
        this.subjectService = subjectService;
    }


    @Override
    @Transactional
    public void updateVisibleProblemsCount(String forumId) {
        getForumOrElseThrow(forumId);
        int count = problemRepository.countVisibleProblemsByForumId(forumId);

        forumRepository.updateVisibleProblemsCount(forumId, count);
    }


    // ======================= ADMIN ZONE =======================
    @Override
    @Transactional(readOnly = true)
    public ForumProtectedDTO getForum(String forumId) {
        Forum forum = getForumOrElseThrow(forumId);

        return toProtectedDTO(forum);
    }

    @Override
    @Transactional
    public void createForum(ForumDTO dto) {
        // do verification
        subjectService.throwIfNotExists(dto.getSubjectId());

        // verify name is unique
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new RuntimeException("name of forum is missing");
        }
        if (forumRepository.findByName(dto.getName()).isPresent()) {
            throw new EntityExistsException("forum already exist with name = " + dto.getName());
        }

        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new RuntimeException("description of forum is missing");
        }

        // verify images source
        if (dto.getImageUrl() != null) {
            FilesUrls.validateUrl(dto.getImageUrl());
        }
        if (dto.getBackgroundImageUrl() != null) {
            FilesUrls.validateUrl(dto.getBackgroundImageUrl());
        }

        Forum forum = new Forum();

        forum.setName(dto.getName());
        forum.setSubjectId(dto.getSubjectId());
        forum.setDescription(dto.getDescription());
        forum.setImageUrl(dto.getImageUrl());
        forum.setBackgroundImageUrl(dto.getBackgroundImageUrl());
        forum.setAuthorId(getCurrentUserId());

        forumRepository.save(forum);
    }

    @Override
    @Transactional
    public void updateForum(String forumId, ForumDTO dto) {
        Forum forum = getForumOrElseThrow(forumId);

        if (dto.getName() != null) {
            // verify name is unique
            if (forumRepository.findByName(dto.getName()).isPresent()) {
                throw new EntityExistsException("forum already exist with name = " + dto.getName());
            }

            forum.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            if (dto.getDescription().isBlank()) {
                throw new RuntimeException("description of forum is missing");
            }

            forum.setDescription(dto.getDescription());
        }

        // verify images source if not null, otherwise just set
        if (dto.getImageUrl() != null) {
            FilesUrls.validateUrl(dto.getImageUrl());
        }
        if (dto.getBackgroundImageUrl() != null) {
            FilesUrls.validateUrl(dto.getBackgroundImageUrl());
        }

        forum.setImageUrl(dto.getImageUrl());
        forum.setBackgroundImageUrl(dto.getBackgroundImageUrl());
    }

    @Override
    @Transactional
    public void deleteForum(String forumId) {
        getForumOrElseThrow(forumId);

        int count = problemRepository.countProblemsByForumId(forumId);
        if (count > 0) {
            throw new RuntimeException("Forum has Posts [Problem, Solution]");
        }

        forumRepository.deleteById(forumId);
    }


    // ===================== MODERATOR ZONE =====================
    @Override
    @Transactional(readOnly = true)
    public List<ForumProtectedDTO> getForumsProtected() {
        return forumRepository
                .findAll()
                .stream()
                .map(this::toProtectedDTO)
                .collect(Collectors.toList());
    }


    // ======================== USER ZONE ========================
    @Override
    @Transactional(readOnly = true)
    public List<ForumPublicDTO> getForumsPublic() {
        return forumRepository
                .findAll()
                .stream()
                .map(this::toPublicDTO)
                .collect(Collectors.toList());
    }


    private Forum getForumOrElseThrow(String forumId) {
        return forumRepository.findById(forumId).orElseThrow(() -> new EntityNotFoundException("forum not found"));
    }

    private ForumPublicDTO toPublicDTO(Forum forum) {
        ForumPublicDTO dto = new ForumPublicDTO();

        dto.setId(forum.getId());
        dto.setName(forum.getName());
        dto.setDescription(forum.getDescription());
        dto.setImageUrl(forum.getImageUrl());
        dto.setBackgroundImageUrl(forum.getBackgroundImageUrl());
        dto.setSubjectId(forum.getSubjectId());
        dto.setNumOfPosts(forum.getNumOfPosts());

        return dto;
    }

    private ForumProtectedDTO toProtectedDTO(Forum forum) {
        ForumProtectedDTO dto = new ForumProtectedDTO();

        dto.setId(forum.getId());
        dto.setName(forum.getName());
        dto.setDescription(forum.getDescription());
        dto.setImageUrl(forum.getImageUrl());
        dto.setBackgroundImageUrl(forum.getBackgroundImageUrl());
        dto.setSubject(subjectService.toDTO(forum.getSubjectId()));
        dto.setNumOfPosts(problemRepository.countProblemsByForumId(forum.getId()));
        dto.setCreatedDate(forum.getCreatedDate().format(Date.FORMAT));
        dto.setUpdatedDate(forum.getUpdatedDate().format(Date.FORMAT));
        dto.setAuthor(userService.toAuthor(forum.getAuthorId()));
        dto.setCreatedBy(userService.toAuthor(forum.getCreatorId()));
        dto.setUpdatedBy(userService.toAuthor(forum.getUpdaterId()));

        return dto;
    }
}
