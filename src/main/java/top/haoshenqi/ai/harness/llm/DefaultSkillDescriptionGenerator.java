package top.haoshenqi.ai.harness.llm;

import top.haoshenqi.ai.harness.config.SkillProperties;
import top.haoshenqi.ai.harness.model.SkillDefinition;

import java.util.Objects;
import java.util.Optional;

public class DefaultSkillDescriptionGenerator implements SkillDescriptionGenerator {

    private final SkillProperties skillProperties;
    private final Optional<LlmClient> llmClient;

    public DefaultSkillDescriptionGenerator(SkillProperties skillProperties, Optional<LlmClient> llmClient) {
        this.skillProperties = Objects.requireNonNull(skillProperties, "skillProperties must not be null");
        this.llmClient = llmClient == null ? Optional.empty() : llmClient;
    }

    @Override
    public String generateDescription(SkillDefinition skillDefinition) {
        if (skillDefinition == null) {
            return "";
        }
        if (skillProperties.getLlm().isEnable() && llmClient.isPresent()) {
            String prompt = buildPrompt(skillDefinition);
            String result = llmClient.get().complete(prompt);
            if (result != null && !result.isBlank()) {
                return result.trim();
            }
        }
        return fallbackDescription(skillDefinition);
    }

    private String buildPrompt(SkillDefinition skillDefinition) {
        StringBuilder builder = new StringBuilder();
        builder.append("请为以下 API 生成简洁、准确、面向 AI Agent 的技能描述：\n");
        builder.append("名称: ").append(skillDefinition.getName()).append('\n');
        builder.append("方法: ").append(skillDefinition.getHttpMethod()).append('\n');
        builder.append("路径: ").append(skillDefinition.getPath()).append('\n');
        builder.append("返回类型: ").append(skillDefinition.getReturnType()).append('\n');
        return builder.toString();
    }

    private String fallbackDescription(SkillDefinition skillDefinition) {
        return "调用 " + skillDefinition.getHttpMethod() + " " + skillDefinition.getPath();
    }
}

