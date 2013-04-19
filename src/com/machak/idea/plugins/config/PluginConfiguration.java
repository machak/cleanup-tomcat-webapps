

/*
 * Copyright 2013 m.milicevic (http://www.machak.com)
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package com.machak.idea.plugins.config;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.TextAccessor;

public class PluginConfiguration extends BaseConfigurable {
    private JLabel label;
    private JPanel mainPanel;
    private JCheckBox showConfirmationDialog;
    private TextFieldWithBrowseButton tomcatDirectory;


    public PluginConfiguration() {

        final DocumentListener listener = new DocumentAdapter() {
            protected void textChanged(DocumentEvent documentEvent) {
                tomcatDirectory.getText();
            }
        };
        tomcatDirectory.getChildComponent().getDocument().addDocumentListener(listener);
        tomcatDirectory.setTextFieldPreferredWidth(50);
        tomcatDirectory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooseFolder(tomcatDirectory, false);
            }
        });


    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Delete tomcat webapps";
    }

    @Override
    public String getHelpTopic() {
        return null;
    }

    @Override
    public JComponent createComponent() {
        return mainPanel;
    }

    @Override
    public void apply() throws ConfigurationException {

    }

    @Override
    public void reset() {
    }

    @Override
    public void disposeUIResources() {
    }

    public boolean isModified(ApplicationComponent component) {
        final boolean changed = showConfirmationDialog.isSelected() != component.isShowDialog();

        if (changed) {
            return true;
        }
        final String tomcatText = tomcatDirectory.getText();
        final String tomcatDir = component.getTomcatDirectory();
        return isTextChanged(tomcatText, tomcatDir);

    }

    public void storeDataTo(ApplicationComponent component) {
        component.setShowDialog(showConfirmationDialog.isSelected());
        component.setTomcatDirectory(tomcatDirectory.getText());

    }

    public void readDataFrom(ApplicationComponent component) {

        showConfirmationDialog.setSelected(component.isShowDialog());
        tomcatDirectory.setText(component.getTomcatDirectory());
    }

    public Project getProject(final Component component) {
        Project project = PlatformDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext(component));
        if (project != null) {
            return project;
        }
        return ProjectManager.getInstance().getDefaultProject();
    }

    private boolean isTextChanged(final String text, final String dir) {
        if (text == null) {
            if (dir == null) {
                return false;
            }
        } else if (text.equals(dir)) {
            return false;
        }
        return true;
    }

    private void createUIComponents() {

    }

    //############################################
    //
    //############################################

    private void chooseFolder(final TextAccessor field, final boolean chooseFiles) {
        final FileChooserDescriptor descriptor = new FileChooserDescriptor(chooseFiles, !chooseFiles, false, false, false, false) {
            public String getName(VirtualFile virtualFile) {
                return virtualFile.getName();
            }

            @Nullable
            public String getComment(VirtualFile virtualFile) {
                return virtualFile.getPresentableUrl();
            }
        };
        descriptor.setTitle("Select Project Destination Folder");

        final String selectedPath = field.getText();
        final VirtualFile preselectedFolder = LocalFileSystem.getInstance().findFileByPath(selectedPath);

        final VirtualFile[] files = FileChooser.chooseFiles(descriptor, mainPanel, getProject(mainPanel), preselectedFolder);
        if (files.length > 0) {
            field.setText(files[0].getPath());
        }
    }
}