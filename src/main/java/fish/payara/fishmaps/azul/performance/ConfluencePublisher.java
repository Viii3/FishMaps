package fish.payara.fishmaps.azul.performance;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.json.JSONObject;

/**
 * Utility class to publish or update content on a Confluence Cloud page.
 *
 * Requires these environment variables:
 *  - CONFLUENCE_BASE_URL (e.g., <a href="https://payara.atlassian.net/wiki">...</a>)
 *  - CONFLUENCE_USERNAME (email of Confluence user)
 *  - CONFLUENCE_API_TOKEN (API token from <a href="https://id.atlassian.com/manage-profile/security/api-tokens">...</a>)
 *  - CONFLUENCE_SPACE_KEY (Confluence space key)
 */
public class ConfluencePublisher {

    private final String baseUrl;
    private final String username;
    private final String apiToken;
    private final String spaceKey;
    private final HttpClient client;

    public ConfluencePublisher() {
        EnvLoader.load();
        // Initialize fields from environment variables
        this.baseUrl = getEnvOrThrow("CONFLUENCE_BASE_URL");
        this.username = getEnvOrThrow("CONFLUENCE_USERNAME");
        this.apiToken = getEnvOrThrow("CONFLUENCE_API_TOKEN");
        this.spaceKey = getEnvOrThrow("CONFLUENCE_SPACE_KEY");
        this.client = HttpClient.newHttpClient();
    }

    private String getEnvOrThrow(String name) {
        // Get the value from system properties (set by EnvLoader)
        String value = System.getProperty(name);

        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Missing required configuration: " + name + ". " +
                "Make sure to call EnvLoader.load() at application startup and that the .env file exists with the required variables.");
        }
        return value.trim();
    }
    
    public void publishResults(List<DurationLogger.PerformanceLog> logs) throws IOException, InterruptedException {
        String pageTitle = "Performance Test Results - FishMaps (" + LocalDate.now() + ")";
        String newContent = generateConfluenceContent(logs);

        // First, try to find an existing page
        String pageId = findPageId(pageTitle);

        if (pageId != null) {
            // Get existing content
            String existingContent = getPageContent(pageId);

            // Keep only the last 5 runs to prevent the page from becoming too large
            String[] runs = existingContent.split("<h2>Test Results</h2>");
            String recentRuns = runs.length > 5
                ? "<h2>Test Results</h2>" + String.join("<h2>Test Results</h2>", Arrays.copyOfRange(runs, runs.length - 4, runs.length))
                : existingContent;

            String updatedContent = recentRuns + "\n\n" + newContent;
            updatePage(pageId, pageTitle, updatedContent);
        } else {
            createPage(pageTitle, newContent);
        }
    }

    /**
     * Generates the Confluence content from the test suite results.
     */
    private String generateConfluenceContent(List<DurationLogger.PerformanceLog> logs) {
        StringBuilder content = new StringBuilder();

        // Build the content in Confluence Storage Format (XHTML)
        content.append("<h2>Test Results</h2>");
        content.append("<p><strong>Suite Name:</strong> ").append(escapeHtml("FishMaps")).append("</p>");

        // Add version information
        String payaraVersion = System.getProperty("payara.version", "N/A");
        String javaVersion = Runtime.version().toString();

        content.append("<p>")
            .append("<strong>Date:</strong> ").append(LocalDate.now()).append("<br/>")
            .append("<strong>Payara Version:</strong> ").append(escapeHtml(payaraVersion)).append("<br/>")
            .append("<strong>JDK Version:</strong> ").append(escapeHtml(javaVersion))
            .append("</p>");

        // Summary section
        content.append("<h3>Summary</h3>");
        content.append("<p>")
            .append("<strong>Total Tests:</strong> ").append(logs.size());
        content.append("</p>");

        // Initialize with default values
        String platformVersion = payaraVersion.startsWith("6") ? "Payara 6" :
            payaraVersion.startsWith("7") ? "Payara 7" : "";

        // Add metrics table
        content.append("<h3>Performance Metrics</h3>");
        content.append("<table class='wrapped' style='table-layout: fixed; width: 100%;'><colgroup>");
        // Define column widths for better readability - increased to prevent text wrapping
        content.append("<col style='width: 140px;'/>");  // Module
        content.append("<col style='width: 120px;'/>");  // Item Size
        content.append("<col style='width: 180px;'/>");  // Duration
        content.append("</colgroup><tbody>");

        // Table header
        content.append("<tr>");
        content.append("<th>Module</th>");
        content.append("<th>Items Processed</th>");
        content.append("<th>Duration (ns)</th>");
        content.append("</tr>");

        for (DurationLogger.PerformanceLog log : logs) {
            content.append("<tr>");
            content.append("<td>").append(log.module()).append("</td>");
            content.append("<td>").append(log.itemSize().map(String::valueOf).orElse("-")).append("</td>");
            content.append("<td>").append(log.nanosecondDuration()).append("</td>");
            content.append("</tr>");
        }
        content.append("</tbody></table>");
        return content.toString();
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }

    /**
     * Creates a new Confluence page.
     */
    private void createPage(String title, String content) throws IOException, InterruptedException {
        JSONObject pageJson = new JSONObject();
        JSONObject spaceJson = new JSONObject();
        spaceJson.put("key", spaceKey);

        // Use storage format for consistency
        JSONObject storage = new JSONObject()
            .put("value", content)
            .put("representation", "storage");

        JSONObject body = new JSONObject()
            .put("storage", storage);

        pageJson.put("type", "page")
            .put("title", title)
            .put("space", spaceJson)
            .put("body", body);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/rest/api/content"))
            .header("Authorization", "Basic " + basicAuth())
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(pageJson.toString()))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Created Confluence page: " + response.statusCode() + " - " + response.body());
    }

    /**
     * Updates an existing Confluence page.
     */
    private void updatePage(String pageId, String title, String content) throws IOException, InterruptedException {
        // First get the current page to get the version number and existing content
        HttpRequest getRequest = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/rest/api/content/" + pageId + "?expand=version,body.storage"))
            .header("Authorization", "Basic " + basicAuth())
            .header("Accept", "application/json")
            .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        JSONObject existingPage = new JSONObject(getResponse.body());

        // Get the current version and increment it
        JSONObject version = existingPage.getJSONObject("version");
        version.put("number", version.getInt("number") + 1);

        // Prepare the update payload with storage format
        JSONObject storage = new JSONObject()
            .put("value", content)
            .put("representation", "storage");

        JSONObject body = new JSONObject()
            .put("storage", storage);

        JSONObject pageJson = new JSONObject()
            .put("id", pageId)
            .put("type", "page")
            .put("title", title)
            .put("version", version)
            .put("body", body);

        // Send the update
        HttpRequest updateRequest = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/rest/api/content/" + pageId))
            .header("Authorization", "Basic " + basicAuth())
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString(pageJson.toString()))
            .build();

        HttpResponse<String> updateResponse = client.send(updateRequest, HttpResponse.BodyHandlers.ofString());

        if (updateResponse.statusCode() >= 200 && updateResponse.statusCode() < 300) {
            System.out.println("Successfully updated Confluence page: " + title);
        } else {
            System.err.println("Failed to update Confluence page. Status: " + updateResponse.statusCode() +
                " Response: " + updateResponse.body());
        }
    }

    /**
     * Find an existing Confluence page by title in the given space.
     */
    private String findPageId(String title) throws IOException, InterruptedException {
        String url = String.format("%s/rest/api/content?title=%s&spaceKey=%s&expand=version",
            baseUrl, encode(title), spaceKey);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Basic " + basicAuth())
            .header("Accept", "application/json")
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject json = new JSONObject(response.body());
        if (json.has("results") && !json.getJSONArray("results").isEmpty()) {
            return json.getJSONArray("results").getJSONObject(0).getString("id");
        }
        return null;
    }

    /** Encode string for URL */
    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /** Generate Basic Auth header */
    private String basicAuth() {
        String auth = username + ":" + apiToken;
        return Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Gets the current content of a Confluence page.
     */
    private String getPageContent(String pageId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(String.format("%s/rest/api/content/%s?expand=body.storage", baseUrl, pageId)))
            .header("Authorization", "Basic " + basicAuth())
            .header("Accept", "application/json")
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject json = new JSONObject(response.body());

        if (json.has("body") && json.getJSONObject("body").has("storage")) {
            return json.getJSONObject("body").getJSONObject("storage").getString("value");
        }
        return "";
    }
}