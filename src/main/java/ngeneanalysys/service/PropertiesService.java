package ngeneanalysys.service;

import ngeneanalysys.util.ResourceUtil;

import java.util.Properties;

/**
 * @author Jang
 * @since 2017-08-08
 */
public class PropertiesService {
    private Properties config;

    private PropertiesService() {
        ResourceUtil resourceUtil = new ResourceUtil();
        config = new Properties();
        try {
            config.load(resourceUtil.getResourceAsStream("/properties/application.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static class PropertiesServiceHelper {
        private PropertiesServiceHelper() {}
        private static final PropertiesService INSTANCE = new PropertiesService();
    }

    public static PropertiesService getInstance() {
        return PropertiesServiceHelper.INSTANCE;
    }

    public Properties getConfig() {
        return config;
    }
}
