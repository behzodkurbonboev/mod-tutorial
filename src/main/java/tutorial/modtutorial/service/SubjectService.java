package tutorial.modtutorial.service;

import tutorial.modtutorial.domain.dto.general.request.NameDTO;
import tutorial.modtutorial.domain.dto.general.response.SubjectDTO;
import tutorial.modtutorial.domain.dto.general.response.TagDTO;

import java.util.List;


public interface SubjectService {
    SubjectDTO toDTO(String subjectId);
    void throwIfNotExists(String subjectId);


    // ======================= ADMIN ZONE =======================
    SubjectDTO createSubject(NameDTO dto);
    void updateSubject(String subjectId, NameDTO dto);
    void deleteSubject(String id);


    // ===================== MODERATOR ZONE =====================
    SubjectDTO getSubject(String id);
    List<SubjectDTO> getSubjects();
    void addTag(String subjectId, NameDTO dto);
    void updateTag(String subjectId, String tagId, NameDTO dto);
    void deleteTag(String subjectId, String tagId);


    // ======================== USER ZONE ========================
    List<SubjectDTO> getSubjectsForArticlesFilter();
    List<SubjectDTO> getSubjectsForSpecialBlocksFilter();
    List<TagDTO> getTagsForForumFilter(String subjectId, String forumId);
}
