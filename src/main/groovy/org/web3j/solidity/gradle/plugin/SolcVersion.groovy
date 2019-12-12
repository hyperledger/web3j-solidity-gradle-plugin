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

/**
 * SolcJ available versions as per
 * https://bintray.com/ethereum/maven/org.ethereum.solcJ-all/.
 */
@CompileStatic
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
