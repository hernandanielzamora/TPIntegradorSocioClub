package ar.edu.siglo21.socioclub.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** La fecha usa la zona del proceso; Clock permite probar cumpleaños sin depender del día. */
@Configuration
public class FechaConfig {
    @Bean
    public Clock reloj() {
        return Clock.systemDefaultZone();
    }
}
