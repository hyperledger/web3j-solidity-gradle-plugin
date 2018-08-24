package org.web3j.solidity.gradle.plugin

import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class SolidityPluginTest {

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()

    @Before
    void setup() throws IOException {
        def resource = getClass().classLoader
                .getResource("solidity/EIP20.sol")

        def sourceDir = new File(resource.file).parentFile

        def buildFile = testProjectDir.newFile("build.gradle")
        buildFile << """
            plugins {
               id 'solidity' version '0.1.0'
            }
            sourceSets {
               main {
                   solidity {
                       srcDir {
                           '$sourceDir.absolutePath'
                       }
                   }
               }
            }
            repositories {
                mavenCentral()
                mavenLocal()
            }
        """

        def settingsFile = testProjectDir.newFile("settings.gradle")
        settingsFile << """
            pluginManagement {
                repositories {
                    mavenCentral()
                    mavenLocal()
                }
            }
        """
    }

    @Test
    void compileSolidity() {
        def compileSolidity = GradleRunner.create()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("compileSolidity")
                .forwardOutput()
                .withDebug(true)

        def success = compileSolidity.build()
        assertEquals(SUCCESS, success.task(":compileSolidity").outcome)

        def compiledSolDir = new File(testProjectDir.root,
                "build/resources/main/solidity")

        def compiledAbi = new File(compiledSolDir, "EIP20.abi")
        assertTrue(compiledAbi.exists())

        def compiledBin = new File(compiledSolDir, "EIP20.bin")
        assertTrue(compiledBin.exists())

        def upToDate = compileSolidity.build()
        assertEquals(UP_TO_DATE, upToDate.task(":compileSolidity").outcome)
    }

}
