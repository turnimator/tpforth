/*
 * program.c
 *
 *  Created on: Sep 29, 2024
 *      Author: Jan Atle Ramsli
 *
 */

#include "program.h"
#include "logg.h"
#include "p_code.h"
#include "variable.h"
#include <string.h>

/**
Loop through the program, calling func for each p-code
*/
p_code_p program_loop(program_p progp, int (*func)(p_code_p, int pcn),
                      int pcn) {
  for (int i = 0; i < progp->npcp_array; i++) {
    if (func(progp->pcp_array[i], pcn == i ? 1 : 0)) {
      return progp->pcp_array[i];
    }
  }
  return 0;
}

int program_dump_cb(p_code_p pcp, int here) {
  var_p v;
  if (here) {
    printf(" >");
  }
  switch (pcp->type) {
  case PCODE_NUMBER:
    printf("NUM:%ld ", pcp->val.l);
    return 0;
  case PCODE_BUILTIN:
    printf("PRMTV:%s ", pcp->name);
    return 0;
  case PCODE_VARIABLE:
    v = variable_get(pcp->val.var_idx);
    printf("VAR:(%s=%s) ", pcp->name, vartype_string(v));
    return 0;
  case PCODE_DICT_ENTRY:
    printf("DEF:%s ", pcp->name);
    program_dump(pcp->val.prog, here);
    return 0;
  case PCODE_IF:
    printf("IF:%ld ", pcp->val.l);
    return 0;
  case PCODE_ELSE:
    printf("ELSE:%ld ", pcp->val.l);
    return 0;
  case PCODE_THEN:
    printf("THEN:%ld ", pcp->val.l);
    return 0;
  case PCODE_LOOP_DO:
    printf("DO:%ld ", pcp->val.l);
    return 0;
  case PCODE_LOOP_END:
    printf("LOOP:%ld ", pcp->val.l);
    return 0;
  case PCODE_I:
    printf("I:%s ", pcp->name);
    return 0;
    break;
  default:
    printf("?");
    return 0;
    break;
  }
  printf("Should never arrive here\n");
  return 1;
}

void program_dump(program_p progp, int pcn) {
  printf("\n----------------| %s |-----------------\n", progp->name);
  program_loop(progp, program_dump_cb, pcn);
  printf("\n----------------------------------------\n\n");
}

static int program_delete_cb(p_code_p pcp, int pcn) {
  p_code_delete(pcp);
  return 0;
}

void program_delete(program_p prog) {
  program_loop(prog, program_delete_cb, 0);
  prog->npcp_array = 0;
  free(prog->pcp_array);
  prog->pcp_array = 0;
  free(prog->name);
  prog->name = 0;
  free(prog);
}

program_p program_create(char *name) {
  program_p prog = malloc(sizeof(program_p));
  prog->pcp_array = malloc(sizeof(p_code_p *));
  prog->name = malloc(strlen(name));
  strcpy(prog->name, name);
  prog->npcp_array = 0;
  return prog;
}

void program_add_p_code(program_p prog, p_code_p pcp) {
  logg(__func__, pcp->name);

  prog->npcp_array++;
  prog->pcp_array =
      realloc(prog->pcp_array, sizeof(p_code_p *) * prog->npcp_array);
  prog->pcp_array[prog->npcp_array - 1] = pcp;
}

long program_last(program_p prog) { return prog->npcp_array - 1; }

void program_add(program_p prog, char *src) {
  p_code_p pc = p_code_parse_word(src);
  if (!pc) {
    printf("\nprogram_add(%s) failed \n", src);
    return;
  }
  program_add_p_code(prog, pc);
}
