#ifndef SEQUENCE_H
#define SEQUENCE_H

typedef struct sequence_s Sequence;

Sequence* seq_create(void);

void seq_destroy(Sequence*);

int seq_length(Sequence* sequence);

void* seq_get(Sequence* sequence, int index);

void seq_insert(Sequence* sequence, void* item, int index);

void seq_remove(Sequence* sequence, int index);

int seq_search(Sequence* sequence, void* item);

#endif
