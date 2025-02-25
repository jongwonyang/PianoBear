package kr.pianobear.application.performance;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.*;

public class RedisTest {

    private static RedisClient redisClient;
    private static RedisCommands<String, String> redisCommands;
    private static final int TEST_COUNT = 10000;
    private static final int DATASET_SIZE = 100000;

    @BeforeAll
    static void setup() {
        redisClient = RedisClient.create("redis://localhost:6379");
        redisCommands = redisClient.connect().sync();

        redisCommands.flushall();

        for (int i = 0; i < DATASET_SIZE; i++) {
            redisCommands.set("jti-" + i, "dummy-value");
        }
    }

    @Test
    void testSelectPerformance() {
        long totalFoundTime = 0;
        long totalNotFoundTime = 0;
        int halfTestCount = TEST_COUNT / 2;

        // Found
        for (int i = 0; i < halfTestCount; i++) {
            String targetJti = "jti-" + (i % DATASET_SIZE);

            long start = System.nanoTime();
            String value = redisCommands.get(targetJti);
            long end = System.nanoTime();

            if (value != null) {
                totalFoundTime += (end - start);
            }
        }

        // Not Found
        for (int i = 0; i < halfTestCount; i++) {
            String targetJti = "not-exist-" + i;

            long start = System.nanoTime();
            String value = redisCommands.get(targetJti);
            long end = System.nanoTime();

            if (value == null) {
                totalNotFoundTime += (end - start);
            }
        }

        System.out.println("Redis 조회 성능 테스트 결과");
        System.out.printf(" - Found 평균 시간: %.3f ms%n", (totalFoundTime / 1_000_000.0) / halfTestCount);
        System.out.printf(" - Not Found 평균 시간: %.3f ms%n", (totalNotFoundTime / 1_000_000.0) / halfTestCount);
    }

    @AfterAll
    static void teardown() {
        if (redisClient != null) {
            redisClient.shutdown();
        }
    }
}
