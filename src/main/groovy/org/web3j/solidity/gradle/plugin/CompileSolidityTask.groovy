package org.web3j.solidity.gradle.plugin

import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

class CompileSolidityTask extends SourceTask {

    private final SourceSet sourceSet

    @Inject
    CompileSolidityTask(final SourceSet sourceSet) {
        this.sourceSet = sourceSet
    }

    @TaskAction
    void compileSolidity() {
        source.each { File contract ->
            project.exec {
                executable = 'solc'
                args = ['--bin', '--abi', '--optimize', '--overwrite',
                        '-o', outputs.files.singleFile.absolutePath,
                        contract.absolutePath]
            }
        }
    }

}
