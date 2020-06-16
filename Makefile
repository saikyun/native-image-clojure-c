clean:
	-rm classes/*
	-rm *.so
	-rm woop
	-rm src/c/*
	-rm target

sdl_starter:
	$(LLVM_TOOLCHAIN)/clang src/sdl_starter.c -lsdl2 -c -emit-llvm

c/sdl.clj:
	lein exec -p src/create_sdl_ns.clj

polyglot: sdl_starter c/sdl.clj
	lein run

ni: JavaFiles CFiles
	native-image -cp ./classes2 --verbose -Djava.library.path=./classes2 -H:CLibraryPath=. Main
