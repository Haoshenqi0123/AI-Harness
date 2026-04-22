# 基础能力

AI Harness 的基础能力分为三层：

## 1. 元信息层

- `@Skill` 注解
- Skill 名称、描述、参数说明
- 是否忽略某个接口

## 2. 扫描层

- 扫描 Spring MVC Controller
- 解析 `@RequestMapping`、`@GetMapping`、`@PostMapping` 等映射
- 提取路径、HTTP Method、参数、返回值

## 3. 输出层

- 生成 Skill 注册信息
- 生成 Markdown 文档
- 生成 JSON 文档
- 生成项目说明和鉴权说明

## 默认能力

- 默认自动扫描公开 Controller
- 默认不依赖 LLM 也可输出基础 Skill 信息
- 当 LLM 配置开启时，自动生成更自然的 Skill 描述

## 基础配置

基础配置集中在 `top.haoshenqi.ai.harness.*` 命名空间下，方便 Spring Boot 自动装配与统一管理。
