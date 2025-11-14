package dev.JavaLovers.MyZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
// Diz ao Spring ONDE procurar por cada tipo de repositório
@EnableJpaRepositories(
    basePackages = "dev.JavaLovers.MyZone.repository", 
    // --- ADICIONE AS CHAVES AQUI ---
    excludeFilters = { @Filter(type = FilterType.ASSIGNABLE_TYPE, 
                              classes = dev.JavaLovers.MyZone.repository.AvaliacaoRepository.class) }
)
@EnableMongoRepositories(
    basePackages = "dev.JavaLovers.MyZone.repository",
    // --- E ADICIONE AS CHAVES AQUI ---
    includeFilters = { @Filter(type = FilterType.ASSIGNABLE_TYPE, 
                              classes = dev.JavaLovers.MyZone.repository.AvaliacaoRepository.class) }
)
public class MyZoneApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyZoneApplication.class, args);
    }

}