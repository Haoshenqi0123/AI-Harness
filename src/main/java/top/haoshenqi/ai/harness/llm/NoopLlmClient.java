package top.haoshenqi.ai.harness.llm;

public class NoopLlmClient implements LlmClient {

    @Override
    public String complete(String prompt) {
        return "";
    }
}

