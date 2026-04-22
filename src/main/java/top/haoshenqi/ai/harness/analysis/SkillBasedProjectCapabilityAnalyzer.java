package top.haoshenqi.ai.harness.analysis;

import top.haoshenqi.ai.harness.model.SkillDefinition;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SkillBasedProjectCapabilityAnalyzer implements ProjectCapabilityAnalyzer {

    @Override
    public ProjectCapabilityReport analyze(String projectName, List<SkillDefinition> skills) {
        List<SkillDefinition> safeSkills = skills == null ? List.of() : List.copyOf(skills);
        Set<String> capabilities = new LinkedHashSet<>();
        for (SkillDefinition skill : safeSkills) {
            capabilities.add(inferCapability(skill));
        }
        String summary = buildSummary(projectName, safeSkills, capabilities);
        return new ProjectCapabilityReport(
                projectName,
                summary,
                new ArrayList<>(capabilities),
                safeSkills
        );
    }

    private String inferCapability(SkillDefinition skillDefinition) {
        String description = skillDefinition.getDescription();
        if (description != null && !description.isBlank()) {
            return description;
        }
        return skillDefinition.getHttpMethod() + " " + skillDefinition.getPath();
    }

    private String buildSummary(String projectName, List<SkillDefinition> skills, Set<String> capabilities) {
        return String.format(Locale.ROOT,
                "%s 包含 %d 个可暴露的技能，覆盖 %d 类核心能力。",
                projectName == null || projectName.isBlank() ? "当前项目" : projectName,
                skills.size(),
                capabilities.size());
    }
}

