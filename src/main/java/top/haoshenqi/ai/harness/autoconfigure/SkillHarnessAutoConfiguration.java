package top.haoshenqi.ai.harness.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import top.haoshenqi.ai.harness.analysis.ProjectCapabilityAnalyzer;
import top.haoshenqi.ai.harness.analysis.SkillBasedProjectCapabilityAnalyzer;
import top.haoshenqi.ai.harness.bootstrap.SkillHarnessBootstrap;
import top.haoshenqi.ai.harness.config.SkillProperties;
import top.haoshenqi.ai.harness.llm.DefaultSkillDescriptionGenerator;
import top.haoshenqi.ai.harness.llm.LlmClient;
import top.haoshenqi.ai.harness.llm.SkillDescriptionGenerator;
import top.haoshenqi.ai.harness.registry.InMemorySkillRegistry;
import top.haoshenqi.ai.harness.registry.SkillRegistry;
import top.haoshenqi.ai.harness.scan.ControllerSkillScanner;
import top.haoshenqi.ai.harness.writer.SkillJsonWriter;
import top.haoshenqi.ai.harness.writer.SkillMarkdownWriter;

import java.util.Optional;

@AutoConfiguration
@ConditionalOnClass(RequestMappingHandlerMapping.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(SkillProperties.class)
public class SkillHarnessAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SkillRegistry skillRegistry() {
        return new InMemorySkillRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public SkillDescriptionGenerator skillDescriptionGenerator(SkillProperties skillProperties,
                                                               Optional<LlmClient> llmClient) {
        return new DefaultSkillDescriptionGenerator(skillProperties, llmClient);
    }

    @Bean
    @ConditionalOnBean(RequestMappingHandlerMapping.class)
    @ConditionalOnMissingBean
    public ControllerSkillScanner controllerSkillScanner(RequestMappingHandlerMapping requestMappingHandlerMapping,
                                                         SkillDescriptionGenerator skillDescriptionGenerator,
                                                         SkillProperties skillProperties) {
        return new ControllerSkillScanner(requestMappingHandlerMapping, skillDescriptionGenerator, skillProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public SkillMarkdownWriter skillMarkdownWriter() {
        return new SkillMarkdownWriter();
    }

    @Bean
    @ConditionalOnMissingBean
    public SkillJsonWriter skillJsonWriter() {
        return new SkillJsonWriter();
    }

    @Bean
    @ConditionalOnMissingBean
    public ProjectCapabilityAnalyzer projectCapabilityAnalyzer() {
        return new SkillBasedProjectCapabilityAnalyzer();
    }

    @Bean
    @ConditionalOnBean(ControllerSkillScanner.class)
    @ConditionalOnMissingBean
    public SkillHarnessBootstrap skillHarnessBootstrap(ControllerSkillScanner controllerSkillScanner,
                                                       SkillRegistry skillRegistry,
                                                       ProjectCapabilityAnalyzer projectCapabilityAnalyzer) {
        return new SkillHarnessBootstrap(controllerSkillScanner, skillRegistry, projectCapabilityAnalyzer);
    }
}

