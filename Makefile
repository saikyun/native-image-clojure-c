clean:
	-rm classes/*
	-rm *.so
	-rm woop
	-rm src/c/*
	-rm -r target

sdl_starter:
	$(LLVM_TOOLCHAIN)/clang src/sdl_starter.c -lsdl2 -c -emit-llvm


sdl_starter_ni:
	clang -shared -o libsdl_starter.so src/sdl_starter.c -Isdl_starter.h -lsdl2


c/sdl.clj:
	lein exec -p src/create_sdl_ns.clj
	-rm -r target

polyglot: sdl_starter c/sdl.clj
	lein run

ni: sdl_starter_ni
	./compile && ./woop
