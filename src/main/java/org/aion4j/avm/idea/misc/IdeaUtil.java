package org.aion4j.avm.idea.misc;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;

public class IdeaUtil {
    public final static String PLUGIN_ID = "org.aion4j.avm";
    public final static String AVM_REMOTE_CONFIG_ACTION = "Avm.remote.configuration";

    public static void showNotification(Project project, String title, String content, NotificationType notificationType, String actionId) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                Notification notification = new Notification(IdeaUtil.PLUGIN_ID,
                        title, content,
                        notificationType);

                if(actionId != null) {
                    ActionManager am = ActionManager.getInstance();
                    AnAction action = am.getAction(actionId);
                    notification.addAction(action);
                }

                Notifications.Bus.notify(notification, project);
            }
        });
    }

    public static void showNotificationWithAction(Project project, String title, String content, NotificationType notificationType, NotificationAction action) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                Notification notification = new Notification(IdeaUtil.PLUGIN_ID,
                        title, content,
                        notificationType);

                notification.addAction(action);

                Notifications.Bus.notify(notification, project);
            }
        });
    }
}
