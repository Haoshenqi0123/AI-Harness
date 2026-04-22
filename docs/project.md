# AI Harness 项目说明

## 定位

AI Harness 是一个面向 Java / Spring Boot 项目的 **Starter + SDK**，目标是把普通业务系统转换成 AI Agent 可理解、可调用、可接入的系统。

## 核心目标

提供一个 jar 包，帮助用户快速获得以下能力：

1. 将 REST API 自动转换为 Skill
2. 自动识别项目鉴权能力并生成登录流程说明
3. 根据代码和接口元数据生成项目能力文档
4. 为 AI Agent 提供标准化、可读、可执行的上下文

## 核心特性

### API to Skill

扫描所有 Controller，将每一个接口转换为可供 AI 直接调用的 Skill。

### Project Capability Analysis

扫描项目代码，识别业务模块、接口能力、依赖关系和鉴权方式，生成项目使用说明。

## 推荐模块划分

```text
ai-harness-core
ai-harness-spring-boot-starter
ai-harness-spring-web
```

### ai-harness-core

包含通用模型、Skill 元数据、文档输出、LLM 抽象和项目能力分析抽象。

### ai-harness-spring-boot-starter

提供 Spring Boot 自动配置、Controller 扫描、注解解析和注册能力。

### ai-harness-spring-web

提供 Spring Web / Spring MVC 的接口适配能力。

## 设计原则

- 对业务代码侵入性低
- 默认自动扫描，必要时可通过注解精确控制
- 输出结果既要适合 AI 阅读，也要适合人类审阅
- 支持 LLM 自动补全，但不强制依赖 LLM

## 建议输出文件

- `skills.json`
- `skills.md`
- `auth-guide.md`
- `project-features.md`

## 典型使用流程

1. 引入 starter 依赖
2. 配置 LLM 与输出目录
3. 给必要接口添加 `@Skill`
4. 启动应用后自动扫描并生成技能信息
5. AI Agent 根据输出文档或运行时注册信息直接调用接口
