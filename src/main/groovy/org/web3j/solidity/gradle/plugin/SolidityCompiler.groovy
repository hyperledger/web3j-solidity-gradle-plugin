package org.web3j.solidity.gradle.plugin

import org.gradle.internal.os.OperatingSystem

import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Based on EthereumJ bundled Solidity compiler at
 * https://github.com/ethereum/ethereumj/blob/develop/ethereumj-core/src/main/java/org/ethereum/solidity/compiler/Solc.java
 */
class SolidityCompiler {

    private File solc = null

    SolidityCompiler() {
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
        final def tmpDir = new File(System.getProperty('java.io.tmpdir'), 'solc')
        tmpDir.mkdirs()

        final def binariesPath = "/native/${getSystemDirectory()}/solc"
        final def stream = getClass().getResourceAsStream("$binariesPath/file.list")

        new Scanner(stream as InputStream).withCloseable {
            while (it.hasNext()) {
                final def scan = it.next()
                final def targetFile = new File(tmpDir, scan)
                targetFile.deleteOnExit()

                final def fis = getClass().getResourceAsStream("$binariesPath/$scan")
                Files.copy(fis, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)

                if (solc == null) {
                    // first file in the list denotes executable
                    solc = targetFile
                    solc.setExecutable(true)
                }
            }
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
