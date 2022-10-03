/*
package la01;  // --------------------------------------------------------------------------
  public static void main( String args[] ) {
    int  numHebras;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 1 ) {
      System.err.println( "Uso: java programa <numHebras>" );
      System.exit( -1 );
    }
    try {
      numHebras = Integer.parseInt( args[ 0 ] );
    } catch( NumberFormatException ex ) {
      numHebras = -1;
      System.out.println( "ERROR: Argumentos numericos incorrectos." );
      System.exit( -1 );
    }

    System.out.println( "numHebras: " + numHebras );
   
    // --------  CODIGO PRINCIPAL TRAS PROCESAR PARAMETROS  -------------------

    System.out.println( "Hebra Principal inicia" );
    // Crear un vector (v) de tipo MiHebra con numHebras elementos
    MiHebra v[] = new MiHebra[ numHebras ];
    // Crear y arrancar las hebras y almacenarlas en v
    // ...
    // Esperar a que terminen todas las hebras almacenadas en v
    // ...
    System.out.println( "Hebra Principal finaliza" );
  }
  // --------------------------------------------------------------------------
*/
