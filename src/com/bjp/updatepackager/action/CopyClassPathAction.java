package com.bjp.updatepackager.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.compiler.ex.CompilerPathsEx;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.awt.datatransfer.StringSelection;

public class CopyClassPathAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        Project project = (Project) CommonDataKeys.PROJECT.getData(dataContext);
        VirtualFile virtualFile = (VirtualFile) CommonDataKeys.VIRTUAL_FILE.getData(dataContext);
        if (virtualFile == null) {
            return;
        }

        Module module = ModuleUtil.findModuleForFile(virtualFile, project);
        if (module != null) {
            String moduleOutputPath = CompilerPathsEx.getModuleOutputPath(module, false);
            VirtualFile[] sourceRoots = ModuleRootManager.getInstance(module).getSourceRoots(false);
            for (VirtualFile sourceRoot : sourceRoots) {
                //排除掉jar包路径
                if(!sourceRoot.getCanonicalPath().endsWith("!/")){
                    if (VfsUtil.isAncestor(sourceRoot, virtualFile, false)) {
                        String classPath = moduleOutputPath + "/" + VfsUtil.getRelativePath(virtualFile, sourceRoot);
                        classPath = classPath.substring(0, classPath.lastIndexOf("/"));
                        CopyPasteManager.getInstance().setContents(new StringSelection(classPath));
                        return;
                    }
                }
            }

            String filePath = virtualFile.getCanonicalPath();
            filePath = filePath.substring(0, filePath.lastIndexOf("/"));
            CopyPasteManager.getInstance().setContents(new StringSelection(filePath));
        }
    }
}
