package org.web3j.solidity.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.file.SourceDirectorySetFactory
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSetContainer

import javax.inject.Inject

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

        final SoliditySourceSetAction sourceSetAction =
                new SoliditySourceSetAction(target, sourceFactory)

        final ConfigureTasks compileSolidity = new ConfigureTasks(target)

        sourceSets.all(sourceSetAction)

        target.afterEvaluate {
            sourceSets.all(compileSolidity)
        }
    }

}
