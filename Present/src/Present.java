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

    public static final int ROUNDS = 31;
    public static final int BASE = 2;
    public static final int BITS_COUNT = 4;
    public static final int BLOCK_SIZE = 64;
    public static final int KEY_SIZE = 80;
    private static String key;
    private static String header;

    public static void main(String[] args){
        long start = System.currentTimeMillis();
        final String clave = getBits(args[0]);
        String file = args[1];
        String bits = getBitsFile(file);
        final String[] bitsArr = getBitsArray(bits,BLOCK_SIZE);
        ExecutorService EXEC = Executors.newCachedThreadPool();
        List<Callable<String>> tasks = new ArrayList<>();
        for (int i = 0; i < bitsArr.length; i++) {
            final int round = i;
            Callable<String> c = () -> {
                return encriptar(bitsArr[round],clave);
            };
            tasks.add(c);
        }
        StringBuilder stringBuilder = new StringBuilder();
        try {
            List<Future<String>> results = EXEC.invokeAll(tasks);
            for (Future<String> fr : results) {
                stringBuilder.append(fr.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String fileCifrado = file.substring(0, file.length() - 4) + "_cifrado" + file.substring(file.length() - 4);
        setBitsFile(fileCifrado, header + stringBuilder.toString());
        long finish = System.currentTimeMillis();
        double time = (finish - start)/1000;
    }

    //obtengo el string de bits del texto
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

    //obtengo el string de bits de la imagen
    private static String getBitsFile(String filePath) {
        StringBuilder builder = new StringBuilder();
        try {
            try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(filePath))) {
                for (int b; (b = is.read()) != -1;) {
                    String s = Integer.toBinaryString(b);
//                    s = s.substring(s.length() - 8);
                    builder.append(StringUtils.leftPad(s,8,"0"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        header = builder.toString().substring(0,432);
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

    private static String encriptar(String bits, String clave) {
        key = clave;
        try {
            for(int round = 0; round <= ROUNDS; round++){
                String roundKey = getRoundKey(round);
                String roundBits = XOR(bits,roundKey);
                String[] sBoxOutputs = sustituir(roundBits);
                bits = PBox.permutar(sBoxOutputs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bits;
    }

    //aplico el sbox a la clave para que varie en cada vuelta
    private static String getRoundKey(int round) {
        String roundkey = key.substring(0,BLOCK_SIZE);
//        key = rotar61Izquierda(key); //por el momento, no lo pude hacer andar
        key = sustituirPrimeros4Bits(key);
        key = actualizarUltimos5Bits(key, round);
        return roundkey;
    }

    private static String rotar61Izquierda(String key) {
        Integer bits = Integer.parseInt(key, BASE);
        Integer rotado = Integer.rotateLeft(bits,61);
        return StringUtils.leftPad(Integer.toBinaryString(rotado),KEY_SIZE, "0");
    }

    private static String sustituirPrimeros4Bits(String key) {
        StringBuilder builder = new StringBuilder();
        builder.append(SBox.sustituir(key.substring(0,BITS_COUNT)));
        builder.append(key.substring(BITS_COUNT));
        return builder.toString();
    }

    private static String actualizarUltimos5Bits(String key, Integer round) {
        StringBuilder builder = new StringBuilder();
        builder.append(key.substring(0,key.length() - 5));
        BigInteger b1 = new BigInteger(key.substring(key.length() - 5),BASE);
        BigInteger b2 = new BigInteger(round.toString());
        BigInteger xor = b1.xor(b2);
        String xorString = xor.toString(BASE);
        if(xorString.length() < 5) {
            xorString = StringUtils.leftPad(xorString, 5, "0");
        } else {
            xorString = xorString.substring(xorString.length() - 5);
        }
        builder.append(xorString);
        return builder.toString();
    }

    //aplico el sbox
    private static String[] sustituir(String bits){
        String[] arrRoundBits = getBitsArray(bits, BITS_COUNT);
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
        return StringUtils.leftPad(xor.toString(BASE), maxLength, "0");
    }

    //spliteo el string de bits en array de strings de largo bitsCount
    private static String[] getBitsArray(String bits, int bitsCount) {
        List<String> list = new ArrayList<>();
        String aux = bits;
        if(bits.length() % bitsCount > 0) {
            int largo = (bits.length() / bitsCount) + 1;
            aux = StringUtils.leftPad(bits, largo*bitsCount, "0");
        }
        while(aux.length() >= bitsCount) {
            list.add(aux.substring(0, bitsCount));
            aux = aux.substring(bitsCount);
        }
        String[] arr = new String[list.size()];
        arr = list.toArray(arr);
        return arr;
    }
}
