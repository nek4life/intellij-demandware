package com.demandware.studio;

import com.demandware.studio.settings.DWSettingsProvider;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.diagnostic.Logger;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.net.ssl.SSLException;
import java.io.IOException;

public class DWServerConnection {
    private final String basePath;
    private final CloseableHttpClient client;
    private final CredentialsProvider credentialsProvider;


    public DWServerConnection(DWSettingsProvider settingsProvider) {
        String hostname = settingsProvider.getHostname();
        String username = settingsProvider.getUsername();
        String password = settingsProvider.getPassword();
        String version = settingsProvider.getVersion();
        basePath = String.format("https://%s/on/demandware.servlet/webdav/Sites/Cartridges/%s", hostname, version);

        credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(hostname, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(username, password));

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(20);

        client = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
    }

    public String getBasePath() {
        return basePath;
    }

    public CloseableHttpClient getClient() {
        return client;
    }

    public CredentialsProvider getCredientials() {
        return credentialsProvider;
    }

    public static class RequestThread extends Thread {
        private final CloseableHttpClient httpClient;
        private final HttpClientContext context;
        private final HttpUriRequest request;
        private final Logger LOG = Logger.getInstance(RequestThread.class);

        public RequestThread(CloseableHttpClient httpClient,
                             CredentialsProvider credentialsProvider,
                             HttpUriRequest request) {

            this.httpClient = httpClient;
            this.request = request;
            this.context = new HttpClientContext();
            this.context.setCredentialsProvider(credentialsProvider);
        }

        @Override
        public void run() {
            try {
                CloseableHttpResponse response = httpClient.execute(request, context);
                try {
                    Notifications.Bus.notify(new Notification("demandware", "[request] ", request.getURI().toString(), NotificationType.INFORMATION));
                } finally {
                    response.close();
                }
            } catch (SSLException e) {
                LOG.error("This plugin requires JDK8 or Upgrade your Java security policies to Unlimited Strength policies", e);
            } catch (ClientProtocolException e) {
                LOG.error(e);
            } catch (IOException e) {
                LOG.error(e);
            }
        }
    }
}
