package tutorial.modtutorial.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tutorial.modtutorial.domain.entity.SpecialBlock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface SpecialBlockRepository extends BaseRepository<SpecialBlock> {
    Optional<SpecialBlock> findByCreatorIdAndId(String creatorId, String blockId);
    List<SpecialBlock> findByOrderByDateDescCreatedDateDesc();


    @Query(value = """
            SELECT * FROM special_block
            WHERE author_id = :authorId AND created_date < :minDate AND visible = true
            ORDER BY created_date DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<SpecialBlock> findSpecialBlocksCreatedByUserSliced(@Param("authorId") String authorId, @Param("minDate") LocalDateTime minDate, @Param("limit") int limit);

    @Query(value = """
            SELECT * FROM special_block
            WHERE date IN (
                SELECT DISTINCT(date) FROM special_block
                WHERE date < :minDate AND visible = true
                ORDER BY date DESC
                LIMIT :limit
            ) AND visible = true
            ORDER BY date DESC, created_date DESC
            """, nativeQuery = true)
    List<SpecialBlock> findSpecialBlocks(@Param("minDate") LocalDateTime minDate, @Param("limit") int limit);

    @Query(value = """
            SELECT * FROM special_block
            WHERE date IN (
                SELECT DISTINCT(date) FROM special_block
                WHERE author_id = :authorId AND date < :minDate AND visible = true
                ORDER BY date DESC
                LIMIT :limit
            ) AND author_id = :authorId AND visible = true
            ORDER BY date DESC, created_date DESC
            """, nativeQuery = true)
    List<SpecialBlock> findSpecialBlocksCreatedByUser(@Param("authorId") String authorId, @Param("minDate") LocalDateTime minDate, @Param("limit") int limit);

    @Query(value = """
            SELECT count(id) FROM special_block
            WHERE author_id = :authorId AND visible = true
            """, nativeQuery = true)
    int countVisibleSpecialBlocksByAuthorId(@Param("authorId") String authorId);

    @Query(value = "SELECT analysed FROM special_block WHERE id = :blockId", nativeQuery = true)
    boolean isBlockAnalysed(@Param("blockId") String blockId);

    @Modifying
    @Query(value = "UPDATE special_block SET score_sum = score_sum + :score, solved_count = solved_count + 1 WHERE id = :blockId", nativeQuery = true)
    void updateScoreSumAndSolvedCount(@Param("blockId") String blockId, @Param("score") int score);
}
