package top.haoshenqi.ai.harness.writer;

import top.haoshenqi.ai.harness.model.SkillDefinition;
import top.haoshenqi.ai.harness.model.SkillParameter;

import java.util.List;
import java.util.stream.Collectors;

public class SkillMarkdownWriter {

    public String write(List<SkillDefinition> skills) {
        StringBuilder builder = new StringBuilder();
        builder.append("# Skills\n\n");
        for (SkillDefinition skill : skills) {
            builder.append("## ").append(skill.getName()).append("\n\n");
            builder.append("- 描述：").append(skill.getDescription()).append("\n");
            builder.append("- HTTP Method：").append(skill.getHttpMethod()).append("\n");
            builder.append("- Path：").append(skill.getPath()).append("\n");
            builder.append("- 返回类型：").append(skill.getReturnType()).append("\n");
            builder.append("- 是否需要认证：").append(skill.isAuthRequired() ? "是" : "否").append("\n");
            if (!skill.getAuthDescription().isBlank()) {
                builder.append("- 认证说明：").append(skill.getAuthDescription()).append("\n");
            }
            builder.append("\n### 参数\n\n");
            if (skill.getParameters().isEmpty()) {
                builder.append("- 无\n\n");
            } else {
                for (SkillParameter parameter : skill.getParameters()) {
                    builder.append("- ")
                            .append(parameter.name())
                            .append(" (")
                            .append(parameter.type())
                            .append(")")
                            .append(parameter.required() ? " [required]" : "")
                            .append(": ")
                            .append(parameter.description())
                            .append("\n");
                }
                builder.append("\n");
            }
            if (!skill.getMetadata().isEmpty()) {
                builder.append("### Metadata\n\n");
                builder.append(skill.getMetadata().entrySet().stream()
                        .map(entry -> "- " + entry.getKey() + ": " + entry.getValue())
                        .collect(Collectors.joining("\n")))
                        .append("\n\n");
            }
        }
        return builder.toString();
    }
}

