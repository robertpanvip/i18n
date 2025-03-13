package com.github.robertpanvip.i18n.toolWindow

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import com.github.robertpanvip.i18n.MyBundle
import com.github.robertpanvip.i18n.services.MyProjectService
import javax.swing.JButton


class ConfigToolWindowFactory : ToolWindowFactory {

    init {
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = ConfigToolWindowPanel(project)
        val content = ContentFactory.getInstance().createContent(myToolWindow.getComponent(), null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true
}
