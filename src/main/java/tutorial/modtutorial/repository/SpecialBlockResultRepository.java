package tutorial.modtutorial.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tutorial.modtutorial.domain.entity.SpecialBlockResult;

import java.util.Optional;


@Repository
public interface SpecialBlockResultRepository extends BaseRepository<SpecialBlockResult>{
    Optional<SpecialBlockResult> findByBlockIdAndUserId(String blockId, String userId);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1 FROM special_block_result
                WHERE block_id = :blockId AND user_id = :userId
            )
            """, nativeQuery = true)
    boolean existsByBlockIdAndUserId(@Param("blockId") String blockId, @Param("userId") String userId);
}
