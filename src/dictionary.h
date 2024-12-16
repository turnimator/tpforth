/*
 * fnf_dict.h
 *
 *  Created on: Sep 21, 2024
 *      Author: Jan Atle Ramsli
 *
 */

#ifndef DICTIONARY_H_
#define DICTIONARY_H_

/**
A : definition creates a dict_entry and adds it to the dictionary
*/
#include "task.h"
typedef struct dict_entry {
  char *name;
  program_p prog;
} dict_entry_t, *dict_entry_p;

typedef struct dictionary {
  char *name;
  dict_entry_p *dep_array;
  int ndep_array;
} dict_t, *dict_p;

void dict_init(void);
void dict_dump(dict_p dp);
dict_p dict_create(char *);
dict_entry_p dict_entry_create(dict_p, char *name);
void dict_entry_add_word(dict_entry_p de, char *);
dict_entry_p dict_lookup(dict_p, char *);
void dict_add_entry(dict_p, dict_entry_p);
void program_add_dict_entry(program_p prog, dict_entry_p de);
dict_entry_p dict_loop(dict_p dp, int (*callback)(dict_entry_p, void *),
                       void *parm);
#endif /* DICTIONARY_H_ */
