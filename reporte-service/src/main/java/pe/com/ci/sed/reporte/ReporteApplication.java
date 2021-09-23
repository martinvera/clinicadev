package pe.com.ci.sed.reporte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class ReporteApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReporteApplication.class, args);
    }
}
