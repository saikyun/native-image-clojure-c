clean:
	-rm woop
	-rm -r src/bindings
	-rm -r target
	-rm -r libs/*

info:
	native-image --expert-options-all

bindings:
	lein exec -ep "(require '[create-sdl-ns]) (create-sdl-ns/-main)"
	-rm -r target

run-p:
	lein with-profiles runner do clean, run

ni:
	NATIVE_IMAGE=true ./compile && LD_LIBRARY_PATH=./libs ./woop
