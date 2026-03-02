package com.mitchmele.musicbrain_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackDto {

    @Setter
    private String name;
    private String artist;
    private String album;
    private String timestamp;
    private boolean nowPlaying;

    private TrackDto() {}

    public static TrackDto of(String name, String artist, String album, String timestamp, boolean nowPlaying) {
        TrackDto dto = new TrackDto();
        dto.name = name;
        dto.artist = artist;
        dto.album = album;
        dto.timestamp = timestamp;
        dto.nowPlaying = nowPlaying;
        return dto;
    }

    @JsonProperty("artist")
    public void setArtist(JsonNode node) {
        if (node != null) {
            this.artist = node.path("#text").asText();
        }
    }

    @JsonProperty("album")
    public void setAlbum(JsonNode node) {
        if (node != null) {
            this.album = node.path("#text").asText();
        }
    }

    @JsonProperty("date")
    public void setDate(JsonNode node) {
        if (node != null && !node.isMissingNode()) {
            this.timestamp = node.path("#text").asText("");
        }
    }

    @JsonProperty("@attr")
    public void setAttr(JsonNode node) {
        if (node != null && !node.isMissingNode()) {
            this.nowPlaying = "true".equalsIgnoreCase(node.path("nowplaying").asText());
        }
    }
}
