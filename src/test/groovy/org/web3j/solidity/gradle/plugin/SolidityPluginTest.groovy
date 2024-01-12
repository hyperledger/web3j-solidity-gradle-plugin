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

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.*
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
     * Folder containing Solidity smart contracts with different versions.
     */
    private final String differentVersionsFolderName = "different_versions"

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
                        exclude "minimal_forwarder/**"
                        exclude "eip/**"
                        exclude "greeter/**"
                        exclude "common/**"
                        exclude "openzeppelin/**"
                        exclude "$differentVersionsFolderName/**"
                    }
                }
            tasks.named("jar").configure { dependsOn("compileSolidity") }
            }
        """

        def success = build()
        assertEquals(SUCCESS, success.task(":compileSolidity").outcome)

        def compiledSolDir = new File(testProjectDir.root, "build/resources/main/solidity")
        def compiledAbi = new File(compiledSolDir, "Greeter.abi")
        assertTrue(compiledAbi.exists())

        def compiledBin = new File(compiledSolDir, "Greeter.bin")
        assertTrue(compiledBin.exists())

        def generatedMeta = new File(compiledSolDir, "Greeter_meta.json")
        assertTrue(generatedMeta.exists())

        def upToDate = build()
        assertEquals(UP_TO_DATE, upToDate.task(":compileSolidity").outcome)
    }

    @Test
    void compileSolidityWithLibraryImports() {
        buildFile << """
            plugins {
               id 'org.web3j.solidity'
            }
            node {
                nodeProjectDir = file("\$project.rootDir/test")
            }
            sourceSets {
                main {
                    solidity {
                        exclude "minimal_forwarder/**"
                        exclude "sol5/**"
                        exclude "common/**"
                        exclude "eip/**"
                        exclude "$differentVersionsFolderName/**"
                        exclude "greeter/**"
                    }
                }
            }
            tasks.named("jar").configure { dependsOn("compileSolidity") }
        """

        def success = build()
        assertEquals(SUCCESS, success.task(":compileSolidity").outcome)

        def compiledSolDir = new File(testProjectDir.root, "build/resources/main/solidity")
        def compiledAbi = new File(compiledSolDir, "MyCollectible.abi")
        assertTrue(compiledAbi.exists())

        def compiledBin = new File(compiledSolDir, "MyCollectible.bin")
        assertTrue(compiledBin.exists())

        def erc721Abi = new File(compiledSolDir, "ERC721.abi")
        assertTrue(erc721Abi.exists())

        def upToDate = build()
        assertEquals(UP_TO_DATE, upToDate.task(":compileSolidity").outcome)
        assertEquals(UP_TO_DATE, upToDate.task(":resolveSolidity").outcome)
    }

    @Test
    void compileSolidityWithVersion() {
        buildFile << """
            plugins {
               id 'org.web3j.solidity'
            }
            solidity {
                version = '0.8.7'
            }
            sourceSets {
               main {
                   solidity {
                       exclude "minimal_forwarder/**"
                       exclude "sol5/**"
                       exclude "greeter/**"
                       exclude "common/**"
                       exclude "openzeppelin/**"
                       exclude "$differentVersionsFolderName/**"
                   }
               }
            tasks.named("jar").configure { dependsOn("compileSolidity") }
            }
        """

        def success = build()
        assertEquals(SUCCESS, success.task(":compileSolidity").outcome)

        def compiledSolDir = new File(testProjectDir.root, "build/resources/main/solidity")
        def compiledAbi = new File(compiledSolDir, "EIP20.abi")
        assertTrue(compiledAbi.exists())

        def compiledBin = new File(compiledSolDir, "EIP20.bin")
        assertTrue(compiledBin.exists())

        def upToDate = build()
        assertEquals(UP_TO_DATE, upToDate.task(":compileSolidity").outcome)
    }

    @Test
    void compileSolidityWithEvmVersion() {
        buildFile << """
            plugins {
               id 'org.web3j.solidity'
            }
            solidity {
                evmVersion = 'ISTANBUL'
            }
            sourceSets {
               main {
                   solidity {
                       exclude "sol5/**"
                       exclude "eip/**"
                       exclude "greeter/**"
                       exclude "common/**"
                       exclude "openzeppelin/**"
                       exclude "$differentVersionsFolderName/**"
                   }
               }
            tasks.named("jar").configure { dependsOn("compileSolidity") }
            }
        """

        def success = build()
        assertEquals(SUCCESS, success.task(":compileSolidity").outcome)

        def compiledSolDir = new File(testProjectDir.root, "build/resources/main/solidity")
        def compiledAbi = new File(compiledSolDir, "MinimalForwarder.abi")
        assertTrue(compiledAbi.exists())

        def compiledBin = new File(compiledSolDir, "MinimalForwarder.bin")
        assertTrue(compiledBin.exists())

        def upToDate = build()
        assertEquals(UP_TO_DATE, upToDate.task(":compileSolidity").outcome)
    }

    @Test
    @Ignore("Requires a specific solc version on the machine to pass")
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
                        exclude "minimal_forwarder/**"
                        exclude "sol5/**"
                        exclude "greeter/**"
                        exclude "common/**"
                        exclude "openzeppelin/**"
                        exclude "$differentVersionsFolderName/**"
                    }
                }
            }
        """
        def success = build()
        assertEquals(SUCCESS, success.task(":compileSolidity").outcome)

        def compiledSolDir = new File(testProjectDir.root, "build/resources/main/solidity")
        def compiledAbi = new File(compiledSolDir, "EIP20.abi")
        assertTrue(compiledAbi.exists())

        def compiledBin = new File(compiledSolDir, "EIP20.bin")
        assertTrue(compiledBin.exists())

        def upToDate = build()
        assertEquals(UP_TO_DATE, upToDate.task(":compileSolidity").outcome)
    }

    /**
     * Requires a running Docker environment.
     */
    @Test
    @Ignore("This is cool but fails if docker is not running. // Needs to be solved in the CI")
    void compileSolidityWithDocker() {
        buildFile << """
            plugins {
               id 'org.web3j.solidity'
            }
            sourceSets {
                main {
                    solidity {
                        exclude "minimal_forwarder/**"
                        exclude "sol5/**"
                        exclude "eip/**"
                        exclude "openzeppelin/**"
                        exclude "$differentVersionsFolderName/**"
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

        def compiledSolDir = new File(testProjectDir.root, "build/resources/main/solidity")
        def compiledAbi = new File(compiledSolDir, "Greeter.abi")
        assertTrue(compiledAbi.exists())

        def compiledBin = new File(compiledSolDir, "Greeter.bin")
        assertTrue(compiledBin.exists())

        def upToDate = build()
        assertEquals(UP_TO_DATE, upToDate.task(":compileSolidity").outcome)
    }

    @Test
    void compileSolidityWithDifferentVersions() {
        buildFile << """
            plugins {
               id 'org.web3j.solidity'
            }
            sourceSets {
                main {
                    solidity {
                        exclude "minimal_forwarder/**"
                        exclude "eip/**"
                        exclude "greeter/**"
                        exclude "common/**"
                        exclude "sol5/**"
                        exclude "openzeppelin/**"
                    }
                }
            }
            tasks.named("jar").configure { dependsOn("compileSolidity") }
        """

        def success = build()
        assertEquals(SUCCESS, success.task(":compileSolidity").outcome)
    }

    private BuildResult build() {
        return GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments("build", "--info")
                .withPluginClasspath()
                .forwardOutput()
                .withDebug(true)
                .build()
    }
}
