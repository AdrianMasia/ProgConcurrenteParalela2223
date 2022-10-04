package la01;  // --------------------------------------------------------------------------
class EjemploCreacionThreadMulti {
    public static void main(String args[]) {
        int numHebras;

        // Comprobacion y extraccion de los argumentos de entrada.
        if (args.length != 1) {
            System.err.println("Uso: java programa <numHebras>");
            System.exit(-1);
        }
        try {
            numHebras = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            numHebras = -1;
            System.out.println("ERROR: Argumentos numericos incorrectos.");
            System.exit(-1);
        }

        System.out.println("numHebras: " + numHebras);

        // --------  CODIGO PRINCIPAL TRAS PROCESAR PARAMETROS  -------------------

        System.out.println("Hebra Principal inicia");
        // Crear un vector (v) de tipo MiHebra con numHebras elementos
        MiHebra v[] = new MiHebra[numHebras];
        // Crear y arrancar las hebras y almacenarlas en v
        for (int i = 0; i < numHebras; i++) {
            MiHebra t = new MiHebra(i, 1, 1000000);
            v[i] = t;
            t.start();
        }
        // Esperar a que terminen todas las hebras almacenadas en v
        try {
            for (int i = 0; i < numHebras; i++) {
                v[i].join();
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.println("Hebra Principal finaliza");
    }
    // --------------------------------------------------------------------------
}