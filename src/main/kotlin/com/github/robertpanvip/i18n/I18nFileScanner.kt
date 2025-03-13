package com.github.robertpanvip.i18n
import com.github.robertpanvip.i18n.toolWindow.PluginConfig
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

object I18nFileScanner {
    fun findI18nFiles(project: Project): List<VirtualFile> {
        val i18nFiles = mutableListOf<VirtualFile>()
        val folderPath = PluginConfig.getInstance().i18nFolderPath
        if (folderPath.isEmpty()) return i18nFiles

        val baseDir = project.baseDir
        val i18nDir = baseDir.findFileByRelativePath(folderPath) ?: return i18nFiles
        i18nDir.refresh(false, true)
        i18nDir.children.forEach { i18nFiles.add(it) }
        return i18nFiles
    }
}