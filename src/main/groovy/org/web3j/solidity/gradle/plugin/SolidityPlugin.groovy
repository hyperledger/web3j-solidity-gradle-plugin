/*
 * Copyright 2019 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.solidity.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.file.SourceDirectorySetFactory
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

import javax.inject.Inject

import static org.codehaus.groovy.runtime.StringGroovyMethods.capitalize
import static org.web3j.solidity.gradle.plugin.SoliditySourceSet.NAME

/**
 * Gradle plugin for Solidity compile automation.
 */
class SolidityPlugin implements Plugin<Project> {

    private final SourceDirectorySetFactory sourceFactory

    @Inject
    SolidityPlugin(final SourceDirectorySetFactory sourceFactory) {
        this.sourceFactory = sourceFactory
    }

    @Override
    void apply(final Project target) {
        target.pluginManager.apply(JavaPlugin.class)
        target.extensions.create(SolidityExtension.NAME,
                SolidityExtension, target)

        final SourceSetContainer sourceSets = target.convention
                .getPlugin(JavaPluginConvention.class).sourceSets

        sourceSets.all { SourceSet sourceSet ->
            configureSourceSet(target, sourceSet)
        }

        target.afterEvaluate {
            sourceSets.all { SourceSet sourceSet ->
                configureTask(target, sourceSet)
                configureAllowPath(target, sourceSet)
            }
        }
    }

    /**
     * Add default source set for Solidity.
     */
    private void configureSourceSet(final Project project, final SourceSet sourceSet) {

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

    /**
     * Configures code compilation tasks for the Solidity source sets defined in the project
     * (e.g. main, test).
     * <p>
     * By default the generated task name for the <code>main</code> source set
     * is <code>compileSolidity</code> and for <code>test</code>
     * <code>compileTestSolidity</code>.
     */
    private void configureTask(final Project project, final SourceSet sourceSet) {

        def srcSetName = sourceSet.name == 'main' ?
                '' : capitalize((CharSequence) sourceSet.name)

        def compileTask = project.tasks.create(
                "compile${srcSetName}Solidity", SolidityCompile)

        def soliditySourceSet = sourceSet.convention.plugins[NAME] as SoliditySourceSet

        if (!requiresBundledExecutable(project)) {
            // Leave executable as specified by the user
            compileTask.executable = project.solidity.executable
        }

        compileTask.version = project.solidity.version
        compileTask.source = soliditySourceSet.solidity
        compileTask.outputComponents = project.solidity.outputComponents
        compileTask.combinedOutputComponents = project.solidity.combinedOutputComponents
        compileTask.overwrite = project.solidity.overwrite
        compileTask.optimize = project.solidity.optimize
        compileTask.optimizeRuns = project.solidity.optimizeRuns
        compileTask.prettyJson = project.solidity.prettyJson
        compileTask.evmVersion = project.solidity.evmVersion
        compileTask.allowPaths = project.solidity.allowPaths
        compileTask.ignoreMissing = project.solidity.ignoreMissing
        compileTask.outputs.dir(soliditySourceSet.solidity.outputDir)
        compileTask.description = "Compiles $sourceSet.name Solidity source."

        project.getTasks().getByName('build') dependsOn(compileTask)
    }

    /**
     * Configure the SolcJ compiler with the bundled executable.
     */
    private static void configureAllowPath(final Project project, final SourceSet sourceSet) {
        def allowPath = "$project.projectDir/src/$sourceSet.name/$NAME"
        project.solidity.allowPaths.add(allowPath)
    }

    private static boolean requiresBundledExecutable(final Project project) {
        return project.solidity.executable == null
    }
}
