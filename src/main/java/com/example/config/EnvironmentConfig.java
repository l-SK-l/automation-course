package com.example.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Key;
import org.aeonbits.owner.Config.DefaultValue;

@Config.Sources({"classpath:config-${env}.properties"})
public interface EnvironmentConfig extends Config {
    @Key("baseUrl")
    String baseUrl();

    @Key("username")
    String username();

    @Key("password")
    String password();

    @Key("timeout")
    @DefaultValue("30")
    int timeout();
}