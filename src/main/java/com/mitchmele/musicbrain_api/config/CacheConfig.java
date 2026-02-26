package com.mitchmele.musicbrain_api.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(List.of(
                buildCache("topTags", Duration.ofHours(1)),
                buildCache("topArtists", Duration.ofMinutes(30)),
                buildCache("recentTracks", Duration.ofMinutes(5)),
                buildCache("similarArtists", Duration.ofHours(1))
        ));
        return manager;
    }

    private CaffeineCache buildCache(String name, Duration ttl) {
        return new CaffeineCache(name, Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(ttl)
                .build());
    }
}
