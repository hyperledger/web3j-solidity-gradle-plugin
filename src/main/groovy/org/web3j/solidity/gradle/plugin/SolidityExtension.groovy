package org.web3j.solidity.gradle.plugin

import groovy.transform.CompileStatic
import org.gradle.api.Project

import javax.inject.Inject

import static EVMVersion.BYZANTIUM

/**
 * Extension for Solidity compilation options.
 */
@CompileStatic
class SolidityExtension {

    static final NAME = 'solidity'

    private Project project

    private String version

    private String executable

    private Boolean overwrite

    private Boolean optimize

    private Integer optimizeRuns

    private Boolean prettyJson

    private Boolean ignoreMissing

    private List<String> allowPaths

    private EVMVersion evmVersion

    private OutputComponent[] outputComponents

    private CombinedOutputComponent[] combinedOutputComponents

    @Inject
    SolidityExtension(final Project project) {
        this.project = project
        this.version = SolcVersion.v0_4_25.value
        this.executable = null
        this.optimize = true
        this.overwrite = true
        this.optimizeRuns = 0
        this.prettyJson = false
        this.ignoreMissing = false
        this.allowPaths = []
        this.evmVersion = BYZANTIUM
        this.outputComponents = [OutputComponent.BIN, OutputComponent.ABI]
        this.combinedOutputComponents = [CombinedOutputComponent.BIN, CombinedOutputComponent.BIN_RUNTIME, CombinedOutputComponent.SRCMAP, CombinedOutputComponent.SRCMAP_RUNTIME]
    }

    String getVersion() {
        return version
    }

    void setVersion(final String version) {
        this.version = version
    }

    String getExecutable() {
        return executable
    }

    void setExecutable(final String executable) {
        this.executable = executable
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

    boolean getOverwrite() {
        return overwrite
    }

    void setOverwrite(final boolean overwrite) {
        this.overwrite = overwrite
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

    CombinedOutputComponent[] getCombinedOutputComponents() {
        return combinedOutputComponents
    }

    void setCombinedOutputComponents(final CombinedOutputComponent[] combinedOutputComponents) {
        this.combinedOutputComponents = combinedOutputComponents
    }
}
