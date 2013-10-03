


/*
 * Copyright 2013 m.milicevic (http://www.machak.com)
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package com.machak.idea.plugins.tomcat.config;

import javax.swing.JComponent;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.components.StorageScheme;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;


@State(name = "TomcatDeleteWebapps", storages = {@Storage(id = "dir", file = StoragePathMacros.APP_CONFIG + "/machak_tomcat_delete.xml", scheme = StorageScheme.DIRECTORY_BASED)})
public class ApplicationComponent implements com.intellij.openapi.components.ApplicationComponent, Configurable, PersistentStateComponent<Element> {


    public static final String SHOW_DIALOG_ATTRIBUTE = "showDialog";
    public static final String TOMCAT_DIR_ATTRIBUTE = "tomcatDirectory";
    public static final String CONFIGURATION_CONFIG_ELEMENT = "tomcat-webapps-config";
    private String tomcatDirectory;
    private PluginConfiguration configPane;
    private boolean showDialog;

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "TomcatDeleteWebappsComponent";
    }

    @Override
    public Element getState() {
        final Element element = new Element(CONFIGURATION_CONFIG_ELEMENT);
        checkNullSave(element, SHOW_DIALOG_ATTRIBUTE, String.valueOf(showDialog));
        checkNullSave(element, TOMCAT_DIR_ATTRIBUTE, tomcatDirectory);
        return element;
    }

    @Override
    public void loadState(final Element element) {

        String show = element.getAttributeValue(SHOW_DIALOG_ATTRIBUTE);
        if (StringUtils.isNotBlank(show)) {
            showDialog = Boolean.valueOf(show);
        }
        tomcatDirectory = element.getAttributeValue(TOMCAT_DIR_ATTRIBUTE);
    }

    public boolean isShowDialog() {
        return showDialog;
    }

    public void setShowDialog(final boolean showDialog) {
        this.showDialog = showDialog;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Delete tomcat webapps";
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
        return configPane != null && configPane.isModified(this);
    }

    @Override
    public void apply() throws ConfigurationException {
        if (configPane != null) {
            configPane.storeDataTo(this);
        }
    }

    @Override
    public void reset() {
        if (configPane != null) {
            configPane.readDataFrom(this);
        }
    }

    @Override
    public void disposeUIResources() {
        configPane = null;
    }

    public String getTomcatDirectory() {
        return tomcatDirectory;
    }

    public void setTomcatDirectory(final String tomcatDirectory) {
        this.tomcatDirectory = tomcatDirectory;
    }

    private void checkNullSave(final Element element, final String attr, final String value) {
        if (value == null) {
            return;
        }
        element.setAttribute(attr, value);
    }
}