package com.github.robertpanvip.i18n

import com.intellij.openapi.vfs.VirtualFile
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Source
import org.graalvm.polyglot.Value

object JsRuntimeParser {
    private val context: Context by lazy {
        Context.newBuilder("js")
            .allowAllAccess(true)
            .build()
    }

    fun parse(file: VirtualFile): Map<String, Any?> {
        return when (file.extension) {
            "js" -> {
                this.parseJs(file)
            }

            "ts" -> {
                this.parseTs(file)
            }

            "json" -> {
                this.parseJson(file)
            }

            else -> emptyMap()
        }
    }

    fun parseJson(file: VirtualFile): Map<String, Any?> {
        val content = String(file.inputStream.readAllBytes(), Charsets.UTF_8)
        val script = """
            JSON.parse('$content')
        """.trimIndent()
        val result = context.eval(Source.create("js", script))
        return convertToMap(result)
    }

    fun parseJs(file: VirtualFile): Map<String, Any?> {
        val content = String(file.inputStream.readAllBytes(), Charsets.UTF_8)
        val script = """
            (function() {
                return $content;
            })();
        """.trimIndent()
        val result = context.eval(Source.create("js", script))
        return convertToMap(result)
    }

    fun parseTs(file: VirtualFile): Map<String, Any?> {
        val jsContent = TsCompiler.compileTsToJs(file)
        val script = """
            (function() {
                return $jsContent;
            })();
        """.trimIndent()
        val result = context.eval(Source.create("js", script))
        return convertToMap(result)
    }

    fun parseYaml(file: VirtualFile): Map<String, Any?> {
        val content = String(file.inputStream.readAllBytes(), Charsets.UTF_8)
        val yaml = org.yaml.snakeyaml.Yaml()
        return yaml.load(content) as? Map<String, Any?> ?: emptyMap()
    }

    private fun convertToMap(value: Value): Map<String, Any?> {
        return if (value.hasMembers()) {
            value.memberKeys.associateWith { key ->
                val member = value.getMember(key)
                when {
                    member.isBoolean -> member.asBoolean()
                    member.isNumber -> member.asDouble()
                    member.isString -> member.asString()
                    member.hasMembers() -> convertToMap(member)
                    else -> null
                }
            }
        } else {
            emptyMap()
        }
    }
}