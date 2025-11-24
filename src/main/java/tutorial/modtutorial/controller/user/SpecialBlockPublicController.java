package tutorial.modtutorial.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorial.modtutorial.domain.dto.general.response.Slice;
import tutorial.modtutorial.domain.dto.test.request.SpecialBlockFilter;
import tutorial.modtutorial.domain.dto.test.response.GroupedByDate;
import tutorial.modtutorial.domain.dto.test.response.SpecialBlockPublicDTO;
import tutorial.modtutorial.domain.dto.test.response.SpecialTestPublicDTO;
import tutorial.modtutorial.service.SpecialBlockService;

import java.util.List;
import java.util.Map;


@Validated
@RestController
@RequestMapping("/api/v1/public/special-blocks")
public class SpecialBlockPublicController {
    private final SpecialBlockService blockService;

    public SpecialBlockPublicController(SpecialBlockService blockService) {
        this.blockService = blockService;
    }


    @PostMapping("/sliced")
    public ResponseEntity<Slice<SpecialBlockPublicDTO>> getSpecialBlocksPublicSliced(@RequestBody SpecialBlockFilter filter) {
        return ResponseEntity.ok(blockService.getSpecialBlocksPublicSliced(filter));
    }

    @PostMapping("/by-author/sliced")
    public ResponseEntity<Slice<SpecialBlockPublicDTO>> getSpecialBlocksCreatedByUserSliced(@RequestBody SpecialBlockFilter filter) {
        return ResponseEntity.ok(blockService.getSpecialBlocksCreatedByUserSliced(filter));
    }

    @PostMapping
    @Deprecated(forRemoval = false)
    public ResponseEntity<Slice<GroupedByDate<SpecialBlockPublicDTO>>> getSpecialBlocksPublic(@RequestBody SpecialBlockFilter filter) {
        return ResponseEntity.ok(blockService.getSpecialBlocksPublic(filter));
    }

    @PostMapping("/by-author")
    @Deprecated(forRemoval = false)
    public ResponseEntity<Slice<GroupedByDate<SpecialBlockPublicDTO>>> getSpecialBlocksCreatedByUser(@RequestBody SpecialBlockFilter filter) {
        return ResponseEntity.ok(blockService.getSpecialBlocksCreatedByUser(filter));
    }

    @GetMapping("/{blockId}/start")
    public ResponseEntity<List<SpecialTestPublicDTO>> startSpecialBlock(@PathVariable String blockId) {
        return ResponseEntity.ok(blockService.startSpecialBlock(blockId));
    }

    @PutMapping("/{blockId}/finish")
    public ResponseEntity<Map<Integer, String>> finishSpecialBlock(@PathVariable String blockId, @RequestBody Map<Integer, Integer> key) {
        return ResponseEntity.ok(blockService.finishSpecialBlock(blockId, key));
    }

    @GetMapping("/{blockId}/analise")
    public ResponseEntity<List<SpecialTestPublicDTO>> analiseSpecialBlock(@PathVariable String blockId) {
        return ResponseEntity.ok(blockService.analiseSpecialBlock(blockId));
    }
}
