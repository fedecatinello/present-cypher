import java.util.Arrays;

public class PBox {

    public static String permutar(String[] inputs) {
        //el largo de los string tiene que ser igual a la cantidad de strings
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            for(String input : inputs){ //para cada string
                output.append(input.charAt(i));
            }
        }
        return output.toString();
    }
}
