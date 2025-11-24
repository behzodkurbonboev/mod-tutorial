package tutorial.modtutorial.controller.moderator;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tutorial.modtutorial.domain.dto.test.request.SpecialBlockDTO;
import tutorial.modtutorial.domain.dto.test.response.SpecialBlockProtectedDTO;
import tutorial.modtutorial.domain.dto.test.response.SpecialTestProtectedDTO;
import tutorial.modtutorial.service.SpecialBlockService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/protected/special-blocks")
public class SpecialBlockProtectedController {
    private final SpecialBlockService blockService;

    public SpecialBlockProtectedController(SpecialBlockService blockService) {
        this.blockService = blockService;
    }


    @GetMapping
    public ResponseEntity<List<SpecialBlockProtectedDTO>> getSpecialBlocksProtected() {
        return ResponseEntity.ok(blockService.getSpecialBlocksProtected());
    }

    @GetMapping("/{blockId}")
    public ResponseEntity<List<SpecialTestProtectedDTO>> getSpecialBlock(@PathVariable String blockId) {
        return ResponseEntity.ok(blockService.getSpecialBlock(blockId));
    }

    @PostMapping
    public void createSpecialBlock(@RequestBody SpecialBlockDTO dto) {
        blockService.createSpecialBlock(dto);
    }

    @PutMapping("/{blockId}")
    public void updateSpecialBlock(@PathVariable String blockId, @RequestBody SpecialBlockDTO dto) {
        blockService.updateSpecialBlock(blockId, dto);
    }

    @PutMapping("/{blockId}/visibility")
    public void changeVisibility(@PathVariable String blockId) {
        blockService.changeVisibility(blockId);
    }

    @PutMapping("/{blockId}/transfer")
    public void transferSpecialBlock(@PathVariable String blockId, @RequestParam String userId) {
        blockService.transferSpecialBlock(blockId, userId);
    }

    @DeleteMapping("/{blockId}")
    public void deleteSpecialBlock(@PathVariable String blockId) {
        blockService.deleteSpecialBlock(blockId);
    }
}
