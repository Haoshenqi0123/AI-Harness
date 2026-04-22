# API to Skill

## 目标

扫描所有 Controller，并将每一个接口转换为 AI Agent 可直接调用的 Skill。

## 设计原则

- 默认自动扫描 Spring MVC 接口
- 支持显式注解控制是否暴露
- 支持 LLM 自动补全描述
- 输出结果既适合 AI 使用，也适合人工审阅

## 鉴权分析

分析项目的鉴权机制，生成登录流程 Skill，帮助 AI Agent 完成鉴权过程。

建议识别以下鉴权方式：

- Spring Security
- Sa-Token
- Shiro
- 自定义 Filter / Interceptor / AOP

## `@Skill` 注解

提供 `@Skill` 注解，允许开发者在接口上标注该接口应该被暴露为 Skill，并且可以提供额外的元信息（如技能描述、参数说明等），帮助 AI Agent 更好地理解和使用这些技能。

### 建议属性

- `ignore`：为 `true` 时忽略该接口，默认 `false`
- `description`：技能描述，默认为空
- `autoDescription`：是否自动生成技能描述，默认为 `true`
- `parameters`：技能参数说明，默认为空

### 自动生成规则

当满足以下条件时，系统应尝试自动生成描述：

1. `description` 为空
2. `autoDescription = true`
3. `top.haoshenqi.ai.harness.llm.enable = true`

## 建议输出的 Skill 元数据

- 名称
- 描述
- HTTP Method
- Path
- 参数列表
- 返回类型
- 是否需要认证
- 认证说明

## 推荐实现路径

1. 扫描 Spring MVC 的 `HandlerMethod`
2. 读取 `@RequestMapping` 系列注解
3. 合并类级别与方法级别的 `@Skill` 配置
4. 生成统一的 SkillDefinition
5. 通过 LLM 补全描述与参数说明
6. 导出为 JSON / Markdown / 注册表结构
