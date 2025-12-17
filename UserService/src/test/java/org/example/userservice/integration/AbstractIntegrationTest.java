// AbstractIntegrationTest.java
package org.example.userservice.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.example.userservice.config.TestSecurityConfig;
import org.example.userservice.config.TestRedisConfig;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, TestRedisConfig.class}) // Добавляем конфигурации
@TestPropertySource(locations = "classpath:application-test.yml")
public abstract class AbstractIntegrationTest {

    static final PostgreSQLContainer<?> postgreSQLContainer;
    static final GenericContainer<?> redisContainer;

    static {
        postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
        postgreSQLContainer.start();

        redisContainer = new GenericContainer<>("redis:7-alpine")
                .withExposedPorts(6379);
        redisContainer.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);

        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379).toString());

        // Отключаем кеширование в тестах для стабильности
        registry.add("spring.cache.type", () -> "none");
        registry.add("spring.jpa.properties.hibernate.cache.use_second_level_cache", () -> "false");
        registry.add("spring.jpa.properties.hibernate.cache.use_query_cache", () -> "false");
    }
}