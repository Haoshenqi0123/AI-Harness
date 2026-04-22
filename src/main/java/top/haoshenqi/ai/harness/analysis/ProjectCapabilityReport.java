package top.haoshenqi.ai.harness.analysis;

import top.haoshenqi.ai.harness.model.SkillDefinition;

import java.util.List;

public record ProjectCapabilityReport(
        String projectName,
        String summary,
        List<String> capabilities,
        List<SkillDefinition> skills
) {
}

