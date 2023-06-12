package com.av;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils {
    public static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = ProducerApp.class.getClassLoader().getResourceAsStream("client.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return properties;
    }
}
