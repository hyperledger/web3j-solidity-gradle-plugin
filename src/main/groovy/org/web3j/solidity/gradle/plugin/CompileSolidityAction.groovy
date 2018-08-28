package org.web3j.solidity.gradle.plugin

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet

import static org.codehaus.groovy.runtime.StringGroovyMethods.capitalize
import static org.web3j.solidity.gradle.plugin.SoliditySourceSet.NAME

/**
 * Gradle action configuring code compilation tasks for the
 * Solidity source sets defined in the project (e.g. main, test).
 * <p>
 * By default the generated task name for the <code>main</code> source set
 * is <code>compileSolidity</code> and for <code>test</code>
 * <code>compileTestSolidity</code>.
 */
class CompileSolidityAction implements Action<SourceSet> {

    private final Project project

    CompileSolidityAction(final Project project) {
        this.project = project
    }

    @Override
    void execute(final SourceSet sourceSet) {

        def srcSetName = sourceSet.name == 'main' ?
                '' : capitalize((CharSequence) sourceSet.name)

        def compileTask = project.tasks.create(
                "compile" + srcSetName + "Solidity", CompileSolidityTask, sourceSet)

        def soliditySourceSet = sourceSet.convention.plugins[NAME] as SoliditySourceSet

        // Set the sources for the generation task
        compileTask.setSource(soliditySourceSet.solidity)

        def outputDir = project.solidity.outputDir.absolutePath
        compileTask.outputComponents = project.solidity.outputComponents
        compileTask.overwrite = project.solidity.overwrite
        compileTask.optimize = project.solidity.optimize
        compileTask.optimizeRuns = project.solidity.optimizeRuns
        compileTask.outputs.dir("$outputDir/$sourceSet.name/$NAME")
        compileTask.description = "Generates web3j contract wrappers " +
                "for $sourceSet.name source set."
    }

}

