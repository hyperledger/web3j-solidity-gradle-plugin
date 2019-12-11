package org.web3j.solidity.gradle.plugin

import groovy.transform.CompileStatic

@CompileStatic
enum CombinedOutputComponent {
    ABI,
    ASM,
    AST,
    BIN,
    BIN_RUNTIME,
    CLONE_BIN,
    COMPACT_FORMAT,
    DEVDOC,
    HASHES,
    INTERFACE,
    METADATA,
    OPCODES,
    SRCMAP,
    SRCMAP_RUNTIME,
    USERDOC

    @Override
    String toString() {
        return name().replaceAll('_', '-').toLowerCase()
    }

}