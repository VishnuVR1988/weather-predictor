package com.tcs.weather.predictor.support;

import com.typesafe.config.ConfigFactory;

import java.io.File;

/**
 * This class contains config values defined by the app
 * @author Vishnu
 * @version 1.0.0
 * @since 1.0.0
 */
public class ServiceConfig {

    private static ServiceConfig config = null;
    public final Spark spark;
    public final Input input;
    public final Output output;


    public ServiceConfig ( com.typesafe.config.Config c ) {
        this.spark = new ServiceConfig.Spark(c.getConfig("spark"));
        this.input = new ServiceConfig.Input(c.getConfig("input"));
        this.output = new ServiceConfig.Output(c.getConfig("output"));
    }

    public static ServiceConfig getConfig () {
        if (config == null) {
            config = new ServiceConfig(ConfigFactory.load().resolve());
        }
        return config;
    }


    public static ServiceConfig getConfig ( String fileName ) {
        if (config == null) {
            config = new ServiceConfig(ConfigFactory.parseFile(new File(fileName)).resolve());
        }
        return config;
    }


    public static class Spark {
        public final String master;
        public final String appName;


        public Spark ( com.typesafe.config.Config c ) {
            this.master = c.getString("master");
            this.appName = c.getString("appName");
        }

    }

    public static class Input {
        public final String dataPath;


        public Input ( com.typesafe.config.Config c ) {
            this.dataPath = c.getString("dataPath");
        }

    }

    public static class Output {
        public final String path;
        public final boolean print;


        public Output ( com.typesafe.config.Config c ) {
            this.path = c.getString("path");
            this.print = c.getBoolean("print");
        }

    }
}



