package com.bjp.updatepackager.setting;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PackSettingPanel {
    private JPanel panel1;
    private JTextField rarField;
    private JTextField expField;
    private JButton selectButton;
    private JButton selectButton1;
    private JCheckBox enableCheckBox;
    private JTextField ftpIpField;
    private JTextField ftpUserField;
    private JTextField ftpPwdField;
    private JTextField ftpPathField;
    private boolean changed;

    public PackSettingPanel() {
        //配置初始化
        PropertiesComponent proComponent = PropertiesComponent.getInstance();
        rarField.setText(proComponent.getValue(PackSetting.CONFIG_WINRAR));
        expField.setText(proComponent.getValue(PackSetting.CONFIG_EXPORT));

        ftpIpField.setText(proComponent.getValue(PackSetting.FTP_IP));
        ftpUserField.setText(proComponent.getValue(PackSetting.FTP_USER));
        ftpPwdField.setText(proComponent.getValue(PackSetting.FTP_PWD));
        ftpPathField.setText(proComponent.getValue(PackSetting.FTP_PATH));
        if ("true".equals(proComponent.getValue(PackSetting.FTP_ENABLE))) {
            enableCheckBox.setSelected(true);
            ftpIpField.setEnabled(enableCheckBox.isSelected());
            ftpUserField.setEnabled(enableCheckBox.isSelected());
            ftpPwdField.setEnabled(enableCheckBox.isSelected());
            ftpPathField.setEnabled(enableCheckBox.isSelected());
        }

        selectButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor();
                FileChooser.chooseFiles(descriptor, null, null, virtualFiles -> {
                    rarField.setText(virtualFiles.get(0).getPath());
                    changed = true;
                });
            }
        });
        selectButton1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
                FileChooser.chooseFiles(descriptor, null, null, virtualFiles -> {
                    expField.setText(virtualFiles.get(0).getPath());
                    changed = true;
                });
            }
        });
        enableCheckBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ftpIpField.setEnabled(enableCheckBox.isSelected());
                ftpUserField.setEnabled(enableCheckBox.isSelected());
                ftpPwdField.setEnabled(enableCheckBox.isSelected());
                ftpPathField.setEnabled(enableCheckBox.isSelected());
                changed = true;
            }
        });

        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changed = true;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changed = true;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                changed = true;
            }
        };
        ftpIpField.getDocument().addDocumentListener(documentListener);
        ftpUserField.getDocument().addDocumentListener(documentListener);
        ftpPwdField.getDocument().addDocumentListener(documentListener);
        ftpPathField.getDocument().addDocumentListener(documentListener);
    }

    public JPanel getPanel1() {
        return panel1;
    }

    public boolean isChanged(){
        return changed;
    }

    public void saveConfig() {
        PropertiesComponent proComponent = PropertiesComponent.getInstance();
        proComponent.setValue(PackSetting.CONFIG_WINRAR, rarField.getText(), "");
        proComponent.setValue(PackSetting.CONFIG_EXPORT, expField.getText(), "");
        if (enableCheckBox.isSelected()) {
            proComponent.setValue(PackSetting.FTP_IP, ftpIpField.getText(), "");
            proComponent.setValue(PackSetting.FTP_USER, ftpUserField.getText(), "");
            proComponent.setValue(PackSetting.FTP_PWD, ftpPwdField.getText(), "");
            proComponent.setValue(PackSetting.FTP_PATH, ftpPathField.getText(), "");
            proComponent.setValue(PackSetting.FTP_ENABLE, "true", "");
        }else{
            proComponent.setValue(PackSetting.FTP_ENABLE, "false", "");
        }
        changed = false;
    }
}
