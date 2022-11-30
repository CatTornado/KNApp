package com.example.kn.integration;

import com.example.kn.console.ConsoleComponent;
import com.example.kn.properties.ApplicationProperties;
import com.example.kn.service.DataAggregationService;
import com.example.kn.web.RestService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URI;

import static com.example.kn.util.Loader.loadTestData;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class IntegrationTests {

    @Autowired
    private ApplicationProperties applicationProperties;
    @Autowired
    private ConsoleComponent consoleComponent;
    @Autowired
    private DataAggregationService dataAggregationService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RestService restService;

    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeAll
    public static void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterAll
    public static void restoreStreams() {
        System.setOut(System.out);
    }

    @Test
    public void testPositive() throws Exception {
        MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
        mockServer.expect(ExpectedCount.times(1),
                          requestTo(new URI(applicationProperties.getCurrentPriceAddress())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(loadTestData("/test_data/currentprice_response_example.json")));

        mockServer.expect(ExpectedCount.times(1),
                        requestTo(new URI(applicationProperties.getStatisticsAddress())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(loadTestData("/test_data/historical_response_example.json")));

        consoleComponent.refresh();
        consoleComponent.stats("EUR");

        String consoleOutput = outContent.toString(UTF_8);

        assertTrue(consoleOutput.contains("16044,96"));
        assertTrue(consoleOutput.contains("27001,28"));
        assertTrue(consoleOutput.contains("24139,46"));
    }

}
