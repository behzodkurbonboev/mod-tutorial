package tutorial.modtutorial.repository;

import org.springframework.stereotype.Repository;
import tutorial.modtutorial.domain.entity.Authority;


@Repository
public interface AuthorityRepository extends BaseRepository<Authority> {
    boolean existsByName(String name);
}
