package com.taptech.spoonscore.config;

import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

@Configuration
@EnableJpaRepositories("com.taptech.spoonscore.repository")
@EnableTransactionManagement
//@PropertySource("classpath:application-${spring.profiles.active}.properties")
public class DatabaseConfiguration implements EnvironmentAware {

    //postgres://postgres:2a2d922d81b097c21de11ed2d364d1ae@dokku-postgres-spoonscore:5432/spoonscore
    private final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);

    private RelaxedPropertyResolver propertyResolver;

    private Environment env;


    @Autowired(required = false)
    private DBProperties dbProperties;


    @Override
    public void setEnvironment(Environment env) {
        this.env = env;
        this.propertyResolver = new RelaxedPropertyResolver(env, "spring.datasource.");
        String httpPort = env.getProperty("http.port");
        if (null == httpPort){
            log.info("Attempting to get port from PORT environment variable PORT");
            httpPort = env.getProperty("PORT");
        }
        if (null == httpPort){
            log.info("Using default port of 5000. Could not find http.port in any env");
            httpPort = "5000";
        }
        System.setProperty("http.port",httpPort);
        Integer jettyPort = Integer.parseInt(httpPort) + 1;
        log.info("Using http.port => {} jetty port => {}",httpPort,jettyPort.toString());
        System.setProperty("jetty.port",jettyPort.toString());
    }

    @Bean(destroyMethod = "shutdown")
    @Qualifier(value = "dataSource")
    public DataSource dataSource() {
        log.info("Configuring Datasource with dbProperties {}",dbProperties);
        String dbURLStr = this.env.getProperty("DATABASE_URL");
        URL url = null;
        log.info("DATABASE_URL is {}",this.env.getProperty("DATABASE_URL"));
        if (null != dbURLStr){
            try {
                url = new URL(null,dbURLStr, new PostgresURLHandler());
                log.info("url.getPath() {}",url.getPath());
                log.info("url.getHost() {}",url.getHost());
                log.info("url.getUserInfo() {}",url.getUserInfo());
            } catch (MalformedURLException e) {
                log.error("Error parsing {}",dbURLStr,e);
            }
        }
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(propertyResolver.getProperty("dataSourceClassName"));
        config.addDataSourceProperty("databaseName", url.getPath().substring(1));
        config.addDataSourceProperty("serverName", url.getHost());
        String [] userInfo = url.getUserInfo().split(":");
        config.addDataSourceProperty("user", userInfo[0]);
        config.addDataSourceProperty("password", userInfo[1]);

        String profiles = Arrays.toString(env.getActiveProfiles());
        log.info("############################DATABASE CONFIG######################");
        log.info("Profiles => {}",profiles);
        log.info("DBURL [{}]", url.toString());
        HikariDataSource dataSource = new HikariDataSource(config);
        dataSource.setMaximumPoolSize(22);
        return dataSource;
    }

    @Bean
    public Hibernate4Module hibernate4Module() {
        return new Hibernate4Module();
    }
}
