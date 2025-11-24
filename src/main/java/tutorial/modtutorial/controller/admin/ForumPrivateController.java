package tutorial.modtutorial.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorial.modtutorial.domain.dto.forum.request.ForumDTO;
import tutorial.modtutorial.domain.dto.forum.response.ForumProtectedDTO;
import tutorial.modtutorial.service.ForumService;


@RestController
@RequestMapping("/api/v1/private/forums")
public class ForumPrivateController {
    private final ForumService forumService;

    public ForumPrivateController(ForumService forumService) {
        this.forumService = forumService;
    }


    @GetMapping("/{forumId}")
    public ResponseEntity<ForumProtectedDTO> getForum(@PathVariable String forumId) {
        return ResponseEntity.ok(forumService.getForum(forumId));
    }

    @PostMapping
    public void createForum(@RequestBody ForumDTO dto) {
        forumService.createForum(dto);
    }

    @PutMapping("/{forumId}")
    public void updateForum(@PathVariable String forumId, @RequestBody ForumDTO dto) {
        forumService.updateForum(forumId, dto);
    }

    @DeleteMapping("/{forumId}")
    public void deleteForum(@PathVariable String forumId) {
        forumService.deleteForum(forumId);
    }
}
