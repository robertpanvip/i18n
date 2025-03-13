package com.github.robertpanvip.i18n.toolWindow

import com.github.robertpanvip.i18n.I18nFileScanner
import com.github.robertpanvip.i18n.JsRuntimeParser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.openapi.vfs.VirtualFileEvent
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class ConfigToolWindowPanel(private val project: Project) {
    private val mainPanel = JPanel(BorderLayout())
    private val folderPathField = TextFieldWithBrowseButton().apply {
        val descriptor = FileChooserDescriptor(
            false, // 不允许选择文件
            true,  // 允许选择文件夹
            false, // 不允许选择 jar 文件
            false, // 不允许选择 jar 内容
            false, // 不允许选择多个
            false  // 不允许创建新文件
        ).apply {
            withTitle("Select i18n Folder")
            withDescription("Choose the folder containing i18n files")
            withShowHiddenFiles(false)
            withTreeRootVisible(true)
            withRoots(project.baseDir)
        }
        addBrowseFolderListener(
            "Select i18n Folder",
            "Choose the folder containing i18n files",
            project,
            descriptor
        )
        // 自定义监听器，在选择文件夹后立即处理
        addBrowseFolderListener(object : TextBrowseFolderListener(descriptor, project) {
            override fun onFileChosen(chosenFile: VirtualFile) {
                val relativePath = chosenFile.path.removePrefix(project?.basePath.orEmpty() + "/")
                textField.text = relativePath
                // 验证路径
                val folder = project?.baseDir?.findFileByRelativePath(relativePath)
                if (folder == null || !folder.isDirectory) {
                    Messages.showErrorDialog("Invalid folder path", "Error")
                    textField.text = PluginConfig.getInstance().i18nFolderPath // 恢复原路径
                    return
                }
                // 保存路径并处理
                PluginConfig.getInstance().i18nFolderPath = relativePath
                Messages.showInfoMessage("Folder path saved: $relativePath", "Success")
                processI18nFiles()
            }
        })
        textField.isEditable = false // 文本框只读
        //textField.columns=30;
    }

    init {
        initUI()
        initListeners()
        // 初始化时加载已有配置
        val savedPath = PluginConfig.getInstance().i18nFolderPath
        if (savedPath.isNotEmpty()) {
            folderPathField.text = savedPath
            processI18nFiles()
        }
        setupFileListener()
    }

    private fun initUI() {
        folderPathField.toolTipText = "Click to select i18n folder"
        val inputPanel = JPanel(GridBagLayout())
        val gbc = GridBagConstraints().apply {
            insets = JBUI.insets(5) // 组件之间的边距
            anchor = GridBagConstraints.WEST // 左对齐
        }

        // 添加标签
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.weightx = 0.0 // 标签不占用额外空间
        gbc.fill = GridBagConstraints.NONE // 标签不填充
        inputPanel.add(JLabel("Folder Path:"),gbc)
        // 添加 folderPathField
        gbc.gridx = 1
        gbc.gridy = 0
        gbc.weightx = 1.0 // folderPathField 占用剩余空间
        gbc.fill = GridBagConstraints.HORIZONTAL // 水平填充
        inputPanel.add(folderPathField,gbc)
        mainPanel.add(inputPanel, BorderLayout.NORTH)
    }

    private fun initListeners() {
    }

    fun getComponent(): JComponent = mainPanel

    private fun processI18nFiles() {
        val i18nFiles = I18nFileScanner.findI18nFiles(project)
        val i18nData = mutableMapOf<String, Map<String, Any?>>()
        i18nFiles.forEach { file ->
            //i18nData.set(file.name, JsRuntimeParser.parse(file))
        }

        //I18nExporter.exportToTs(project, i18nData)
        Messages.showInfoMessage("i18n data exported to i18nData.ts", "Success")
    }

    private fun setupFileListener() {
        val folderPath = PluginConfig.getInstance().i18nFolderPath
        if (folderPath.isNotEmpty()) {
            val i18nDir = project.baseDir.findFileByRelativePath(folderPath) ?: return
            VirtualFileManager.getInstance().addVirtualFileListener(object : VirtualFileListener {
                override fun contentsChanged(event: VirtualFileEvent) {
                    if (event.file.parent == i18nDir) {
                        //processI18nFiles()
                    }
                }
            })
        }
    }
}