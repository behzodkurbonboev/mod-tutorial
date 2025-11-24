package tutorial.modtutorial.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tutorial.modtutorial.domain.entity.SpecialTest;

import java.util.List;
import java.util.Optional;


@Repository
public interface SpecialTestRepository extends BaseRepository<SpecialTest> {
    Optional<SpecialTest> findByCreatorIdAndId(String creatorId, String testId);
    boolean existsByIdAndSubjectId(String testId, String subjectId);
    @Query(value = "SELECT * FROM special_test WHERE subject_id = :subjectId AND usage_count <= :maxUsageCount ORDER BY updated_date DESC", nativeQuery = true)
    List<SpecialTest> findAll(@Param("subjectId") String subjectId, @Param("maxUsageCount") int maxUsageCount);

    @Modifying
    @Query(value = "UPDATE special_test SET usage_count = usage_count + :change WHERE id = :testId", nativeQuery = true)
    void updateUsageCount(@Param("testId") String testId, @Param("change") int change);

    @Modifying
    @Query(value = "UPDATE special_test SET deleted = true WHERE id = :testId", nativeQuery = true)
    void setDeleted(@Param("testId") String testId);
}
