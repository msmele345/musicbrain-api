package com.mitchmele.musicbrain_api.service;

import com.mitchmele.musicbrain_api.dto.TagDto;
import com.mitchmele.musicbrain_api.dto.TopTagsResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LastFmServiceTest {

    private MockWebServer mockWebServer;
    private LastFmService lastFmService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .defaultHeader("User-Agent", "MusicBrain/1.0")
                .build();

        lastFmService = new LastFmService(webClient, "test-api-key", "test-user");
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getTopTags_returnsTagsFromLastFm() throws InterruptedException {
        String json = """
                {
                  "toptags": {
                    "tag": [
                      { "name": "rock", "count": 500, "url": "https://last.fm/tag/rock" },
                      { "name": "indie", "count": 300, "url": "https://last.fm/tag/indie" }
                    ]
                  }
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(json)
                .addHeader("Content-Type", "application/json"));

        Mono<TopTagsResponse> result = lastFmService.getTopTags(25);

        StepVerifier.create(result)
                .assertNext(response -> {
                    List<TagDto> tags = response.getTopTags().getTags();
                    assertThat(tags).hasSize(2);
                    assertThat(tags.get(0).getName()).isEqualTo("rock");
                    assertThat(tags.get(0).getCount()).isEqualTo(500);
                    assertThat(tags.get(1).getName()).isEqualTo("indie");
                })
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath()).contains("method=user.getTopTags");
        assertThat(request.getPath()).contains("user=test-user");
        assertThat(request.getPath()).contains("api_key=test-api-key");
        assertThat(request.getPath()).contains("format=json");
        assertThat(request.getPath()).contains("limit=25");
    }

    @Test
    void getTopTags_includesCorrectUserAgentHeader() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"toptags\":{\"tag\":[]}}")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(lastFmService.getTopTags(10))
                .assertNext(response -> assertThat(response).isNotNull())
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getHeader("User-Agent")).isEqualTo("MusicBrain/1.0");
    }
}
