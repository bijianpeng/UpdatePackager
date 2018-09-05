package com.bjp.updatepackager.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.MessageView;

import javax.swing.*;
import java.awt.*;

public class MessageUtil {
    private static final String ID = "UpdatePacker";
    private static  MessagePanel messagePanel = null;

    private static MessagePanel getInstance(Project project) {
        if (messagePanel != null) {
            return messagePanel;
        }

        MessageView messageView = MessageView.SERVICE.getInstance(project);
        for (Content content : messageView.getContentManager().getContents()) {
            if (ID.equals(content.getTabName())){
                messagePanel = (MessagePanel)content.getComponent();
                return messagePanel;
            }
        }

        messagePanel = new MessagePanel();
        Content content = ContentFactory.SERVICE.getInstance().createContent(messagePanel, ID, true);
        messageView.getContentManager().addContent(content);
        messageView.getContentManager().setSelectedContent(content);
        return messagePanel;
    }

    public static void clear(Project project){
        getInstance(project).clear();
    }

    public static void info(Project project, String msg) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ToolWindowId.MESSAGES_WINDOW);
        if (toolWindow != null) {
            toolWindow.activate(null, false);
        }
        getInstance(project).info(msg);
    }

    static class MessagePanel extends JScrollPane {
        private JTextArea textArea;

        public MessagePanel() {
            textArea = new JTextArea();
            textArea.setEditable(false);
            this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            this.setViewportView(textArea);
        }

        public void clear() {
            this.textArea.setText("");
        }

        public void info(String msg) {
            this.textArea.append(msg + "\n");
        }
    }
}
