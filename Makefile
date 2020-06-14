clean:
	-rm classes/*
	-rm *.so

JavaFiles:
	javac -d classes/ src/Headers.java src/Main.java

CFiles:
	clang -shared -o libtriple.so src/triple.cc -Itriple.h

ni: JavaFiles CFiles
	native-image -cp ./classes --verbose -Djava.library.path=./classes -H:CLibraryPath=. Main
