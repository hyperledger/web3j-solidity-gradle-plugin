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

        final String srcSetName = capitalize((CharSequence) sourceSet.name)
        final DefaultSoliditySourceSet soliditySourceSet =
                new DefaultSoliditySourceSet(srcSetName, sourceFactory)

        sourceSet.convention.plugins.put(NAME, soliditySourceSet)

        final File defaultSrcDir = new File(project.projectDir,
                "src/" + sourceSet.name + "/" + NAME)

        soliditySourceSet.solidity.srcDir(defaultSrcDir)
        sourceSet.allJava.source(soliditySourceSet.solidity)
        sourceSet.allSource.source(soliditySourceSet.solidity)
    }

}
