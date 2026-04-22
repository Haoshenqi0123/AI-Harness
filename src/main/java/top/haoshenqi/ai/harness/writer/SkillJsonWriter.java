package top.haoshenqi.ai.harness.writer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import top.haoshenqi.ai.harness.model.SkillDefinition;

import java.util.List;

public class SkillJsonWriter {

    private final ObjectMapper objectMapper;

    public SkillJsonWriter() {
        this(new ObjectMapper());
    }

    public SkillJsonWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String write(List<SkillDefinition> skills) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(skills);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize skills to JSON", e);
        }
    }
}

