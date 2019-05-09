package org.web3j.solidity.gradle.plugin

/**
 * SolcJ available versions as per
 * https://bintray.com/ethereum/maven/org.ethereum.solcJ-all/.
 */
enum SolcVersion {
    v0_5_7('0.5.7'),
    v0_5_6('0.5.6'),
    v0_5_2('0.5.2'),
    v0_4_25('0.4.25'),
    v0_4_24('0.4.24'),
    v0_4_23('0.4.23'),
    v0_4_19('0.4.19'),
    v0_4_10('0.4.10'),
    v0_4_8('0.4.8'),
    v0_4_7('0.4.7'),
    v0_4_6('0.4.6'),
    v0_4_4('0.4.4'),
    v0_4_3('0.4.3')

    private String value

    SolcVersion(String value) {
        this.value = value
    }

    String getValue() {
        return value
    }
}
