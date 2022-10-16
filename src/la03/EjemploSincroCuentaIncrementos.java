package la03;

// ============================================================================
class SincroCuentaIncrementos {
// ============================================================================
  volatile int numIncrementos = 0;

  // --------------------------------------------------------------------------
  synchronized void incrementaNumIncrementos() {
    numIncrementos++;
  }

  // --------------------------------------------------------------------------
  synchronized int dameNumIncrementos() {
    return( numIncrementos );
  }
}


// ============================================================================
class MiHebraSincro extends Thread {
// ============================================================================
  int                  tope;
  SincroCuentaIncrementos  c;

  // --------------------------------------------------------------------------
  public MiHebraSincro( int tope, SincroCuentaIncrementos c ) {
    this.tope  = tope;
    this.c     = c;
  }

  // --------------------------------------------------------------------------
  public void run() {
    for( int i = 0; i < tope; i++ ) {
      c.incrementaNumIncrementos();
    }
  }
}

// ============================================================================
class EjemploSincroCuentaIncrementos {
// ============================================================================

  // --------------------------------------------------------------------------
  public static void main( String args[] ) {
    long    t1, t2;
    double  tt;
    int     numHebras, tope;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.err.println( "Uso: java programa <numHebras> <tope>" );
      System.exit( -1 );
    }
    try {
      numHebras = Integer.parseInt( args[ 0 ] );
      tope      = Integer.parseInt( args[ 1 ] );
    } catch( NumberFormatException ex ) {
      numHebras = -1;
      tope      = -1;
      System.out.println( "ERROR: Argumentos numericos incorrectos." );
      System.exit( -1 );
    }

    System.out.println( "numHebras: " + numHebras );
    System.out.println( "tope:      " + tope );
    
    System.out.println( "Creando y arrancando " + numHebras + " hebras." );
    t1 = System.nanoTime();
    MiHebraSincro v[] = new MiHebraSincro[ numHebras ];
    SincroCuentaIncrementos c = new SincroCuentaIncrementos();
    for( int i = 0; i < numHebras; i++ ) {
      v[ i ] = new MiHebraSincro( tope, c );
      v[ i ].start();
    }
    for( int i = 0; i < numHebras; i++ ) {
      try {
        v[ i ].join();
      } catch( InterruptedException ex ) {
        ex.printStackTrace();
      }
    }
    t2 = System.nanoTime();
    tt = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.println( "Total de incrementos: " + c.dameNumIncrementos() );
    System.out.println( "Tiempo transcurrido en segs.: " + tt );
  }
}

