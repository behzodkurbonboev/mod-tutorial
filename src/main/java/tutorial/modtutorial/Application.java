package tutorial.modtutorial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import tutorial.modtutorial.configuration.ApplicationProperties;

import java.util.TimeZone;


@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class Application {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Karachi"));
        SpringApplication.run(Application.class, args);
    }
}
