package com.taptech.spoonscore.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by tap on 9/29/15.
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix="spring.datasource")
public class DBProperties {

    private String dataSourceClassName;
    private String url;
    private String databaseName;
    private String serverName;
    private String username;
    private String password;

    public String getDataSourceClassName() {
        return dataSourceClassName;
    }

    public void setDataSourceClassName(String dataSourceClassName) {
        this.dataSourceClassName = dataSourceClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DBProperties{");
        sb.append("dataSourceClassName='").append(dataSourceClassName).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", databaseName='").append(databaseName).append('\'');
        sb.append(", serverName='").append(serverName).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
