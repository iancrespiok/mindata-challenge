package com.ian.challenge;

import com.ian.challenge.infrastructure.adapter.in.rest.dto.SearchCountResponseDTO;
import com.ian.challenge.infrastructure.adapter.in.rest.dto.SearchRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1, topics = "hotel_availability_searches")
@DirtiesContext
class SearchFlowIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void registersSearchAndReturnsMatchingCount() {
        SearchRequestDTO request = Fixture.defaultRequestDto();

        ResponseEntity<SearchCountResponseDTO> searchResponse = restTemplate.postForEntity(
                url("/search"), request, SearchCountResponseDTO.class);

        assertThat(searchResponse.getStatusCode().value()).isEqualTo(201);
        String searchId = searchResponse.getBody().searchId();
        assertThat(searchId).isNotBlank();

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            ResponseEntity<SearchCountResponseDTO> countResponse = restTemplate.getForEntity(
                    url("/count?searchId=" + searchId), SearchCountResponseDTO.class);
            assertThat(countResponse.getStatusCode().value()).isEqualTo(200);
            assertThat(countResponse.getBody().count()).isGreaterThanOrEqualTo(1L);
            assertThat(countResponse.getBody().search().hotelId()).isEqualTo("1234aBc");
            assertThat(countResponse.getBody().search().ages()).containsExactly(30, 29, 1, 3);
        });
    }

    @Test
    void countReturns404ForUnknownSearchId() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                url("/count?searchId=does-not-exist"), String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
