package top.haoshenqi.ai.harness.analysis;

import top.haoshenqi.ai.harness.model.SkillDefinition;

import java.util.List;

public interface ProjectCapabilityAnalyzer {

    ProjectCapabilityReport analyze(String projectName, List<SkillDefinition> skills);
}

