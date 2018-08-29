package org.web3j.solidity.gradle.plugin

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.internal.file.SourceDirectorySetFactory
import org.gradle.api.tasks.SourceSet

import static org.codehaus.groovy.runtime.StringGroovyMethods.capitalize
import static org.web3j.solidity.gradle.plugin.SoliditySourceSet.NAME

/**
 * Add default source set for Solidity.
 */
class SoliditySourceSetAction implements Action<SourceSet> {

    private final Project project
    private final SourceDirectorySetFactory sourceFactory

    SoliditySourceSetAction(
            final Project project,
            final SourceDirectorySetFactory sourceFactory) {
        this.project = project
        this.sourceFactory = sourceFactory
    }

    @Override
    void execute(final SourceSet sourceSet) {

        def srcSetName = capitalize((CharSequence) sourceSet.name)
        def soliditySourceSet = new DefaultSoliditySourceSet(srcSetName, sourceFactory)

        sourceSet.convention.plugins.put(NAME, soliditySourceSet)

        def defaultSrcDir = new File(project.projectDir, "src/$sourceSet.name/$NAME")
        def defaultOutputDir = new File(project.buildDir, "resources/$sourceSet.name/$NAME")

        soliditySourceSet.solidity.srcDir(defaultSrcDir)
        soliditySourceSet.solidity.outputDir = defaultOutputDir

        sourceSet.allJava.source(soliditySourceSet.solidity)
        sourceSet.allSource.source(soliditySourceSet.solidity)
    }

}
