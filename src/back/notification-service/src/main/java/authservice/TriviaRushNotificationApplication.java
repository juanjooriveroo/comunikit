package authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class TriviaRushNotificationApplication {
    public static void main(String[] args) {
        SpringApplication.run(TriviaRushNotificationApplication.class, args);
    }
}