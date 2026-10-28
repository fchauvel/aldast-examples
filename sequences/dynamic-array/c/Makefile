CFLAGS = -Wall

all: blowout-fixed blowout-dynamic

# Fixed capacity (100 items): the program aborts on the 101st insertion
blowout-fixed: blowout.c fixed_sequence.c sequence.h
	$(CC) $(CFLAGS) -o $@ blowout.c fixed_sequence.c

# Growing array: the program inserts 200 items without trouble
blowout-dynamic: blowout.c dynamic_sequence.c sequence.h
	$(CC) $(CFLAGS) -o $@ blowout.c dynamic_sequence.c

clean:
	rm -f blowout-fixed blowout-dynamic
