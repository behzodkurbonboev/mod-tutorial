package tutorial.modtutorial.service;

import tutorial.modtutorial.domain.dto.article.request.ArticleDTO;
import tutorial.modtutorial.domain.dto.article.request.ArticleFilter;
import tutorial.modtutorial.domain.dto.article.response.ArticleContentDTO;
import tutorial.modtutorial.domain.dto.article.response.ArticleProtectedDTO;
import tutorial.modtutorial.domain.dto.article.response.ArticlePublicDTO;
import tutorial.modtutorial.domain.dto.general.response.Slice;

import java.util.List;


public interface ArticleService {
    // ===================== MODERATOR ZONE =====================
    List<ArticleProtectedDTO> getArticlesProtected();
    void createArticle(ArticleDTO dto);
    void updateArticle(String articleId, ArticleDTO dto);
    void changeVisibility(String articleId);
    void transferArticle(String articleId, String userId);
    void deleteArticle(String articleId);


    // ======================== USER ZONE ========================
    Slice<ArticlePublicDTO> getArticlesPublic(ArticleFilter filter);
    Slice<ArticlePublicDTO> getArticlesSavedByUser(ArticleFilter filter);
    Slice<ArticlePublicDTO> getArticlesCreatedByUser(ArticleFilter filter);
    ArticleContentDTO getArticleContent(String articleId);
    void handleLike(String articleId);
    void handleSave(String articleId);
    void handleSee(String articleId);
    void handleShare(String articleId);
}
