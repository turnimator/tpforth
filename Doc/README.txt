The basic unit of execution is the p-code.
A p-code can be a Primitive that calls a C-function.
A collection of p-codes can be assembled into a program.
A program can be executed at runtime.

Initially, the parser state machine will be in the state EXPECTING_ANY
In this state, it will call the function
parser_state_t parse_word(program_p prog, char *tok) 

Hacking:
How to create a DO control structure:

Parse:
In the function
parser_state_t parse_word(program_p prog, char *tok) 
look for the token "DO"
Compile:
If found,  push the current control structure onto the compile-time stack
using 
ct_push(CT_DO, current_loop); // Must be popped on LOOP
The CT_DO control structure type is defined in compiler.h
The compile time stack is necessary for nested control words.

create a P-CODE for it
using
current_loop = p_code_ct_create(PCODE_DO);
PCODE_DO is found in pcode.h and it's a P-CODE-TYPE.
If you create an new kind of loop, e.g. WHILE, you must extend this enum to contain a PCODE_WHILE.
Record the place in the program 
Add the P-CODE to the program
Finally, enter the parser state DO_EXPECTING_LOOP

Run:
in runtime.c, create an function for running P-CODES of type PC_DE_DO

To edit the p_code jump table (adding p-codes)
p_code.h extend / modify the table
runtime.c copy the table from p_code.h into a comment above p_code_jump_table and verify that the entry functions ef_xxx place in the table can be indexed by the correct number the number.
Example: cb_ef_number should be at should be at place  PCODE_NUMBER (2) in the jump table.

p_code.c Make sure the new/modified word is being compiled correctly in p_code_compile_word()

