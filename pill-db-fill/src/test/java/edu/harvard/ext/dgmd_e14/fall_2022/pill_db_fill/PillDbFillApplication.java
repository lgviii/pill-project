package edu.harvard.ext.dgmd_e14.fall_2022.pill_db_fill;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_db_fill.c3pi.C3piDatabaseConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:db-fill-application-test.properties")
@Import({C3piDatabaseConfiguration.class})
@ComponentScan({"edu.harvard.ext.dgmd_e14.fall_2022.pill_db_fill",
                "edu.harvard.ext.dgmd_e14.fall_2022.pill_match"})
@SpringBootApplication
public class PillDbFillApplication {

    public static void main(String[] args) {
        SpringApplication.run(PillDbFillApplication.class, args);
    }
}
