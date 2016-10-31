package com.limeri.leon;

import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Present {

    public static final int ROUNDS = 31;
    public static final int BASE = 2;
    public static final int BITS_COUNT = 4;

    public static String encriptar(String textoPlano, String clave){
        String roundKey = getBits(clave);
        String bits = getBits(textoPlano);
        try {
            for(int r = 1; r <= ROUNDS; r++){
                roundKey = getRoundKey(roundKey);
                String roundBits = XOR(bits,roundKey);
                String[] sBoxOutputs = sustituir(roundBits);
                String[] pBoxOutput = PBox.permutar(sBoxOutputs);
                bits = getBitsString(pBoxOutput);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bits;
    }

    //aplico el sbox a la clave para que varie en cada vuelta
    private static String getRoundKey(String clave) {
        String[] arrKey = sustituir(clave);
        return getBitsString(arrKey);
    }

    //joineo el array de strings en un string
    private static String getBitsString(String[] arr) {
        StringBuilder builder = new StringBuilder();
        for(String s : arr) {
            builder.append(s);
        }
        return builder.toString();
    }

    //aplico el sbox
    private static String[] sustituir(String bits){
        String[] arrRoundBits = getBitsArray(bits);
        String[] sBoxOutputs = new String[arrRoundBits.length];
        for (int i=0; i < arrRoundBits.length; i++){
            sBoxOutputs[i] = SBox.sustituir(arrRoundBits[i]);
        }
        return sBoxOutputs;
    }

    private static String XOR(String palabra1, String palabra2) {
        int maxLength = palabra1.length() > palabra2.length() ? palabra1.length() : palabra2.length();
        BigInteger bi1 = new BigInteger(palabra1, BASE);
        BigInteger bi2 = new BigInteger(palabra2, BASE);
        BigInteger xor = bi1.xor(bi2);
        return StringUtils.leftPad(xor.toString(BASE),maxLength, "0");
    }

    //spliteo el string de bits en array de strings de largo BITS_COUNT
    private static String[] getBitsArray(String bits) {
        List<String> list = new ArrayList<>();
        while(bits.length() >= BITS_COUNT) {
            list.add(bits.substring(0, BITS_COUNT));
            bits = bits.substring(BITS_COUNT);
        }
        String[] arr = new String[list.size()];
        arr = list.toArray(arr);
        return arr;
    }

    //convierto el string a string de bits
    private static String getBits(String texto) {
        StringBuilder builder = new StringBuilder();
        try {
            byte[] chars = texto.getBytes("UTF-8");
            for (byte c : chars) {
                String s = Integer.toBinaryString(c);
                builder.append(StringUtils.leftPad(s,8,"0"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
