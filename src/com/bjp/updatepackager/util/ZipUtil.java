package com.bjp.updatepackager.util;

import com.bjp.updatepackager.setting.PackSetting;
import com.intellij.ide.util.PropertiesComponent;

import java.io.File;

public class ZipUtil {

    public static String zip(String path) throws Exception{
        PropertiesComponent proComponent = PropertiesComponent.getInstance();
        String rarPath = proComponent.getValue(PackSetting.CONFIG_WINRAR);

        File folder = new File(path);
        String folderName = folder.getName();
        String zipName = folderName + ".zip";
        StringBuffer cmd = new StringBuffer("cmd /c ");
        cmd.append(path.substring(0, 2));
        cmd.append(" && cd " + folder.getParent());
        cmd.append(" && del \"" + zipName + "\"");
        cmd.append(" && \"" + rarPath + "\" a \"" + zipName + "\" \"" + folderName + "\"");
        cmd.append(" && rd /s/q " + folderName);
        Runtime.getRuntime().exec(cmd.toString());
        return folder.getParent() + File.separator + zipName;
    }
}
