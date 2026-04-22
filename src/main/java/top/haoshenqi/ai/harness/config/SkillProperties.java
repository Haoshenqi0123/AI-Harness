package top.haoshenqi.ai.harness.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "top.haoshenqi.ai.harness")
public class SkillProperties {
    private Llm llm = new Llm();

    public Llm getLlm() {
        return llm;
    }

    public void setLlm(Llm llm) {
        this.llm = llm;
    }

    public static class Llm {

        private boolean enable;
        private String provider = "openai";
        private String apiKey;
        private String model = "gpt-4o-mini";
        private double temperature = 0.7d;
        private int maxTokens = 2048;
        private Duration timeout = Duration.ofSeconds(30);
        private final Map<String, String> prompt = new LinkedHashMap<>();
        private SkillScan skill = new SkillScan();

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public double getTemperature() {
            return temperature;
        }

        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }

        public int getMaxTokens() {
            return maxTokens;
        }

        public void setMaxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
        }

        public Duration getTimeout() {
            return timeout;
        }

        public void setTimeout(Duration timeout) {
            this.timeout = timeout;
        }

        public Map<String, String> getPrompt() {
            return prompt;
        }

        public void setPrompt(Map<String, String> prompt) {
            this.prompt.clear();
            if (prompt != null) {
                this.prompt.putAll(prompt);
            }
        }

        public SkillScan getSkill() {
            return skill;
        }

        public void setSkill(SkillScan skill) {
            this.skill = skill;
        }
    }

    public static class SkillScan {

        private boolean autoScan = true;

        public boolean isAutoScan() {
            return autoScan;
        }

        public void setAutoScan(boolean autoScan) {
            this.autoScan = autoScan;
        }
    }
}

