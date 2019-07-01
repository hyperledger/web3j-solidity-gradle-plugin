Solidity Gradle Plugin
======================

Simple Gradle plugin used by the [web3j plugin](https://github.com/web3j/web3j-gradle-plugin) 
to compile Solidity contracts, but it can be used in any standalone project for this purpose.

## Plugin configuration

The minimum Gradle version to run the plugin is `5.+`.

### Using the `buildscript` convention

To install the Solidity Plugin using the old Gradle `buildscript` convention, you should add 
the following to the first line of your build file (at the moment only release versions 
are supported in Gradle, not SNAPSHOT):

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'org.web3j:solidity-gradle-plugin:0.1.6'
    }
}

apply plugin: 'solidity'
```

### Using the plugins DSL

Alternatively, if you are using the more modern plugins DSL, add the following line to your 
build file:

```groovy
plugins {
    id 'solidity' version '0.1.6'
}
```

You will need to add the following configuration in the first line of your `settings.gradle` 
file to resolve the artifact from the Epiphyte repository.

```groovy
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
```

Then run this command from your project containing Solidity contracts:

```
./gradlew build
```

After the task execution, the base directory for compiled code (by default 
`$buildDir/resources/solidity`) will contain a directory for each source set 
(by default `main` and `test`), and each of those a directory with the compiled code.


## Code generation

The `solidity` DSL allows to configure the generated code, e.g.:

```groovy
solidity {
    outputComponents = [BIN, ABI, ASM_JSON]
    optimizeRuns = 500
}
```

The properties accepted by the DSL are listed in the following table:

|  Name              | Type                | Default value                            | Description                 |
|--------------------|:-------------------:|:----------------------------------------:|-----------------------------|
| `executable`       | `String`            | `null` (bundled with the plugin)         | Solidity compiler path.     |
| `version`          | `String`            | `0.4.25`                                 | Solidity compiler version.  |
| `overwrite`        | `Boolean`           | `true`                                   | Overwrite existing files.   |
| `optimize`         | `Boolean`           | `true`                                   | Enable byte code optimizer. |
| `optimizeRuns`     | `Integer`           | `200`                                    | Set for how many contract runs to optimize. |
| `prettyJson`       | `Boolean`           | `false`                                  | Output JSON in pretty format. Enables the combined JSON output. |
| `ignoreMissing`    | `Boolean`           | `false`                                  | Ignore missing files. |
| `allowPaths`       | `List<String>`      | `['src/main/solidity', 'src/test/solidity', ...]` | Allow a given path for imports. |
| `evmVersion`       | `EVMVersion`        | `BYZANTIUM`                              | Select desired EVM version. |
| `outputComponents` | `OutputComponent[]` | `[BIN, ABI]`                             | List of output components to produce. |

**Notes:** 
  - Setting the `executable` property will disable the bundled `solc` and use your local or containerized executable:
  ```groovy
    solidity {
        executable = "docker run --rm -v $projectDir:/src satran004/aion-fastvm:latest solc"
        version = '0.4.15'
    }
  ```
  - Use `version` to change the bundled Solidity version. 
    Check the [SolJ distributions](https://bintray.com/ethereum/maven/org.ethereum.solcJ-all/) 
    for all available versions.
  - `allowPaths` contains all project's Solidity source sets by default.

## Source sets

By default, all `.sol` files in `$projectDir/src/main/solidity` will be processed by the plugin.
To specify and add different source sets, use the `sourceSets` DSL. You can also set your preferred
output directory for compiled code.

```groovy
sourceSets {
    main {
        solidity {
            srcDir {
                "my/custom/path/to/solidity"
             }
             output.resourcesDir = file('out/bin/compiledSol') 
        }
    }
}
```

## Plugin tasks

The [Java Plugin](https://docs.gradle.org/current/userguide/java_plugin.html)
adds tasks to your project build using a naming convention on a per source set basis
(i.e. `compileJava`, `compileTestJava`).

Similarly, the Solidity plugin will add the `compileSolidity` task for the project `main`
source set, and a `compile[SourceSet]Solidity` for each remaining source set (e.g. `test`). 

To obtain a list and description of all added tasks, run the command:

```
./gradlew tasks --all
```