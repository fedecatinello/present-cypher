class PBox {

    public static final int WORD_LENGHT = 16;

    static String[] permutar(String[] inputs, boolean desencriptar) {
        String[] outputs = new String[inputs.length];
        for (int i = 0; i < outputs.length; i++) {
            outputs[i] = "";
        }
        if(!desencriptar) {
            StringBuilder output = new StringBuilder();
            int pos = 0;
            for (int i = 0; i < WORD_LENGHT; i++) {
                for (String input : inputs) { //para cada string
                    output.append(input.charAt(i));
                }
                if (output.length() == WORD_LENGHT) {
                    outputs[pos++] = output.toString();
                    output = new StringBuilder();
                }
            }
        } else {
            for(String input : inputs){ //para cada string
                for (int i = 0; i < WORD_LENGHT; i++) {
                    outputs[i % inputs.length] += input.charAt(i);
                }
            }
        }
        return outputs;
    }
}
