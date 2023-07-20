package org.apache.camel.example;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class WireMockTestResource implements QuarkusTestResourceLifecycleManager {

    private static final Logger logger = LoggerFactory.getLogger(WireMockTestResource.class);

    private WireMockServer server;

    @Override
    public Map<String, String> start() {
        // Setup & start the server
        server = new WireMockServer(wireMockConfig().dynamicPort());
        server.start();

        /* Add logging of request and any matched response. */
        server.addMockServiceRequestListener(
                WireMockTestResource::requestReceived);

        // create mock endpoint
        server.stubFor(
            post(urlEqualTo("/camel/individual/details"))
            .willReturn(
                aResponse()
                .withHeader("Content-Type", "application/xml")
                .withStatus(200)
                .withBodyFile("individual.xml")
            )
        );

        // obtain value as Camel property expects
        String host = getDomainName(server.baseUrl()).concat(":").concat(String.valueOf(server.port()));

        // Ensure the camel component API client passes requests through the WireMock proxy
        Map<String, String> conf = new HashMap<>();
        conf.put("api.backend1.host", host);
        return conf;
    }

    @Override
    public void stop() {
        if (server != null) {
            server.stop();
        }
    }

    private String getDomainName(String url) {
        String domain = "";
        try {
            URI uri = new URI(url);
            domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (URISyntaxException e) {
            // ignore
        }
        return domain;
    }

    /**
     * Logs information from supplied WireMock request and response objects.
     *
     * @param inRequest Object containing information from received request.
     * @param inResponse Response object containing data from the selected response.
     * If no response was matched, payload will be null and there will be no response
     * headers.
     */
    protected static void requestReceived(com.github.tomakehurst.wiremock.http.Request inRequest,
                                          com.github.tomakehurst.wiremock.http.Response inResponse) {
        logger.info("WireMock request at URL: {}", inRequest.getAbsoluteUrl());
        logger.info("WireMock request body: \n{}", inRequest.getBodyAsString());
        logger.info("WireMock request headers: \n{}", inRequest.getHeaders());
        logger.info("WireMock response body: \n{}", inResponse.getBodyAsString());
        logger.info("WireMock response headers: \n{}", inResponse.getHeaders());
    }
}
