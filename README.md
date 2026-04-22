# AI Harness

AI Harness 的目标是把 Java 项目快速变成“AI 可理解、可调用、可交互”的项目。

## 定位

AI Harness = **Skill as a Service**

它是一个面向 Java / Spring Boot 项目的 Starter + SDK，核心能力是：

- 扫描 Controller 并自动转换为 Skill
- 分析项目鉴权机制，生成登录 / 鉴权流程说明
- 根据接口元数据和 LLM 自动补全技能描述
- 扫描项目代码并生成项目能力文档，帮助 AI Agent 快速接入

## 适用场景

- 现有 Spring Boot 项目需要快速接入 AI Agent
- 希望把 REST API 暴露成标准化的 Skill
- 需要让 AI 自动理解项目能力、鉴权方式和调用方式
- 想生成可被 AI 读取的项目说明文档、技能索引和认证指南

## 推荐技术栈

- Java 17+
- Spring Boot 3.x
- Spring Web / Spring MVC
- Maven 

## 核心特性

### 1. API to Skill

扫描所有 Controller，将每个接口转换成 AI Agent 可调用的 Skill。

### 2. Auth Analyzer

分析项目中的鉴权机制，自动生成登录、获取 token、刷新 token、访问受保护接口的说明。

### 3. `@Skill` 注解

允许开发者显式标记某个接口是否应该暴露为 Skill，并补充描述、参数说明等元信息。

### 4. Project Capability Analysis

扫描项目代码、识别模块和业务能力，自动生成项目说明文档，帮助 AI Agent 快速理解项目。

## 约定配置

```properties
top.haoshenqi.ai.harness.llm.enable=true
top.haoshenqi.ai.harness.llm.provider=openai
top.haoshenqi.ai.harness.llm.api-key=your_api_key
top.haoshenqi.ai.harness.llm.model=gpt-4o-mini
top.haoshenqi.ai.harness.llm.temperature=0.7
top.haoshenqi.ai.harness.llm.max-tokens=2048
top.haoshenqi.ai.harness.llm.timeout=30s
```

## 默认输出物

- `skills.json`
- `skills.md`
- `auth-guide.md`
- `project-features.md`

## 代码结构建议

```text
top.haoshenqi.ai.harness
├── annotation
├── autoconfigure
├── auth
├── config
├── llm
├── model
├── registry
├── scan
├── writer
└── prompt
```

## 接入方式

```java
@SpringBootApplication
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
```

然后在接口上使用：

```java
@Skill(description = "查询订单详情")
@GetMapping("/orders/{id}")
public OrderDTO getOrder(@PathVariable String id) {
	...
}
```

AI Harness 会把该接口作为 Skill 暴露给 AI Agent 使用。
