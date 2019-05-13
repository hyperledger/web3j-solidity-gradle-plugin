package org.web3j.solidity.gradle.plugin

import groovy.transform.CompileStatic

@CompileStatic
enum EVMVersion {
    HOMESTEAD('homestead'),
    TANGERINE_WHISTLE('tangerineWhistle'),
    SPURIOUS_DRAGON('spuriousDragon'),
    BYZANTIUM('byzantium'),
    CONSTANTINOPLE('constantinople')

    private String value

    EVMVersion(String value) {
        this.value = value
    }

    String getValue() {
        return value
    }
}
