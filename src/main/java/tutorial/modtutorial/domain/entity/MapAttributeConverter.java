package tutorial.modtutorial.domain.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


@Converter
public class MapAttributeConverter implements AttributeConverter<Map<Integer, Integer>, String> {
//    private static final Logger log = LoggerFactory.getLogger(MapAttributeConverter.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<Integer, Integer> map) {
        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException jpe) {
//            log.warn("Cannot convert 'Map<String, String>' into 'text': " + map);
            return null;
        }
    }

    @Override
    public Map<Integer, Integer> convertToEntityAttribute(String text) {
        try {
            return mapper.readValue(text, new TypeReference<Map<Integer, Integer>>() {});
        } catch (JsonProcessingException e) {
//            log.warn("Cannot convert 'text' into 'Map<String, String>': " + text);
            return null;
        }
    }
}
