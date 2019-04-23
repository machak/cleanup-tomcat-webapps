/*
 * Copyright 2018 Hippo B.V. (http://www.onehippo.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.machak.idea.plugins.tomcat.config;


import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;


@State(
        name = "TomcatDeleteWebapps",
        storages = {
                @Storage(value = "TomcatDeleteWebapps.xml"),
                @Storage(value = "TomcatDeleteWebapps.xml", scheme = StorageScheme.DIRECTORY_BASED)})
public class StorageState implements PersistentStateComponent<StorageState> {
    
    private String tomcatDirectory;
    private boolean showDialog;
    private boolean deleteLogFiles;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final StorageState that = (StorageState) o;
        return showDialog == that.showDialog &&
                deleteLogFiles == that.deleteLogFiles &&
                Objects.equals(tomcatDirectory, that.tomcatDirectory);
    }

    @Override
    public int hashCode() {

        return Objects.hash(tomcatDirectory, showDialog, deleteLogFiles);
    }

    public String getTomcatDirectory() {
        return tomcatDirectory;
    }

    public void setTomcatDirectory(final String tomcatDirectory) {
        this.tomcatDirectory = tomcatDirectory;
    }

    public boolean isShowDialog() {
        return showDialog;
    }

    public void setShowDialog(final boolean showDialog) {
        this.showDialog = showDialog;
    }

    public boolean isDeleteLogFiles() {
        return deleteLogFiles;
    }

    public void setDeleteLogFiles(final boolean deleteLogFiles) {
        this.deleteLogFiles = deleteLogFiles;
    }

    @Nullable
    @Override
    public StorageState getState() {
        return this;
    }

    @Override
    public void loadState(StorageState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    @Nullable
    public static StorageState getInstance() {
        return ServiceManager.getService(StorageState.class);
    }
    @Nullable
    public static StorageState getInstance(Project project) {
        return ServiceManager.getService(project, StorageState.class);
    }
}
