#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "present.h"

/**
 *
 *      C IMPLEMENTATION OF PRESENT BLOCK CYPHER ALGORITHM
 *
 * **/


input_t* init(char** args){

    input_t* data = malloc(sizeof(int32_t)*3+strlen(args[2])+strlen(args[3]));

    data->rounds = 32;   //By default algorithm manages 32 rounds
    data->encrypt = !strcmp(args[1], "encrypt");
    data->_80bitkey = strlen(args[3]) == 10;

    data->key = malloc(strlen(args[3]));
    strcpy(data->key, args[3]);

    data->text = malloc(strlen(args[2]));
    strcpy(data->text, args[2]);

    return data;
}

void generateRoundKeys(input_t* data) {


}


int main(int argc, char** argv) {

    if(argc == 2 && !strcmp(argv[1], "help")) {
        printf(ANSI_COLOR_BOLDCYAN "Este programa recibe tres parametros: ");
        printf(ENTER);
        printf("1. El tipo de operacion ---> encrypt o decrypt \n");
        printf("2. El texto de entrada (plano o cifrado) segun corresponda \n");
        printf("3. La clave a aplicar de 80 o 128 bits" ANSI_COLOR_RESET);
        return EXIT_SUCCESS;
    }

    if(argc != 4) {
        printf(ANSI_COLOR_BOLDRED "Cantidad erronea de parametros. Este programa recibe tres parametros" ANSI_COLOR_RESET);
        printf(ENTER);
        printf(ANSI_COLOR_BOLDYELLOW "Ingrese help para recibir ayuda" ANSI_COLOR_RESET);
        return EXIT_FAILURE;
    }

    input_t* data = init(argv);

    generateRoundKeys(data);

//    for(int i = 1; i<(data->rounds)-1; i++) {
//          addRoundKey(state,Ki)
//          sBoxLayer(state)
//          pLayer(state)
//    }
//    addRoundKey(state,K32)


    return EXIT_SUCCESS;

}
