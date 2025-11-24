package tutorial.modtutorial.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorial.modtutorial.service.FileService;


@RestController
@RequestMapping("api/v1/private/files")
public class FilePrivateController {
    private final FileService fileService;

    public FilePrivateController(FileService fileService) {
        this.fileService = fileService;
    }


    @DeleteMapping("/{fileId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable String fileId) {
        return ResponseEntity.ok(fileService.deleteById(fileId));
    }
}
