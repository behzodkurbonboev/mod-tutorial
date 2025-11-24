package tutorial.modtutorial.controller.user;

import io.minio.MinioClient;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorial.modtutorial.service.FileService;

import java.time.Duration;


@RestController
@RequestMapping("api/v1/public/files")
public class FilePublicController {
    private MinioClient client;
    private final FileService fileService;

    private static final MediaType type = MediaType.valueOf("image/png");
    private static final CacheControl control = CacheControl.maxAge(Duration.ofDays(30));

    public FilePublicController(FileService fileService) {
        this.fileService = fileService;
    }


    @GetMapping("/{fileId}")
    public ResponseEntity<?> getById(@PathVariable String fileId) {
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(type)
                .cacheControl(control)
                .body(fileService.getById(fileId));
    }
}
