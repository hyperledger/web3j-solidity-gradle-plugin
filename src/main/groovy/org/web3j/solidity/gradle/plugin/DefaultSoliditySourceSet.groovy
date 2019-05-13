package org.web3j.solidity.gradle.plugin

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.SourceDirectorySetFactory
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
            final SourceDirectorySetFactory setFactory) {

        solidity = setFactory.create(NAME, displayName + " Solidity Sources")
        solidity.getFilter().include("**/*.sol")
        allSolidity = setFactory.create(displayName + " Solidity Sources")
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
