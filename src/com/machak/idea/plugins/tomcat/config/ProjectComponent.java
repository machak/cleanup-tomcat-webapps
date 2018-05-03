


/*
 * Copyright 2013 m.milicevic (http://www.machak.com)
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package com.machak.idea.plugins.tomcat.config;


import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.components.StorageScheme;

/**
 * @version "$Id$"
 */
@State(
        name = "TomcatDeleteWebappsConfig",
        storages = {
                @Storage(file = StoragePathMacros.PROJECT_FILE),
                @Storage(file = StoragePathMacros.PROJECT_CONFIG_DIR + "/machak_tomcat_delete.xml", scheme = StorageScheme.DIRECTORY_BASED)})
public class ProjectComponent extends ApplicationComponent implements com.intellij.openapi.components.ProjectComponent {


    @Override
    public void projectOpened() {

    }

    @Override
    public void projectClosed() {

    }
}
