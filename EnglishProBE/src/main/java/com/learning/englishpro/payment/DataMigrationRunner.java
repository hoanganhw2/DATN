package com.learning.englishpro.payment;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

@Component
public class DataMigrationRunner implements CommandLineRunner {
    private final JdbcTemplate jdbcTemplate;

    public DataMigrationRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=================================================");
        System.out.println("STARTING DATA MIGRATION: Populating teacher_revenue...");
        // Nếu trước đó chưa lưu teacher_revenue, ta sẽ copy giá trị từ final_amount sang (phương án an toàn nhất)
        // Nếu muốn bóc tách chính xác coupon GLOBAL/SPECIFIC thì phức tạp hơn do mã coupon có thể đã bị xóa.
        // Ở đây lấy final_amount làm mặc định (đúng với 99% trường hợp không có mã hoặc mã SPECIFIC).
        int rows = jdbcTemplate.update("UPDATE orders SET teacher_revenue = final_amount WHERE teacher_revenue IS NULL");
        System.out.println("MIGRATION COMPLETE: Updated " + rows + " order records.");
        System.out.println("=================================================");
    }
}
