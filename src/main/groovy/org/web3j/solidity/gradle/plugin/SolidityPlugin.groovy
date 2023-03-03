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

import com.github.gradle.node.NodeExtension
import com.github.gradle.node.NodePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
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

    private final ObjectFactory objectFactory;
    private final SoliditySourceSet resolvedSolidity

    @Inject
    SolidityPlugin(final ObjectFactory objectFactory) {
        this.objectFactory = objectFactory
        this.resolvedSolidity = new DefaultSoliditySourceSet("All", objectFactory)
    }

    @Override
    void apply(final Project target) {
        target.pluginManager.apply(JavaPlugin.class)
        target.pluginManager.apply(NodePlugin.class)
        target.extensions.create(SolidityExtension.NAME,
                SolidityExtension, target)

        final SourceSetContainer sourceSets = target.convention
                .getPlugin(JavaPluginConvention.class).sourceSets

        sourceSets.all { SourceSet sourceSet ->
            configureSourceSet(target, sourceSet)
        }
        // Set nodeProjectDir to build before the node plugin evaluation
        def nodeExtension = target.extensions.getByName(NodeExtension.NAME) as NodeExtension
        nodeExtension.nodeProjectDir = target.objects.directoryProperty().convention(target.layout.buildDirectory)
        nodeExtension.download.set(true)

        target.afterEvaluate {
            sourceSets.all { SourceSet sourceSet ->
                configureSolidityCompile(target, sourceSet)
                configureAllowPath(target, sourceSet)
                sourceSet.allSource.srcDirs.forEach {
                    resolvedSolidity.solidity.srcDir(it)
                }
            }
            configureSolidityResolve(target, nodeExtension.nodeProjectDir)
        }
    }

    /**
     * Add default source set for Solidity.
     */
    private void configureSourceSet(final Project project, final SourceSet sourceSet) {

        def srcSetName = capitalize((CharSequence) sourceSet.name)
        def soliditySourceSet = new DefaultSoliditySourceSet(srcSetName, objectFactory)

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
    private static void configureSolidityCompile(final Project project, final SourceSet sourceSet) {

        def srcSetName = sourceSet.name == 'main' ? '' : capitalize((CharSequence) sourceSet.name)
        def compileTask = project.tasks.create("compile${srcSetName}Solidity", SolidityCompile)
        def soliditySourceSet = sourceSet.convention.plugins[NAME] as SoliditySourceSet

        if (!requiresBundledExecutable(project)) {
            // Leave executable as specified by the user
            compileTask.executable = project.solidity.executable
        }
        compileTask.pathRemappings = project.solidity.pathRemappings
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

        if (project.solidity.resolvePackages) {
            project.getTasks().named('npmInstall').configure {
                it.dependsOn(project.getTasks().named("resolveSolidity"))
            }
            compileTask.dependsOn(project.getTasks().named("npmInstall"))
        }

        project.getTasks().named('build').configure {
            it.dependsOn(compileTask)
        }
    }

    private void configureSolidityResolve(Project target, DirectoryProperty nodeProjectDir) {
        def resolveSolidity = target.tasks.create("resolveSolidity", SolidityResolve)
        resolveSolidity.sources = resolvedSolidity.solidity
        resolveSolidity.description = "Resolve external Solidity contract modules."
        resolveSolidity.allowPaths = target.solidity.allowPaths
        resolveSolidity.onlyIf { target.solidity.resolvePackages }

        def packageJson = new File(nodeProjectDir.asFile.get(), "package.json")
        resolveSolidity.packageJson = packageJson
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
