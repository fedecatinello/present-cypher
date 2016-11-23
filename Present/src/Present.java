import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Present {

    private static final int ROUNDS = 31;
    private static final int BASE = 2;
    private static final int BITS_COUNT = 16;
    private static final int BLOCK_SIZE = 64;
    private static String key;
    private static String[] keys = new String[ROUNDS + 1];
    private static String header;

    public static void main(String[] args) {
        String bitsClave = getBits(args[0]);
        generarRoundKeys(bitsClave);
        String file = args[1];
        String bits = getBitsFile(file);

        //encriptar
        final String[] bitsArr = getBitsArray(bits, BLOCK_SIZE);
        int bitsAgregados = bits.length() % BLOCK_SIZE;
        ExecutorService EXEC = Executors.newCachedThreadPool();
        List<Callable<String>> blocks = new ArrayList<>();
        for (int i = 0; i < bitsArr.length; i++) {
            final int block = i;
            Callable<String> c = () -> encriptar(bitsArr[block]);
            blocks.add(c);
        }
        StringBuilder stringBuilder = new StringBuilder();
        try {
            List<Future<String>> results = EXEC.invokeAll(blocks);
            for (Future<String> fr : results) {
                stringBuilder.append(fr.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String fileCifrado = file.substring(0, file.length() - 4) + "_cifrado" + file.substring(file.length() - 4);
        String bitsCifrados = stringBuilder.toString();
        setBitsFile(fileCifrado, header + bitsCifrados);

        //desencriptar
        final String[] bitsEncriptArr = getBitsArray(bitsCifrados, BLOCK_SIZE);
        EXEC = Executors.newCachedThreadPool();
        blocks = new ArrayList<>();
        for (int i = 0; i < bitsEncriptArr.length; i++) {
            final int block = i;
            Callable<String> c = () -> desencriptar(bitsEncriptArr[block]);
            blocks.add(c);
        }
        stringBuilder = new StringBuilder();
        try {
            List<Future<String>> results = EXEC.invokeAll(blocks);
            for (Future<String> fr : results) {
                stringBuilder.append(fr.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String fileDescifrado = file.substring(0, file.length() - 4) + "_descifrado" + file.substring(file.length() - 4);
        String bitsDescifrados = stringBuilder.toString().substring(bitsAgregados);
        setBitsFile(fileDescifrado, header + bitsDescifrados);

    }

    //obtengo el string de bits del texto
    private static String getBits(String texto) {
        StringBuilder builder = new StringBuilder();
        try {
            byte[] chars = texto.getBytes("UTF-8");
            for (byte c : chars) {
                String s = Integer.toBinaryString(c);
                builder.append(StringUtils.leftPad(s, 8, "0"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    //obtengo el string de bits de la imagen
    private static String getBitsFile(String filePath) {
        StringBuilder builder = new StringBuilder();
        try {
            try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(filePath))) {
                for (int b; (b = is.read()) != -1; ) {
                    String s = Integer.toBinaryString(b);
                    builder.append(StringUtils.leftPad(s, 8, "0"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        header = builder.toString().substring(0, 432);
        return builder.toString().substring(432);
    }

    //grabo la imagen cifrada
    private static void setBitsFile(String filePath, String bits) {
        try {
            byte[] bytes = new BigInteger(bits, 2).toByteArray();
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(bytes);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String encriptar(String bits) {
        try {
            for (int round = 0; round < ROUNDS; round++) {
                String roundKey = keys[round];
                String roundBits = XOR(bits, roundKey);
                String[] arrRoundBits = getBitsArray(roundBits, BITS_COUNT);
                String[] sBoxOutputs = sustituir(arrRoundBits, false);
                String[] pBoxOutputs = PBox.permutar(sBoxOutputs, false);
                bits = getBitsStr(pBoxOutputs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        bits = XOR(bits, keys[ROUNDS]);
        return bits;
    }

    private static String getBitsStr(String[] bits) {
        StringBuilder result = new StringBuilder();
        for (String s : bits) {
            result.append(s);
        }
        return result.toString();
    }

    //aplico el sbox a la clave para que varie en cada vuelta
    private static String getRoundKey(int round) {
        String roundkey = key.substring(0, BLOCK_SIZE);
//        key = rotar61Izquierda(key); //por el momento, no lo pude hacer andar
        key = sustituirPrimeros4Bits(key);
        key = actualizarUltimos5Bits(key, round);
        return roundkey;
    }

/*
    private static String rotar61Izquierda(String key) {
        Integer bits = Integer.parseInt(key, BASE);
        Integer rotado = Integer.rotateLeft(bits,61);
        return StringUtils.leftPad(Integer.toBinaryString(rotado),KEY_SIZE, "0");
    }
*/

    private static String sustituirPrimeros4Bits(String key) {
        return SBox.sustituir(key.substring(0, 16), false) + key.substring(16);
    }

    private static String actualizarUltimos5Bits(String key, Integer round) {
        StringBuilder builder = new StringBuilder();
        builder.append(key.substring(0, key.length() - 5));
        BigInteger b1 = new BigInteger(key.substring(key.length() - 5), BASE);
        BigInteger b2 = new BigInteger(round.toString());
        BigInteger xor = b1.xor(b2);
        String xorString = xor.toString(BASE);
        if (xorString.length() < 5) {
            xorString = StringUtils.leftPad(xorString, 5, "0");
        } else {
            xorString = xorString.substring(xorString.length() - 5);
        }
        builder.append(xorString);
        return builder.toString();
    }

    //aplico el sbox
    private static String[] sustituir(String[] bits, boolean desencriptar) {
        String[] sBoxOutputs = new String[bits.length];
        for (int i = 0; i < bits.length; i++) {
            sBoxOutputs[i] = SBox.sustituir(bits[i], desencriptar);
        }
        return sBoxOutputs;
    }

    private static String XOR(String palabra1, String palabra2) {
        int maxLength = palabra1.length() > palabra2.length() ? palabra1.length() : palabra2.length();
        BigInteger bi1 = new BigInteger(palabra1, BASE);
        BigInteger bi2 = new BigInteger(palabra2, BASE);
        BigInteger xor = bi1.xor(bi2);
        return StringUtils.leftPad(xor.toString(BASE), maxLength, "0");
    }

    //spliteo el string de bits en array de strings de largo bitsCount
    private static String[] getBitsArray(String bits, int bitsCount) {
        List<String> list = new ArrayList<>();
        String aux = bits;
        if (bits.length() % bitsCount > 0) {
            int largo = (bits.length() / bitsCount) + 1;
            aux = StringUtils.leftPad(bits, largo * bitsCount, "0");
        }
        while (aux.length() >= bitsCount) {
            list.add(aux.substring(0, bitsCount));
            aux = aux.substring(bitsCount);
        }
        String[] arr = new String[list.size()];
        arr = list.toArray(arr);
        return arr;
    }

    private static String desencriptar(String bits) {
        try {
            bits = XOR(bits, keys[ROUNDS]);
            for (int round = ROUNDS - 1; round >= 0 ; round--) {
                String roundKey = keys[round];
                String[] arrRoundBits = getBitsArray(bits, BITS_COUNT);
                String[] pBoxOutputs = PBox.permutar(arrRoundBits,true);
                String[] sBoxOutputs = sustituir(pBoxOutputs, true);
                String roundBits = getBitsStr(sBoxOutputs);
                bits = XOR(roundBits, roundKey);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bits;
    }

    private static void generarRoundKeys(String clave) {
        key = clave;
        for (int round = 0; round <= ROUNDS; round++) {
            keys[round] = getRoundKey(round);
        }

    }
}