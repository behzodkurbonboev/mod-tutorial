package tutorial.modtutorial.repository;

import org.springframework.stereotype.Repository;
import tutorial.modtutorial.domain.entity.Subject;

import java.util.Optional;


@Repository
public interface SubjectRepository extends BaseRepository<Subject> {
    Optional<Subject> findByName(String name);
}
