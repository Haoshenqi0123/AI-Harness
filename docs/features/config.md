# 配置说明

## skill 配置

```properties
 #默认启用，扫描全部controller。若改为false，则只扫描@Skill 注解的类和方法
top.haoshenqi.ai.harness.llm.skill.auto-scan=true
```

## LLM 配置

```properties
top.haoshenqi.ai.harness.llm.enable=false
top.haoshenqi.ai.harness.llm.provider=openai
top.haoshenqi.ai.harness.llm.api-key=your_api_key
top.haoshenqi.ai.harness.llm.model=gpt-4o-mini
top.haoshenqi.ai.harness.llm.temperature=0.7
top.haoshenqi.ai.harness.llm.max-tokens=2048
top.haoshenqi.ai.harness.llm.timeout=30s
```

### 配置项说明

- `enable`：是否启用 LLM 自动生成能力
- `provider`：LLM 服务商，例如 `openai`、`deepseek`、`ollama`
- `api-key`：访问 LLM 的密钥
- `model`：模型名称
- `temperature`：生成随机性
- `max-tokens`：单次生成最大 token 数
- `timeout`：请求超时时间

## Prompt 配置

```properties
top.haoshenqi.ai.harness.llm.prompt.config-test=你好。
top.haoshenqi.ai.harness.llm.prompt.project-feature=阅读这个项目代码，理解它的功能和实现细节，并总结成一个文档 project-features.md，要求内容清晰、结构合理、覆盖全面，AI Agent 读取后能迅速理解并开始使用该项目。
```

### 常见 Prompt 场景

- 项目能力总结
- 接口技能描述生成
- 鉴权流程说明生成
- Controller 参数解释生成

## 输出配置（建议后续支持）

后续版本可支持将生成结果输出到：

- 本地文件系统
- Maven / Gradle 构建目录
- HTTP 端点
- 自定义文档存储
