package top.haoshenqi.ai.harness;

import org.junit.jupiter.api.Test;
import top.haoshenqi.ai.harness.config.SkillProperties;
import top.haoshenqi.ai.harness.llm.DefaultSkillDescriptionGenerator;
import top.haoshenqi.ai.harness.model.SkillDefinition;
import top.haoshenqi.ai.harness.model.SkillParameter;
import top.haoshenqi.ai.harness.registry.InMemorySkillRegistry;
import top.haoshenqi.ai.harness.writer.SkillMarkdownWriter;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SkillHarnessCoreTest {

    @Test
    void registryAndMarkdownWriterShouldWork() {
        SkillDefinition definition = new SkillDefinition(
                "getOrder",
                "查询订单",
                "GET",
                "/orders/{id}",
                List.of(new SkillParameter("id", "String", true, "订单ID", "")),
                "OrderDTO",
                true,
                "Requires authentication",
                java.util.Map.of("controller", "OrderController")
        );

        InMemorySkillRegistry registry = new InMemorySkillRegistry();
        registry.register(definition);

        String markdown = new SkillMarkdownWriter().write(registry.findAll());
        assertTrue(markdown.contains("getOrder"));
        assertTrue(markdown.contains("/orders/{id}"));
    }

    @Test
    void generatorShouldFallbackWhenLlmDisabled() {
        SkillProperties properties = new SkillProperties();
        DefaultSkillDescriptionGenerator generator = new DefaultSkillDescriptionGenerator(properties, Optional.empty());
        SkillDefinition definition = new SkillDefinition(
                "listOrders",
                "",
                "GET",
                "/orders",
                List.of(),
                "List<OrderDTO>",
                false,
                "",
                java.util.Map.of()
        );

        String description = generator.generateDescription(definition);
        assertTrue(description.contains("GET"));
        assertTrue(description.contains("/orders"));
    }
}

