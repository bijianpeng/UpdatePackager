package com.bjp.updatepackager.setting;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class PackSetting implements Configurable {
    public static final String CONFIG_WINRAR = "UpdatePackager.WinRarPath";
    public static final String CONFIG_EXPORT = "UpdatePackager.ExportPath";
    public static final String FTP_IP = "UpdatePackager.Ip";
    public static final String FTP_USER = "UpdatePackager.User";
    public static final String FTP_PWD = "UpdatePackager.Pwd";
    public static final String FTP_PATH = "UpdatePackager.Path";
    public static final String FTP_ENABLE = "UpdatePackager.Ftp";
    private PackSettingPanel settingPanel;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "UpdateExport";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        settingPanel = new PackSettingPanel();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(BorderLayout.NORTH, settingPanel.getPanel1());
        return panel;
    }

    @Override
    public boolean isModified() {
        return settingPanel.isChanged();
    }

    @Override
    public void apply() throws ConfigurationException {
        settingPanel.saveConfig();
    }
}
