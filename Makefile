clean:
	-rm classes/*
	-rm *.so
	-rm woop
	-rm src/c/*
	-rm -r target
	-rm -r libs

info:
	native-image --expert-options-all

sdl_starter:
	$(LLVM_TOOLCHAIN)/clang src/sdl_starter.c -lSDL2 -c -emit-llvm

sdl_starter_ni:
	clang -shared -o libsdl_starter.so src/sdl_starter.c -Isdl_starter.h -lSDL2 -fPIC

c/sdl.clj:
	lein exec -ep "(require '[create-sdl-ns]) (create-sdl-ns/-main)"
	-rm -r target

polyglot: sdl_starter c/sdl.clj
	lein with-profiles runner run

run-p:
	lein with-profiles runner run

ni: sdl_starter_ni
	./compile && ./woop
