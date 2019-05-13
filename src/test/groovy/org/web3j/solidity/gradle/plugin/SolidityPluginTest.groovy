package org.web3j.solidity.gradle.plugin

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import java.nio.file.Files
import java.nio.file.StandardCopyOption

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class SolidityPluginTest {

    /**
     * Gradle project directory where the test project will be run.
     * Has to be under <code>/tmp</code> because of Docker file sharing defaults.
     */
    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder(new File('/tmp'))

    /**
     * Solidity sources directory.
     */
    private static File sourcesDir

    /**
     * Gradle build file.
     */
    private File buildFile

    @BeforeClass
    static void setUp() throws Exception {
        final def resource = SolidityPlugin.getClassLoader().getResource('solidity/eip/EIP20.sol')
        sourcesDir = new File(resource.file).parentFile.parentFile
    }

    @Before
    void setup() throws IOException {
        buildFile = testProjectDir.newFile('build.gradle')
        testProjectDir.newFolder('src', 'main')
        Files.walk(sourcesDir.toPath()).each {
            // Copy .sol files into temp folder for Docker
            final def fileName = sourcesDir.relativePath(it.toFile())
            final def file = testProjectDir.newFile("src/main/solidity/$fileName")
            Files.copy(it, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }

    @Test
    void compileSolidity() {
        buildFile << """
            plugins {
               id 'org.web3j.solidity'
            }
            sourceSets {
               main {
                   solidity {
                       exclude "greeter/**"
                       exclude "common/**"
                   }
               }
            }
        """

        def success = build()
        assertEquals(SUCCESS, success.task(":compileSolidity").outcome)

        def compiledSolDir = new File(testProjectDir.root,
                "build/resources/main/solidity")

        def compiledAbi = new File(compiledSolDir, "EIP20.abi")
        assertTrue(compiledAbi.exists())

        def compiledBin = new File(compiledSolDir, "EIP20.bin")
        assertTrue(compiledBin.exists())

        def upToDate = build()
        assertEquals(UP_TO_DATE, upToDate.task(":compileSolidity").outcome)
    }

    @Test
    void compileSolidityAllowedPath() {
        buildFile << """
            plugins {
               id 'org.web3j.solidity'
            }
            sourceSets {
               main {
                   solidity {
                       exclude "common/**"
                       exclude "eip/**"
                   }
               }
            }
        """

        def success = build()
        assertEquals(SUCCESS, success.task(":compileSolidity").outcome)

        def compiledSolDir = new File(testProjectDir.root,
                "build/resources/main/solidity")

        def compiledAbi = new File(compiledSolDir, "Greeter.abi")
        assertTrue(compiledAbi.exists())

        def compiledBin = new File(compiledSolDir, "Greeter.bin")
        assertTrue(compiledBin.exists())

        def excludedAbi = new File(compiledSolDir, "Mortal.abi")
        assertTrue(excludedAbi.exists())

        def upToDate = build()
        assertEquals(UP_TO_DATE, upToDate.task(":compileSolidity").outcome)
    }

    @Test
    void compileSolidityWithVersion() {
        buildFile << """
            plugins {
               id 'org.web3j.solidity'
            }
            solidity {
                version = '0.4.10'
            }
            sourceSets {
               main {
                   solidity {
                       exclude "greeter/**"
                       exclude "common/**"
                   }
               }
            }
        """

        def success = build()
        assertEquals(SUCCESS, success.task(":compileSolidity").outcome)

        def compiledSolDir = new File(testProjectDir.root,
                "build/resources/main/solidity")

        def compiledAbi = new File(compiledSolDir, "EIP20.abi")
        assertTrue(compiledAbi.exists())

        def compiledBin = new File(compiledSolDir, "EIP20.bin")
        assertTrue(compiledBin.exists())

        def upToDate = build()
        assertEquals(UP_TO_DATE, upToDate.task(":compileSolidity").outcome)
    }

    @Test
    void compileSolidityWithExecutable() {
        buildFile << """
            plugins {
               id 'org.web3j.solidity'
            }
            solidity {
                executable = 'solc'
            }
            sourceSets {
               main {
                   solidity {
                       exclude "greeter/**"
                       exclude "common/**"
                   }
               }
            }
        """
        def success = build()
        assertEquals(SUCCESS, success.task(":compileSolidity").outcome)

        def compiledSolDir = new File(testProjectDir.root,
                "build/resources/main/solidity")

        def compiledAbi = new File(compiledSolDir, "EIP20.abi")
        assertTrue(compiledAbi.exists())

        def compiledBin = new File(compiledSolDir, "EIP20.bin")
        assertTrue(compiledBin.exists())

        def upToDate = build()
        assertEquals(UP_TO_DATE, upToDate.task(":compileSolidity").outcome)
    }

    @Test
    void compileSolidityWithDocker() {
        buildFile << """
            plugins {
               id 'org.web3j.solidity'
            }
            sourceSets {
               main {
                   solidity {
                       exclude "eip/**"
                   }
               }
            }
            solidity {
                executable = 'docker run --rm -v $testProjectDir.root:/src satran004/aion-fastvm:0.3.1 solc'
                allowPaths = ['/src/src/main/solidity']
                version = '0.4.15'
            }
        """

        def success = build()
        assertEquals(SUCCESS, success.task(":compileSolidity").outcome)

        def compiledSolDir = new File(testProjectDir.root,
                "build/resources/main/solidity")

        def compiledAbi = new File(compiledSolDir, "Greeter.abi")
        assertTrue(compiledAbi.exists())

        def compiledBin = new File(compiledSolDir, "Greeter.bin")
        assertTrue(compiledBin.exists())

        def upToDate = build()
        assertEquals(UP_TO_DATE, upToDate.task(":compileSolidity").outcome)
    }

    private BuildResult build() {
        return GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments("build")
                .withPluginClasspath()
                .forwardOutput()
                .withDebug(true)
                .build()
    }
}
