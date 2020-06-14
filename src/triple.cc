#include "triple.h"

static uint64_t counter = 1;

extern "C" {

	triple_t* allocRandomTriple() {
	    triple_t *triple = (triple_t*) malloc(sizeof(triple_t));
	    triple->subject.id = counter++;
	    triple->predicate.id = counter++;
	    triple->object.id = counter++;
	    return triple;
	}

	void freeTriple(triple_t *triple) {
	    free(triple);
	}

}
