package top.haoshenqi.ai.harness.registry;

import top.haoshenqi.ai.harness.model.SkillDefinition;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SkillRegistry {

    void register(SkillDefinition skillDefinition);

    void registerAll(Collection<SkillDefinition> skillDefinitions);

    Optional<SkillDefinition> findByName(String name);

    List<SkillDefinition> findAll();

    void clear();
}

