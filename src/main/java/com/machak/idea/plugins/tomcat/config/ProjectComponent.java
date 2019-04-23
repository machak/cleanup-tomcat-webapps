


/*
 * Copyright 2013 m.milicevic (http://www.machak.com)
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package com.machak.idea.plugins.tomcat.config;


import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.components.StorageScheme;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ProjectComponent extends BaseConfig implements Configurable {


    private PluginConfiguration configPane;

    public ProjectComponent(@NotNull Project project) {
        this.state = StorageState.getInstance(project);
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (configPane == null) {
            configPane = new PluginConfiguration();
        }
        return configPane.createComponent();
    }

    @Override
    public boolean isModified() {
        return configPane != null && configPane.isModified(state);
    }

    @Override
    public void apply() {
        if (configPane != null) {
            configPane.setData(state);
        }
    }

    @Override
    public void reset() {
        if (configPane != null) {
            configPane.getData(state);
        }
    }

    @Override
    public void disposeUIResources() {
        configPane = null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Delete tomcat webapps (Project)";
    }


}