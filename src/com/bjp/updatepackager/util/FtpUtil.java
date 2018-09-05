package com.bjp.updatepackager.util;

import com.bjp.updatepackager.setting.PackSetting;
import com.intellij.ide.util.PropertiesComponent;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FtpUtil {
    public static boolean putFile(String filePath){
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }

        PropertiesComponent proComponent = PropertiesComponent.getInstance();
        String ip = proComponent.getValue(PackSetting.FTP_IP);
        String user = proComponent.getValue(PackSetting.FTP_USER);
        String pwd = proComponent.getValue(PackSetting.FTP_PWD);
        String path = proComponent.getValue(PackSetting.FTP_PATH);

        try {
            FTPClient ftpClient = new FTPClient();
            ftpClient.connect(ip);
            if (ftpClient.login(user, pwd)) {
                if (StringUtils.isNotBlank(path)) {
                    ftpClient.changeWorkingDirectory(path);
                }
                FileInputStream inputStream = new FileInputStream(filePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.storeFile(file.getName(), inputStream);
                ftpClient.disconnect();
                inputStream.close();
            }
            ftpClient.disconnect();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
