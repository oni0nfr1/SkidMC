package io.github.oni0nfr1.skidTest.processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class SkidTestProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return SkidTestProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
        )
    }
}
