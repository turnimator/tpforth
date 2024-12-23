/*
 ============================================================================
 Name        : fnf.c
 Author      : Jan Atle Ramsli
 Version     :
 Copyright   : (C) JAR
 Description : Hello World in C, Ansi-style
 ============================================================================
 */

// #define DEBUG

#include "parser.h"
#include "runtime.h"
#include "task.h"
#include <stdio.h>
#include <stdlib.h>
#include <sys/stat.h>
extern char *readfile(char *);

void banner() {
  puts("     +----------------------------------------------------+");
  puts("     | TP-FORTH - A Forth Language SmartToken Interpreter |");
  puts("     |            © 2024 Jan Atle Ramsli, GPL             |");
  puts("     +----------------------------------------------------+");
}

int main(int ac, char *av[]) {

  char buf[256];
  dict_init();
  builtin_build_db();
  banner();
  ftask_p t = ftask_create("Main");

  char *filename = "startup.fs";
  if (ac > 1) {
    filename = av[1];
  }

  char *src = readfile(filename);
  if (src) {
    puts(src);
    parse(t, src);
    run_task(t);
    // free(src);
  }

  t = ftask_create("Main");
  while (fgets(buf, 255, stdin)) {
    parse(t, buf);
    run_task(t);
    printf("\n%s", "ok ");
  }

  return EXIT_SUCCESS;
}
