/*
 * fnf_dict.c
 *
 *  Created on: Sep 21, 2024
 *      Author: Jan Atle Ramsli
 *
 */

// #define DEBUG

#include <stdlib.h>
#include <string.h>

#include "dictionary.h"

#include "logg.h"
#include "program.h"
#include "smtok.h"

dict_p Default_dict;

void dict_init(void) { Default_dict = dict_create("DEFAULT"); }

dict_p dict_create(char *name) {
  logg(name, "");
  dict_p rv = malloc(sizeof(dict_t));
  rv->name = malloc(strlen(name) + 1);
  rv->dep_array = malloc(sizeof(dict_entry_p *));
  strcpy(rv->name, name);
  return rv;
}

dict_entry_p dict_loop(dict_p dp, int (*callback)(dict_entry_p, void *),
                       void *parm) {
  if (!dp) {
    dp = Default_dict;
  }
  for (int i = 0; i < dp->ndep_array; i++) {
    if (callback(dp->dep_array[i], parm))
      return (dp->dep_array[i]);
  }
  return 0;
}

static inline int cb_lookup(dict_entry_p e, void *p) {
  logg((char *)p, "");
  return (strcmp(e->name, (char *)p) == 0) ? 1 : 0;
}

dict_entry_p dict_lookup(dict_p dp, char *key) {
  logg("LOOKUP", key);
  if (!dp) {
    dp = Default_dict;
  }
  return dict_loop(dp, cb_lookup, (void *)key);
}

static int cb_dict_dump(dict_entry_p dep, void *p) {
  printf("%s:%s==>", dep->name, dep->prog->name);

  for (int i = 0; i < dep->prog->npcp_array; i++) {

    smtok_p pcp = dep->prog->pcp_array[i];

    switch (pcp->jtidx) {
    case SMTOK_NUMBER:
      printf("NUM:%ld ", pcp->val.l);
      break;
    case SMTOK_BUILTIN:
      printf("BIN:%s ", pcp->name);
      break;
    case SMTOK_VARIABLE:
      printf("VAR:%s=%ld ", pcp->name, pcp->val.l);
      break;
    case SMTOK_DICT_ENTRY:
      printf("DEF:%s ", pcp->name);
      // program_dump(pcp->val.prog, p);
      break;
    case SMTOK_LOOP_DO:
      printf("DO:%s ", pcp->name);
      break;
    case SMTOK_LOOP_END:
      printf("LOOP:%s ", pcp->name);
      break;
    case SMTOK_IF:
      printf("IF:%s ", pcp->name);
      break;
    case SMTOK_ERROR:
      printf("ERR:%s ", pcp->name);
      break;
    case SMTOK_I:
      printf("I:%s ", pcp->name);
      break;
    case SMTOK_DEFER:
      printf("' %s", pcp->name);
      break;
    case SMTOK_ELSE:
      printf("ELSE:%s ", pcp->name);
      break;
    case SMTOK_EXEC:
      printf("EXEC:%s ", pcp->name);
      break;
    case SMTOK_EXIT:
      printf("EXIT:%s ", pcp->name);
      break;
    case SMTOK_SPAWN:
      printf("SPWN:%s ", pcp->name);
      break;
    case SMTOK_STRING:
      printf("STR:%s ", pcp->val.s);
      break;
    case SMTOK_THEN:
      printf("THEN:%s ", pcp->name);
      break;
    default:
      printf("SMTOK%d? ", pcp->jtidx);
      break;
    }
  }
  printf("\n");
  return 0;
}

void dict_dump(dict_p dp) {
  logg("", "");
  if (!dp) {
    dp = Default_dict;
  }
  int i = 0;
  printf("\n---------------| DICT |--------------\n");
  dict_loop(dp, cb_dict_dump, &i);
  printf("------------------------------------\n");
}

void dict_add_entry(dict_p dp, dict_entry_p dep) {
  logg(dep->name, "");
  if (!dp) {
    dp = Default_dict;
  }

  dict_entry_p dep_exist = dict_lookup(dp, dep->name);
  if (dep_exist) {
    dep_exist = dep;
    return;
  }

  dp->ndep_array++;
  dp->dep_array[dp->ndep_array - 1] = dep;
//  printf("\nReallocating %ld bytes: ", sizeof(dict_entry_p) * dp->ndep_array);
  dp->dep_array = realloc(dp->dep_array, sizeof(dict_entry_p) * (dp->ndep_array+1));
#ifdef DEBUG
  dict_dump(dp);
#endif
}

dict_entry_p dict_entry_create(dict_p dp, char *name) {
  logg(name, "");
  if (!dp) {
    dp = Default_dict;
  }
  dict_entry_p rv = malloc(sizeof(dict_entry_t));
  rv->prog = program_create(name);
  rv->name = malloc(strlen(name));
  strcpy(rv->name, name);

  return rv;
}

void dict_entry_add_word(dict_entry_p dep, char *word) {
  logg(dep->name, word);
  program_add(dep->prog, word);
}
