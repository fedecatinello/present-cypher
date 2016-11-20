import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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
    private static String[] bitsEncriptadosArr;
    private static String header;

    public static void test(){
        long start = System.currentTimeMillis();
        final String clave = getBits("ABCDEFGHIJ");
//        String bits = getBits("Hola mundo");
        String bits = getBitsFile("C:\\Users\\Nico\\Desktop\\arbol.bmp");
        final String[] bitsArr = getBitsArray(bits,BLOCK_SIZE);
        bitsEncriptadosArr = new String[bitsArr.length];
        ExecutorService EXEC = Executors.newCachedThreadPool();
        List<Callable<String>> tasks = new ArrayList<>();
        for (int i = 0; i < bitsArr.length; i++) {
            final int round = i;
            Callable<String> c = () -> {
                String result = encriptar(bitsArr[round],clave);
                bitsEncriptadosArr[round] = result;
                return result;
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
        String bitsEncriptadosStr = getBitsString(bitsEncriptadosArr);
        String result = stringBuilder.toString();
        setBitsFile("C:\\Users\\Nico\\Desktop\\arbol_cifrado.bmp", header + result);
        long finish = System.currentTimeMillis();
        double time = (finish - start)/1000;
    }

    private static void setBitsFile(String filePath, String bits) {
        try {
            byte[] bytes = new BigInteger(bits, 2).toByteArray();
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(bytes);
            fos.close();
//            BufferedImage img = null;
//            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
//            try {
//                img = ImageIO.read(bais);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            ImageIO.write(img, "BMP", new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String getBitsFile(String filePath) {
        StringBuilder builder = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();
        FileOutputStream fos = null;
        FileOutputStream fos2 = null;
        BufferedOutputStream out = null;
        BufferedOutputStream out2 = null;
        try {
/*
            File file = new File("filename.bin");
            byte[] fileData = new byte[file.length()];
            FileInputStream in = new FileInputStream(file);
            in.read(fileData):
            in.close();
*/
//            File file = new File(filePath);
//
//            FileInputStream fis = new FileInputStream(file);
//            //create FileInputStream which obtains input bytes from a file in a file system
//            //FileInputStream is meant for reading streams of raw bytes such as image data. For reading streams of characters, consider using FileReader.
//
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            byte[] buf = new byte[1024];
//            for (int readNum; (readNum = fis.read(buf)) != -1;) {
//                //Writes to this byte array output stream
//                bos.write(buf, 0, readNum);
//            }
//            byte[] bytes = bos.toByteArray();
//            for (int i = 0; i < bytes.length; i++) {
//                String s = Integer.toBinaryString(bytes[i]);
//                builder.append(StringUtils.leftPad(s,8,"0"));
//                builder2.append(getBits(bytes[i]));
//            }

            try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(filePath))) {
                for (int b; (b = is.read()) != -1;) {
                    String s = "0000000" + Integer.toBinaryString(b);
                    s = s.substring(s.length() - 8);
//                    builder.append(s).append(' ');
                    builder.append(s);
                }
            }
//            out = new BufferedOutputStream(new FileOutputStream("C:\\Users\\Nico\\Desktop\\imagen_temp.bmp"));
//            Scanner sc = new Scanner(builder.toString());
//            while (sc.hasNextInt()) {
//                int b = sc.nextInt(2);
//                out.write(b);
//            }
//            fos = new FileOutputStream("C:\\Users\\Nico\\Desktop\\imagen_temp3.bmp");
//            fos.write(decodeBinary(builder2.toString()));
//            fos2 = new FileOutputStream("C:\\Users\\Nico\\Desktop\\imagen_temp4.bmp");
//            fos2.write(decodeBinary2(builder2.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            try {
////                fos.close();
////                fos2.close();
////                out.close();
////                out2.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        header = builder.toString().substring(0,432);
//        setBitsFile("C:\\Users\\Nico\\Desktop\\imagen_temp2.bmp", builder.toString().substring(54));
        return builder.toString().substring(432);
    }

    private static String getBits(byte b)
    {
        String result = "";
        for(int i = 0; i < 8; i++)
            result += (b & (1 << i)) == 0 ? "0" : "1";
        return result;
    }

    private static byte[] decodeBinary(String s) {
        if (s.length() % 8 != 0) throw new IllegalArgumentException(
                "Binary data length must be multiple of 8");
        byte[] data = new byte[s.length() / 8];
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '1') {
                data[i >> 3] |= 0x80 >> (i & 0x7);
            } else if (c != '0') {
                throw new IllegalArgumentException("Invalid char in binary string");
            }
        }
        return data;
    }

    private static byte[] decodeBinary2(String s) {
        if (s.length() % 8 != 0) throw new IllegalArgumentException(
                "Binary data length must be multiple of 8");
        byte[] data = new byte[s.length() / 8];
        for (int i = 0; i < s.length(); i++) {
            data[i] = (byte) Long.parseLong(s.substring(0,8), 2);
            s = s.substring(8);
        }
        return data;
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
