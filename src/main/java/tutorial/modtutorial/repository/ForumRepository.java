package tutorial.modtutorial.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tutorial.modtutorial.domain.entity.Forum;

import java.util.Optional;


@Repository
public interface ForumRepository extends BaseRepository<Forum> {
    Optional<Forum> findByName(String name);

    @Modifying
    @Query(value = "UPDATE forum SET num_of_posts = :numOfPosts WHERE id = :forumId", nativeQuery = true)
    void updateVisibleProblemsCount(@Param("forumId") String forumId, @Param("numOfPosts") int numOfPosts);
}
