package tutorial.modtutorial.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorial.modtutorial.domain.dto.forum.response.ForumPublicDTO;
import tutorial.modtutorial.service.ForumService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/public/forums")
public class ForumPublicController {
    private final ForumService forumService;

    public ForumPublicController(ForumService forumService) {
        this.forumService = forumService;
    }


    @GetMapping
    public ResponseEntity<List<ForumPublicDTO>> getForumsPublic() {
        return ResponseEntity.ok(forumService.getForumsPublic());
    }
}
