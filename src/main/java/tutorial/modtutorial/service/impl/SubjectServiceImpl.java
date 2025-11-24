package tutorial.modtutorial.service.impl;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorial.modtutorial.constant.SpecialUsers;
import tutorial.modtutorial.domain.dto.general.request.NameDTO;
import tutorial.modtutorial.domain.dto.general.response.SubjectDTO;
import tutorial.modtutorial.domain.dto.general.response.TagDTO;
import tutorial.modtutorial.domain.entity.Subject;
import tutorial.modtutorial.domain.entity.Tag;
import tutorial.modtutorial.repository.SubjectRepository;
import tutorial.modtutorial.repository.TagRepository;
import tutorial.modtutorial.service.SubjectService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static tutorial.modtutorial.utils.SecurityUtils.getCurrentUserId;


@Service
public class SubjectServiceImpl implements SubjectService {
    @PersistenceContext
    private EntityManager entityManager;
    private final SubjectRepository subjectRepository;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public SubjectServiceImpl(
            SubjectRepository subjectRepository,
            TagRepository tagRepository, TagMapper tagMapper
    ) {
        this.subjectRepository = subjectRepository;
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }


    @Override
    @Transactional(readOnly = true)
    public SubjectDTO toDTO(String subjectId) {
        Subject subject = getSubjectByIdElseThrow(subjectId);

        SubjectDTO dto = new SubjectDTO();

        dto.setId(subject.getId());
        dto.setName(subject.getName());

        return dto;
    }

    @Override
    public void throwIfNotExists(String subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new EntityNotFoundException("Subject with 'id'=[" + subjectId + "] not found.");
        }
    }


    // ======================= ADMIN ZONE =======================
    @Override
    @Transactional
    public SubjectDTO createSubject(NameDTO dto) {
        // if there is a subject with the same name just return existing one else create new one
        Subject subject = subjectRepository.findByName(dto.getName())
                .orElseGet(() -> {
                    Subject s = new Subject();
                    s.setName(dto.getName());

                    return subjectRepository.save(s);
                });

        return toDTO(subject);
    }

    @Override
    @Transactional
    public void updateSubject(String subjectId, NameDTO dto) {
        // get subject by id
        Subject subject = getSubjectByIdElseThrow(subjectId);

        // check if new name for the subject is unique across platform
        if (subjectRepository.findByName(dto.getName()).isPresent()) {
            throw new EntityExistsException("subject already exist with name = " + dto.getName());
        }

        // update name of the subject to new name
        subject.setName(dto.getName());
        subjectRepository.save(subject);
    }

    @Override
    @Transactional
    public void deleteSubject(String id) {
        // check if subject exists
        getSubjectByIdElseThrow(id);

        try {
            // delete subject if it does not have connection with any resource(s)
            // TODO: check if subject does not have tags
            subjectRepository.deleteById(id);
        } catch (Exception ex) {
            throw new RuntimeException("This 'subject' has connection with other resources");
        }
    }


    // ===================== MODERATOR ZONE =====================
    @Override
    @Transactional(readOnly = true)
    public SubjectDTO getSubject(String id) {
        return toDTO(getSubjectByIdElseThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectDTO> getSubjects() {
        return subjectRepository
                .findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addTag(String subjectId, NameDTO dto) {
        // check if subject exists
        getSubjectByIdElseThrow(subjectId);

        // if there is a tag with the same name in required subject just return, else create new one
        tagRepository.findByNameAndSubjectId(dto.getName(), subjectId).orElseGet(() -> {
            Tag tag = new Tag();

            tag.setSubjectId(subjectId);
            tag.setName(dto.getName());

            return tagRepository.save(tag);
        });
    }

    @Override
    @Transactional
    public void updateTag(String subjectId, String tagId, NameDTO dto) {
        // get the tag to be updated
        Tag changingTag = getTagIfAllowed(tagId, subjectId);

        // check if new name for the tag is unique in its subject
        if (tagRepository.findByNameAndSubjectId(dto.getName(), subjectId).isPresent()) {
            throw new EntityExistsException(String.format("Tag already exist with 'name' = %s in Subject with 'id' = %s", dto.getName(), subjectId));
        }

        // update name of the tag to new name
        changingTag.setName(dto.getName());
        tagRepository.save(changingTag);
    }

    @Override
    @Transactional
    public void deleteTag(String subjectId, String tagId) {
        // check if tag exists
        getTagIfAllowed(tagId, subjectId);

        try {
            // delete tag if it does not have connection with any resource(s)
            // TODO: check if tag is not used in any resource
            tagRepository.deleteByIdAndSubjectId(tagId, subjectId);
        } catch (Exception ex) {
            throw new RuntimeException("This tag depends on other resources");
        }
    }


    // ======================== USER ZONE ========================
    @Override
    @Transactional(readOnly = true)
    public List<SubjectDTO> getSubjectsForArticlesFilter() {
        Query query = entityManager
                .createNativeQuery("""
                        SELECT tag_id, COUNT(tag_id) FROM articles_tags
                        WHERE article_id IN (SELECT id FROM article WHERE visible = true)
                        GROUP BY tag_id;
                    """);

        List<Object[]> tagIdCount = query.getResultList();

        Map<String, List<TagDTO>> subjectIdTags = new HashMap<>();
        for (Object[] pair : tagIdCount) {
            String tagId = (String) pair[0];
            int count = ((Long) pair[1]).intValue();

            Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new EntityNotFoundException("Tag with id = '" + tagId + "' not found"));
            String subjectId = tag.getSubjectId();
            List<TagDTO> tagDTOs = subjectIdTags.getOrDefault(subjectId, new ArrayList<>());
            tagDTOs.add(tagMapper.toDTO(tagId, count));

            subjectIdTags.put(subjectId, tagDTOs);
        }

        List<SubjectDTO> result = new ArrayList<>();
        subjectIdTags.forEach((subjectId, tagDTOs) -> {
            String subjectName = getSubjectByIdElseThrow(subjectId).getName();

            result.add(SubjectDTO.of(subjectId, subjectName, tagDTOs));
        });

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectDTO> getSubjectsForSpecialBlocksFilter() {
        Query query = entityManager
                .createNativeQuery("""
                        SELECT subject_id, COUNT(id) FROM special_block
                        WHERE visible = true
                        GROUP BY subject_id;
                    """);

        List<Object[]> subjectIdCount = query.getResultList();

        List<SubjectDTO> result = new ArrayList<>();
        for (Object[] pair : subjectIdCount) {
            String subjectId = (String) pair[0];
            int count = ((Long) pair[1]).intValue();

            String subjectName = getSubjectByIdElseThrow(subjectId).getName();

            result.add(SubjectDTO.of(subjectId, subjectName, count));
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagDTO> getTagsForForumFilter(String subjectId, String forumId) {
        Query query = entityManager
                .createNativeQuery("""
                        SELECT tag_id, COUNT(tag_id) FROM problems_tags
                        WHERE problem_id IN (SELECT id FROM problem WHERE forum_id = :forumId AND visible = true)
                        GROUP BY tag_id;
                    """);
        query.setParameter("forumId", forumId);

        List<Object[]> tagIdCount = query.getResultList();

        List<TagDTO> result = new ArrayList<>();
        for (Object[] pair : tagIdCount) {
            String tagId = (String) pair[0];
            int count = ((Long) pair[1]).intValue();

            result.add(tagMapper.toDTO(tagId, count));
        }

        return result;
    }


    private Subject getSubjectByIdElseThrow(String subjectId) {
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with 'subjectId' = " + subjectId));
    }

    private SubjectDTO toDTO(Subject subject) {
        SubjectDTO dto = new SubjectDTO();

        dto.setId(subject.getId());
        dto.setName(subject.getName());

        List<TagDTO> tagDTOs = subject.getTags().stream()
                .map(tagMapper::toDTO).toList();
        dto.setTags(tagDTOs);

        return dto;
    }

    private Tag getTagIfAllowed(String tagId, String subjectId) {
        Optional<Tag> optionalTag;

        if (SpecialUsers.contains(getCurrentUserId())) {
            optionalTag = tagRepository.findByIdAndSubjectId(tagId, subjectId);
        } else {
            optionalTag = tagRepository.findByIdAndSubjectIdAndCreatorId(tagId, subjectId, getCurrentUserId());
        }

        return optionalTag.orElseThrow(() -> new EntityNotFoundException(String.format("Tag not found with tagId = %s and SubjectId = %s ", tagId, subjectId)));
    }
}
