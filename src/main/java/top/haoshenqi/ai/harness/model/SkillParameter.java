package top.haoshenqi.ai.harness.model;

public record SkillParameter(
        String name,
        String type,
        boolean required,
        String description,
        String example
) {
}

