package com.demandware.studio.webdav;

import com.demandware.studio.settings.DWSettingsProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
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
    private final HttpClientContext context;

    public DWServerConnection(DWSettingsProvider settingsProvider) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        this.settingsProvider = settingsProvider;

        // SSLContextFactory to allow all hosts. Without this an SSLException is thrown with self signed certs
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (arg0, arg1) -> true).build();
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("https", socketFactory).build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        // This may need to be adjusted. When a significant number of files changes, say changing a branch the server rejects the requests.
        connectionManager.setMaxTotal(15);
        connectionManager.setDefaultMaxPerRoute(3);

        client = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        context = new HttpClientContext();
        context.setCredentialsProvider(getCredientials());
    }

    /*
     * Returns the base path to the demandware server instance to make connections to webdav.
     */
    public String getBaseServerPath() {
        return String.format("https://%s/on/demandware.servlet/webdav/Sites/Cartridges/%s", settingsProvider.getHostname(), settingsProvider.getVersion());
    }


    private String getCartridgeName(String rootPath) {
        return Paths.get(rootPath).getFileName().toString();
    }

    /*
     * Returns the remote file path for the current file path.
     * @param rootPath the source root path for the current file
     * @param filePath the full file path of the current file
     */
    public String getRemoteFilePath(String rootPath, String filePath) {
        String relPath = filePath.substring(rootPath.length(), filePath.length());
        String cartridgeName = getCartridgeName(rootPath);
        return getBaseServerPath() + "/" + cartridgeName + relPath;
    }

    /*
     * Returns all the remote directory paths based on the current file path. This is to be used to create
     * any directories that have not yet been created before attempting to upload the file.
     */
    public ArrayList<String> getRemoteDirPaths(String rootPath, String filePath) {
        ArrayList<String> serverPaths = new ArrayList<>();
        Path relPath = Paths.get(rootPath).relativize(Paths.get(filePath)).getParent();
        String cartridgeName = getCartridgeName(rootPath);

        String dirPath = "";
        for (Path subPath : relPath) {
            dirPath = dirPath + "/" + subPath.getFileName();
            serverPaths.add(getBaseServerPath() + "/" + cartridgeName + dirPath);
        }

        return serverPaths;
    }

    /*
     * Returns a closeable http client
     */
    public CloseableHttpClient getClient() {
        return client;
    }

    /*
     * Returns an http client context
     */
    public HttpClientContext getContext() {
        return context;
    }

    /*
     * Returns a credentials provider for the http client using the plugins settings provider
     * to supply the hostname, username and password.
     */
    private CredentialsProvider getCredientials() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(settingsProvider.getHostname(), AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(settingsProvider.getUsername(), settingsProvider.getPassword()));
        return credentialsProvider;
    }

}

