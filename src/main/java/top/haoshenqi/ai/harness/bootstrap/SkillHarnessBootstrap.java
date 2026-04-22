package top.haoshenqi.ai.harness.bootstrap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import top.haoshenqi.ai.harness.analysis.ProjectCapabilityAnalyzer;
import top.haoshenqi.ai.harness.model.SkillDefinition;
import top.haoshenqi.ai.harness.registry.SkillRegistry;
import top.haoshenqi.ai.harness.scan.ControllerSkillScanner;

import java.util.List;
import java.util.Objects;

public class SkillHarnessBootstrap implements SmartInitializingSingleton {

    private static final Log log = LogFactory.getLog(SkillHarnessBootstrap.class);

    private final ControllerSkillScanner controllerSkillScanner;
    private final SkillRegistry skillRegistry;
    private final ProjectCapabilityAnalyzer projectCapabilityAnalyzer;

    public SkillHarnessBootstrap(ControllerSkillScanner controllerSkillScanner,
                                 SkillRegistry skillRegistry,
                                 ProjectCapabilityAnalyzer projectCapabilityAnalyzer) {
        this.controllerSkillScanner = Objects.requireNonNull(controllerSkillScanner, "controllerSkillScanner must not be null");
        this.skillRegistry = Objects.requireNonNull(skillRegistry, "skillRegistry must not be null");
        this.projectCapabilityAnalyzer = Objects.requireNonNull(projectCapabilityAnalyzer, "projectCapabilityAnalyzer must not be null");
    }

    @Override
    public void afterSingletonsInstantiated() {
        List<SkillDefinition> scanned = controllerSkillScanner.scan();
        skillRegistry.clear();
        skillRegistry.registerAll(scanned);
        log.info("AI Harness registered " + scanned.size() + " skill(s).");

        // Build a lightweight capability report to validate scanner output at startup.
        var report = projectCapabilityAnalyzer.analyze("current-project", scanned);
        log.debug("AI Harness capability summary: " + report.summary());
    }
}

