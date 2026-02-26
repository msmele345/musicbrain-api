package com.mitchmele.musicbrain_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArtistDto {

    private String name;
    private String playcount;
    private String url;
    private String imageUrl;

    public ArtistDto() {}

    public static ArtistDto of(String name, String playcount, String url, String imageUrl) {
        ArtistDto dto = new ArtistDto();
        dto.name = name;
        dto.playcount = playcount;
        dto.url = url;
        dto.imageUrl = imageUrl;
        return dto;
    }

    public String getName() { return name; }
    public String getPlaycount() { return playcount; }
    public String getUrl() { return url; }
    public String getImageUrl() { return imageUrl; }

    public void setName(String name) { this.name = name; }
    public void setPlaycount(String playcount) { this.playcount = playcount; }
    public void setUrl(String url) { this.url = url; }

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
