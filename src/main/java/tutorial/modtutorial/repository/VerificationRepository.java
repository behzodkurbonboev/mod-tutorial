package tutorial.modtutorial.repository;

import org.springframework.stereotype.Repository;
import tutorial.modtutorial.domain.entity.Verification;

import java.util.Optional;


@Repository
public interface VerificationRepository extends BaseRepository<Verification> {
    Optional<Verification> findByUserId(String userId);
}
