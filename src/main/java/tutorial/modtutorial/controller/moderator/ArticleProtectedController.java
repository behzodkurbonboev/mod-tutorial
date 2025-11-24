package tutorial.modtutorial.controller.moderator;

import org.springframework.http.HttpStatus;
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
import tutorial.modtutorial.domain.dto.article.request.ArticleDTO;
import tutorial.modtutorial.domain.dto.article.response.ArticleProtectedDTO;
import tutorial.modtutorial.service.ArticleService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/protected/articles")
public class ArticleProtectedController {
    private final ArticleService articleService;

    public ArticleProtectedController(ArticleService articleService) {
        this.articleService = articleService;
    }


    @GetMapping
    public ResponseEntity<List<ArticleProtectedDTO>> getArticlesProtected() {
        return new ResponseEntity<>(articleService.getArticlesProtected(), HttpStatus.OK);
    }

    @PostMapping
    public void createArticle(@RequestBody ArticleDTO dto) {
        articleService.createArticle(dto);
    }

    @PutMapping("/{articleId}")
    public void updateArticle(@PathVariable String articleId, @RequestBody ArticleDTO dto) {
        articleService.updateArticle(articleId, dto);
    }

    @PutMapping("/{articleId}/visibility")
    public void changeVisibility(@PathVariable String articleId) {
        articleService.changeVisibility(articleId);
    }

    @PutMapping("/{articleId}/transfer")
    public void transferArticle(@PathVariable String articleId, @RequestParam String userId) {
        articleService.transferArticle(articleId, userId);
    }

    @DeleteMapping("/{articleId}")
    public void deleteArticle(@PathVariable String articleId) {
        articleService.deleteArticle(articleId);
    }
}
