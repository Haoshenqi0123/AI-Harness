package top.haoshenqi.ai.harness.registry;

import top.haoshenqi.ai.harness.model.SkillDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemorySkillRegistry implements SkillRegistry {

    private final Map<String, SkillDefinition> skills = new LinkedHashMap<>();

    @Override
    public synchronized void register(SkillDefinition skillDefinition) {
        if (skillDefinition != null) {
            skills.put(skillDefinition.getName(), skillDefinition);
        }
    }

    @Override
    public synchronized void registerAll(Collection<SkillDefinition> skillDefinitions) {
        if (skillDefinitions == null) {
            return;
        }
        for (SkillDefinition skillDefinition : skillDefinitions) {
            register(skillDefinition);
        }
    }

    @Override
    public synchronized Optional<SkillDefinition> findByName(String name) {
        return Optional.ofNullable(skills.get(name));
    }

    @Override
    public synchronized List<SkillDefinition> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(skills.values()));
    }

    @Override
    public synchronized void clear() {
        skills.clear();
    }
}

