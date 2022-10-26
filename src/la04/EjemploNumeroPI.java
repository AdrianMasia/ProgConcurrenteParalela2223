package la04;

import java.util.concurrent.atomic.DoubleAdder;

// ===========================================================================
class Acumula {
// ===========================================================================
  double  suma;

  // -------------------------------------------------------------------------
  Acumula() {
    super();
  }

  // -------------------------------------------------------------------------
  synchronized void acumulaDato( double dato ) {
    suma += dato;
  }

  // -------------------------------------------------------------------------
  synchronized double dameDato() {
    return suma;
  }
}

// ===========================================================================
class MiHebraMultAcumulaciones extends Thread {
// ===========================================================================
  int      miId, numHebras;
  long     numRectangulos;
  Acumula  a;

  // -------------------------------------------------------------------------
  MiHebraMultAcumulaciones( int miId, int numHebras, long numRectangulos,
                              Acumula a ) {
    this.miId = miId;
    this.numHebras = numHebras;
    this.numRectangulos = numRectangulos;
    this.a = a;
  }

  // -------------------------------------------------------------------------
  public void run() {
    double baseRectangulo = 1.0 / ( ( double ) numRectangulos );
    double x, dato;
    for(int i = miId; i < numRectangulos; i += numHebras) {
      x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
      dato = 4.0/( 1.0 + x*x );
      a.acumulaDato(dato);
    }
  }
}

// ===========================================================================
class MiHebraUnaAcumulacion extends Thread {
// ===========================================================================
  int      miId, numHebras;
  long     numRectangulos;
  Acumula  a;

  // -------------------------------------------------------------------------
  MiHebraUnaAcumulacion( int miId, int numHebras, long numRectangulos,
                              Acumula a ) {
    this.miId = miId;
    this.numHebras = numHebras;
    this.numRectangulos = numRectangulos;
    this.a = a;
  }

  // -------------------------------------------------------------------------
  public void run() {
    double baseRectangulo = 1.0 / ( ( double ) numRectangulos );
    double x, sumaL = 0.0;
    for(int i = miId; i < numRectangulos; i += numHebras) {
      x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
      sumaL += 4.0/( 1.0 + x*x );
    }
    a.acumulaDato(sumaL);
  }
}

// ===========================================================================
class MiHebraMultAcumulacionAtomica extends Thread {
// ===========================================================================
  DoubleAdder acumulacion;
  int      miId, numHebras;
  long     numRectangulos;

  // -------------------------------------------------------------------------
  MiHebraMultAcumulacionAtomica( int miId, int numHebras, long numRectangulos,
                                 DoubleAdder acumulacion ) {
    this.miId = miId;
    this.numHebras = numHebras;
    this.numRectangulos = numRectangulos;
    this.acumulacion = acumulacion;
  }

  // -------------------------------------------------------------------------
  public void run() {
    double baseRectangulo = 1.0 / ( ( double ) numRectangulos );
    double x, dato;
    for(int i = miId; i < numRectangulos; i += numHebras) {
      x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
      dato = 4.0/( 1.0 + x*x );
      acumulacion.add(dato);
    }
  }
}

// ===========================================================================
class MiHebraUnaAcumulacionAtomica extends Thread {
// ===========================================================================
  DoubleAdder acumulacion;
  int      miId, numHebras;
  long     numRectangulos;

  // -------------------------------------------------------------------------
  MiHebraUnaAcumulacionAtomica( int miId, int numHebras, long numRectangulos,
                                DoubleAdder acumulacion ) {
    this.miId = miId;
    this.numHebras = numHebras;
    this.numRectangulos = numRectangulos;
    this.acumulacion = acumulacion;
  }

  // -------------------------------------------------------------------------
  public void run() {
    double baseRectangulo = 1.0 / ( ( double ) numRectangulos );
    double x, sumaL = 0.0;
    for(int i = miId; i < numRectangulos; i += numHebras) {
      x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
      sumaL += 4.0/( 1.0 + x*x );
    }
    acumulacion.add(sumaL);
  }
}

// ===========================================================================
class EjemploNumeroPI {
// ===========================================================================

  // -------------------------------------------------------------------------
  public static void main( String args[] ) {
    long                        numRectangulos;
    double                      baseRectangulo, x, suma, pi;
    int                         numHebras;
    long                        t1, t2;
    double                      tSec, tPar;
    Acumula                     a;
    MiHebraMultAcumulaciones    vt[];
    MiHebraUnaAcumulacion[]     vtu;
    DoubleAdder                 acumulacion;
    MiHebraMultAcumulacionAtomica[] vta;
    MiHebraUnaAcumulacionAtomica[]  vtau;

    // Comprobacion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.out.println( "ERROR: numero de argumentos incorrecto.");
      System.out.println( "Uso: java programa <numHebras> <numRectangulos>" );
      System.exit( -1 );
    }
    try {
      numHebras      = Integer.parseInt( args[ 0 ] );
      numRectangulos = Long.parseLong( args[ 1 ] );
    } catch( NumberFormatException ex ) {
      numHebras      = -1;
      numRectangulos = -1;
      System.out.println( "ERROR: Numeros de entrada incorrectos." );
      System.exit( -1 );
    }

    System.out.println();
    System.out.println( "Calculo del numero PI mediante integracion." );

    //
    // Calculo del numero PI de forma secuencial.
    //
    System.out.println();
    System.out.println( "Comienzo del calculo secuencial." );
    t1 = System.nanoTime();
    baseRectangulo = 1.0 / ( ( double ) numRectangulos );
    suma           = 0.0;
    for( long i = 0; i < numRectangulos; i++ ) {
      x = baseRectangulo * ( ( ( double ) i ) + 0.5 );
      suma += f( x );
    }
    pi = baseRectangulo * suma;
    t2 = System.nanoTime();
    tSec = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Version secuencial. Numero PI: " + pi );
    System.out.println( "Tiempo secuencial (s.):        " + tSec );

    //
    // Calculo del numero PI de forma paralela: 
    // Multiples acumulaciones por hebra.
    //
    System.out.println();
    System.out.print( "Comienzo del calculo paralelo: " );
    System.out.println( "Multiples acumulaciones por hebra." );
    t1 = System.nanoTime();
    a = new Acumula();
    vt = new MiHebraMultAcumulaciones[numHebras];
    for(int i = 0; i < numHebras; i++) {
      vt[i] = new MiHebraMultAcumulaciones(i, numHebras, numRectangulos, a);
      vt[i].start();
    }
    try {
      for(int i = 0; i < numHebras; i++) {
        vt[i].join();
      }
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
    pi = baseRectangulo * a.dameDato();
    t2 = System.nanoTime();
    tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Calculo del numero PI:   " + pi );
    System.out.println( "Tiempo ejecucion (s.):   " + tPar );
    System.out.println( "Incremento velocidad :   " + tSec/tPar );

    //
    // Calculo del numero PI de forma paralela: 
    // Una acumulacion por hebra.
    //
    System.out.println();
    System.out.print( "Comienzo del calculo paralelo: " );
    System.out.println( "Una acumulacion por hebra." );
    t1 = System.nanoTime();
    a = new Acumula();
    vtu = new MiHebraUnaAcumulacion[numHebras];
    for(int i = 0; i < numHebras; i++) {
      vtu[i] = new MiHebraUnaAcumulacion(i, numHebras, numRectangulos, a);
      vtu[i].start();
    }
    try {
      for(int i = 0; i < numHebras; i++) {
        vtu[i].join();
      }
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
    pi = baseRectangulo * a.dameDato();
    t2 = System.nanoTime();
    tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Calculo del numero PI:   " + pi );
    System.out.println( "Tiempo ejecucion (s.):   " + tPar );
    System.out.println( "Incremento velocidad :   " + tSec/tPar );

    //
    // Calculo del numero PI de forma paralela: 
    // Multiples acumulaciones por hebra (Atomica)
    //
    System.out.println();
    System.out.print( "Comienzo del calculo paralelo: " );
    System.out.println( "Multiples acumulaciones por hebra (At)." );
    t1 = System.nanoTime();
    acumulacion = new DoubleAdder();
    vta = new MiHebraMultAcumulacionAtomica[numHebras];
    for(int i = 0; i < numHebras; i++) {
      vta[i] = new MiHebraMultAcumulacionAtomica(i, numHebras, numRectangulos, acumulacion);
      vta[i].start();
    }
    try {
      for(int i = 0; i < numHebras; i++) {
        vta[i].join();
      }
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
    pi = baseRectangulo * acumulacion.sum();
    t2 = System.nanoTime();
    tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Calculo del numero PI:   " + pi );
    System.out.println( "Tiempo ejecucion (s.):   " + tPar );
    System.out.println( "Incremento velocidad :   " + tSec/tPar );

    //
    // Calculo del numero PI de forma paralela: 
    // Una acumulacion por hebra (Atomica).
    //
    System.out.println();
    System.out.print( "Comienzo del calculo paralelo: " );
    System.out.println( "Una acumulacion por hebra (At)." );
    t1 = System.nanoTime();
    acumulacion = new DoubleAdder();
    vtau = new MiHebraUnaAcumulacionAtomica[numHebras];
    for(int i = 0; i < numHebras; i++) {
      vtau[i] = new MiHebraUnaAcumulacionAtomica(i, numHebras, numRectangulos, acumulacion);
      vtau[i].start();
    }
    try {
      for(int i = 0; i < numHebras; i++) {
        vtau[i].join();
      }
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
    pi = baseRectangulo * acumulacion.sum();
    t2 = System.nanoTime();
    tPar = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Calculo del numero PI:   " + pi );
    System.out.println( "Tiempo ejecucion (s.):   " + tPar );
    System.out.println( "Incremento velocidad :   " + tSec/tPar );

    System.out.println();
    System.out.println( "Fin de programa." );
  }

  // -------------------------------------------------------------------------
  static double f( double x ) {
    return ( 4.0/( 1.0 + x*x ) );
  }
}

