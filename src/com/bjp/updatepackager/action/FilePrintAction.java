package com.bjp.updatepackager.action;

import com.bjp.updatepackager.util.MessageUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

public class FilePrintAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent event) {
        DataContext dataContext = event.getDataContext();
        Project project = (Project) CommonDataKeys.PROJECT.getData(dataContext);
        VirtualFile workspace = project.getBaseDir().getParent();
        VirtualFile[] virtualFiles = (VirtualFile[])CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
        MessageUtil.clear(project);
        for (VirtualFile virtualFile : virtualFiles) {
            MessageUtil.info(project, "/" + VfsUtil.getRelativePath(virtualFile, workspace));
        }
    }
}
