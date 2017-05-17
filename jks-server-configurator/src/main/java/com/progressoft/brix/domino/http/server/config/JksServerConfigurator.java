package com.progressoft.brix.domino.http.server.config;

import com.google.auto.service.AutoService;
import com.progressoft.brix.domino.api.server.config.HttpServerConfigurator;
import com.progressoft.brix.domino.api.server.config.ServerConfiguration;
import com.progressoft.brix.domino.api.server.entrypoint.VertxContext;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;

import static java.lang.Boolean.TRUE;

@AutoService(HttpServerConfigurator.class)
public class JksServerConfigurator implements HttpServerConfigurator {

    private static final String SSL_CONFIGURATION_KEY = "ssl.enabled";
    private static final String SSL_JKS_PATH = "ssl.jks.path";
    private static final String SSL_JKS_SECRET = "ssl.jks.password";
    public static final String DEFAULT_EMPTY = "";

    @Override
    public void configureHttpServer(VertxContext context, HttpServerOptions options) {
        applyConfigurations(context.config(), options);
    }

    private void applyConfigurations(ServerConfiguration configuration, HttpServerOptions options) {
        validateConfiguration(configuration);
        if (sslEnabled(configuration)) {
            enableSsl(configuration, options);
        }
    }

    private void enableSsl(ServerConfiguration configuration,HttpServerOptions options) {
        options.setSsl(TRUE);
        options.setKeyStoreOptions(new JksOptions()
                .setPath(getPath(configuration))
                .setPassword(getSecret(configuration)));
    }

    private String getSecret(ServerConfiguration configuration) {
        return configuration.getString(SSL_JKS_SECRET);
    }

    private String getPath(ServerConfiguration configuration) {
        return configuration.getString(SSL_JKS_PATH);
    }

    private void validateConfiguration(ServerConfiguration configuration) {
        if (sslEnabled(configuration))
            validateSslPathAndPassword(configuration);
    }

    private Boolean sslEnabled(ServerConfiguration configuration) {
        return configuration.getBoolean(SSL_CONFIGURATION_KEY, false);
    }

    private void validateSslPathAndPassword(ServerConfiguration configuration) {
        if (missingSslJksPath(configuration))
            throw new MissingJksPathInConfigurationException();
        if (missingSslJksPassword(configuration))
            throw new MissingJksPasswordInConfigurationException();
    }

    private boolean missingSslJksPath(ServerConfiguration configuration) {
        return configuration.getString(SSL_JKS_PATH, DEFAULT_EMPTY).isEmpty();
    }

    private boolean missingSslJksPassword(ServerConfiguration configuration) {
        return configuration.getString(SSL_JKS_SECRET, DEFAULT_EMPTY).isEmpty();
    }

    public class MissingJksPathInConfigurationException extends RuntimeException {
    }

    public class MissingJksPasswordInConfigurationException extends RuntimeException {
    }
}
