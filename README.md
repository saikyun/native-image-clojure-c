# clojure calling c -- compiled to a binary with native-image

## Prerequisites

* graalvm -- tested with `graalvm-ce-java11-20.2.0-dev`: https://github.com/graalvm/graalvm-ce-dev-builds/releases
** set `GRAALVM_HOME = /path/to/graalvm-ce-java11-20.2.0-dev/Contents/Home/`
** set `JAVA_HOME = $GRAALVM_HOME`
* leiningen -- https://leiningen.org/

I've only tested on mac.

## Steps

Modify `src/Headers.java`, specifically: `/Users/test/programmering/clojure/graal-native-interaction/graal/src/triple.h` needs to point to your `src/triple.h`.

```
$ make clean CFiles && ./compile && ./woop
1 # this value comes from a c-struct!
```

## Acknowledgements

Java code from: https://github.com/cornerwings/graal-native-interaction
Project setup for clojure / native compilation from: https://github.com/borkdude/clj-kondo

Thanks sogaiu for helping me get this working. :)
