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
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.net.ssl.SSLException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class DWServerConnection {
    private final String baseServerPath;
    private final CloseableHttpClient client;
    private final CredentialsProvider credentialsProvider;


    public DWServerConnection(DWSettingsProvider settingsProvider) {
        String hostname = settingsProvider.getHostname();
        String username = settingsProvider.getUsername();
        String password = settingsProvider.getPassword();
        String version = settingsProvider.getVersion();
        baseServerPath = String.format("https://%s/on/demandware.servlet/webdav/Sites/Cartridges/%s", hostname, version);

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

    public String getBaseServerPath() {
        return baseServerPath;
    }

    public String getCartridgeName(String rootPath, String filePath) {
        String[] parts = filePath.substring(0, rootPath.length()).split(File.separator);
        return parts[parts.length - 1];
    }

    public String getRemoteFilePath(String rootPath, String filePath) {
        String relPath = filePath.substring(rootPath.length(), filePath.length());
        String cartridgeName = getCartridgeName(rootPath, filePath);
        return baseServerPath + "/" + cartridgeName + relPath;
    }

    public ArrayList<String> getRemoteDirPaths(String rootPath, String filePath) {
        ArrayList<String> serverPaths = new ArrayList<String>();
        String relPath = filePath.substring(rootPath.length(), filePath.length());
        String cartridgeName = getCartridgeName(rootPath, filePath);

        if (relPath.startsWith(File.separator)) {
            relPath = relPath.substring(1, relPath.length());
        }

        String[] relParts = relPath.split(File.separator);
        String[] relDirs = Arrays.copyOfRange(relParts, 0, relParts.length - 1);

        String dirPath = "";
        for (String relDir : relDirs) {
            dirPath = dirPath + "/" + relDir;
            serverPaths.add(baseServerPath + "/" + cartridgeName + dirPath);
        }

        return serverPaths;
    }

    public CloseableHttpClient getClient() {
        return client;
    }

    public CredentialsProvider getCredientials() {
        return credentialsProvider;
    }

    public static class UpdateFileThread extends Thread {
        private final Logger LOG = Logger.getInstance(UpdateFileThread.class);

        private final CloseableHttpClient httpClient;
        private final HttpClientContext context;
        private final ArrayList<String> remoteDirpaths;
        private final String remoteFilePath;
        private final String localFilePath;

        public UpdateFileThread(CloseableHttpClient httpClient,
                                CredentialsProvider credentialsProvider,
                                ArrayList<String> remoteDirPaths,
                                String remoteFilePath,
                                String localFilePath) {

            this.httpClient = httpClient;
            this.context = new HttpClientContext();
            this.context.setCredentialsProvider(credentialsProvider);
            this.remoteDirpaths = remoteDirPaths;
            this.remoteFilePath = remoteFilePath;
            this.localFilePath = localFilePath;
        }

        @Override
        public void run() {
            boolean isNewRemoteFile = true;

            HttpUriRequest getRequest = RequestBuilder.create("GET").setUri(remoteFilePath).build();
            try {
                CloseableHttpResponse response = httpClient.execute(getRequest, context);
                if (response.getStatusLine().getStatusCode() == 200) {
                    isNewRemoteFile = false;
                }
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Create Remote Directories if file is a new local or remote file
            if (isNewRemoteFile) {
                for (String path : remoteDirpaths) {
                    HttpUriRequest mkcolRequest = RequestBuilder.create("MKCOL").setUri(path + "/").build();
                    try {
                        CloseableHttpResponse response = httpClient.execute(mkcolRequest, context);
                        try {
                            if (response.getStatusLine().getStatusCode() == 201) {
                                Notifications.Bus.notify(new Notification("Demandware", "[Created] ", mkcolRequest.getURI().toString(), NotificationType.INFORMATION));
                            }
                        } finally {
                            response.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Put remote file
            HttpUriRequest request = RequestBuilder.create("PUT")
                    .setUri(remoteFilePath)
                    .setEntity(new FileEntity(new File(localFilePath)))
                    .build();

            try {
                CloseableHttpResponse response = httpClient.execute(request, context);
                try {
                    if (isNewRemoteFile) {
                        Notifications.Bus.notify(new Notification("Demandware", "[Created] ", request.getURI().toString(), NotificationType.INFORMATION));
                    } else {
                        Notifications.Bus.notify(new Notification("Demandware", "[Updated] ", request.getURI().toString(), NotificationType.INFORMATION));
                    }
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
