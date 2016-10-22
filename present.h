
#ifndef PRESENT_CYPHER_PRESENT_H
#define PRESENT_CYPHER_PRESENT_H

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
    char* key;
    int32_t _80bitkey;  //Flag that indicate if key has 80-bit length or 128-bit length
    char* text;

}input_t;

input_t* init(char**);
void generateRoundKeys(input_t*);

#endif //PRESENT_CYPHER_PRESENT_H
