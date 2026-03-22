package io.github.oni0nfr1.skidTest.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration

class SkidTestProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    private var generated = false

    override fun process(resolver: Resolver): List<com.google.devtools.ksp.symbol.KSAnnotated> {
        if (generated) {
            return emptyList()
        }

        val tests = resolver
            .getSymbolsWithAnnotation(ANNOTATION_NAME)
            .filterIsInstance<KSClassDeclaration>()
            .sortedBy { it.qualifiedName?.asString().orEmpty() }
            .toList()

        val validTests = tests.filter { test ->
            if (test.classKind != ClassKind.OBJECT) {
                logger.error("@SkidTest can only be applied to Kotlin objects.", test)
                false
            } else {
                true
            }
        }

        writeRegistry(validTests)
        generated = true
        return emptyList()
    }

    private fun writeRegistry(tests: List<KSClassDeclaration>) {
        val dependencies = Dependencies(
            aggregating = true,
            *tests.mapNotNull { it.containingFile }.toTypedArray(),
        )
        val file = codeGenerator.createNewFile(
            dependencies = dependencies,
            packageName = GENERATED_PACKAGE,
            fileName = GENERATED_FILE_NAME,
        )

        file.writer().use { writer ->
            writer.appendLine("package $GENERATED_PACKAGE")
            writer.appendLine()
            writer.appendLine("object $GENERATED_FILE_NAME {")
            writer.appendLine("    fun bootstrap() {")

            tests.forEach { test ->
                val qualifiedName = test.qualifiedName?.asString() ?: return@forEach
                writer.appendLine("        $qualifiedName")
            }

            writer.appendLine("    }")
            writer.appendLine("}")
        }
    }

    private companion object {
        const val ANNOTATION_NAME = "io.github.oni0nfr1.skidTest.annotations.SkidTest"
        const val GENERATED_PACKAGE = "io.github.oni0nfr1.skidTest.client.generated"
        const val GENERATED_FILE_NAME = "GeneratedSkidTests"
    }
}
