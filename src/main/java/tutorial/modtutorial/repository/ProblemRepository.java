package tutorial.modtutorial.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tutorial.modtutorial.domain.entity.Problem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface ProblemRepository extends BaseRepository<Problem> {
    Optional<Problem> findByCreatorIdAndId(String userId, String problemId);
    List<Problem> findAllByForumIdOrderByUpdatedDateDesc(String forumId);

    @Query(value = """
            SELECT * FROM problem
            INNER JOIN (
                SELECT problem_id FROM problems_users_saved
                WHERE user_id = :userId
            ) pus ON problem.id = pus.problem_id
            WHERE problem.sort_date < :minDate AND problem.visible = true
            ORDER BY problem.sort_date DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Problem> findProblemsSavedByUser(@Param("userId") String userId, @Param("minDate") LocalDateTime minDate, @Param("limit") int limit);

    @Query(value = """
            SELECT p.* FROM problem p
            WHERE (p.creator_id = :authorId OR (p.id IN (SELECT s.problem_id FROM solution s WHERE s.creator_id = :authorId AND s.visible = true))) AND p.sort_date < :minDate AND p.visible = true
            ORDER BY p.sort_date DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Problem> findProblemsCreatedByUser(@Param("authorId") String authorId, @Param("minDate") LocalDateTime minDate, @Param("limit") int limit);

    @Query(value = """
            SELECT
                (SELECT COUNT(*) FROM problem p
                WHERE p.visible = true AND p.creator_id = :authorId)
                +
                (SELECT COUNT(*) FROM solution s
                 JOIN problem p ON s.problem_id = p.id
                 WHERE s.visible = true AND s.creator_id = :authorId AND p.visible = true)
            AS total_count
            """, nativeQuery = true)
    int countVisiblePostsByAuthorId(@Param("authorId") String authorId);

    @Query(value = "SELECT COUNT(*) FROM problem WHERE forum_id = :forumId AND visible = true", nativeQuery = true)
    int countVisibleProblemsByForumId(@Param("forumId") String forumId);

    @Query(value = "SELECT COUNT(*) FROM problem WHERE forum_id = :forumId", nativeQuery = true)
    int countProblemsByForumId(@Param("forumId") String forumId);

    @Modifying
    @Query(value = "UPDATE problem SET solution_count = :solutionCount WHERE id = :problemId", nativeQuery = true)
    void updateVisibleSolutionsCount(@Param("problemId") String problemId, @Param("solutionCount") int solutionCount);

    @Modifying
    @Query(value = "UPDATE problem SET sort_date = :date WHERE id = :problemId", nativeQuery = true)
    void updateSortDate(@Param("problemId") String problemId, @Param("date") LocalDateTime date);


    // problem like:
    @Query(value = "SELECT update_problem_liked(:problemId, :userId)", nativeQuery = true)
    void addOrDeleteLiked(@Param("problemId") String problemId, @Param("userId") String userId);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1 FROM problems_users_liked
                WHERE problem_id = :problemId AND user_id = :userId
            )
            """, nativeQuery = true)
    boolean isLikedByUser(@Param("problemId") String problemId, @Param("userId") String userId);


    // problem save:
    @Query(value = "SELECT update_problem_saved(:problemId, :userId)", nativeQuery = true)
    void addOrDeleteSaved(@Param("problemId") String problemId, @Param("userId") String userId);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1 FROM problems_users_saved
                WHERE problem_id = :problemId AND user_id = :userId
            )
            """, nativeQuery = true)
    boolean isSavedByUser(@Param("problemId") String problemId, @Param("userId") String userId);


    // problem see:
    @Query(value = "SELECT update_problem_seen(:problemId, :userId)", nativeQuery = true)
    void addOrNothingSeen(@Param("problemId") String problemId, @Param("userId") String userId);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1 FROM problems_users_seen
                WHERE problem_id = :problemId AND user_id = :userId
            )
            """, nativeQuery = true)
    boolean isSeenByUser(@Param("problemId") String problemId, @Param("userId") String userId);


    // problem share:
    @Modifying
    @Query(value = "UPDATE problem SET share_count = share_count + 1 WHERE id = :problemId", nativeQuery = true)
    void incrementShareCount(@Param("problemId") String problemId);
}
