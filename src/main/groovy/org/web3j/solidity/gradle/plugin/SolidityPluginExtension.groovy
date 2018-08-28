package org.web3j.solidity.gradle.plugin

import org.gradle.api.Project

import javax.inject.Inject

/**
 * Extension for Solidity compilation options.
 */
class SolidityPluginExtension {

    static final NAME = 'solidity'

    private Project project

    private Boolean overwrite

    private Boolean optimize

    private Integer optimizeRuns

    private Boolean prettyJson

    private File outputDir

    private OutputComponent[] outputComponents

    @Inject
    SolidityPluginExtension(final Project project) {
        this.project = project
        this.optimize = true
        this.overwrite = true
        this.prettyJson = false
        this.optimizeRuns = 0
        this.outputDir = new File("$project.buildDir/resources")
        this.outputComponents = [OutputComponent.BIN, OutputComponent.ABI]
    }

    boolean getOptimize() {
        return optimize
    }

    void setOptimize(final boolean optimize) {
        this.optimize = optimize
    }

    int getOptimizeRuns() {
        return optimizeRuns
    }

    void setOptimizeRuns(final int optimizeRuns) {
        this.optimizeRuns = optimizeRuns
    }

    boolean getPrettyJson() {
        return prettyJson
    }

    void setPrettyJson(final boolean prettyJson) {
        this.prettyJson = prettyJson
    }

    File getOutputDir() {
        return outputDir
    }

    void setOutputDir(final File outputDir) {
        this.outputDir = outputDir
    }

    boolean getOverwrite() {
        return overwrite
    }

    void setOverwrite(final boolean overwrite) {
        this.overwrite = overwrite
    }

    OutputComponent[] getOutputComponents() {
        return outputComponents
    }

    void setOutputComponents(final OutputComponent[] outputComponents) {
        this.outputComponents = outputComponents
    }

}
