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
import org.gradle.api.internal.file.SourceDirectorySetFactory
import org.gradle.api.model.ObjectFactory
import org.gradle.api.reflect.HasPublicType
import org.gradle.api.reflect.TypeOf
import org.gradle.util.ConfigureUtil

/**
 * SoliditySourceSet default implementation.
 */
@CompileStatic
class DefaultSoliditySourceSet implements SoliditySourceSet, HasPublicType {

    private final SourceDirectorySet solidity
    private final SourceDirectorySet allSolidity

    DefaultSoliditySourceSet(
            final String displayName,
            final ObjectFactory objectFactory) {

        final String sourceDirectoryDisplayName = displayName + " Solidity Sources"
        solidity = objectFactory.sourceDirectorySet(NAME, sourceDirectoryDisplayName)
        solidity.getFilter().include("**/*.sol")
        allSolidity = objectFactory.sourceDirectorySet(sourceDirectoryDisplayName, sourceDirectoryDisplayName)
        allSolidity.getFilter().include("**/*.sol")
        allSolidity.source(solidity)
    }

    @Override
    SourceDirectorySet getSolidity() {
        return solidity
    }

    @Override
    SoliditySourceSet solidity(final Closure configureClosure) {
        ConfigureUtil.configure(configureClosure, getSolidity())
        return this
    }

    @Override
    SoliditySourceSet solidity(final Action<? super SourceDirectorySet> configureAction) {
        configureAction.execute(getSolidity())
        return this
    }

    @Override
    SourceDirectorySet getAllSolidity() {
        return allSolidity
    }

    @Override
    TypeOf<?> getPublicType() {
        return TypeOf.typeOf(SoliditySourceSet.class)
    }

}
