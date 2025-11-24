package tutorial.modtutorial.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tutorial.modtutorial.domain.entity.Solution;

import java.util.List;
import java.util.Optional;


@Repository
public interface SolutionRepository extends BaseRepository<Solution> {
    Optional<Solution> findByCreatorIdAndId(String userId, String solutionId);
    List<Solution> findAllByProblemIdOrderByCreatedDateAsc(String problemId);

    List<Solution> findAllByProblemIdAndVisibleTrueOrderByCreatedDateAsc(String problemId);

    @Query(value = "SELECT COUNT(*) FROM solution WHERE problem_id = :problemId AND visible = true", nativeQuery = true)
    int countVisibleSolutionsByProblemId(@Param("problemId") String problemId);


    // solution like
    @Query(value = "SELECT update_solution_liked(:solutionId, :userId)", nativeQuery = true)
    void addOrDeleteLiked(@Param("solutionId") String solutionId, @Param("userId") String userId);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1 FROM solutions_users_liked
                WHERE solution_id = :solutionId AND user_id = :userId
            )
            """, nativeQuery = true)
    boolean isLikedByUser(@Param("solutionId") String solutionId, @Param("userId") String userId);
}
