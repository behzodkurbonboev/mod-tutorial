package tutorial.modtutorial.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tutorial.modtutorial.domain.dto.general.response.AuthorDTO;
import tutorial.modtutorial.domain.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface UserRepository extends BaseRepository<User> {
    Optional<User> findByUsername(String username);

    @Query("SELECT new tutorial.modtutorial.domain.dto.general.response.AuthorDTO(u.id, u.firstName, u.lastName, u.imageUrl) FROM User u WHERE u.id = :userId")
    AuthorDTO findAuthorById(@Param("userId") String userId);

    @Query("SELECT u.verified, COUNT(u) FROM User u GROUP BY u.verified")
    List<Object[]> countByVerified();

    @Query("SELECT u FROM User u JOIN u.authorities a WHERE a.name IN :auths")
    List<User> findByAuthorities(@Param("auths") Set<String> auths);

    // resource count update queries
    @Modifying
    @Query(value = "UPDATE app_user SET articles_count = :count WHERE id = :userId", nativeQuery = true)
    void updateArticlesCount(@Param("userId") String userId, @Param("count") int count);

    @Modifying
    @Query(value = "UPDATE app_user SET posts_count = :count WHERE id = :userId", nativeQuery = true)
    void updatePostsCount(@Param("userId") String userId, @Param("count") int count);

    @Modifying
    @Query(value = "UPDATE app_user SET blocks_count = :count WHERE id = :userId", nativeQuery = true)
    void updateBlocksCount(@Param("userId") String userId, @Param("count") int count);
}
