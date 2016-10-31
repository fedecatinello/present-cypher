package com.limeri.leon;

import java.util.Arrays;

public class PBox {

    public static String[] permutar(String[] inputs) {
        //el largo de los string tiene que ser igual a la cantidad de strings
        String[] outputs = new String[inputs.length];
        for(String input : inputs){ //para cada string
            char[] charInput = input.toCharArray(); //obtengo los caracters
            for(int i=0; i < charInput.length; i++){
                outputs[i] = (outputs[i] == null ? "" : outputs[i]) + charInput[i]; //pongo cada caracter en un string distinto
            }
        }
        return outputs;
    }
}
