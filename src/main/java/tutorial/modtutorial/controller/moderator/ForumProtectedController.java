package tutorial.modtutorial.controller.moderator;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorial.modtutorial.domain.dto.forum.response.ForumProtectedDTO;
import tutorial.modtutorial.service.ForumService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/protected/forums")
public class ForumProtectedController {
    private final ForumService forumService;

    public ForumProtectedController(ForumService forumService) {
        this.forumService = forumService;
    }


    @GetMapping
    public ResponseEntity<List<ForumProtectedDTO>> getForumsProtected() {
        return ResponseEntity.ok(forumService.getForumsProtected());
    }
}
