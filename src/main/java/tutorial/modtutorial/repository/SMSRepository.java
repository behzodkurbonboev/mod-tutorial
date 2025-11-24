package tutorial.modtutorial.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tutorial.modtutorial.domain.entity.SMS;

import java.util.List;


@Repository
public interface SMSRepository extends BaseRepository<SMS> {
    @Query("SELECT s.template, COUNT(s) FROM SMS s GROUP BY s.template")
    List<Object[]> countByTemplate();
}
