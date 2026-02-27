package com.mitchmele.musicbrain_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArtistDto {

    private String name;
    private String playcount;
    private String url;
    private String imageUrl;

    private ArtistDto() {}

    public static ArtistDto of(String name, String playcount, String url, String imageUrl) {
        ArtistDto dto = new ArtistDto();
        dto.name = name;
        dto.playcount = playcount;
        dto.url = url;
        dto.imageUrl = imageUrl;
        return dto;
    }

    @JsonProperty("image")
    public void setImage(List<Map<String, String>> images) {
        if (images != null) {
            images.stream()
                    .filter(img -> "large".equals(img.get("size")))
                    .map(img -> img.get("#text"))
                    .findFirst()
                    .ifPresent(u -> this.imageUrl = u);
        }
    }
}
