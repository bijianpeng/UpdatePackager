package com.bjp.updatepackager.action;

import com.bjp.updatepackager.setting.PackSetting;
import com.bjp.updatepackager.util.FtpUtil;
import com.bjp.updatepackager.util.MessageUtil;
import com.bjp.updatepackager.util.ZipUtil;
import com.intellij.facet.FacetManager;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.javaee.web.WebRoot;
import com.intellij.javaee.web.facet.WebFacet;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.compiler.ex.CompilerPathsEx;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FilePackAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        int n = JOptionPane.showConfirmDialog(null, "The project has been builded ?","Confirm",JOptionPane.YES_NO_OPTION);
        if (n == 1) {
            return;
        }

        DataContext dataContext = e.getDataContext();
        Project project = (Project) CommonDataKeys.PROJECT.getData(dataContext);

        //配置信息
        PropertiesComponent proComponent = PropertiesComponent.getInstance();
        String rarPath = proComponent.getValue(PackSetting.CONFIG_WINRAR);
        String packPath = proComponent.getValue(PackSetting.CONFIG_EXPORT);
        String enableFtp = proComponent.getValue(PackSetting.FTP_ENABLE);
        packPath = packPath + "/" + project.getName();

        MessageUtil.clear(project);
        MessageUtil.info(project, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//        MessageUtil.info(project, "工程目录:" + project.getBasePath());
//        MessageUtil.info(project, "输出目录:" + packPath);

        VirtualFile[] virtualFiles = (VirtualFile[])CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
        if (virtualFiles == null) {
            return;
        }

        if(virtualFiles.length == 1 && virtualFiles[0].getPath().equals(project.getBasePath())){
            FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor();
            VirtualFile choosedFile = FileChooser.chooseFile(descriptor, project, null);
            try {
                BufferedReader br = new BufferedReader(new FileReader(choosedFile.getPath()));
                List<VirtualFile> vfList = new ArrayList<>();
                LocalFileSystem localFileSystem = LocalFileSystem.getInstance();
                String line;
                while((line = br.readLine()) != null){
                    if(!"".equals(line)){
                        if(line.startsWith("/")){
                            line = project.getBaseDir().getParent().getPath() + line;
                        }
                        VirtualFile vf = localFileSystem.findFileByPath(line);
                        if (vf.exists()) {
                            vfList.add(vf);
                        }
                    }
                }
                virtualFiles = vfList.toArray(new VirtualFile[]{});
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        for (int i=0; i<virtualFiles.length; i++) {
            VirtualFile virtualFile = virtualFiles[i];
            Module module = ModuleUtil.findModuleForFile(virtualFile, project);
            String moduleOutputPath = CompilerPathsEx.getModuleOutputPath(module, false);
            String fromPath = "";
            String toPath = "";
            String relativePath = this.getRelativePath(module, virtualFile);
            if (!"".equals(relativePath)) {
                fromPath = moduleOutputPath + "/" + relativePath;
                toPath = packPath + "/WEB-INF/classes/" + relativePath;
            }else{
                WebFacet webFacet = FacetManager.getInstance(module).getFacetByType(WebFacet.ID);
                List<WebRoot> webRoots = webFacet.getWebRoots(false);
                for (WebRoot webRoot : webRoots) {
                    if (VfsUtil.isAncestor(webRoot.getFile(), virtualFile, false)) {
                        fromPath = virtualFile.getPath();
                        toPath = packPath + "/" +  VfsUtil.getRelativePath(virtualFile, webRoot.getFile());
                    }
                }
            }

            try {
                boolean r = this.copyFile(fromPath, toPath);
                MessageUtil.info(project,i + ":" + r + ":" + virtualFile.getPath());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        try {
            String zipFilePath = ZipUtil.zip(packPath);
            //文件名复制到剪切板
            CopyPasteManager.getInstance().setContents(new StringSelection(new File(zipFilePath).getName()));

            if ("true".equals(enableFtp)) {
                Thread.sleep(1000);
                boolean result = FtpUtil.putFile(zipFilePath);
                MessageUtil.info(project,zipFilePath + " put to ftp:" + result);
                if (result) {
                    new File(zipFilePath).delete();
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private String getRelativePath(Module module, VirtualFile virtualFile) {
        if (module != null) {
            VirtualFile[] sourceRoots = ModuleRootManager.getInstance(module).getSourceRoots(false);
            for (VirtualFile sourceRoot : sourceRoots) {
                if (VfsUtil.isAncestor(sourceRoot, virtualFile, false)) {
                    String relativePath = VfsUtil.getRelativePath(virtualFile, sourceRoot);
                    if (relativePath.endsWith("java")) {
                        relativePath = relativePath.substring(0, relativePath.lastIndexOf(".")) + ".class";
                    }
                    return relativePath;
                }
            }
        }
        return "";
    }

    private boolean copyFile(String from, String to) throws  Exception{
        File fromFile = new File(from);
        File toFile = new File(to);
        if (fromFile.exists() && !fromFile.isDirectory()) {
            File folder = toFile.getParentFile();
            if (!folder.exists()) {
                folder.mkdirs();
            }

            InputStream in = new FileInputStream(fromFile);
            OutputStream out = new FileOutputStream(toFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

            String fileName = fromFile.getName();
            if (fileName.endsWith(".class")) {
                final String filePrefix = fileName.substring(0, fileName.lastIndexOf(".")) + "$";
                File sFolder = fromFile.getParentFile();
                File[] files = sFolder.listFiles(new FileFilter() {
                    public boolean accept(File pathname) {
                        return pathname.getName().startsWith(filePrefix);
                    }
                });
                if ((files != null) && (files.length > 0)) {
                    for (File ex : files) {
                        in = new FileInputStream(ex);
                        File exFile = new File(folder.getAbsolutePath() + File.separator + ex.getName());
                        out = new FileOutputStream(exFile);
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                        in.close();
                        out.close();
                    }
                }
            }
            return true;
        }
        return false;
    }
}
