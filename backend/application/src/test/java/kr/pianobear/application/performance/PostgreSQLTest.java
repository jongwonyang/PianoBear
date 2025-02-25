package kr.pianobear.application.performance;

import org.junit.jupiter.api.*;
import java.sql.*;

public class PostgreSQLTest {

    private static Connection connection;
    private static final int TEST_COUNT = 10000;
    private static final int DATASET_SIZE = 100000;
    private static final String TABLE_NAME = "jti_store";

    @BeforeAll
    static void setup() throws Exception {
        connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/testdb",
                "postgres",
                "postgres"
        );

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (jti TEXT PRIMARY KEY)");
            stmt.execute("TRUNCATE TABLE " + TABLE_NAME);

            // 10,000개의 JTI 데이터 삽입
            try (PreparedStatement pstmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " (jti) VALUES (?)")) {
                for (int i = 0; i < DATASET_SIZE; i++) {
                    pstmt.setString(1, "jti-" + i);
                    pstmt.executeUpdate();
                }
            }
        }
    }

    @Test
    void testSelectPerformance() throws Exception {
        long totalFoundTime = 0;
        long totalNotFoundTime = 0;
        int halfTestCount = TEST_COUNT / 2;

        String query = "SELECT jti FROM " + TABLE_NAME + " WHERE jti = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            // Found
            for (int i = 0; i < halfTestCount; i++) {
                String targetJti = "jti-" + (i % DATASET_SIZE);

                long start = System.nanoTime();
                pstmt.setString(1, targetJti);
                ResultSet rs = pstmt.executeQuery();
                boolean exists = rs.next();
                long end = System.nanoTime();

                if (exists) {
                    totalFoundTime += (end - start);
                }
            }

            // Not Found
            for (int i = 0; i < halfTestCount; i++) {
                String targetJti = "not-exist-" + i;

                long start = System.nanoTime();
                pstmt.setString(1, targetJti);
                ResultSet rs = pstmt.executeQuery();
                boolean exists = rs.next();
                long end = System.nanoTime();

                if (!exists) {
                    totalNotFoundTime += (end - start);
                }
            }
        }

        System.out.println("PostgreSQL 조회 성능 테스트 결과");
        System.out.printf(" - Found 평균 시간: %.3f ms%n", (totalFoundTime / 1_000_000.0) / halfTestCount);
        System.out.printf(" - Not Found 평균 시간: %.3f ms%n", (totalNotFoundTime / 1_000_000.0) / halfTestCount);
    }

    @AfterAll
    static void teardown() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }
}
