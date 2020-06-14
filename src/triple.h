#include <stdlib.h>
#include <stdio.h>
#include <inttypes.h>

typedef enum type_t {
	I,
	F,
	S
} type_t;

typedef struct value_t {
	type_t type;
	uint64_t id;
} value_t;

typedef struct triple_t {
    value_t subject;
    value_t predicate;
    value_t object;
} triple_t;
