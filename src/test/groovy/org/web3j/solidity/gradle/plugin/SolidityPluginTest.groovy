package org.web3j.solidity.gradle.plugin

import org.gradle.testkit.runner.GradleRunner
import org.junit.*
import org.junit.rules.TemporaryFolder

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE
import static org.junit.Assert.*

class SolidityPluginTest {

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()

    /**
     * Solidity sources directory.
     */
    private static File sourceDir

    /**
     * Gradle build file.
     */
    private File buildFile

    @BeforeClass
    static void setUp() throws Exception {
        final def resource = SolidityPlugin.getClassLoader().getResource('solidity/EIP20.sol')
        sourceDir = new File(resource.file).parentFile
    }

    @Before
    void setup() throws IOException {
        buildFile = testProjectDir.newFile('build.gradle')
    }

    @After
    void tearDown() throws Exception {
        buildFile.delete()
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
                       srcDir '$sourceDir.absolutePath'
                       exclude 'common/**'
                       exclude 'subdir/**'
                   }
               }
            }
            repositories {
                mavenCentral()
            }
        """
        buildAndValidate()
    }

    @Test
    void compileSolidityWithAllowedPaths() {
        buildFile << """
            plugins {
               id 'org.web3j.solidity'
            }
            sourceSets {
               main {
                   solidity {
                       srcDir '$sourceDir.absolutePath'
                       exclude 'common/**'
                   }
               }
            }
            solidity {
                allowPaths = ['$sourceDir.absolutePath']
            }
            repositories {
                mavenCentral()
            }
        """
        buildAndValidate()
    }

    @Test
    void compileSolidityWithVersion() {
        buildFile << """
            plugins {
               id 'org.web3j.solidity'
            }
            sourceSets {
               main {
                   solidity {
                       srcDir '$sourceDir.absolutePath'
                       exclude 'common/**'
                       exclude 'subdir/**'
                   }
               }
            }
            solidity {
                version = '0.4.10'
            }
            repositories {
                mavenCentral()
            }
        """
        buildAndValidate()
    }

    @Test
    void compileSolidityWithExecutable() {
        buildFile << """
            plugins {
               id 'org.web3j.solidity'
            }
            sourceSets {
               main {
                   solidity {
                       srcDir '$sourceDir.absolutePath'
                       exclude 'common/**'
                       exclude 'subdir/**'
                   }
               }
            }
            solidity {
                executable = 'solc'
            }
            repositories {
                mavenCentral()
            }
        """
        buildAndValidate()
    }

    @Test
    void compileSolidityWithDockerExecutable() {
        buildFile << """
            plugins {
               id 'org.web3j.solidity'
            }
            sourceSets {
               main {
                   solidity {
                       srcDir '$sourceDir.absolutePath'
                       output.resourcesDir = file('.')
                       exclude 'common/**'
                       exclude 'subdir/**'
                   }
               }
            }
            solidity {
                executable = 'docker run --rm -v $sourceDir.absolutePath:/src satran004/aion-fastvm:0.3.1 solc'
                version = '0.4.15'
            }
            repositories {
                mavenCentral()
            }
        """
        buildAndValidate()
    }

    private void buildAndValidate() {

        def compileSolidity = GradleRunner.create()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("build")
                .withPluginClasspath()
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

        def excludedAbi = new File(compiledSolDir, "Ownable.abi")
        assertFalse(excludedAbi.exists())

        def upToDate = compileSolidity.build()
        assertEquals(UP_TO_DATE, upToDate.task(":compileSolidity").outcome)
    }
}
