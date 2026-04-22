package top.haoshenqi.ai.harness.llm;

import top.haoshenqi.ai.harness.model.SkillDefinition;

public interface SkillDescriptionGenerator {

    String generateDescription(SkillDefinition skillDefinition);
}

