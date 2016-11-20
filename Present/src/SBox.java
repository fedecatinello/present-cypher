import java.util.HashMap;
import java.util.Map;

public class SBox {

    private static Map<String,Map<String,String>> tablaSustitucion;
    static {
        tablaSustitucion = new HashMap<>();
        Map<String,String> mapa = new HashMap<>();
        mapa.put("00","0000");
        mapa.put("01","1110");
        mapa.put("10","0100");
        mapa.put("11","1011");
        tablaSustitucion.put("00",mapa);
        mapa = new HashMap<>();
        mapa.put("00","1100");
        mapa.put("01","1011");
        mapa.put("10","0010");
        mapa.put("11","1000");
        tablaSustitucion.put("01",mapa);
        mapa = new HashMap<>();
        mapa.put("00","0100");
        mapa.put("01","0010");
        mapa.put("10","0001");
        mapa.put("11","1100");
        tablaSustitucion.put("10",mapa);
        mapa = new HashMap<>();
        mapa.put("00","0001");
        mapa.put("01","1100");
        mapa.put("10","1011");
        mapa.put("11","0111");
        tablaSustitucion.put("11",mapa);

    }

    public static String sustituir(String input) {
        String interior = input.substring(1,3); //busco los 2 bits interiores
        String exterior = input.substring(0,1) + input.substring(3,4); //busco los 2 bits exteriores
        return tablaSustitucion.get(interior).get(exterior); //los busco en la tabla
    }
}
