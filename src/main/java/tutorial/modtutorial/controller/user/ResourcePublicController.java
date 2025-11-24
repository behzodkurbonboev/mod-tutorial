package tutorial.modtutorial.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorial.modtutorial.domain.dto.resource.response.ProblemResourceDTO;
import tutorial.modtutorial.service.ResourceService;


@RestController
@RequestMapping("api/v1/public/resources")
public class ResourcePublicController {
    private final ResourceService resourceService;

    public ResourcePublicController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProblemResourceDTO> getResource(@PathVariable String id, @RequestParam String type) {
        return ResponseEntity.ok(resourceService.get(id, type));
    }
}
