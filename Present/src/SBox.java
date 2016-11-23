class SBox {

    private static int[] arrEncriptar = {12,5,6,11,9,0,10,13,3,14,15,8,4,7,1,2};
    private static int[] arrDesencriptar = {5,14,15,8,12,1,2,13,11,4,6,3,0,7,9,10};

    static String sustituir(String input, boolean desencriptar) {
        String result = "";
        if(!desencriptar) {
            for (int destino : arrEncriptar) {
                result += input.charAt(destino);
            }
        } else {
            for (int destino : arrDesencriptar) {
                result += input.charAt(destino);
            }
        }
        return result;
    }
}
