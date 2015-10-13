package com.taptech.spoonscore;

import com.taptech.spoonscore.config.DatabaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;

@ComponentScan
@Configuration
@Import({DatabaseConfiguration.class})
//@ComponentScan
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})

@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class,DispatcherServletAutoConfiguration.class,
        WebMvcAutoConfiguration.class, EmbeddedServletContainerAutoConfiguration.class})
@EnableConfigurationProperties
public class ApplicationTest {

    private static final Logger log = LoggerFactory.getLogger(ApplicationTest.class);

    @Autowired
    private Environment env;

    /**
     * Initializes spoonscore.
     * <p/>
     * Spring profiles can be configured with a program arguments --spring.profiles.active=your-active-profile
     * <p/>
     */
    @PostConstruct
    public void initApplication() throws IOException {
        if (env.getActiveProfiles().length == 0) {
            log.warn("No Spring profile configured, running with default configuration");
        } else {
            log.info("Running with Spring profile(s) : {}", Arrays.toString(env.getActiveProfiles()));
        }
    }


}
