package tutorial.modtutorial.controller.moderator;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tutorial.modtutorial.service.FileService;


@RestController
@RequestMapping("api/v1/protected/files")
public class FileProtectedController {
    private final FileService fileService;

    public FileProtectedController(FileService fileService) {
        this.fileService = fileService;
    }


    @PostMapping
    public ResponseEntity<String> save(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(fileService.save(file));
    }
}
