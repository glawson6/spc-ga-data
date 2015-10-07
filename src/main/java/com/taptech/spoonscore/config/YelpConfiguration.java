package com.taptech.spoonscore.config;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.inject.Inject;

@Configuration
public class YelpConfiguration {

    @Inject
    Environment env;

    @Bean
    public OAuthService getOAuthService(){
        String CONSUMER_KEY = env.getProperty("YELP_CONSUMER_KEY");
        String CONSUMER_SECRET = env.getProperty("YELP_CONSUMER_SECRET");
        OAuthService service =
                new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(CONSUMER_KEY)
                        .apiSecret(CONSUMER_SECRET).build();
        return service;
    }

    @Bean
    public Token getToken(){
        String TOKEN  = env.getProperty("YELP_TOKEN");
        String TOKEN_SECRET = env.getProperty("YELP_TOKEN_SECRET");
        Token accessToken = new Token(TOKEN, TOKEN_SECRET);
        return accessToken;
    }

    public static class TwoStepOAuth  extends DefaultApi10a {

        @Override
        public String getAccessTokenEndpoint() {
            return null;
        }

        @Override
        public String getAuthorizationUrl(Token arg0) {
            return null;
        }

        @Override
        public String getRequestTokenEndpoint() {
            return null;
        }
    }
}
