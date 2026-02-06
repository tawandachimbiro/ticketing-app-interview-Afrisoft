package com.changamire.configs;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache Configuration
 * <p>
 * Configures Caffeine as the cache provider for the application.
 * Defines cache regions with specific TTL and size limits for event-related operations
 * to improve performance and reduce database load.
 * <p>
 * Cache Regions:
 * - events-all: All events paginated listings (5 min TTL, 100 entries)
 * - event-by-id: Individual event details (15 min TTL, 500 entries)
 * - events-filtered: Filtered/searched events (5 min TTL, 200 entries)
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-05
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure Caffeine cache manager with custom settings for each cache region
     * 
     * @return configured CacheManager
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
            "events-all",
            "event-by-id", 
            "events-filtered"
        );
        
        // Configure default cache settings
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats()
        );
        
        return cacheManager;
    }
    
    /**
     * Cache configuration for all events listing (paginated)
     * Short TTL because new events may be added frequently
     */
    @Bean
    public Caffeine<Object, Object> eventsAllCacheConfig() {
        return Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .recordStats();
    }
    
    /**
     * Cache configuration for individual event details
     * Longer TTL because individual event details change less frequently
     */
    @Bean
    public Caffeine<Object, Object> eventByIdCacheConfig() {
        return Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .recordStats();
    }
    
    /**
     * Cache configuration for filtered/searched events
     * Short TTL due to dynamic query combinations
     */
    @Bean
    public Caffeine<Object, Object> eventsFilteredCacheConfig() {
        return Caffeine.newBuilder()
            .maximumSize(200)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .recordStats();
    }
}
