package la03;

import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.min;

// ===========================================================================
public class EjemploMuestraPrimosEnVector {
// ===========================================================================

  // -------------------------------------------------------------------------
  public static void main( String args[] ) {
    int     numHebras, vectOpt;
    boolean option = true;
    long    t1, t2;
    double  ts, tc, tb, td;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.err.println( "Uso: java programa <numHebras> <vectorOption>" );
      System.exit( -1 );
    }
    try {
      numHebras = Integer.parseInt( args[ 0 ] );
      vectOpt   = Integer.parseInt( args[ 1 ] );
      if ( ( vectOpt != 0 ) && ( vectOpt != 1 ) ) {
        System.out.println( "ERROR: vectorOption should be 0 or 1.");
        System.exit( -1 );
      } else {
        option = (vectOpt == 0);
      }
    } catch( NumberFormatException ex ) {
      numHebras = -1;
      System.out.println( "ERROR: Argumentos numericos incorrectos." );
      System.exit( -1 );
    }

    //
    // Eleccion del vector de trabajo
    //
    VectorNumeros vn = new VectorNumeros (option);
    long vectorNumeros[] = vn.vector;

    //
    // Implementacion secuencial.
    //
    System.out.println( "" );
    System.out.println( "Implementacion secuencial." );
    t1 = System.nanoTime();
    for( int i = 0; i < vectorNumeros.length; i++ ) {
      if( esPrimo( vectorNumeros[ i ] ) ) {
        System.out.println( "  Encontrado primo: " + vectorNumeros[ i ] );
      }
    }
    t2 = System.nanoTime();
    ts = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Tiempo secuencial (seg.):                    " + ts );

    //
    // Implementacion paralela ciclica.
    //
    System.out.println( "" );
    System.out.println( "Implementacion paralela ciclica." );
    t1 = System.nanoTime();
    // Gestion de hebras para la implementacion paralela ciclica
    MiHebraPrimoDistCiclica[] vectorHebras = new MiHebraPrimoDistCiclica[numHebras];
    for(int i = 0; i < numHebras; i++) {
      vectorHebras[i] = new MiHebraPrimoDistCiclica(i, numHebras, vectorNumeros);
      vectorHebras[i].start();
    }
    try {
      for(int i = 0; i < numHebras; i++) {
        vectorHebras[i].join();
      }
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
    t2 = System.nanoTime();
    tc = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Tiempo paralela ciclica (seg.):              " + tc );
    System.out.println( "Incremento paralela ciclica:                 " + ts/tc );
    //
    // Implementacion paralela por bloques.
    //
    System.out.println( "" );
    System.out.println( "Implementacion paralela por bloques." );
    t1 = System.nanoTime();
    // Gestion de hebras para la implementacion paralela por bloques
    MiHebraPrimoDistPorBloques[] vectorHebrasB = new MiHebraPrimoDistPorBloques[numHebras];
    for(int i = 0; i < numHebras; i++) {
      vectorHebrasB[i] = new MiHebraPrimoDistPorBloques(i, numHebras, vectorNumeros);
      vectorHebrasB[i].start();
    }
    try {
      for(int i = 0; i < numHebras; i++) {
        vectorHebrasB[i].join();
      }
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
    t2 = System.nanoTime();
    tb = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Tiempo paralela por bloques (seg.):              " + tb );
    System.out.println( "Incremento paralela por bloques:                 " + ts/tb );
    //
    // Implementacion paralela dinamica.
    //
    System.out.println( "" );
    System.out.println( "Implementacion paralela dinamica." );
    t1 = System.nanoTime();
    // Gestion de hebras para la implementacion paralela dinamica
    MiHebraPrimoDistDinamica[] vectorHebrasD = new MiHebraPrimoDistDinamica[numHebras];
    AtomicInteger puntero = new AtomicInteger(0);
    for(int i = 0; i < numHebras; i++) {
      vectorHebrasD[i] = new MiHebraPrimoDistDinamica(i, numHebras, vectorNumeros, puntero);
      vectorHebrasD[i].start();
    }
    try {
      for(int i = 0; i < numHebras; i++) {
        vectorHebrasD[i].join();
      }
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
    t2 = System.nanoTime();
    td = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Tiempo paralela dinamica (seg.):              " + td );
    System.out.println( "Incremento paralela dinamica:                 " + ts/td );
    //

  }

  // -------------------------------------------------------------------------
  static boolean esPrimo( long num ) {
    boolean primo;
    if( num < 2 ) {
      primo = false;
    } else {
      primo = true;
      long i = 2;
      while( ( i < num )&&( primo ) ) { 
        primo = ( num % i != 0 );
        i++;
      }
    }
    return( primo );
  }
}

// ===========================================================================
class VectorNumeros {
// ===========================================================================
  long    vector[];

  // -------------------------------------------------------------------------
  public VectorNumeros (boolean caso) {
    if (caso) {
      vector = new long [] {
      200000033L, 200000039L, 200000051L, 200000069L,
      200000081L, 200000083L, 200000089L, 200000093L,
      200000107L, 200000117L, 200000123L, 200000131L,
      200000161L, 200000183L, 200000201L, 200000209L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L
			};
    } else {
      vector = new long [] {
      200000033L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000039L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000051L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000069L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000081L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000083L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000089L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000093L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000107L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000117L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000123L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000131L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000161L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000183L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000201L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L,
      200000209L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L, 4L 
      };
    }
  }
}

class MiHebraPrimoDistCiclica extends Thread {
  int miId, numHebras;
  long[] vector;

  public MiHebraPrimoDistCiclica(int miId, int numHebras, long[] vector) {
    this.miId = miId;
    this.numHebras = numHebras;
    this.vector = vector;
  }

  @Override
  public void run() {
    for( int i = miId; i < vector.length; i += numHebras) {
      if( EjemploMuestraPrimosEnVector.esPrimo( vector[ i ] ) ) {
        System.out.println( "  Encontrado primo: " + vector[ i ] );
      }
    }
  }
}

class MiHebraPrimoDistPorBloques extends Thread {
  int miId, numHebras;
  long[] vector;

  public MiHebraPrimoDistPorBloques(int miId, int numHebras, long[] vector) {
    this.miId = miId;
    this.numHebras = numHebras;
    this.vector = vector;
  }

  @Override
  public void run() {
    int tamBloque = (vector.length + numHebras - 1) / numHebras;
    int inicio = miId * tamBloque;
    int fin = min(miId + tamBloque, vector.length);
    for( int i = inicio; i < fin; i++) {
      if( EjemploMuestraPrimosEnVector.esPrimo( vector[ i ] ) ) {
        System.out.println( "  Encontrado primo: " + vector[ i ] );
      }
    }
  }
}

class MiHebraPrimoDistDinamica extends Thread {
  int miId, numHebras;
  long[] vector;
  AtomicInteger puntero;

  public MiHebraPrimoDistDinamica(int miId, int numHebras, long[] vector, AtomicInteger puntero) {
    this.miId = miId;
    this.numHebras = numHebras;
    this.vector = vector;
    this.puntero = puntero;
  }

  @Override
  public void run() {
    int posicion = puntero.getAndIncrement();
    while(posicion < vector.length) {
      if( EjemploMuestraPrimosEnVector.esPrimo( vector[ posicion ] ) ) {
        System.out.println( "  Encontrado primo: " + vector[ posicion ] );
      }
      posicion = puntero.getAndIncrement();
    }
  }
}

