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
