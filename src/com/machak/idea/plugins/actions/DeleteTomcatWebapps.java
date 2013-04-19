/*
 * Copyright 2013 m.milicevic (http://www.machak.com)
 * http://www.apache.org/licenses/LICENSE-2.0
 */


package com.machak.idea.plugins.actions;

import java.io.File;

import com.google.common.base.Strings;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.machak.idea.plugins.config.ApplicationComponent;
import com.machak.idea.plugins.config.ProjectComponent;


public class DeleteTomcatWebapps extends AnAction {

    public static final NotificationGroup log = NotificationGroup.logOnlyGroup("Tomcat delete webapps");
    public static final NotificationGroup ERROR_GROUP = new NotificationGroup("Tomcat delete webapps error messages", NotificationDisplayType.BALLOON, true);
    public static final NotificationGroup INFO_GROUP = new NotificationGroup("Tomcat delete webapps messages", NotificationDisplayType.NONE, false);
    private Project project;
    private String[] myFiles;
    private boolean[] myCheckedMarks;

    public void actionPerformed(AnActionEvent event) {


        project = PlatformDataKeys.PROJECT.getData(event.getDataContext());
        if (project != null) {
            // project settings have higher priority (override):
            ApplicationComponent component = project.getComponent(ProjectComponent.class);
            if (notValid(component)) {
                // try to fetch application (global) settings:
                component = ApplicationManager.getApplication().getComponent(ApplicationComponent.class);
                if (notValid(component)) {
                    if (component == null) {
                        error("Couldn't read settings (project nor application wide)");
                        return;
                    }
                }
            }

            String webappsDirectory = component.getTomcatDirectory();
            // check if we have correct settings:
            if (Strings.isNullOrEmpty(webappsDirectory)) {
                error("Tomcat webapps dir not defined");
                return;
            }

            if (!webappsDirectory.endsWith(File.separator)) {
                webappsDirectory = webappsDirectory + File.separator;
            }
            final File sharedDirectory = new File(webappsDirectory);
            if (!sharedDirectory.exists()) {
                error("Tomcat webapps directory does not exists: " + webappsDirectory);
                return;
            }


            final String[] filePaths = sharedDirectory.list();
            //if (!component.isShowDialog()) {
            for (String name : filePaths) {
                deleteFile(new File(String.format("%s%s", webappsDirectory, name)));
            }
            //}

        }

    }

    private void deleteFile(final File file) {
        final String filePath = file.getPath();
        if (!file.exists()) {
            return;
        }
        final boolean deleted = file.delete();
        if (deleted) {
            info(String.format("Deleted: %s", filePath));
        } else {
            warn(String.format("Failed to delete: %s", filePath));
        }
    }

    private boolean notValid(final ApplicationComponent component) {
        return component == null || Strings.isNullOrEmpty(component.getTomcatDirectory());
    }

    private void error(final String message) {
        final Notification notification = ERROR_GROUP.createNotification(message, NotificationType.ERROR);
        notification.notify(project);
    }

    private void info(final String message) {
        final Notification notification = INFO_GROUP.createNotification(message, NotificationType.INFORMATION);
        notification.notify(project);
    }

    private void warn(final String message) {
        final Notification notification = INFO_GROUP.createNotification(message, NotificationType.WARNING);
        notification.notify(project);
    }

}
