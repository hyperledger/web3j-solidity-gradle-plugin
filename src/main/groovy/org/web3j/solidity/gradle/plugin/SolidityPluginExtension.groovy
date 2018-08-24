package org.web3j.solidity.gradle.plugin

class SolidityPluginExtension {

    static final NAME = 'solidity'

    boolean optimize

    int optimizeRuns

    boolean prettyJson

    File outputDir

    boolean overwrite

    OutputComponent[] outputComponents

    enum OutputComponent {
        AST,
        AST_JSON,
        AST_COMPACT_JSON,
        ASM,
        ASM_JSON,
        OPCODES,
        BIN,
        BIN_RUNTIME,
        CLONE_BIN,
        ABI,
        HASHES,
        USERDOC,
        DEVDOC,
        METADATA,
        FORMAL
    }

}
