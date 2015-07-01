package com.demandware.studio.webdav;

import com.demandware.studio.toolWindow.DWConsoleService;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DWUpdateFileTask extends Task.Backgroundable {
    private final Logger LOG = Logger.getInstance(DWUpdateFileTask.class);

    private final CloseableHttpClient httpClient;
    private final HttpClientContext context;
    private final ArrayList<String> remoteDirPaths;
    private final String remoteFilePath;
    private final String localFilePath;
    private final Project project;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");

    public DWUpdateFileTask(Project project,
                            Module module,
                            final String title,
                            final boolean canBeCancelled,
                            final PerformInBackgroundOption backgroundOption,
                            String sourceRootPath,
                            String localFilePath) {
        super(project, title, canBeCancelled, backgroundOption);
        DWServerConnection serverConnection = ModuleServiceManager.getService(module, DWServerConnection.class);
        this.project = project;
        this.localFilePath = localFilePath;
        this.context = new HttpClientContext();
        this.context.setCredentialsProvider(serverConnection.getCredientials());
        this.httpClient = serverConnection.getClient();
        this.remoteDirPaths = serverConnection.getRemoteDirPaths(sourceRootPath, localFilePath);
        this.remoteFilePath = serverConnection.getRemoteFilePath(sourceRootPath, localFilePath);
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        boolean isNewRemoteFile = true;
        ConsoleView consoleView = ServiceManager.getService(project, DWConsoleService.class).getConsoleView();
        indicator.setFraction(.33);

        HttpUriRequest getRequest = RequestBuilder.create("GET").setUri(remoteFilePath).build();
        try (CloseableHttpResponse response = httpClient.execute(getRequest, context)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                isNewRemoteFile = false;
            }

            if (response.getStatusLine().getStatusCode() == 401) {
                Notifications.Bus.notify(new Notification("Demandware", "Unauthorized Request",
                    "Please check your server configuration in the Demandware facet settings.", NotificationType.INFORMATION));
                return;
            }
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
            for (String path : remoteDirPaths) {
                HttpUriRequest mkcolRequest = RequestBuilder.create("MKCOL").setUri(path + "/").build();

                try (CloseableHttpResponse response = httpClient.execute(mkcolRequest, context)) {
                    if (response.getStatusLine().getStatusCode() == 201) {
                        Date now = new Date();
                        consoleView.print("[" + timeFormat.format(now) + "] " + "Created " + mkcolRequest.getURI().toString() + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
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

        try (CloseableHttpResponse response = httpClient.execute(request, context)) {
            if (isNewRemoteFile) {
                Date now = new Date();
                consoleView.print("[" + timeFormat.format(now) + "] " + "Created " + request.getURI().toString() + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
            } else {
                Date now = new Date();
                consoleView.print("[" + timeFormat.format(now) + "] " + "Modified " + request.getURI().toString() + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
            }
        } catch (IOException e) {
            LOG.error(e);
        }

        indicator.setFraction(1);
    }
}
