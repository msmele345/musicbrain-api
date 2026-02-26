package com.mitchmele.musicbrain_api.service;

import com.mitchmele.musicbrain_api.dto.ArtistDto;
import com.mitchmele.musicbrain_api.dto.RecentTracksResponse;
import com.mitchmele.musicbrain_api.dto.SimilarArtistDto;
import com.mitchmele.musicbrain_api.dto.SimilarArtistsResponse;
import com.mitchmele.musicbrain_api.dto.TagDto;
import com.mitchmele.musicbrain_api.dto.TopArtistsResponse;
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

    @Test
    void getTopArtists_returnsArtistsFromLastFm() throws InterruptedException {
        String json = """
                {
                  "topartists": {
                    "artist": [
                      {
                        "name": "Radiohead",
                        "playcount": "1500",
                        "url": "https://last.fm/music/Radiohead",
                        "image": [
                          {"#text": "https://img.small.jpg", "size": "small"},
                          {"#text": "https://img.large.jpg", "size": "large"}
                        ]
                      },
                      {
                        "name": "Portishead",
                        "playcount": "800",
                        "url": "https://last.fm/music/Portishead",
                        "image": []
                      }
                    ]
                  }
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(json)
                .addHeader("Content-Type", "application/json"));

        Mono<TopArtistsResponse> result = lastFmService.getTopArtists(10, "7day");

        StepVerifier.create(result)
                .assertNext(response -> {
                    List<ArtistDto> artists = response.getTopArtists().getArtists();
                    assertThat(artists).hasSize(2);
                    assertThat(artists.get(0).getName()).isEqualTo("Radiohead");
                    assertThat(artists.get(0).getPlaycount()).isEqualTo("1500");
                    assertThat(artists.get(0).getImageUrl()).isEqualTo("https://img.large.jpg");
                    assertThat(artists.get(1).getName()).isEqualTo("Portishead");
                })
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath()).contains("method=user.getTopArtists");
        assertThat(request.getPath()).contains("user=test-user");
        assertThat(request.getPath()).contains("api_key=test-api-key");
        assertThat(request.getPath()).contains("format=json");
        assertThat(request.getPath()).contains("limit=10");
        assertThat(request.getPath()).contains("period=7day");
    }

    @Test
    void getRecentTracks_returnsTracksFromLastFm() throws InterruptedException {
        String json = """
                {
                  "recenttracks": {
                    "track": [
                      {
                        "name": "Fake Plastic Trees",
                        "artist": {"#text": "Radiohead", "mbid": ""},
                        "album": {"#text": "The Bends", "mbid": ""},
                        "@attr": {"nowplaying": "true"}
                      },
                      {
                        "name": "Glory Box",
                        "artist": {"#text": "Portishead", "mbid": ""},
                        "album": {"#text": "Dummy", "mbid": ""},
                        "date": {"uts": "1700000000", "#text": "14 Nov 2023, 20:00"}
                      }
                    ]
                  }
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(json)
                .addHeader("Content-Type", "application/json"));

        Mono<RecentTracksResponse> result = lastFmService.getRecentTracks(20);

        StepVerifier.create(result)
                .assertNext(response -> {
                    var tracks = response.getRecentTracks().getTracks();
                    assertThat(tracks).hasSize(2);
                    assertThat(tracks.get(0).getName()).isEqualTo("Fake Plastic Trees");
                    assertThat(tracks.get(0).getArtist()).isEqualTo("Radiohead");
                    assertThat(tracks.get(0).getAlbum()).isEqualTo("The Bends");
                    assertThat(tracks.get(0).isNowPlaying()).isTrue();
                    assertThat(tracks.get(1).getName()).isEqualTo("Glory Box");
                    assertThat(tracks.get(1).isNowPlaying()).isFalse();
                    assertThat(tracks.get(1).getTimestamp()).isEqualTo("14 Nov 2023, 20:00");
                })
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath()).contains("method=user.getRecentTracks");
        assertThat(request.getPath()).contains("user=test-user");
        assertThat(request.getPath()).contains("api_key=test-api-key");
        assertThat(request.getPath()).contains("format=json");
        assertThat(request.getPath()).contains("limit=20");
    }

    @Test
    void getSimilarArtists_returnsSimilarArtistsFromLastFm() throws InterruptedException {
        String json = """
                {
                  "similarartists": {
                    "artist": [
                      {
                        "name": "Massive Attack",
                        "match": "0.87",
                        "url": "https://last.fm/music/Massive+Attack",
                        "image": [
                          {"#text": "https://img.ma.jpg", "size": "large"}
                        ]
                      },
                      {
                        "name": "Tricky",
                        "match": "0.72",
                        "url": "https://last.fm/music/Tricky",
                        "image": []
                      }
                    ]
                  }
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(json)
                .addHeader("Content-Type", "application/json"));

        Mono<SimilarArtistsResponse> result = lastFmService.getSimilarArtists("Portishead", 10);

        StepVerifier.create(result)
                .assertNext(response -> {
                    List<SimilarArtistDto> artists = response.getSimilarArtists().getArtists();
                    assertThat(artists).hasSize(2);
                    assertThat(artists.get(0).getName()).isEqualTo("Massive Attack");
                    assertThat(artists.get(0).getMatch()).isEqualTo(0.87);
                    assertThat(artists.get(0).getImageUrl()).isEqualTo("https://img.ma.jpg");
                    assertThat(artists.get(1).getName()).isEqualTo("Tricky");
                    assertThat(artists.get(1).getMatch()).isEqualTo(0.72);
                })
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath()).contains("method=artist.getSimilar");
        assertThat(request.getPath()).contains("artist=Portishead");
        assertThat(request.getPath()).contains("api_key=test-api-key");
        assertThat(request.getPath()).contains("format=json");
        assertThat(request.getPath()).contains("limit=10");
    }
}
