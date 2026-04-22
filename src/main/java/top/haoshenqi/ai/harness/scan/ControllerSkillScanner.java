package top.haoshenqi.ai.harness.scan;

import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import top.haoshenqi.ai.harness.annotation.Skill;
import top.haoshenqi.ai.harness.config.SkillProperties;
import top.haoshenqi.ai.harness.llm.SkillDescriptionGenerator;
import top.haoshenqi.ai.harness.model.SkillDefinition;
import top.haoshenqi.ai.harness.model.SkillParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ControllerSkillScanner {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final SkillDescriptionGenerator descriptionGenerator;
    private final boolean autoScan;

    public ControllerSkillScanner(RequestMappingHandlerMapping requestMappingHandlerMapping,
                                  SkillDescriptionGenerator descriptionGenerator,
                                  SkillProperties skillProperties) {
        this.requestMappingHandlerMapping = Objects.requireNonNull(requestMappingHandlerMapping, "requestMappingHandlerMapping must not be null");
        this.descriptionGenerator = Objects.requireNonNull(descriptionGenerator, "descriptionGenerator must not be null");
        this.autoScan = skillProperties == null
                || skillProperties.getLlm() == null
                || skillProperties.getLlm().getSkill() == null
                || skillProperties.getLlm().getSkill().isAutoScan();
    }

    public List<SkillDefinition> scan() {
        List<SkillDefinition> result = new ArrayList<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : requestMappingHandlerMapping.getHandlerMethods().entrySet()) {
            RequestMappingInfo mappingInfo = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();
            SkillDefinition skillDefinition = toSkillDefinition(mappingInfo, handlerMethod);
            if (skillDefinition != null) {
                result.add(skillDefinition);
            }
        }
        return result;
    }

    private SkillDefinition toSkillDefinition(RequestMappingInfo mappingInfo, HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        Class<?> beanType = handlerMethod.getBeanType();

        Skill classSkill = AnnotatedElementUtils.findMergedAnnotation(beanType, Skill.class);
        Skill methodSkill = AnnotatedElementUtils.findMergedAnnotation(method, Skill.class);

        if (!autoScan && classSkill == null && methodSkill == null) {
            return null;
        }

        if ((classSkill != null && classSkill.ignore()) || (methodSkill != null && methodSkill.ignore())) {
            return null;
        }

        String description = firstNonBlank(methodSkill == null ? null : methodSkill.description(),
                classSkill == null ? null : classSkill.description());
        String name = firstNonBlank(methodSkill == null ? null : methodSkill.name(),
                classSkill == null ? null : classSkill.name());
        if (!StringUtils.hasText(name)) {
            name = buildDefaultSkillName(beanType, method, mappingInfo);
        }

        List<SkillParameter> parameters = extractParameters(method);
        String returnType = method.getReturnType().getSimpleName();
        boolean authRequired = detectAuthRequired(beanType, method);
        String authDescription = authRequired ? "Requires authentication" : "Public endpoint";

        SkillDefinition baseDefinition = new SkillDefinition(
                name,
                description,
                resolveHttpMethod(mappingInfo),
                resolvePath(mappingInfo),
                parameters,
                returnType,
                authRequired,
                authDescription,
                buildMetadata(beanType, method)
        );

        if (!StringUtils.hasText(baseDefinition.getDescription())) {
            Skill methodOrClassSkill = methodSkill != null ? methodSkill : classSkill;
            if (methodOrClassSkill == null || methodOrClassSkill.autoDescription()) {
                return baseDefinition.withDescription(descriptionGenerator.generateDescription(baseDefinition));
            }
        }
        return baseDefinition;
    }

    private String buildDefaultSkillName(Class<?> beanType, Method method, RequestMappingInfo mappingInfo) {
        String methodPart = method.getName();
        String pathPart = sanitizeForName(resolvePath(mappingInfo));
        String httpMethod = resolveHttpMethod(mappingInfo).toLowerCase();
        return beanType.getSimpleName() + "_" + methodPart + "_" + httpMethod + "_" + pathPart;
    }

    private String sanitizeForName(String value) {
        if (!StringUtils.hasText(value)) {
            return "root";
        }
        String result = value.replace("/", "_")
                .replace("{", "")
                .replace("}", "")
                .replace("-", "_")
                .replace('.', '_');
        result = result.replaceAll("_+", "_");
        return result.startsWith("_") ? result.substring(1) : result;
    }

    private String resolveHttpMethod(RequestMappingInfo mappingInfo) {
        Set<RequestMethod> methods = mappingInfo.getMethodsCondition().getMethods();
        if (methods == null || methods.isEmpty()) {
            return "ALL";
        }
        return methods.iterator().next().name();
    }

    private String resolvePath(RequestMappingInfo mappingInfo) {
        Set<String> paths = new LinkedHashSet<>();
        if (mappingInfo.getPathPatternsCondition() != null) {
            paths.addAll(mappingInfo.getPathPatternsCondition().getPatternValues());
        } else if (mappingInfo.getPatternsCondition() != null) {
            paths.addAll(mappingInfo.getPatternsCondition().getPatterns());
        }
        if (paths.isEmpty()) {
            return "/";
        }
        return paths.iterator().next();
    }

    private List<SkillParameter> extractParameters(Method method) {
        List<SkillParameter> parameters = new ArrayList<>();
        MethodParameter[] methodParameters = new MethodParameter[method.getParameterCount()];
        for (int i = 0; i < method.getParameterCount(); i++) {
            methodParameters[i] = new MethodParameter(method, i);
        }
        for (MethodParameter methodParameter : methodParameters) {
            if (isFrameworkParameter(methodParameter)) {
                continue;
            }
            String name = resolveParameterName(methodParameter);
            String type = methodParameter.getParameterType().getSimpleName();
            boolean required = isRequired(methodParameter);
            String description = resolveParameterDescription(methodParameter);
            parameters.add(new SkillParameter(name, type, required, description, ""));
        }
        return parameters;
    }

    private boolean isFrameworkParameter(MethodParameter methodParameter) {
        Class<?> type = methodParameter.getParameterType();
        return org.springframework.ui.Model.class.isAssignableFrom(type)
                || java.security.Principal.class.isAssignableFrom(type)
                || org.springframework.web.context.request.WebRequest.class.isAssignableFrom(type)
                || org.springframework.web.servlet.ModelAndView.class.isAssignableFrom(type);
    }

    private boolean isRequired(MethodParameter methodParameter) {
        RequestParam requestParam = methodParameter.getParameterAnnotation(RequestParam.class);
        if (requestParam != null) {
            return requestParam.required();
        }
        PathVariable pathVariable = methodParameter.getParameterAnnotation(PathVariable.class);
        if (pathVariable != null) {
            return pathVariable.required();
        }
        RequestHeader requestHeader = methodParameter.getParameterAnnotation(RequestHeader.class);
        if (requestHeader != null) {
            return requestHeader.required();
        }
        RequestBody requestBody = methodParameter.getParameterAnnotation(RequestBody.class);
        if (requestBody != null) {
            return requestBody.required();
        }
        return !methodParameter.isOptional();
    }

    private String resolveParameterName(MethodParameter methodParameter) {
        String name = null;
        RequestParam requestParam = methodParameter.getParameterAnnotation(RequestParam.class);
        if (requestParam != null && StringUtils.hasText(requestParam.value())) {
            name = requestParam.value();
        }
        PathVariable pathVariable = methodParameter.getParameterAnnotation(PathVariable.class);
        if (!StringUtils.hasText(name) && pathVariable != null && StringUtils.hasText(pathVariable.value())) {
            name = pathVariable.value();
        }
        RequestHeader requestHeader = methodParameter.getParameterAnnotation(RequestHeader.class);
        if (!StringUtils.hasText(name) && requestHeader != null && StringUtils.hasText(requestHeader.value())) {
            name = requestHeader.value();
        }
        if (!StringUtils.hasText(name)) {
            name = methodParameter.getParameterName();
        }
        if (!StringUtils.hasText(name)) {
            name = "arg" + methodParameter.getParameterIndex();
        }
        return name;
    }

    private String resolveParameterDescription(MethodParameter methodParameter) {
        StringBuilder builder = new StringBuilder();
        if (methodParameter.hasParameterAnnotation(PathVariable.class)) {
            builder.append("Path variable");
        } else if (methodParameter.hasParameterAnnotation(RequestParam.class)) {
            builder.append("Request parameter");
        } else if (methodParameter.hasParameterAnnotation(RequestHeader.class)) {
            builder.append("Request header");
        } else if (methodParameter.hasParameterAnnotation(RequestBody.class)) {
            builder.append("Request body");
        }
        if (builder.length() == 0) {
            builder.append("Method parameter");
        }
        return builder.toString();
    }

    private boolean detectAuthRequired(Class<?> beanType, Method method) {
        return hasSecurityAnnotation(beanType, method,
                "org.springframework.security.access.prepost.PreAuthorize",
                "org.springframework.security.access.annotation.Secured",
                "jakarta.annotation.security.RolesAllowed",
                "org.apache.shiro.authz.annotation.RequiresRoles",
                "org.apache.shiro.authz.annotation.RequiresPermissions",
                "cn.dev33.satoken.annotation.SaCheckLogin",
                "cn.dev33.satoken.annotation.SaCheckRole",
                "cn.dev33.satoken.annotation.SaCheckPermission");
    }

    private boolean hasSecurityAnnotation(Class<?> beanType, Method method, String... annotationClassNames) {
        for (String annotationClassName : annotationClassNames) {
            if (hasAnnotation(beanType, annotationClassName) || hasAnnotation(method, annotationClassName)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAnnotation(Class<?> type, String annotationClassName) {
        return findAnnotation(type.getAnnotations(), annotationClassName);
    }

    private boolean hasAnnotation(Method method, String annotationClassName) {
        return findAnnotation(method.getAnnotations(), annotationClassName);
    }

    private boolean findAnnotation(Annotation[] annotations, String annotationClassName) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getName().equals(annotationClassName)) {
                return true;
            }
        }
        return false;
    }

    private Map<String, Object> buildMetadata(Class<?> beanType, Method method) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("controller", beanType.getName());
        metadata.put("method", method.getName());
        metadata.put("declaringClass", method.getDeclaringClass().getName());
        return metadata;
    }

    private String firstNonBlank(String first, String second) {
        if (StringUtils.hasText(first)) {
            return first;
        }
        if (StringUtils.hasText(second)) {
            return second;
        }
        return "";
    }
}

