package tutorial.modtutorial.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tutorial.modtutorial.service.BackUpService;

import java.io.IOException;
import java.io.InputStream;


@Service
public class BackUpServiceImpl implements BackUpService {
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;


    // ======================= ADMIN ZONE =======================
    @Override
    public InputStream getBackup() {
        Process process;

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "pg_dump",
                    "-h", "127.0.0.1",
                    "-p", "5432",
                    "-U", username,
                    "-F", "c",
                    getDatabaseName()
            );

            processBuilder.environment().put("PGPASSWORD", password);

            process = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create backup file", e.getCause());
        }

        return process.getInputStream();
    }


    private String getDatabaseName() {
        String[] parts = url.split("/");
        return parts[parts.length - 1].split("\\?")[0];
    }
}
