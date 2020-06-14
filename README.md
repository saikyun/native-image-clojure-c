# clojure calling c -- compiled to a binary with native-image

## Steps
```
$ make clean CFiles && ./compile && ./woop
1 # this value comes from a c-struct!
```

## Acknowledgements

Java code from: https://github.com/cornerwings/graal-native-interaction
Project setup for clojure / native compilation from: https://github.com/borkdude/clj-kondo

Thanks sogaiu for helping me get this working. :)
