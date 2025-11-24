package tutorial.modtutorial.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tutorial.modtutorial.domain.entity.Article;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface ArticleRepository extends BaseRepository<Article> {
    Optional<Article> findByCreatorIdAndId(String userId, String articleId);
    List<Article> findAllByOrderByUpdatedDateDesc();

    @Query(value = """
            SELECT * FROM article
            INNER JOIN (
                SELECT article_id FROM articles_users_saved
                WHERE user_id = :userId
            ) aus ON article.id = aus.article_id
            WHERE article.created_date < :minDate AND article.visible = true
            ORDER BY article.created_date DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Article> findArticlesSavedByUser(@Param("userId") String userId, @Param("minDate") LocalDateTime minDate, @Param("limit") int limit);

    @Query(value = """
            SELECT * FROM article
            WHERE creator_id = :authorId AND created_date < :minDate AND visible = true
            ORDER BY created_date DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Article> findArticlesCreatedByUser(@Param("authorId") String authorId, @Param("minDate") LocalDateTime minDate, @Param("limit") int limit);

    @Query(value = """
            SELECT count(id) FROM article
            WHERE creator_id = :authorId AND visible = true
            """, nativeQuery = true)
    int countVisibleArticlesByAuthorId(@Param("authorId") String authorId);


    // article like:
    @Query(value = "SELECT update_article_liked(:articleId, :userId)", nativeQuery = true)
    void addOrDeleteLiked(@Param("articleId") String articleId, @Param("userId") String userId);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1 FROM articles_users_liked
                WHERE article_id = :articleId AND user_id = :userId
            )
            """, nativeQuery = true)
    boolean isLikedByUser(@Param("articleId") String articleId, @Param("userId") String userId);


    // article save:
    @Query(value = "SELECT update_article_saved(:articleId, :userId)", nativeQuery = true)
    void addOrDeleteSaved(@Param("articleId") String articleId, @Param("userId") String userId);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1 FROM articles_users_saved
                WHERE article_id = :articleId AND user_id = :userId
            )
            """, nativeQuery = true)
    boolean isSavedByUser(@Param("articleId") String articleId, @Param("userId") String userId);


    // article see:
    @Query(value = "SELECT update_article_seen(:articleId, :userId)", nativeQuery = true)
    void addOrNothingSeen(@Param("articleId") String articleId, @Param("userId") String userId);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1 FROM articles_users_seen
                WHERE article_id = :articleId AND user_id = :userId
            )
            """, nativeQuery = true)
    boolean isSeenByUser(@Param("articleId") String articleId, @Param("userId") String userId);


    // article share:
    @Modifying
    @Query(value = "UPDATE article SET share_count = share_count + 1 WHERE id = :articleId", nativeQuery = true)
    void incrementShareCount(@Param("articleId") String articleId);
}
