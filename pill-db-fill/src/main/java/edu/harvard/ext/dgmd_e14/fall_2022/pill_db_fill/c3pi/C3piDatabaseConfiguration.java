package edu.harvard.ext.dgmd_e14.fall_2022.pill_db_fill.c3pi;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan({
        "edu.harvard.ext.dgmd_e14.fall_2022.pill_match",
        "edu.harvard.ext.dgmd_e14.fall_2022.pill_db_fill.c3pi"
})
@EnableJpaRepositories(
        basePackages = {
                "edu.harvard.ext.dgmd_e14.fall_2022.pill_match",
                "edu.harvard.ext.dgmd_e14.fall_2022.pill_db_fill.c3pi"
        }
)
public class C3piDatabaseConfiguration {

}
