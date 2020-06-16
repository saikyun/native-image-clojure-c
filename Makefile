clean:
	-rm classes/*
	-rm *.so
	-rm woop
	-rm src/c/*

sdl_starter:
	$(LLVM_TOOLCHAIN)/clang src/sdl_starter.c -lsdl2 -c -emit-llvm


sdl_starter_ni:
	clang -shared -o libsdl_starter.so src/sdl_starter.c -Isdl_starter.h -lsdl2


c/sdl.clj:
	lein exec -p src/create_sdl_ns.clj

polyglot: sdl_starter c/sdl.clj
	lein run

ni: sdl_starter
	native-image --verbose -H:CLibraryPath=. Main
