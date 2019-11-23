package org.jenkinsci.plugins.slacknotifier;

public class FeatureResult {
    private final String uri;
    private final String name;
    private final int passPercentage;
    private final int totalFailedScenarios;
    private final int totalScenarios;

    public FeatureResult(String name, int passPercentage) {
        this(name, passPercentage, 0, 0, name);
    }

    public FeatureResult(String name, int passPercentage, int totalScenarios, int totalFailedScenarios, String uri) {
        this.uri = uri;
        this.name = name;
        this.passPercentage = passPercentage;
        this.totalScenarios = totalScenarios;
        this.totalFailedScenarios = totalFailedScenarios;
    }

    public String toString() {
        return this.uri + "=" + this.passPercentage;
    }

    public String getUri() {
        return this.uri;
    }

    public String getDisplayName() {
        return this.name.replaceAll("_", " ").replace(" feature$", "");
    }

    public int getPassPercentage() {
        return this.passPercentage;
    }

    public int getTotalScenarios() {
        return this.totalScenarios;
    }

    public int getTotalFailedScenarios() {
        return this.totalFailedScenarios;
    }

    public int getTotalPassedScenarios() {
        return this.totalScenarios - this.totalFailedScenarios;
    }

    public String getFeatureUri() {
        return this.uri.replace(".feature", "-feature").replace(" ", "-") + ".html";
    }
}