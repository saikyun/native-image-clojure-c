# clojure calling c -- compiled to a binary with native-image

## Prerequisites

* graalvm -- tested with `graalvm-ce-java11-20.2.0-dev`: https://github.com/graalvm/graalvm-ce-dev-builds/releases
  * download, then add the following to e.g. .zprofile
  * `export GRAALVM_HOME = "/path/to/graalvm-ce-java11-20.2.0-dev/Contents/Home/"`
  * `export JAVA_HOME = $GRAALVM_HOME`
* install native-image: `$GRAALVM_HOME/bin/gu install native-image` (will be installed by `./compile` otherwise)
* llvm toolchain -- https://www.graalvm.org/docs/reference-manual/languages/llvm/
  * export LLVM_TOOLCHAIN as in the instructions
* leiningen -- https://leiningen.org/

I've only tested on mac.

## Steps for polyglot

You need libSDL2 on your path and in `:resource-paths` in your `project.clj`.

```
make polyglot # this will also run `lein run`
```

## Steps for native-image

Modify `src/Headers.java`, specifically: `/Users/test/programmering/clojure/graal-native-interaction/graal/src/triple.h` needs to point to your `src/triple.h`.

```
$ make clean CFiles && ./compile && ./woop
1 # this value comes from a c-struct!
```

## Acknowledgements

Java code from: https://github.com/cornerwings/graal-native-interaction
Project setup for clojure / native compilation from: https://github.com/borkdude/clj-kondo

Thanks sogaiu for helping me get this working. :)
