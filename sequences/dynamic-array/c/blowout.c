#include <stdio.h>
#include "sequence.h"

int main(int argc, char** argv) {

  Sequence* seq = seq_create();

  int test = 23;
  for (int i=0 ; i<200 ; i++) {
    printf("%d ", i);
    seq_insert(seq, (void*) &test, seq_length(seq) + 1);
  }

  printf("Done.\n");
  seq_destroy(seq);
}
