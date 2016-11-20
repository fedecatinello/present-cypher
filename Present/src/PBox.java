import java.util.Arrays;

public class PBox {

    public static String[] permutar(String[] inputs) {
        //el largo de los string tiene que ser igual a la cantidad de strings
        String[] outputs = new String[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            StringBuilder output = new StringBuilder();
            for(String input : inputs){ //para cada string
                output.append(input.charAt(i));
            }
            outputs[i] = output.toString();
        }
        return outputs;
    }
}
