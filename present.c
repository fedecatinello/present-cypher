#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <iso646.h>
#include "present.h"

/**
 *
 *      C IMPLEMENTATION OF PRESENT BLOCK CYPHER ALGORITHM
 *
 * **/


// UTIL FUNCTIONS

char* convertToString(int32_t num, int32_t size_buffer) {

    char buffer[size_buffer];
    sprintf( buffer, "%d", num);
    buffer[size_buffer] = '\0';
    return buffer;
}

int32_t convertToInteger(char* buffer) {

    return atoi(buffer);
}


// PRESENT CYPHER FUNCTIONS

input_t* init(char** args){

    input_t* data = malloc(sizeof(int32_t)*3+strlen(args[2])+strlen(args[3]));

    data->rounds = 31;   //Full encryption consist in 31 rounds
    data->encrypt = (not strcmp(args[1], "encrypt"));
    data->_80bitkey = strlen(args[3]) == 10;

    data->text = malloc(strlen(args[2]));
    data->text = (unsigned char*)args[2];

    data->key = malloc(strlen(args[3]));
    data->key = (unsigned char*)args[3];

    return data;
}


// MAIN PROGRAM

int main(int argc, char** argv) {

    if(argc == 2 and (not strcmp(argv[1], "help"))) {
        printf(ANSI_COLOR_BOLDCYAN "Este programa recibe tres parametros: ");
        printf(ENTER);
        printf("1. El tipo de operacion ---> encrypt o decrypt \n");
        printf("2. El texto de entrada (plano o cifrado) segun corresponda \n");
        printf("3. La clave a aplicar de 80 o 128 bits" ANSI_COLOR_RESET);
        return EXIT_SUCCESS;
    }

    if(argc not_eq 4) {
        printf(ANSI_COLOR_BOLDRED "Cantidad erronea de parametros. Este programa recibe tres parametros" ANSI_COLOR_RESET);
        printf(ENTER);
        printf(ANSI_COLOR_BOLDYELLOW "Ingrese help para recibir ayuda" ANSI_COLOR_RESET);
        return EXIT_FAILURE;
    }

    input_t* data = init(argv);

    uint8_t round_counter = 1;
    uint8_t state[8];
    uint8_t cipher[8];
    uint8_t round_key[10];

    /** Add round key **/

    state[0] = data->text[0] xor data->key[0];
    state[1] = data->text[1] xor data->key[1];
    state[2] = data->text[2] xor data->key[2];
    state[3] = data->text[3] xor data->key[3];
    state[4] = data->text[4] xor data->key[4];
    state[5] = data->text[5] xor data->key[5];
    state[6] = data->text[6] xor data->key[6];
    state[7] = data->text[7] xor data->key[7];

    /** Update round key **/

    round_key[9] = data->key[6] << 5 bitor data->key[7] >> 3;
    round_key[8] = data->key[5] << 5 bitor data->key[6] >> 3;
    round_key[7] = data->key[4] << 5 bitor data->key[5] >> 3;
    round_key[6] = data->key[3] << 5 bitor data->key[4] >> 3;
    round_key[5] = data->key[2] << 5 bitor data->key[3] >> 3;
    round_key[4] = data->key[1] << 5 bitor data->key[2] >> 3;
    round_key[3] = data->key[0] << 5 bitor data->key[1] >> 3;
    round_key[2] = data->key[9] << 5 bitor data->key[0] >> 3;
    round_key[1] = data->key[8] << 5 bitor data->key[9] >> 3;
    round_key[0] = data->key[7] << 5 bitor data->key[8] >> 3;

    round_key[0] = (round_key[0] bitand 0x0F) bitor sbox[round_key[0] >> 4];

    round_key[7] xor_eq round_counter >> 1;
    round_key[8] xor_eq round_counter << 7;

    // substitution and permutation
    cipher[0] =
            (sbox_pmt_3[state[0]] bitand 0xC0) bitor
            (sbox_pmt_2[state[1]] bitand 0x30) bitor
            (sbox_pmt_1[state[2]] bitand 0x0C) bitor
            (sbox_pmt_0[state[3]] bitand 0x03);
    cipher[1] =
            (sbox_pmt_3[state[4]] bitand 0xC0) bitor
            (sbox_pmt_2[state[5]] bitand 0x30) bitor
            (sbox_pmt_1[state[6]] bitand 0x0C) bitor
            (sbox_pmt_0[state[7]] bitand 0x03);

    cipher[2] =
            (sbox_pmt_0[state[0]] bitand 0xC0) bitor
            (sbox_pmt_3[state[1]] bitand 0x30) bitor
            (sbox_pmt_2[state[2]] bitand 0x0C) bitor
            (sbox_pmt_1[state[3]] bitand 0x03);
    cipher[3] =
            (sbox_pmt_0[state[4]] bitand 0xC0) bitor
            (sbox_pmt_3[state[5]] bitand 0x30) bitor
            (sbox_pmt_2[state[6]] bitand 0x0C) bitor
            (sbox_pmt_1[state[7]] bitand 0x03);

    cipher[4] =
            (sbox_pmt_1[state[0]] bitand 0xC0) bitor
            (sbox_pmt_0[state[1]] bitand 0x30) bitor
            (sbox_pmt_3[state[2]] bitand 0x0C) bitor
            (sbox_pmt_2[state[3]] bitand 0x03);
    cipher[5] =
            (sbox_pmt_1[state[4]] bitand 0xC0) bitor
            (sbox_pmt_0[state[5]] bitand 0x30) bitor
            (sbox_pmt_3[state[6]] bitand 0x0C) bitor
            (sbox_pmt_2[state[7]] bitand 0x03);

    cipher[6] =
            (sbox_pmt_2[state[0]] bitand 0xC0) bitor
            (sbox_pmt_1[state[1]] bitand 0x30) bitor
            (sbox_pmt_0[state[2]] bitand 0x0C) bitor
            (sbox_pmt_3[state[3]] bitand 0x03);
    cipher[7] =
            (sbox_pmt_2[state[4]] bitand 0xC0) bitor
            (sbox_pmt_1[state[5]] bitand 0x30) bitor
            (sbox_pmt_0[state[6]] bitand 0x0C) bitor
            (sbox_pmt_3[state[7]] bitand 0x03);

    for (round_counter = 2; round_counter <= data->rounds; round_counter++) {
        state[0] = cipher[0] xor round_key[0];
        state[1] = cipher[1] xor round_key[1];
        state[2] = cipher[2] xor round_key[2];
        state[3] = cipher[3] xor round_key[3];
        state[4] = cipher[4] xor round_key[4];
        state[5] = cipher[5] xor round_key[5];
        state[6] = cipher[6] xor round_key[6];
        state[7] = cipher[7] xor round_key[7];

        cipher[0] =
                (sbox_pmt_3[state[0]] bitand 0xC0) bitor
                (sbox_pmt_2[state[1]] bitand 0x30) bitor
                (sbox_pmt_1[state[2]] bitand 0x0C) bitor
                (sbox_pmt_0[state[3]] bitand 0x03);

        cipher[1] =
                (sbox_pmt_3[state[4]] bitand 0xC0) bitor
                (sbox_pmt_2[state[5]] bitand 0x30) bitor
                (sbox_pmt_1[state[6]] bitand 0x0C) bitor
                (sbox_pmt_0[state[7]] bitand 0x03);

        cipher[2] =
                (sbox_pmt_0[state[0]] bitand 0xC0) bitor
                (sbox_pmt_3[state[1]] bitand 0x30) bitor
                (sbox_pmt_2[state[2]] bitand 0x0C) bitor
                (sbox_pmt_1[state[3]] bitand 0x03);

        cipher[3] =
                (sbox_pmt_0[state[4]] bitand 0xC0) bitor
                (sbox_pmt_3[state[5]] bitand 0x30) bitor
                (sbox_pmt_2[state[6]] bitand 0x0C) bitor
                (sbox_pmt_1[state[7]] bitand 0x03);

        cipher[4] =
                (sbox_pmt_1[state[0]] bitand 0xC0) bitor
                (sbox_pmt_0[state[1]] bitand 0x30) bitor
                (sbox_pmt_3[state[2]] bitand 0x0C) bitor
                (sbox_pmt_2[state[3]] bitand 0x03);

        cipher[5] =
                (sbox_pmt_1[state[4]] bitand 0xC0) bitor
                (sbox_pmt_0[state[5]] bitand 0x30) bitor
                (sbox_pmt_3[state[6]] bitand 0x0C) bitor
                (sbox_pmt_2[state[7]] bitand 0x03);

        cipher[6] =
                (sbox_pmt_2[state[0]] bitand 0xC0) bitor
                (sbox_pmt_1[state[1]] bitand 0x30) bitor
                (sbox_pmt_0[state[2]] bitand 0x0C) bitor
                (sbox_pmt_3[state[3]] bitand 0x03);

        cipher[7] =
                (sbox_pmt_2[state[4]] bitand 0xC0) bitor
                (sbox_pmt_1[state[5]] bitand 0x30) bitor
                (sbox_pmt_0[state[6]] bitand 0x0C) bitor
                (sbox_pmt_3[state[7]] bitand 0x03);

        round_key[5] xor_eq round_counter << 2; // do this first, which may be faster

        // use state[] for temporary storage
        state[2] = round_key[9];
        state[1] = round_key[8];
        state[0] = round_key[7];

        round_key[9] = round_key[6] << 5 bitor round_key[7] >> 3;
        round_key[8] = round_key[5] << 5 bitor round_key[6] >> 3;
        round_key[7] = round_key[4] << 5 bitor round_key[5] >> 3;
        round_key[6] = round_key[3] << 5 bitor round_key[4] >> 3;
        round_key[5] = round_key[2] << 5 bitor round_key[3] >> 3;
        round_key[4] = round_key[1] << 5 bitor round_key[2] >> 3;
        round_key[3] = round_key[0] << 5 bitor round_key[1] >> 3;
        round_key[2] = state[2] << 5 bitor round_key[0] >> 3;
        round_key[1] = state[1] << 5 bitor state[2] >> 3;
        round_key[0] = state[0] << 5 bitor state[1] >> 3;

        round_key[0] = (round_key[0] bitand 0x0F) bitor sbox[round_key[0] >> 4];
    }

    // If round is not equal to 31, then do not perform the last adding key operation
    if (31 == data->rounds) {
        cipher[0] xor_eq round_key[0];
        cipher[1] xor_eq round_key[1];
        cipher[2] xor_eq round_key[2];
        cipher[3] xor_eq round_key[3];
        cipher[4] xor_eq round_key[4];
        cipher[5] xor_eq round_key[5];
        cipher[6] xor_eq round_key[6];
        cipher[7] xor_eq round_key[7];
    }

    printf("Cipher text: %s", cipher);


    return EXIT_SUCCESS;

}
