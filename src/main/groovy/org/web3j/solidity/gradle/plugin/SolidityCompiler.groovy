package org.web3j.solidity.gradle.plugin

import groovy.transform.CompileStatic
import org.gradle.internal.os.OperatingSystem

import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Based on EthereumJ bundled Solidity compiler at
 * https://github.com/ethereum/ethereumj/blob/develop/ethereumj-core/src/main/java/org/ethereum/solidity/compiler/Solc.java
 */
@CompileStatic
class SolidityCompiler {

    private File solc = null
    private ClassLoader classLoader

    SolidityCompiler() {
        this(null)
    }

    SolidityCompiler(final ClassLoader classLoader) {
        this.classLoader = classLoader
        try {
            init()
        } catch (IOException e) {
            throw new RuntimeException('Can\'t init solc compiler: ', e)
        }
    }

    File getExecutable() {
        return solc
    }

    private void init() throws IOException {
        if (OperatingSystem.current().isWindows() && !checkWindowsVcppExists(System.getenv("PATH"))) {
            throw new IOException("Visual C++ Redistributable is not installed please download it from: https://www.microsoft.com/en-us/download/details.aspx?id=52685")
        }

        final def tmpDir = new File(System.getProperty('java.io.tmpdir'), 'solc')
        tmpDir.mkdirs()

        final def binariesPath = "native/${getSystemDirectory()}/solc"
        final def stream = getResourceAsStream("$binariesPath/file.list")

        if (stream != null) {
            new Scanner(stream as InputStream).withCloseable {
                while (it.hasNext()) {
                    final def scan = it.next()
                    final def targetFile = new File(tmpDir, scan)
                    targetFile.deleteOnExit()

                    final def fis = getResourceAsStream("$binariesPath/$scan")
                    Files.copy(fis, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)

                    if (solc == null) {
                        // first file in the list denotes executable
                        solc = targetFile
                        solc.setExecutable(true)
                    }
                }
            }
        }
    }

    static boolean checkWindowsVcppExists(String pathVariable) {
        return Arrays.stream(pathVariable.split(";")).anyMatch({ p -> new File(p + File.separator + "vcruntime140.dll").exists() })
    }

    private InputStream getResourceAsStream(final String name) {
        if (classLoader != null) {
            return classLoader.getResourceAsStream(name)
        } else {
            return getClass().getResourceAsStream("/$name")
        }
    }

    private static String getSystemDirectory() {
        if (OperatingSystem.current().isWindows()) {
            return 'win'
        } else if (OperatingSystem.current().isLinux()) {
            return 'linux'
        } else if (OperatingSystem.current().isMacOsX()) {
            return 'mac'
        } else {
            throw new RuntimeException("Can't find solc compiler: unrecognized OS: ${OperatingSystem.current().name}")
        }
    }
}