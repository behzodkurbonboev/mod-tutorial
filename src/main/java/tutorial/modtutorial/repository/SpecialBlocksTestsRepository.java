package tutorial.modtutorial.repository;

import org.springframework.stereotype.Repository;
import tutorial.modtutorial.domain.entity.SpecialBlocksTests;

import java.util.List;


@Repository
public interface SpecialBlocksTestsRepository extends BaseRepository<SpecialBlocksTests> {
    List<SpecialBlocksTests> findAllByBlockId(String blockId);
}
