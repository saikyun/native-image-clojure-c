clean:
	-rm classes/*
	-rm *.so
	-rm woop

JavaFiles:
	javac -d classes/ src/Headers.java src/Main.java src/TripletLib.java src/Triple.java src/Value.java

CFiles:
	clang -shared -o libtriple.so src/triple.cc -Itriple.h

ni: JavaFiles CFiles
	native-image -cp ./classes2 --verbose -Djava.library.path=./classes2 -H:CLibraryPath=. Main
