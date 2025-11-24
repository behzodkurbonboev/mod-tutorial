package tutorial.modtutorial.repository;

import org.springframework.stereotype.Repository;
import tutorial.modtutorial.domain.entity.Tag;

import java.util.Optional;


@Repository
public interface TagRepository extends BaseRepository<Tag> {
    Optional<Tag> findByNameAndSubjectId(String name, String subjectId);
    Optional<Tag> findByIdAndSubjectId(String tagId, String subjectId);
    Optional<Tag> findByIdAndSubjectIdAndCreatorId(String tagId, String subjectId, String creatorId);
    void deleteByIdAndSubjectId(String tagId, String subjectId);
}
