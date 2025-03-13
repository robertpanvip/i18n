package com.github.robertpanvip.i18n.toolWindow

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

import com.intellij.openapi.components.*

@Service
@State(name = "PluginConfig", storages = [Storage("i18nPluginConfig.xml")])
class PluginConfig : PersistentStateComponent<PluginConfig.State> {
    data class State(var i18nFolderPath: String = "")

    private var state = State()

    override fun getState(): State = state
    override fun loadState(state: State) {
        this.state = state
    }

    companion object {
        fun getInstance(): PluginConfig = service()
    }

    var i18nFolderPath: String
        get() = state.i18nFolderPath
        set(value) {
            state.i18nFolderPath = value
        }
}