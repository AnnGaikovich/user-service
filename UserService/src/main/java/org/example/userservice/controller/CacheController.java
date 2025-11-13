package org.example.userservice.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/cache")
public class CacheController {

    private final RedisTemplate<String, Object> redisTemplate;

    public CacheController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/keys")
    public ResponseEntity<List<String>> getAllCacheKeys() {
        Set<String> keys = redisTemplate.keys("*");
        return ResponseEntity.ok(new ArrayList<>(keys));
    }

    @GetMapping("/keys/{pattern}")
    public ResponseEntity<List<String>> getCacheKeysByPattern(@PathVariable String pattern) {
        Set<String> keys = redisTemplate.keys("*" + pattern + "*");
        return ResponseEntity.ok(new ArrayList<>(keys));
    }

    @GetMapping("/value/{key}")
    public ResponseEntity<Object> getCacheValue(@PathVariable String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return ResponseEntity.ok(value);
    }

    @DeleteMapping("/keys/{key}")
    public ResponseEntity<String> deleteCacheKey(@PathVariable String key) {
        Boolean deleted = redisTemplate.delete(key);
        if (Boolean.TRUE.equals(deleted)) {
            return ResponseEntity.ok("Key '" + key + "' deleted successfully");
        } else {
            return ResponseEntity.badRequest().body("Key '" + key + "' not found");
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearAllCache() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            return ResponseEntity.ok("Cache cleared successfully. Deleted " + keys.size() + " keys");
        }
        return ResponseEntity.ok("Cache is already empty");
    }

    @GetMapping("/info")
    public ResponseEntity<String> getCacheInfo() {
        Long size = redisTemplate.getConnectionFactory().getConnection().dbSize();
        return ResponseEntity.ok("Total keys in cache: " + size);
    }
}