package org.web3j.solidity.gradle.plugin

import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

@CacheableTask
class SolidityDependenciesResolver extends SourceTask {

    Set<String> dependencies = ["@openzeppelin/contracts", "synthetix"]

    @TaskAction
    void resolveSolidityDependencies() {
        Set<String> libraries = []
        for (def contract in source) {
            contract.readLines().forEach { line ->
                dependencies.forEach { it ->
                    if (line.contains(it)) {
                        libraries.add(it)
                    }
                }
            }
        }
        def map = new HashMap()
        def importNames = new HashMap()
        libraries.forEach {
            item -> importNames.put(item, "latest")
        }

        map.put("name", project.name)
        map.put("dependencies", importNames)
        def json = new JsonBuilder()
        json map
        def packageJson = new File("$project.buildDir/resources/package.json")
        //   def packageJson = outputs.files.asFileTree.singleFile
        packageJson.append(JsonOutput.prettyPrint(json.toString()))

        if (!packageJson.exists()) {
            packageJson.parentFile.mkdirs()
            packageJson.createNewFile()
            packageJson.append(JsonOutput.prettyPrint(json.toString()))
        }


    }


}
