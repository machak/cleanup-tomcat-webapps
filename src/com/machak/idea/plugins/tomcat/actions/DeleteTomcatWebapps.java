/*
 * Copyright 2013 m.milicevic (http://www.machak.com)
 * http://www.apache.org/licenses/LICENSE-2.0
 */


package com.machak.idea.plugins.tomcat.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.google.common.base.Strings;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.io.FileUtil;
import com.machak.idea.plugins.tomcat.config.ApplicationComponent;
import com.machak.idea.plugins.tomcat.config.ProjectComponent;


public class DeleteTomcatWebapps extends AnAction {

    public static final NotificationGroup log = NotificationGroup.logOnlyGroup("Tomcat delete webapps");
    public static final NotificationGroup ERROR_GROUP = new NotificationGroup("Tomcat delete webapps error messages", NotificationDisplayType.BALLOON, true);
    public static final NotificationGroup INFO_GROUP = new NotificationGroup("Tomcat delete webapps messages", NotificationDisplayType.NONE, false);
    private Project project;


    @Override
    public void actionPerformed(AnActionEvent event) {


        project = CommonDataKeys.PROJECT.getData(event.getDataContext());
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
            final File webappDir = new File(webappsDirectory);
            if (!webappDir.exists()) {
                error("Tomcat webapps directory does not exists: " + webappsDirectory);
                return;
            }
            // check if tomcat directory:
            final File tomcatRoot = webappDir.getParentFile();

            final File[] files = tomcatRoot.listFiles();
            if (files == null) {
                return;
            }
            boolean invalidDir = true;
            mainLoop:
            for (File file : files) {
                if (file.getName().equals("bin")) {
                    final File[] configFiles = file.listFiles();
                    if (configFiles != null) {
                        for (File configFile : configFiles) {
                            final String name = configFile.getName();
                            if (name.equals("catalina.sh") || name.equals("catalina.bat")) {
                                invalidDir = false;
                                break mainLoop;
                            }
                        }
                    }
                }
            }
            if (invalidDir) {
                error("Tomcat webapps directory is not a tomcat directory: " + webappsDirectory);
                return;
            }

            if (component.isShowDialog()) {
                showDeletePopup(webappsDirectory, webappDir);

            } else {
                deleteFiles(webappsDirectory, webappDir);
            }

        }

    }

    private void deleteFiles(final String webappsDirectory, final File webappDir) {
        final String[] filePaths = webappDir.list();
        for (String name : filePaths) {
            deleteFile(new File(String.format("%s%s", webappsDirectory, name)));
        }
    }

    private void showDeletePopup(final String webappsDirectory, final File webappDir) {

        final String absolutePath = webappDir.getAbsolutePath();
        final DialogBuilder dialogBuilder = new DialogBuilder(project);
        dialogBuilder.setTitle("Create project file:");
        final JPanel simplePanel = new JPanel();
        simplePanel.add(new JLabel("All directories within: " + absolutePath + " will be deleted"));
        dialogBuilder.setCenterPanel(simplePanel);

        final Action acceptAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                deleteFiles(webappsDirectory, webappDir);
                dialogBuilder.getDialogWrapper().close(DialogWrapper.OK_EXIT_CODE);


            }
        };
        acceptAction.putValue(Action.NAME, "OK");
        dialogBuilder.addAction(acceptAction);

        dialogBuilder.addCancelAction();
        dialogBuilder.showModal(true);
    }


    private void deleteFile(final File file) {
        final String filePath = file.getPath();
        if (!file.exists()) {
            return;
        }
        final boolean deleted = FileUtil.delete(file);
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
