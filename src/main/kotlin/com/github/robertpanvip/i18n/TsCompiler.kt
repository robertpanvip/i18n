package com.github.robertpanvip.i18n

import com.intellij.openapi.vfs.VirtualFile
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Source

object TsCompiler {
    private val context: Context by lazy {
        Context.newBuilder("js")
            .allowAllAccess(true) // 调试用，生产环境谨慎
            .option("engine.WarnInterpreterOnly", "false") // 优化性能
            .build()
    }

    init {
        // 加载 typescript.js
        val tsScript = this::class.java.classLoader.getResource("typescript.js")?.readText()
            ?: throw IllegalStateException("TypeScript runtime not found in resources")
        context.eval(Source.create("js", tsScript))
    }

    fun compileTsToJs(file: VirtualFile): String {
        val tsContent = String(file.inputStream.readAllBytes(), Charsets.UTF_8)
        val script = """
            var ts = Polyglot.import("ts");
            ts.transpile('$tsContent', {
                compilerOptions: {
                    module: ts.ModuleKind.CommonJS,
                    removeComments: true,
                    target: ts.ScriptTarget.ESNext
                }
            });
        """.trimIndent()
        val result = context.eval(Source.create("js", script))
        return result.asString()
    }
}