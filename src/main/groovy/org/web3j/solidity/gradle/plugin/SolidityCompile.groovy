package org.web3j.solidity.gradle.plugin

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

class SolidityCompile extends SourceTask {

    @Input
    @Optional
    private String executable

    @Input
    @Optional
    private String version

    @Input
    @Optional
    private Boolean overwrite

    @Input
    @Optional
    private Boolean optimize

    @Input
    @Optional
    private Integer optimizeRuns

    @Input
    @Optional
    private Boolean prettyJson

    @Input
    @Optional
    private Boolean ignoreMissing

    @Input
    @Optional
    private List<String> allowPaths

    @Input
    @Optional
    private EVMVersion evmVersion

    @Input
    @Optional
    private OutputComponent[] outputComponents

    @TaskAction
    void compileSolidity() {
        for (def contract in source) {
            def options = []

            for (output in outputComponents) {
                options.add("--$output")
            }

            if (optimize) {
                options.add('--optimize')

                if (0 < optimizeRuns) {
                    options.add('--optimize-runs')
                    options.add(optimizeRuns)
                }
            }

            if (overwrite) {
                options.add('--overwrite')
            }

            if (prettyJson) {
                options.add('--pretty-json')
                options.add(options.add("--$OutputComponent.ASM_JSON"))
            }

            if (ignoreMissing) {
                options.add('--ignore-missing')
            }

            if (!allowPaths.isEmpty()) {
                options.add("--allow-paths")
                options.add(allowPaths.join(','))
            }

            if (supportsEvmVersionOption()) {
                if (evmVersion != null) {
                    options.add("--evm-version")
                    options.add(evmVersion.value)
                }
            }

            options.add('--output-dir')
            options.add(project.projectDir.relativePath(outputs.files.singleFile))
            options.add(project.projectDir.relativePath(contract))

            def executableParts = executable.split(' ')
            options.addAll(0, executableParts.drop(1))

            project.exec {
                // Use first part as executable
                executable = executableParts[0]
                // Use other parts and options as args
                args = options
            }
        }
    }

    String getExecutable() {
        return executable
    }

    void setExecutable(final String executable) {
        this.executable = executable
    }

    String getVersion() {
        return version
    }

    void setVersion(String version) {
        this.version = version
    }

    boolean supportsEvmVersionOption() {
        return version.split('\\.').last().toInteger() >= 24
    }

    Boolean getOverwrite() {
        return overwrite
    }

    void setOverwrite(final Boolean overwrite) {
        this.overwrite = overwrite
    }

    Boolean getOptimize() {
        return optimize
    }

    void setOptimize(final Boolean optimize) {
        this.optimize = optimize
    }

    Integer getOptimizeRuns() {
        return optimizeRuns
    }

    void setOptimizeRuns(final Integer optimizeRuns) {
        this.optimizeRuns = optimizeRuns
    }

    Boolean getPrettyJson() {
        return prettyJson
    }

    void setPrettyJson(final Boolean prettyJson) {
        this.prettyJson = prettyJson
    }

    Boolean getIgnoreMissing() {
        return ignoreMissing
    }

    void setIgnoreMissing(final Boolean ignoreMissing) {
        this.ignoreMissing = ignoreMissing
    }

    List<String> getAllowPaths() {
        return allowPaths
    }

    void setAllowPaths(final List<String> allowPaths) {
        this.allowPaths = allowPaths
    }

    EVMVersion getEvmVersion() {
        return evmVersion
    }

    void setEvmVersion(final EVMVersion evmVersion) {
        this.evmVersion = evmVersion
    }

    OutputComponent[] getOutputComponents() {
        return outputComponents
    }

    void setOutputComponents(final OutputComponent[] outputComponents) {
        this.outputComponents = outputComponents
    }

}
