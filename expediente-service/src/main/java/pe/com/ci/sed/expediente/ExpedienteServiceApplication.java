package pe.com.ci.sed.expediente;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ExpedienteServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpedienteServiceApplication.class, args);
    }

}
