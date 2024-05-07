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
import org.gradle.api.Action
import org.gradle.api.file.SourceDirectorySet

/**
 * Source set for Solidity classes in a Gradle project.
 */
@CompileStatic
interface SoliditySourceSet {

    /**
     * Returns the source to be compiled by the Solidity compiler for this source set.
     * This may contain both Java and Solidity source files.
     *
     * @return The Solidity source. Never returns null.
     */
    SourceDirectorySet getSolidity()

    /**
     * Configures the Solidity source for this set.
     *
     * <p>The given closure is used to configure the {@link SourceDirectorySet}
     * which contains the Solidity source.
     *
     * @param configureClosure The closure to use to configure the Solidity source.
     * @return this
     */
    SoliditySourceSet solidity(Closure configureClosure)

    /**
     * Configures the Solidity source for this set.
     *
     * <p>The given action is used to configure the {@link SourceDirectorySet}
     * which contains the Solidity source.
     *
     * @param configureAction The action to use to configure the Solidity source.
     * @return this
     */
    SoliditySourceSet solidity(Action<? super SourceDirectorySet> configureAction)

    /**
     * All Solidity source for this source set.
     *
     * @return the Solidity source. Never returns null.
     */
    SourceDirectorySet getAllSolidity()

    String NAME = "solidity"

    // New method to set the EVM version for the Solidity compiler
    void setEvmVersion(EVMVersion evmVersion)

    // New method to get the EVM version for the Solidity compiler
    EVMVersion getEvmVersion()

}
