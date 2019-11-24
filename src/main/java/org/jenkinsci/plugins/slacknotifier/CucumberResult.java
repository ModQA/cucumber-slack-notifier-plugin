package org.jenkinsci.plugins.slacknotifier;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class CucumberResult {
	final List<FeatureResult> featureResults;
	final int passPercentage;
	final int totalScenarios;
	
	public CucumberResult(List<FeatureResult> featureResults, int totalScenarios, int passPercentage) {
		this.featureResults = featureResults;
		this.totalScenarios = totalScenarios;
		this.passPercentage = passPercentage;
	}
	
	public int getPassPercentage() {
		return this.passPercentage;
	}
	
	public int getTotalFeatures() {
		return this.featureResults.size();
	}
	
	public int getTotalScenarios() {
		return this.totalScenarios;
	}
	
	public List<FeatureResult> getFeatureResults() {
		return this.featureResults;
	}
	
	public String toSlackMessage(final String jobName,
			final int buildNumber, final String channel, final String jenkinsUrl, final String buildUrl) {
		final JsonObject json = new JsonObject();
		json.addProperty("channel", "#" + channel);
		addCaption(json, buildNumber, jobName, jenkinsUrl, buildUrl);
		json.add("fields", getFields(buildUrl, buildNumber, jenkinsUrl));

		if (getPassPercentage() == 100) {
			addColourAndIcon(json, "good", ":thumbsup:");
		} else if (getPassPercentage() >= 98) {
			addColourAndIcon(json, "warning", ":hand:");
		} else {
			addColourAndIcon(json, "danger", ":thumbsdown:");
		}

		json.addProperty("username", jobName);
		return json.toString();
	}

	private String getJenkinsHyperlink(final String jenkinsUrl, final String buildUrl, final int buildNumber) {
		StringBuilder s = new StringBuilder();
		s.append(jenkinsUrl);
		if (!jenkinsUrl.trim().endsWith("/")) {
			s.append("/");
		}
		s.append(buildUrl);
		return s.toString();
	}
	
	public String toHeader(final String jobName, final int buildNumber, final String jenkinsUrl, final String buildUrl) {
		StringBuilder s = new StringBuilder();
		s.append("Features: ");
		s.append(getTotalFeatures());
		s.append(", Scenarios: ");
		s.append(getTotalScenarios());
		s.append(", Build: <");
		s.append(jenkinsUrl);
		if (!jenkinsUrl.trim().endsWith("/")) {
			s.append("/");
		}
		s.append(buildUrl);
		s.append(String.format("cucumber-html-reports/|%d", buildNumber));
		s.append(">");
		return s.toString();
	}
	
	private void addCaption(final JsonObject json, final int buildNumber, final String jobName, final String jenkinsUrl, final String buildUrl) {
		json.addProperty("pretext", toHeader(jobName, buildNumber, jenkinsUrl, buildUrl));
	}
	
	private void addColourAndIcon(JsonObject json, String good, String value) {
		json.addProperty("color", good);
		json.addProperty("icon_emoji", value);
	}

	private JsonArray getFields(final String buildUrl, final int buildNumber, final String jenkinsUrl) {
		final String hyperLink = getJenkinsHyperlink(jenkinsUrl, buildUrl, buildNumber) + "cucumber-html-reports/";
		final JsonArray fields = new JsonArray();
		fields.add(shortTitle("Features"));
		fields.add(shortTitle("Pass %"));
		generateFeaturesFields(fields, hyperLink);
		fields.add(shortObject("-------------------------------"));
		fields.add(shortObject("-------"));
		fields.add(shortObject("Total Passed"));
		fields.add(shortObject(getPassPercentage() + " %"));
		return fields;
	}

	private void generateFeaturesFields(JsonArray fields, String hyperLink){
		int counter = 0;
		for (FeatureResult feature : getFeatureResults()) {
			final String featureDisplayName = feature.getDisplayName();
			final String featureFileUri = feature.getUri();

			if (counter == 0){
				fields.add(shortObject("<" + hyperLink + "report-feature_" + toValidFileName(featureFileUri) + ".html|" + featureDisplayName + ">"));
			}else{
				fields.add(shortObject("<" + hyperLink + "report-feature_" + counter + "_" + toValidFileName(featureFileUri) + ".html|" + featureDisplayName + ">"));
			}

			fields.add(shortObject(feature.getPassPercentage() + " %"));
			counter++;
		}
	}

	
	private JsonObject shortObject(final String value) {
		JsonObject obj = new JsonObject();
		obj.addProperty("value", value);
		obj.addProperty("short", true);
		return obj;
	}

	private JsonObject shortTitle(final String title) {
		JsonObject obj = new JsonObject();
		obj.addProperty("title", title);
		obj.addProperty("short", true);
		return obj;
	}

	/**
	 * Converts characters of passed string and replaces to hash which can be treated as valid file name
	 *
	 * @param fileName sequence that should be converted
	 * @return converted string
	 */
	private String toValidFileName(String fileName) {
		// adds MAX_VALUE to eliminate minus character which might be returned by hashCode()
		return Long.toString((long) fileName.hashCode() + Integer.MAX_VALUE);
	}
}