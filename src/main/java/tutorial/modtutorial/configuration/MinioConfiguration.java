package tutorial.modtutorial.configuration;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MinioConfiguration {
    private final ApplicationProperties.Minio props;

    public MinioConfiguration(ApplicationProperties applicationProperties) {
        this.props = applicationProperties.getMinio();
    }


    @Bean
    MinioClient minioClient() {
        return MinioClient
                .builder()
                .endpoint(props.getHost())
                .credentials(props.getUsername(), props.getPassword())
                .build();
    }
}
