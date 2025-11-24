package tutorial.modtutorial.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;
import tutorial.modtutorial.domain.dto.general.response.TagDTO;
import tutorial.modtutorial.domain.entity.Tag;
import tutorial.modtutorial.repository.SubjectRepository;
import tutorial.modtutorial.repository.TagRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
public class TagMapper {
    private final TagRepository tagRepository;
    private final SubjectRepository subjectRepository;

    public TagMapper(TagRepository tagRepository, SubjectRepository subjectRepository) {
        this.tagRepository = tagRepository;
        this.subjectRepository = subjectRepository;
    }


    public TagDTO toDTO(String tagId, int count) {
        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new EntityNotFoundException("tag.not.found"));

        TagDTO dto = new TagDTO();

        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto.setSubjectId(tag.getSubjectId());
        dto.setCount(count);

        return dto;
    }

    public TagDTO toDTO(Tag tag) {
        TagDTO dto = new TagDTO();

        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto.setSubjectId(tag.getSubjectId());

        return dto;
    }

    public List<TagDTO> toDTOList(Set<Tag> tags) {
        return tags.stream().map(this::toDTO).toList();
    }

    public Set<Tag> toEntitiesSet(List<String> tags, String subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new EntityNotFoundException("subject not fount with id = " + subjectId);
        }

        if (tags == null || tags.isEmpty()) {
            throw new RuntimeException("At least one tag should be supplied");
        }

        Set<Tag> result = new HashSet<>();
        tags.forEach(tagId -> tagRepository.findByIdAndSubjectId(tagId, subjectId).ifPresent(result::add));

        if (result.isEmpty()) {
            throw new RuntimeException("At least one tag should be supplied");
        }

        return result;
    }

    public List<String> toNameList(Set<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return new ArrayList<>();
        }

        return tags.stream().map(Tag::getName).toList();
    }
}
