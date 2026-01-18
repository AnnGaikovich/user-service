package org.example.userservice.controller;

import org.springframework.data.redis.core.RedisTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/cache")
@Tag(name = "Cache Management", description = "APIs for managing Redis cache")
@PreAuthorize("hasRole('ADMIN')")
public class CacheController {

    private final RedisTemplate<String, Object> redisTemplate;

    public CacheController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Operation(summary = "Get all cache keys", description = "Retrieves all keys currently stored in Redis cache")
    @GetMapping("/keys")
    public ResponseEntity<List<String>> getAllCacheKeys() {
        Set<String> keys = redisTemplate.keys("*");
        return ResponseEntity.ok(new ArrayList<>(keys));
    }

    @Operation(summary = "Get cache value by key", description = "Retrieves the value for a specific cache key")
    @GetMapping("/value/{key}")
    public ResponseEntity<Object> getCacheValue(
            @Parameter(description = "Cache key") @PathVariable String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return ResponseEntity.ok(value);
    }

    @Operation(summary = "Delete cache key", description = "Deletes a specific key from Redis cache")
    @DeleteMapping("/keys/{key}")
    public ResponseEntity<String> deleteCacheKey(
            @Parameter(description = "Cache key to delete") @PathVariable String key) {
        Boolean deleted = redisTemplate.delete(key);
        if (Boolean.TRUE.equals(deleted)) {
            return ResponseEntity.ok("Key '" + key + "' deleted successfully");
        } else {
            return ResponseEntity.badRequest().body("Key '" + key + "' not found");
        }
    }

    @Operation(summary = "Clear all cache", description = "Clears all keys from Redis cache")
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearAllCache() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            return ResponseEntity.ok("Cache cleared successfully. Deleted " + keys.size() + " keys");
        }
        return ResponseEntity.ok("Cache is already empty");
    }

    @Operation(summary = "Get cache information", description = "Retrieves information about the Redis cache")
    @GetMapping("/info")
    public ResponseEntity<String> getCacheInfo() {
        Long size = redisTemplate.getConnectionFactory().getConnection().dbSize();
        return ResponseEntity.ok("Total keys in cache: " + size);
    }
}