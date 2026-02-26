package com.mitchmele.musicbrain_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecentTracksResponse {

    @JsonProperty("recenttracks")
    private RecentTracks recentTracks;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RecentTracks {
        @JsonProperty("track")
        private List<TrackDto> tracks;
    }
}
