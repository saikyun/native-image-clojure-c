clean:
	-rm sdl_example
	-rm -r src/bindings
	-rm -r target
	-rm -r libs/*

info:
	native-image --expert-options-all

bindings:
	lein exec -ep "(require '[catamari.examples.sdl.create-sdl-lib]) (catamari.examples.sdl.create-sdl-lib/-main)"
	-rm -r target

poly: bindings
	lein with-profiles runner do clean, run

ni: bindings
	NATIVE_IMAGE=true ./compile && LD_LIBRARY_PATH=./libs ./sdl_example
