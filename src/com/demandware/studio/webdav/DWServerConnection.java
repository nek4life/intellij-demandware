package com.demandware.studio.webdav;

import com.demandware.studio.settings.DWSettingsProvider;
import com.demandware.studio.toolWindow.DWConsoleService;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;

public class DWServerConnection {
    private final DWSettingsProvider settingsProvider;
    private final CloseableHttpClient client;

    public DWServerConnection(DWSettingsProvider settingsProvider) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        this.settingsProvider = settingsProvider;

        // SSLContextFactory to allow all hosts. Without this an SSLException is thrown with self signed certs
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (arg0, arg1) -> true).build();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("https", socketFactory).build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(20);

        client = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
    }

    public String getBaseServerPath() {
        return String.format("https://%s/on/demandware.servlet/webdav/Sites/Cartridges/%s", settingsProvider.getHostname(), settingsProvider.getVersion());
    }

    public String getCartridgeName(String rootPath) {
        return Paths.get(rootPath).getFileName().toString();
    }

    public String getRemoteFilePath(String rootPath, String filePath) {
        String relPath = filePath.substring(rootPath.length(), filePath.length());
        String cartridgeName = getCartridgeName(rootPath);
        return getBaseServerPath() + "/" + cartridgeName + relPath;
    }

    public ArrayList<String> getRemoteDirPaths(String rootPath, String filePath) {
        ArrayList<String> serverPaths = new ArrayList<String>();
        Path relPath = Paths.get(rootPath).relativize(Paths.get(filePath)).getParent();
        String cartridgeName = getCartridgeName(rootPath);

        String dirPath = "";
        for (Path subPath : relPath) {
            dirPath = dirPath + "/" + subPath.getFileName();
            serverPaths.add(getBaseServerPath() + "/" + cartridgeName + dirPath);
        }

        return serverPaths;
    }

    public CloseableHttpClient getClient() {
        return client;
    }

    public CredentialsProvider getCredientials() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(settingsProvider.getHostname(), AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(settingsProvider.getUsername(), settingsProvider.getPassword()));
        return credentialsProvider;
    }

    public static class UpdateFileThread extends Task.Backgroundable {
        private final Logger LOG = Logger.getInstance(UpdateFileThread.class);

        private final CloseableHttpClient httpClient;
        private final HttpClientContext context;
        private final ArrayList<String> remoteDirpaths;
        private final String remoteFilePath;
        private final String localFilePath;
        private final Project project;

        public UpdateFileThread(Project project,
                                final String title,
                                final boolean canBeCancelled,
                                final PerformInBackgroundOption backgroundOption,
                                CloseableHttpClient httpClient,
                                CredentialsProvider credentialsProvider,
                                ArrayList<String> remoteDirPaths,
                                String remoteFilePath,
                                String localFilePath) {
            super(project, title, canBeCancelled, backgroundOption);
            this.project = project;
            this.httpClient = httpClient;
            this.context = new HttpClientContext();
            this.context.setCredentialsProvider(credentialsProvider);
            this.remoteDirpaths = remoteDirPaths;
            this.remoteFilePath = remoteFilePath;
            this.localFilePath = localFilePath;
        }

        @Override
        public void run(@NotNull ProgressIndicator indicator) {
            boolean isNewRemoteFile = true;
            ConsoleView consoleView = ServiceManager.getService(project, DWConsoleService.class).getConsoleView();

            indicator.setFraction(.33);

            HttpUriRequest getRequest = RequestBuilder.create("GET").setUri(remoteFilePath).build();
            try {
                CloseableHttpResponse response = httpClient.execute(getRequest, context);
                if (response.getStatusLine().getStatusCode() == 200) {
                    isNewRemoteFile = false;
                }
                if (response.getStatusLine().getStatusCode() == 401) {
                    Notifications.Bus.notify(new Notification("Demandware", "Unauthorized Request",
                            "Please check your server configuration in the Demandware facet settings.", NotificationType.INFORMATION));
                    return;
                }
                response.close();
            } catch (UnknownHostException e) {
                Notifications.Bus.notify(new Notification("Demandware", "Unknown Host",
                        "Please check your server configuration in the Demandware facet settings.", NotificationType.INFORMATION));
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }

            indicator.setFraction(.5);

            // Create Remote Directories if file is a new local or remote file
            if (isNewRemoteFile) {
                for (String path : remoteDirpaths) {
                    HttpUriRequest mkcolRequest = RequestBuilder.create("MKCOL").setUri(path + "/").build();
                    try {
                        try (CloseableHttpResponse response = httpClient.execute(mkcolRequest, context)) {
                            if (response.getStatusLine().getStatusCode() == 201) {
                                consoleView.print("[Created] " + mkcolRequest.getURI().toString() + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            indicator.setFraction(.80);

            // Put remote file
            HttpUriRequest request = RequestBuilder.create("PUT")
                    .setUri(remoteFilePath)
                    .setEntity(new FileEntity(new File(localFilePath)))
                    .build();

            try {
                try (CloseableHttpResponse response = httpClient.execute(request, context)) {
                    if (isNewRemoteFile) {
                        consoleView.print("[Created] " + request.getURI().toString() + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
                    } else {
                        consoleView.print("[Updated] " + request.getURI().toString() + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
                    }
                }
            } catch (IOException e) {
                LOG.error(e);
            }

            indicator.setFraction(1);
        }
    }
}

