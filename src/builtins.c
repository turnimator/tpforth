/*
 * builtins.c
 *
 *  Created on: Sep 18, 2024
 *      Author: atle
 */

#include "builtins.h"

#include "custom.h"
#include "dictionary.h"
#include "n_queue.h"
#include "program.h"
#include "runtime.h"
#include "smtok.h"
#include "task.h"
#include "tp_queue.h"
#include "variable.h"
#include <malloc.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

builtin_p *DB_builtins;
int N_builtins = 0;

int STEP = 0;

idx_builtin_t *IDX_builtins = 0;

static void bdb_create() {
  DB_builtins = malloc(sizeof(builtin_p *));
  N_builtins = 0;
}

static void bdb_add(builtin_p bip) {
  bip->op = N_builtins;
  N_builtins++;
  DB_builtins = realloc(DB_builtins, sizeof(builtin_p *) * N_builtins);
  DB_builtins[bip->op] = bip;
}
//// RETURN (LOOPS)
void r_push(ftask_p task, smtok_p *p) {
  task->r_stack[task->r_top] = p;
  task->r_top++;
}

smtok_p *r_pop(ftask_p task) {
  task->r_top--;
  return task->r_stack[task->r_top];
}

smtok_p *r_tos(ftask_p task) { return task->r_stack[task->r_top - 1]; }

///// PROGRAM (SUBROUTINES=DICT_ENTRIES=COLON DEFS)///////
program_p prog_pop(ftask_p task) {
  task->prog_top--;
  return task->prog_stack[task->prog_top];
}

program_p prog_tos(ftask_p task) {
  return task->prog_stack[task->prog_top - 1];
}

void prog_push(ftask_p task, program_p p) {
  task->prog_stack[task->prog_top] = p;
  task->prog_top++;
}

static void d_string_exec(ftask_p task) {
  char *s = (char *)d_pop(task);
  dict_entry_p dictentry = dict_lookup(0, s);
  if (!dictentry) {
    printf("No such word: %s\n", s);
    return;
  }
  run_prog(task, dictentry->prog);
}

/**
Create a builtin
 */
builtin_p builtin_create(char *name, funcptr code) {
  builtin_p rv = malloc(sizeof(builtin_t));
  rv->name = name;
  rv->code = code;
  return rv;
}

////////////////////////// ------ LOOP STACK OPERATIONS --------------
///////////////////////
inline void lu_push(ftask_p task, long val) {
  task->loop_upper[task->lu_top] = val;
  task->lu_top++;
}

inline void ld_push(ftask_p task, long val) {
  task->ld[task->lu_top] = val;
  task->ld_top++;
}

inline long ld_pop(ftask_p task) {
  if (task->ld_top < 0) {
    printf("Data Stack Underflow -- press x to exit");
    if (getchar() == 'x') {
      exit(-1);
    }
    return 0;
  }
  long l = task->ld[task->d_top - 1];
  task->ld_top--;
  return l;
}

inline long lu_pop(ftask_p task) {
  if (task->lu_top < 0) {
    printf("Upper Loop Stack Underflow");
    printf(" -- press x to exit");
    if (getchar() == 'x') {
      exit(-1);
    }
    return 0;
  }
  long l = task->loop_upper[task->lu_top - 1];
  task->lu_top--;
  return l;
}

/*
Using macros for these
inline long ll_tos(ftask_p task) { return task->loop_lower[task->ll_top - 1]; }

inline long lu_tos(ftask_p task) { return task->loop_upper[task->lu_top - 1]; }
 */

inline long ll_pop(ftask_p task) {
  if (task->ll_top < 0) {
    printf("Lower Loop Stack Underflow");
    printf(" -- press x to exit");
    if (getchar() == 'x') {
      exit(-1);
    }
    return 0;
  }
  long l = task->loop_lower[task->ll_top - 1];
  task->ll_top--;
  return l;
}

long ld_tos(ftask_p task) { return task->ld[task->ld_top - 1]; }

inline void ll_push(ftask_p task, long val) {
  task->loop_lower[task->ll_top] = val;
  task->ll_top++;
}

/////////////////////////// ---- DATA STACK OPERATIONS -----
////////////////////////

inline void d_push(ftask_p task, long val) {
  task->d_stack[task->d_top] = val;
  task->d_top++;
}

inline long d_pop(ftask_p task) {
  if (task->d_top < 0) {
    printf("Data Stack Underflow -- press x to exit");
    if (getchar() == 'x') {
      exit(-1);
    }
    return 0;
  }
  long l = task->d_stack[task->d_top - 1];
  task->d_top--;
  return l;
}

long d_pick(ftask_p task, int num) {
  return task->d_stack[task->d_top - (num + 1)];
}

/***
if PICK is modeled after PEEK, then POCK must be modeled after POKE, right?
 */
void d_pock(ftask_p task, int num, long val) {
  task->d_stack[task->d_top - (num + 1)] = val;
}

long d_tos(ftask_p task) { return task->d_stack[task->d_top - 1]; }
/*
void v_push(ftask_p task, int i) {
  task->v_stack[task->v_top] = i;
  task->v_top++;
}

int v_pop(ftask_p task) {
  if (task->v_top <= 0) {
    printf("\n****VariableStack Underflow***");
    printf(" -- press x to exit");
    if (getchar() == 'x') {
      exit(-1);
    }
    return 0;
  }
  int i = task->v_stack[task->v_top - 1];
  task->v_top--;
  return i;
}

inline int v_tos(ftask_p task) { return task->v_stack[task->v_top - 1]; }
*/

static inline void d_step(ftask_p task) {
  int l1 = d_pop(task);
  if (l1 == 0) {
    STEP = 1;
    printf("\nPress x to exit stepping ...\n==>");
  } else {
    STEP = 0;
  }
}

////////// ------ DATA STACK ARITHMETIC / LOGIC OPERATIONS ----------
/////////////////////////////

static inline void d_gt(ftask_p task) {
  long l1 = d_pop(task);
  long l2 = d_pop(task);
  d_push(task, (l2 > l1) ? 0 : 1);
}

static inline void d_and(ftask_p task) {
  long l1 = d_pop(task);
  long l2 = d_pop(task);
  d_push(task, (!l2 && !l1) ? 0 : 1);
}

static inline void d_or(ftask_p task) {
  long l1 = d_pop(task);
  long l2 = d_pop(task);
  d_push(task, (!l2 || !l1) ? 0 : 1);
}

static inline void d_not(ftask_p task) {
  long l1 = d_pop(task);
  d_push(task, (l1 == 0) ? 1 : 0);
}

static inline void d_lt(ftask_p task) {
  long l1 = d_pop(task);
  long l2 = d_pop(task);
  d_push(task, (l2 < l1) ? 0 : 1);
}

static inline void d_eq(ftask_p task) {
  long l1 = d_pop(task);
  long l2 = d_pop(task);
  d_push(task, (l2 == l1) ? F_TRUE : F_FALSE);
}

static inline void d_true(ftask_p task) { d_push(task, F_TRUE); }

static inline void d_false(ftask_p task) { d_push(task, F_FALSE); }

static void d_plus(ftask_p task) {
  long l1 = d_pop(task);
  long l2 = d_pop(task);
  d_push(task, l1 + l2);
}

static void d_minus(ftask_p task) {
  long l1 = d_pop(task);
  long l2 = d_pop(task);
  d_push(task, l2 - l1);
}

static void d_mul(ftask_p task) {
  long l1 = d_pop(task);
  long l2 = d_pop(task);
  d_push(task, l2 * l1);
}

static void d_modulo(ftask_p task) {
  long l1 = d_pop(task);
  long l2 = d_pop(task);
  d_push(task, l2 % l1);
}

static void d_div(ftask_p task) {
  long l1 = d_pop(task);
  long l2 = d_pop(task);
  d_push(task, l2 / l1);
}

static void d_i(ftask_p task) {}

//////// ---------| QUEUES |---------- //////
static void d_q_create(ftask_p task) {
  long size = d_pop(task);
  char *name = (char *)d_pop(task);
  // printf("Creating que %s of size %ld\n", name, size);
  tpq_p q = q_create(name, (int)size);
  d_push(task, (long)q);
}

static void d_q_write(ftask_p task) {
  char *msg = (char *)d_pop(task);
  tpq_p q = (tpq_p)d_pop(task);
  // printf("Writing message %s to queue %s\n", msg, q->name);
  q_put(q, msg);
}

static void d_q_read(ftask_p task) {
  tpq_p q = (tpq_p)d_pop(task);
  // printf("Reading message from queue %s\n", q->name);
  char *msg = q_get(q);
  d_push(task, (long)msg);
}

static void d_q_find(ftask_p task) {
  tpq_p q;
  char *name = (char *)d_pop(task);
  // printf("Looking up queue %s", name);
  q = (tpq_p)q_find(name);
  if (!q) {
    // printf(" not found\n");
  } else {
    // printf(" found %s\n", q->name);
  }
  d_push(task, (long)q);
}

static void d_q_dot(ftask_p task) {
  tpq_p q = (tpq_p)d_tos(task);
  printf("%s(%d,%s)", q->name, q->msz, q->full ? "full" : "empty");
}

static void d_nq_create(ftask_p task) {
  long size = d_pop(task);
  char *name = (char *)d_pop(task);
  nq_p q = nq_create(name, size);
  d_push(task, (long)q);
}

static void d_nq_read(ftask_p task) {
  nq_p q = (nq_p)d_pop(task);
  d_push(task, (long)nq_read(q));
}

static void d_nq_write(ftask_p task) {
  char *message = (char *)d_pop(task);
  nq_p q = (nq_p)d_pop(task);
  //  printf("Writing %s to %d\n", message, q->connect_addr.sin_addr.s_addr);
  nq_write(q, message);
}

static void d_nq_destroy(ftask_p task) {
  nq_p q = (nq_p)d_pop(task);
  nq_destroy(q);
}
///////////////////------| IO |--------- ////////////////////
static void d_dot(ftask_p task) {
  if (task->d_top <= 0) {
    printf(" . Data Stack Underflow");
    printf(" -- press x to exit");
    if (getchar() == 'x') {
      exit(-1);
    }
    return;
  }
  task->d_top--;
  printf("%ld ", task->d_stack[task->d_top]);
}

static void d_cr(ftask_p task) { printf("\n"); }

static void d_getkey(ftask_p task) { d_push(task, getchar()); }

static void d_emit(ftask_p task) { printf("%c", (char)d_pop(task)); }

static void d_ms(ftask_p task) {
  long millis = d_pop(task);
  usleep(millis);
}

/////////////////////////////////////////////////////////
static int dict_words_cb(dict_entry_p de, void *p) {
  printf(" %s", de->name);
  return 0;
}

static void f_words(ftask_p task) {
  for (int i = 0; i < N_builtins; i++) {
    printf(" %s", DB_builtins[i]->name);
  }
  dict_loop(0, dict_words_cb, 0);
}

//////// --------- DATA STACK MANIPULATION ---------------- /////////////////
void d_dup(ftask_p task) {
  task->d_stack[task->d_top] = task->d_stack[task->d_top - 1];
  task->d_top++;
}

static inline void f_pick(ftask_p task) {
  long l = d_pick(task, d_pop(task));
  d_push(task, l);
}

static inline void d_swap(ftask_p task) {
  long l1 = d_pop(task);
  long l2 = d_pop(task);
  d_push(task, l1);
  d_push(task, l2);
}

static inline void d_over(ftask_p task) {
  long l1 = d_pick(task, 1);
  d_push(task, l1);
}

static inline void d_rot(ftask_p task) {
  long l3 = d_pop(task);
  long l2 = d_pop(task);
  long l1 = d_pop(task);
  d_push(task, l2);
  d_push(task, l3);
  d_push(task, l1);
}

static inline void d_drop(ftask_p task) { task->d_top--; }

static inline void d_drop2(ftask_p task) { task->d_top -= 2; }

///////////////// DATA STACK VARIABLE OPERATIONS //////////////////////
/**
Store value on data stack in variable on variable stack
 */
static void d_variable_store(ftask_p task) {
  long val = d_pop(task);
  var_p v = (var_p)d_pop(task);
  v->val.l = val;
}

/*
 */
static void d_variable_store_at(ftask_p task) {
  long val = d_pop(task);
  long array_index = d_pop(task);
  var_p v = (var_p)d_pop(task);

  if (array_index > v->val.l) {
    printf("Array index out of range!");
    return;
  }
  v->val.addr.lp[array_index] = val;
}

static void d_variable_load(ftask_p task) {
  var_p v = (var_p)d_pop(task);
  d_push(task, v->val.l);
}

static void d_variable_load_at(ftask_p task) {
  long array_index = d_pop(task);
  var_p v = (var_p)d_pop(task);

  if (array_index > v->val.l) {
    printf("Array index out of range!");
    return;
  }
  d_push(task, v->val.addr.lp[array_index]);
}

static void d_variable_allot(ftask_p task) {
  long elem_sz = sizeof(long); // d_pop(task);
  long n_elems = d_pop(task);
  var_p v = (var_p)d_pop(task);

  v->val.l = n_elems;
  printf("Allocating %ld * %ld\n", elem_sz, n_elems);
  v->val.addr.lp = malloc(v->val.l * v->val.sz);

  v->t = VTYP_ARRAY;
}

static void d_variable_index(ftask_p task) {
  long elem = d_pop(task);
  var_p v = variable_get(d_pop(task));
  if (!v) {
    printf("\nVariable not found!\n");
    return;
  }
  if (v->t != VTYP_ARRAY) {
    printf("\nVariable not ARRAY!\n");
    return;
  }
  printf("Accessing elm no %ld\n", elem);
  d_push(task, v->val.addr.cp[elem]);
}

static void v_dump(ftask_p task) { vartable_dump(); }

static void v_dot(ftask_p task) {
  int idx = d_pop(task);
  var_p v = variable_get(idx);
  switch (v->t) {
  case VTYP_ARRAY:
    printf("Arr:");
    for (int i = 0; i < 20; i++) {
      if (i >= v->val.l) {
        break;
      }
      printf("%ld", v->val.addr.lp[i]);
    }
    break;
  case VTYP_ADDR:
    break;
  case VTYP_DOES:
    break;
  case VTYP_LONG:
    printf("%s %ld", v->name, v->val.l);
    break;
  case VTYP_STR:
    printf("%s %s", v->name, v->val.addr.cp);
    break;
  }
}

void d_stack_dump(ftask_p task) {
  printf("\n[");
  for (int i = 0; i < task->d_top; i++) {
    printf(" %ld", task->d_stack[i]);
  }
  printf(" ]\n");
}

//////////////// ------------- ---------------- ////////////////////
static void f_exit(ftask_p task) {
  // Handled in runtime.c
}

void f_dict_dump(ftask_p task) { dict_dump(0); }

//// RETURN STACK WORDS ///////////

/*
data stack to return stack
*/
static void d_r(ftask_p task) { r_push(task, (smtok_p *)d_pop(task)); }

static void r_d(ftask_p task) { d_push(task, (long)r_pop(task)); }

inline void r_fetch(ftask_p task) {
  d_push(task, (long)task->prog_stack[task->prog_top - 1]);
}

void d_dummy(ftask_p task) {}

/////////////////// STRING OPERATIONS //////

void s_dot(ftask_p task) {
  char *s = (char *)d_pop(task);
  printf("%s", s);
}

void s_cmp(ftask_p task) {
  char *s1 = (char *)d_pop(task);
  char *s2 = (char *)d_pop(task);
  d_push(task, strcmp(s1, s2));
}

void s_to_long(ftask_p task) {
  char *s = (char *)d_pop(task);
  d_push(task, atol(s));
}

void long_to_s(ftask_p task) {
  static char buf[256];
  long l = d_pop(task);
  sprintf(buf, "%ld", l);
  d_push(task, (long)buf);
}

/////////////////////////

///////////////////////////////////

void builtin_add(char *name, funcptr code) {
  bdb_add(builtin_create(name, code));
}

void builtins_test(ftask_p task) {}

static int qsort_builtins_compare(const void *a, const void *b) {
  idx_builtin_p pa = (idx_builtin_p)a;
  idx_builtin_p pb = (idx_builtin_p)b;
  return strcmp(pa->name, pb->name);
}

idx_builtin_p builtin_lookup(char *key) {
  int hi = N_builtins - 1;
  int lo = 0;

  //	printf("lookup(%s)\n", key);

  for (lo = 0, hi = N_builtins - 1; hi > lo;) {
    int i = (hi + lo) >> 1;
    int cmpval = strcmp(IDX_builtins[i].name, key);
    if (cmpval == 0) {
      return IDX_builtins + i;
    }
    if (hi - lo <= 1) {
      return 0;
    }
    if (cmpval < 0) {
      lo = i;
    } else if (cmpval > 0) {
      hi = i;
    }
  }
  return 0;
}

void index_names() {
  if (IDX_builtins) {
    IDX_builtins = realloc(IDX_builtins, sizeof(idx_builtin_t) * (N_builtins));
  } else {
    IDX_builtins = malloc(sizeof(idx_builtin_t) * (N_builtins));
  }
  for (int i = 0; i < N_builtins; i++) {
    IDX_builtins[i].name = DB_builtins[i]->name;
    IDX_builtins[i].op = DB_builtins[i]->op;
  }
  qsort(IDX_builtins, N_builtins, sizeof(idx_builtin_t),
        qsort_builtins_compare);
}

void builtin_db_dump() {
  printf("\nDB_builtins\n----------------------------\n");
  for (int i = 0; i < N_builtins; i++) {
    printf("%s\t%d %lx\n", DB_builtins[i]->name, DB_builtins[i]->op,
           (unsigned long)DB_builtins[i]->code);
  }
  printf("----------------------------\n");
}

void idx_builtins_dump() {
  printf("\nIDX\n----------------------------\n");
  for (int i = 0; i < N_builtins; i++) {
    printf("%s\t%d\n", IDX_builtins[i].name, IDX_builtins[i].op);
  }
  printf("----------------------------\n");
}

/*
Put all the builtins in the place indicated by their opcode.
There must be one entry here for each opcode.
If one is missing, the system will crash on qsort().
 */
void builtin_build_db() {
  bdb_create();
  builtin_add("DUP", d_dup);
  builtin_add("SWAP", d_swap);
  builtin_add("OVER", d_over);
  builtin_add("ROT", d_rot);
  builtin_add("DROP", d_drop);
  builtin_add("2DROP", d_drop2);
  builtin_add("PICK", f_pick);
  builtin_add("WORDS", f_words);
  builtin_add("+", d_plus);
  builtin_add("-", d_minus);
  builtin_add(".", d_dot);
  builtin_add("AND", d_and);
  builtin_add("OR", d_or);
  builtin_add("NOT", d_not);
  builtin_add("CR", d_cr);
  builtin_add("V.", v_dot);
  builtin_add(".s", d_stack_dump);
  builtin_add(">", d_gt);
  builtin_add("<", d_lt);
  builtin_add("=", d_eq);
  builtin_add("*", d_mul);
  builtin_add("MOD", d_modulo);
  builtin_add("%", d_modulo);
  builtin_add("/", d_div);
  builtin_add("TRUE", d_true);
  builtin_add("FALSE", d_false);
  builtin_add("I", d_i);
  builtin_add("!", d_variable_store);
  builtin_add("!!", d_variable_store_at);
  builtin_add("@", d_variable_load);
  builtin_add("@@", d_variable_load_at);
  builtin_add("STEP", d_step);
  builtin_add("EXIT", f_exit);
  builtin_add("DICT", f_dict_dump);
  builtin_add("SEXEC", d_string_exec);
  builtin_add("VARS", v_dump);
  builtin_add(">r", d_r);
  builtin_add("r>", r_d);
  builtin_add("'", d_dummy);
  builtin_add("TYPE", s_dot);
  builtin_add("S.", s_dot);
  builtin_add("S=", s_cmp);
  builtin_add("S>L", s_to_long);
  builtin_add("L>S", long_to_s);
  builtin_add("KEY", d_getkey);
  builtin_add("EMIT", d_emit);
  builtin_add("ALLOT", d_variable_allot);
  builtin_add("CELLS+", d_variable_index);
  builtin_add("QCREATE", d_q_create);
  builtin_add("Q", d_q_find);
  builtin_add("Q>", d_q_write);
  builtin_add("<Q", d_q_read);
  builtin_add("Q.", d_q_dot);
  builtin_add("MS", d_ms);
  builtin_add("NQCREATE", d_nq_create);
  builtin_add("NQDESTROY", d_nq_destroy);
  builtin_add("NQ>", d_nq_write);
  builtin_add("<NQ", d_nq_read);

  add_custom_builtins();
  //    builtin_db_dump();
  index_names();
  //    idx_builtins_dump();
}
