
#ifndef PRESENT_CYPHER_PRESENT_H
#define PRESENT_CYPHER_PRESENT_H

#include <stdint.h>

#define ENTER "\n\n\n"
#define ANSI_COLOR_RED     "\x1b[31m"
#define ANSI_COLOR_GREEN   "\x1b[32m"
#define ANSI_COLOR_YELLOW  "\x1b[33m"
#define ANSI_COLOR_BLUE    "\x1b[34m"
#define ANSI_COLOR_MAGENTA "\x1b[35m"
#define ANSI_COLOR_CYAN    "\x1b[36m"
#define ANSI_COLOR_BOLDBLACK   "\033[1m\033[30m"
#define ANSI_COLOR_BOLDRED     "\033[1m\033[31m"
#define ANSI_COLOR_BOLDGREEN   "\033[1m\033[32m"
#define ANSI_COLOR_BOLDYELLOW  "\033[1m\033[33m"
#define ANSI_COLOR_BOLDBLUE    "\033[1m\033[34m"
#define ANSI_COLOR_BOLDMAGENTA "\033[1m\033[35m"
#define ANSI_COLOR_BOLDCYAN    "\033[1m\033[36m"
#define ANSI_COLOR_BOLDWHITE   "\033[1m\033[37m"
#define ANSI_COLOR_RESET "\x1b[0m"

#define EXIT_FAILURE 1
#define EXIT_SUCCESS 0

typedef struct input_data {
    int32_t rounds;
    int32_t encrypt;  //Flag that indicate if operation is encrypt or decrypt
    const uint8_t* key;
    int32_t _80bitkey;  //Flag that indicate if key has 80-bit length or 128-bit length
    const uint8_t* text;

}input_t;


// Bit Substitution Layer
static const uint8_t Sbox[16] = {0xc,0x5,0x6,0xb,0x9,0x0,0xa,0xd,0x3,0xe,0xf,0x8,0x4,0x7,0x1,0x2};

// Bit Permutation Layer
static const uint8_t Pbox[] = {0,16,32,48,1,17,33,49,2,18,34,50,3,19,35,51,
                              4,20,36,52,5,21,37,53,6,22,38,54,7,23,39,55,
                              8,24,40,56,9,25,41,57,10,26,42,58,11,27,43,59,
                              12,28,44,60,13,29,45,61,14,30,46,62,15,31,47,63};

input_t* init(char**);
void generateRoundKeys(input_t*);

// UTIL FUNCTIONS
char* convertToString(int32_t, int32_t);
int32_t convertToInteger(char*);

#endif //PRESENT_CYPHER_PRESENT_H
