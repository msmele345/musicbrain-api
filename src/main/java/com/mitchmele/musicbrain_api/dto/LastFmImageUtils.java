package com.mitchmele.musicbrain_api.dto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class LastFmImageUtils {

    static final String LASTFM_PLACEHOLDER = "2a96cbd8b46e442fc41c2b86b821562f";

    private LastFmImageUtils() {}

    public static Optional<String> extractLargeImageUrl(List<Map<String, String>> images) {
        if (images == null) return Optional.empty();
        return images.stream()
                .filter(img -> "large".equals(img.get("size")))
                .map(img -> img.get("#text"))
                .filter(u -> u != null && !u.contains(LASTFM_PLACEHOLDER))
                .findFirst();
    }
}
