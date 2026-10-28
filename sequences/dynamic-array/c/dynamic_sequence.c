#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include "sequence.h"



// Initial capacity of the array (override with -DINITIAL_CAPACITY=n)
#ifndef INITIAL_CAPACITY
#define INITIAL_CAPACITY 4
#endif

const double GROWTH_THRESHOLD = 1.0;
const double GROWTH_FACTOR = 2.0;

const double SHRINK_THRESHOLD = 0.5;
const double SHRINK_FACTOR = 0.5;

struct sequence_s {
  int    capacity;
  int    length;
  void** items;
};

double load_factor(Sequence*);

void resize(Sequence*, double);

Sequence* seq_create(void) {
  Sequence* new_sequence = malloc(sizeof(Sequence));
  new_sequence->capacity = INITIAL_CAPACITY;
  new_sequence->length   = 0;
  new_sequence->items    = malloc(INITIAL_CAPACITY * sizeof(void*));
  return new_sequence;
}

void
seq_destroy(Sequence* sequence) {
  assert(sequence != NULL);
  free(sequence->items);
  free(sequence);
}

int
seq_length(Sequence* sequence) {
  assert(sequence != NULL);
  return sequence->length;
}

void*
seq_get(Sequence* sequence, int index) {
  assert(sequence != NULL);
  assert(index > 0 && index <= sequence->length);
  return sequence->items[index];
}

void
seq_insert(Sequence* sequence, void* item, int index) {
  assert(sequence != NULL);
  assert(index > 0 && index <= sequence->length + 1);
  if (load_factor(sequence) >= GROWTH_THRESHOLD) {
    resize(sequence, GROWTH_FACTOR);
  }
  for (int i=sequence->length ; i>=index ; i--) {
    sequence->items[i+1] = sequence->items[i];
  }
  sequence->items[index] = item;
  sequence->length++;
}

double
load_factor(Sequence* sequence) {
  assert(sequence != NULL);
  return sequence->length / sequence->capacity;
}


void
resize(Sequence* sequence, double factor) {
  assert(sequence != NULL);
  assert(factor > 0);
  if (sequence->capacity > 1 || factor >= 1) {
    sequence->capacity = (int) sequence->capacity * factor;
    void** old_array = sequence->items;
    void** new_array = malloc((unsigned long) sequence->capacity * sizeof(void*));
    for(int i=0 ; i<sequence->length ; i++) {
      new_array[i] = old_array[i];
    }
    free(old_array);
    sequence->items = new_array;
  }
}

void
seq_remove(Sequence* sequence, int index) {
  assert(sequence != NULL);
  assert(index > 0 && index <= sequence->length + 1);
  if (load_factor(sequence) < SHRINK_THRESHOLD) {
    resize(sequence, SHRINK_FACTOR);
  }
  for(int i=index ; i<sequence->length ; i++) {
    sequence->items[i] = sequence->items[i+1];
  }
  sequence->items[sequence->length] = NULL;
  sequence->length--;
}


int
seq_search(Sequence* sequence, void* item) {
  assert(sequence != NULL);
  int found = 0;
  int index = 1;
  while (!found && index <= sequence->length) {
    void* current = seq_get(sequence, index);
    if (current == item) {
      found = index;
    }
    index++;
  }
  return found;
}


void
show(Sequence* sequence) {
  assert(sequence != NULL);
  printf("(");
  for (int i=1 ; i<=sequence->length ; i++) {
    printf("%s", (char*) seq_get(sequence, i));
    if (i < sequence->length) {
      printf(", ");
    }
  }
  printf(") %d item(s)\n", sequence->length);
}



/* int main(int argc, char** argv) { */
/*   char* perry = "Perry"; */
/*   Sequence* seq = seq_create(); */

/*   for (int i=0 ; i< */

/*   seq_insert(seq, (char*) "Hugo", 1); */
/*   seq_insert(seq, (char*) "Lisa", 1); */
/*   seq_insert(seq, (char*) "John", 2); */
/*   seq_insert(seq, perry, 4); */
/*   seq_insert(seq, (char*) "Franck", 3); */
/*   seq_remove(seq, 3); */
/*   printf("Perry at %d\n", seq_search(seq, perry)); */
/*   show(seq); */
/*   seq_destroy(seq); */
/* }
 */
