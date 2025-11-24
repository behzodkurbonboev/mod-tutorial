package tutorial.modtutorial.controller.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tutorial.modtutorial.domain.dto.article.request.ArticleFilter;
import tutorial.modtutorial.domain.dto.article.response.ArticleContentDTO;
import tutorial.modtutorial.domain.dto.article.response.ArticlePublicDTO;
import tutorial.modtutorial.domain.dto.general.response.Slice;
import tutorial.modtutorial.service.ArticleService;


@Validated
@RestController
@RequestMapping("/api/v1/public/articles")
public class ArticlePublicController {
    private final ArticleService articleService;

    public ArticlePublicController(ArticleService articleService) {
        this.articleService = articleService;
    }


    @PostMapping
    public ResponseEntity<Slice<ArticlePublicDTO>> getArticlesPublic(@RequestBody ArticleFilter filter) {
        return new ResponseEntity<>(articleService.getArticlesPublic(filter), HttpStatus.OK);
    }

    @PostMapping("/saved")
    public ResponseEntity<Slice<ArticlePublicDTO>> getArticlesSavedByUser(@RequestBody ArticleFilter filter) {
        return new ResponseEntity<>(articleService.getArticlesSavedByUser(filter), HttpStatus.OK);
    }

    @PostMapping("/by-author")
    public ResponseEntity<Slice<ArticlePublicDTO>> getArticlesCreatedByUser(@RequestBody ArticleFilter filter) {
        return new ResponseEntity<>(articleService.getArticlesCreatedByUser(filter), HttpStatus.OK);
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleContentDTO> getArticleContent(@PathVariable String articleId) {
        return new ResponseEntity<>(articleService.getArticleContent(articleId), HttpStatus.OK);
    }

    @PutMapping("/{articleId}/like")
    public void handleLike(@PathVariable String articleId) {
        articleService.handleLike(articleId);
    }

    @PutMapping("/{articleId}/save")
    public void handleSave(@PathVariable String articleId) {
        articleService.handleSave(articleId);
    }

    @PutMapping("/{articleId}/see")
    public void handleSee(@PathVariable String articleId) {
        articleService.handleSee(articleId);
    }

    @PutMapping("/{articleId}/share")
    public void handleShare(@PathVariable String articleId) {
        articleService.handleShare(articleId);
    }
}
