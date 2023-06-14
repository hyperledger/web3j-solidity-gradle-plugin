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

@CompileStatic
enum EVMVersion {
    HOMESTEAD('homestead'),
    TANGERINE_WHISTLE('tangerineWhistle'),
    SPURIOUS_DRAGON('spuriousDragon'),
    BYZANTIUM('byzantium'),
    CONSTANTINOPLE('constantinople'),
    PETERSBURG('petersburg'),
    ISTANBUL('istanbul'),
    BERLIN('berlin'),
    LONDON('london'),
    PARIS('paris'),
    SHANGHAI('shanghai')

    private String value

    EVMVersion(String value) {
        this.value = value
    }

    String getValue() {
        return value
    }
}
