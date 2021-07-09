/*
 * Copyright 2021 Web3 Labs Ltd.
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

import groovy.transform.Memoized

/**
 * Helper class to resolve the external imports from a Solidity file.
 *
 * Supported providers are:
 * <ul>
 *     <li><a href="https://www.npmjs.com/package/@openzeppelin/contracts">Open Zeppelin</a></li>
 *     <li><a href="https://www.npmjs.com/package/@uniswap/lib">Uniswap</a></li>
 * </ul>
 */
@Singleton
class ImportsResolver {

    private Set<String> PROVIDERS = ["@openzeppelin/contracts", "@uniswap/lib"]

    /**
     * Looks for external imports in Solidity files, eg:
     * <br>
     * <p>
     * <code>
     * import "@openzeppelin/contracts/token/ERC721/ERC721.sol";
     * </code>
     * </p>
     *
     * @param solFile where to search external imports
     * @param nodeProjectDir the Node.js project directory
     * @return
     */
    @Memoized
    Map<String, String> resolveImports(final File solFile, final File nodeProjectDir) {
        final Map<String, String> imports = [:]
        PROVIDERS.forEach { String provider ->
            def importFound = !solFile.readLines().findAll {
                it.contains(provider)
            }.isEmpty()
            if (importFound) {
                imports.put(provider, "$nodeProjectDir.path/node_modules/$provider")
            }
        }
        return imports
    }
}
