package top.haoshenqi.ai.harness.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class SkillDefinition {

    private final String name;
    private final String description;
    private final String httpMethod;
    private final String path;
    private final List<SkillParameter> parameters;
    private final String returnType;
    private final boolean authRequired;
    private final String authDescription;
    private final Map<String, Object> metadata;

    public SkillDefinition(
            String name,
            String description,
            String httpMethod,
            String path,
            List<SkillParameter> parameters,
            String returnType,
            boolean authRequired,
            String authDescription,
            Map<String, Object> metadata
    ) {
        this.name = Objects.requireNonNullElse(name, "unknownSkill");
        this.description = Objects.requireNonNullElse(description, "");
        this.httpMethod = Objects.requireNonNullElse(httpMethod, "ALL");
        this.path = Objects.requireNonNullElse(path, "/");
        this.parameters = parameters == null ? List.of() : List.copyOf(parameters);
        this.returnType = Objects.requireNonNullElse(returnType, "void");
        this.authRequired = authRequired;
        this.authDescription = Objects.requireNonNullElse(authDescription, "");
        this.metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public List<SkillParameter> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    public String getReturnType() {
        return returnType;
    }

    public boolean isAuthRequired() {
        return authRequired;
    }

    public String getAuthDescription() {
        return authDescription;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public SkillDefinition withDescription(String newDescription) {
        return new SkillDefinition(
                name,
                newDescription,
                httpMethod,
                path,
                parameters,
                returnType,
                authRequired,
                authDescription,
                metadata
        );
    }
}

