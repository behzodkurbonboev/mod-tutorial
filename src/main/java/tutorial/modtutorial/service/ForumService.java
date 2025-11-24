package tutorial.modtutorial.service;

import tutorial.modtutorial.domain.dto.forum.request.ForumDTO;
import tutorial.modtutorial.domain.dto.forum.response.ForumProtectedDTO;
import tutorial.modtutorial.domain.dto.forum.response.ForumPublicDTO;

import java.util.List;


public interface ForumService {
    void updateVisibleProblemsCount(String forumId);


    // ======================= ADMIN ZONE =======================
    ForumProtectedDTO getForum(String forumId);
    void createForum(ForumDTO dto);
    void updateForum(String forumId, ForumDTO dto);
    void deleteForum(String forumId);


    // ===================== MODERATOR ZONE =====================
    List<ForumProtectedDTO> getForumsProtected();


    // ======================== USER ZONE ========================
    List<ForumPublicDTO> getForumsPublic();
}
