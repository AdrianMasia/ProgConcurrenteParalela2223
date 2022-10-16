package la02;

import static java.lang.Math.min;

// ============================================================================
class EjemploMuestraNumeros {
// ============================================================================

  // --------------------------------------------------------------------------
  public static void main( String args[] ) {
    int  n, numHebras;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.err.println( "Uso: java programa <numHebras> <n>" );
      System.exit( -1 );
    }
    try {
      numHebras = Integer.parseInt( args[ 0 ] );
      n         = Integer.parseInt( args[ 1 ] );
    } catch( NumberFormatException ex ) {
      numHebras = -1;
      n         = -1;
      System.out.println( "ERROR: Argumentos numericos incorrectos." );
      System.exit( -1 );
    }
    //
    // Implementacion paralela con distribucion ciclica o por bloques.
    //
    class MiHebraCiclo extends Thread {
      final int miId, n, numHebras;

      public MiHebraCiclo(int miId, int n, int numHebras) {
        this.miId = miId;
        this.n = n;
        this.numHebras = numHebras;
      }

      @Override
      public void run() {
        int inicio = miId, fin = n, incremento = numHebras;
        for(int i = inicio; i < fin; i += incremento) {
          System.out.println(i);
        }
      }
    }

    class MiHebraBloque extends Thread {
      final int miId, n, numHebras, tamBloque;

      public MiHebraBloque(int miId, int n, int numHebras) {
        this.miId = miId;
        this.n = n;
        this.numHebras = numHebras;
        this.tamBloque = (n + numHebras - 1) / numHebras;
      }

      @Override
      public void run() {
        int inicio = miId * tamBloque, fin = min(inicio + tamBloque, n), incremento = 1;
        for(int i = inicio; i < fin; i += incremento) {
          System.out.println(i);
        }
      }
    }

/*    // Crea y arranca el vector de hebras.
    MiHebraCiclo[] vc = new MiHebraCiclo[numHebras];
    for(int i = 0; i < numHebras; i++) {
      vc[i] = new MiHebraCiclo(i, n, numHebras);
      vc[i].start();
    }*/

    // Crea y arranca el vector de hebras.
    MiHebraBloque[] vb = new MiHebraBloque[numHebras];
    for(int i = 0; i < numHebras; i++) {
      vb[i] = new MiHebraBloque(i, n, numHebras);
      vb[i].start();
    }

/*    // Espera a que terminen las hebras.
    try {
      for (int i = 0; i < numHebras; i++) {
        vc[i].join();
      }
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }*/

    // Espera a que terminen las hebras.
    try {
      for (int i = 0; i < numHebras; i++) {
        vb[i].join();
      }
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }

  }
}
