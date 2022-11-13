package la07;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

// ============================================================================
class EjemploPalabraMasUsada {
// ============================================================================

  // -------------------------------------------------------------------------
  public static void main( String args[] ) {
    long                     t1, t2;
    double                   tt, tp1, tp2, tp3, tp4, tp5, tp6, tp7;
    int                      numHebras;
    String                   nombreFichero, palabraActual;
    Vector<String>           vectorLineas;
    HashMap<String,Integer>  hmCuentaPalabras, maCuentaPalabras;

    // Comprobacion y extraccion de los argumentos de entrada.
    if( args.length != 2 ) {
      System.err.println( "Uso: java programa <numHebras> <fichero>" );
      System.exit( -1 );
    }
    try {
      numHebras     = Integer.parseInt( args[ 0 ] );
      nombreFichero = args[ 1 ];
    } catch( NumberFormatException ex ) {
      numHebras = -1;
      nombreFichero = "";
      System.out.println( "ERROR: Argumentos numericos incorrectos." );
      System.exit( -1 );
    }

    // Lectura y carga de lineas en "vectorLineas".
    vectorLineas = readFile( nombreFichero );
    System.out.println( "Numero de lineas leidas: " + vectorLineas.size() );
    System.out.println();

    //
    // Implementacion secuencial sin temporizar.
    //
    hmCuentaPalabras = new HashMap<String,Integer>( 1000, 0.75F );
    for( int i = 0; i < vectorLineas.size(); i++ ) {
      // Procesa la linea "i".
      String[] palabras = vectorLineas.get( i ).split( "\\W+" );
      for( int j = 0; j < palabras.length; j++ ) {
        // Procesa cada palabra de la linea "i", si es distinta de blanco.
        palabraActual = palabras[ j ].trim();
        if( palabraActual.length() > 0 ) {
          contabilizaPalabra( hmCuentaPalabras, palabraActual );
        }
      }
    }

    //
    // Implementacion secuencial.
    //
    t1 = System.nanoTime();
    hmCuentaPalabras = new HashMap<String,Integer>( 1000, 0.75F );
    for( int i = 0; i < vectorLineas.size(); i++ ) {
      // Procesa la linea "i".
      String[] palabras = vectorLineas.get( i ).split( "\\W+" );
      for( int j = 0; j < palabras.length; j++ ) {
        // Procesa cada palabra de la linea "i", si es distinta de blanco.
        palabraActual = palabras[ j ].trim();
        if( palabraActual.length() > 0 ) {
          contabilizaPalabra( hmCuentaPalabras, palabraActual );
        }
      }
    }
    t2 = System.nanoTime();
    tt = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.print( "Implementacion secuencial: " );
    imprimePalabraMasUsadaYVeces( hmCuentaPalabras );
    System.out.println( " Tiempo(s): " + tt );
    System.out.println( "Num. elems. tabla hash: " + hmCuentaPalabras.size() );
    System.out.println();

    //
    // Implementacion paralela 1: Uso de synchronizedMap.
    //
    t1 = System.nanoTime();
    maCuentaPalabras = new HashMap<String,Integer>( 1000, 0.75F );
    MiHebra_1 vh1[] = new MiHebra_1[numHebras];
    for(int i = 0; i < numHebras; i++) {
      vh1[i] = new MiHebra_1(numHebras, i, maCuentaPalabras, vectorLineas);
      vh1[i].start();
    }
    for(int i = 0; i < numHebras; i++) {
      try{
        vh1[i].join();
      } catch (InterruptedException ex) {ex.printStackTrace();}
    }
    t2 = System.nanoTime();
    tp1 = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.print( "Implementacion paralela 1: " );
    imprimePalabraMasUsadaYVeces( maCuentaPalabras );
    System.out.println( " Tiempo(s): " + tp1  + " , Incremento " + tt/tp1);
    System.out.println( "Num. elems. tabla hash: " + maCuentaPalabras.size() );
    System.out.println();

    //
    // Implementacion paralela 2: Uso de Hashtable.
    //
    t1 = System.nanoTime();
    Hashtable<String, Integer> mbCuentaPalabras = new Hashtable<>( 1000, 0.75F );
    MiHebra_1 vh2[] = new MiHebra_1[numHebras];
    for(int i = 0; i < numHebras; i++) {
      vh2[i] = new MiHebra_1(numHebras, i, mbCuentaPalabras, vectorLineas);
      vh2[i].start();
    }
    for(int i = 0; i < numHebras; i++) {
      try{
        vh2[i].join();
      } catch (InterruptedException ex) {ex.printStackTrace();}
    }
    t2 = System.nanoTime();
    tp2 = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.print( "Implementacion paralela 2: " );
    imprimePalabraMasUsadaYVeces( mbCuentaPalabras );
    System.out.println( " Tiempo(s): " + tp2  + " , Incremento " + tt/tp2);
    System.out.println( "Num. elems. tabla hash: " + mbCuentaPalabras.size() );
    System.out.println();

    //
    // Implementacion paralela 3: Uso de ConcurrentHashMap
    //
    t1 = System.nanoTime();
    ConcurrentHashMap<String, Integer> mcCuentaPalabras =
            new ConcurrentHashMap<>( 1000, 0.75F );
    MiHebra_1 vh3[] = new MiHebra_1[numHebras];
    for(int i = 0; i < numHebras; i++) {
      vh3[i] = new MiHebra_1(numHebras, i, mcCuentaPalabras, vectorLineas);
      vh3[i].start();
    }
    for(int i = 0; i < numHebras; i++) {
      try{
        vh3[i].join();
      } catch (InterruptedException ex) {ex.printStackTrace();}
    }
    t2 = System.nanoTime();
    tp3 = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.print( "Implementacion paralela 3: " );
    imprimePalabraMasUsadaYVeces( mcCuentaPalabras );
    System.out.println( " Tiempo(s): " + tp3  + " , Incremento " + tt/tp3);
    System.out.println( "Num. elems. tabla hash: " + mcCuentaPalabras.size() );
    System.out.println();

    //
    // Implementacion paralela 4: Uso de ConcurrentHashMap 
    //
    t1 = System.nanoTime();
    ConcurrentHashMap<String, Integer> mdCuentaPalabras =
            new ConcurrentHashMap<>( 1000, 0.75F );
    MiHebra_4 vh4[] = new MiHebra_4[numHebras];
    for(int i = 0; i < numHebras; i++) {
      vh4[i] = new MiHebra_4(numHebras, i, mdCuentaPalabras, vectorLineas);
      vh4[i].start();
    }
    for(int i = 0; i < numHebras; i++) {
      try{
        vh4[i].join();
      } catch (InterruptedException ex) {ex.printStackTrace();}
    }
    t2 = System.nanoTime();
    tp4 = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.print( "Implementacion paralela 4: " );
    imprimePalabraMasUsadaYVeces( mdCuentaPalabras );
    System.out.println( " Tiempo(s): " + tp4  + " , Incremento " + tt/tp4);
    System.out.println( "Num. elems. tabla hash: " + mdCuentaPalabras.size() );
    System.out.println();

    //
    // Implementacion paralela 5: Uso de ConcurrentHashMap
    //
    t1 = System.nanoTime();
    ConcurrentHashMap<String, AtomicInteger> meCuentaPalabras =
            new ConcurrentHashMap<>( 1000, 0.75F );
    MiHebra_5 vh5[] = new MiHebra_5[numHebras];
    for(int i = 0; i < numHebras; i++) {
      vh5[i] = new MiHebra_5(numHebras, i, meCuentaPalabras, vectorLineas);
      vh5[i].start();
    }
    for(int i = 0; i < numHebras; i++) {
      try{
        vh5[i].join();
      } catch (InterruptedException ex) {ex.printStackTrace();}
    }
    t2 = System.nanoTime();
    tp5 = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.print( "Implementacion paralela 5: " );
    imprimePalabraMasUsadaYVecesSincro( meCuentaPalabras );
    System.out.println( " Tiempo(s): " + tp5  + " , Incremento " + tt/tp5);
    System.out.println( "Num. elems. tabla hash: " + meCuentaPalabras.size() );
    System.out.println();

    //
    // Implementacion paralela 6: Uso de ConcurrentHashMap 
    //
    t1 = System.nanoTime();
    ConcurrentHashMap<String, AtomicInteger> mfCuentaPalabras =
            new ConcurrentHashMap<>( 1000, 0.75F, 256 );
    MiHebra_5 vh6[] = new MiHebra_5[numHebras];
    for(int i = 0; i < numHebras; i++) {
      vh6[i] = new MiHebra_5(numHebras, i, mfCuentaPalabras, vectorLineas);
      vh6[i].start();
    }
    for(int i = 0; i < numHebras; i++) {
      try{
        vh6[i].join();
      } catch (InterruptedException ex) {ex.printStackTrace();}
    }
    t2 = System.nanoTime();
    tp6 = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.print( "Implementacion paralela 6: " );
    imprimePalabraMasUsadaYVecesSincro( mfCuentaPalabras );
    System.out.println( " Tiempo(s): " + tp6  + " , Incremento " + tt/tp6);
    System.out.println( "Num. elems. tabla hash: " + mfCuentaPalabras.size() );
    System.out.println();

    //
    // Implementacion paralela 7: Uso de Streams
    // t1 = System.nanoTime();
     Map<String,Long> stCuentaPalabras = vectorLineas.parallelStream()
                                           .filter( s -> s != null )
                                           .map( s -> s.split( "\\W+" ) )
                                           .flatMap( Arrays::stream )
                                           .map( String::trim )
                                           .filter( s -> (s.length() > 0) )
                                           .collect( groupingBy (s -> s, counting()));
     t2 = System.nanoTime();
    tp7 = ( ( double ) ( t2 - t1 ) ) / 1.0e9;
    System.out.print( "Implementacion paralela 7: " );
    imprimePalabraMasUsadaYVecesStream( stCuentaPalabras );
    System.out.println( " Tiempo(s): " + tp7  + " , Incremento " + tt/tp7);
    System.out.println( "Num. elems. tabla hash: " + stCuentaPalabras.size() );
    System.out.println();

    System.out.println( "Fin de programa." );
  }

  // -------------------------------------------------------------------------
  public static Vector<String> readFile( String fileName ) {
    BufferedReader br; 
    String         linea;
    Vector<String> data = new Vector<String>();

    try {
      br = new BufferedReader( new FileReader( fileName ) );
      while( ( linea = br.readLine() ) != null ) {
        //// System.out.println( "Leida linea: " + linea );
        data.add( linea );
      }
      br.close(); 
    } catch( FileNotFoundException ex ) {
      ex.printStackTrace();
    } catch( IOException ex ) {
      ex.printStackTrace();
    }
    return data;
  }

  // -------------------------------------------------------------------------
  public static void contabilizaPalabra( 
                         HashMap<String,Integer> cuentaPalabras,
                         String palabra ) {
    Integer numVeces = cuentaPalabras.get( palabra );
    if( numVeces != null ) {
      cuentaPalabras.put( palabra, numVeces+1 );
    } else {
      cuentaPalabras.put( palabra, 1 );
    }
  }

  // -------------------------------------------------------------------------
  public static synchronized void contabilizaPalabraSincro(
          Map<String,Integer> cuentaPalabras,
          String palabra ) {
    /*Integer numVeces = cuentaPalabras.putIfAbsent(palabra, 1);
    if(numVeces != null) {
      int veces = numVeces;
      boolean cambiado;
      while(true) {
        cambiado = cuentaPalabras.replace(palabra, veces, veces + 1);
        if(cambiado) break;
        veces = cuentaPalabras.get(palabra);
      }
    }*/
    cuentaPalabras.merge(palabra, 1, Integer::sum);
  }

  // -------------------------------------------------------------------------
  public static void contabilizaPalabraSincro2(
          ConcurrentHashMap<String, Integer> cuentaPalabras,
          String palabra ) {
    Integer numVeces = cuentaPalabras.putIfAbsent(palabra, 1);
    if(numVeces != null) {
      int veces = numVeces;
      boolean cambiado;
      while(true) {
        cambiado = cuentaPalabras.replace(palabra, veces, veces + 1);
        if(cambiado) break;
        veces = cuentaPalabras.get(palabra);
      }
    }
  }

  // -------------------------------------------------------------------------
  public static void contabilizaPalabraSincro3(
          ConcurrentHashMap<String, AtomicInteger> cuentaPalabras,
          String palabra ) {
    AtomicInteger numVeces = cuentaPalabras.putIfAbsent(palabra, new AtomicInteger(1));
    if(numVeces != null) {
      numVeces.getAndIncrement();
    }
  }

  // --------------------------------------------------------------------------
  static void imprimePalabraMasUsadaYVeces(
                  Map<String,Integer> cuentaPalabras ) {
    Vector<Map.Entry> lista = 
        new Vector<Map.Entry>( cuentaPalabras.entrySet() );

    String palabraMasUsada = "";
    int    numVecesPalabraMasUsada = 0;
    // Calcula la palabra mas usada.
    for( int i = 0; i < lista.size(); i++ ) {
      String palabra = ( String ) lista.get( i ).getKey();
      int numVeces = ( Integer ) lista.get( i ).getValue();
      if( i == 0 ) {
        palabraMasUsada = palabra;
        numVecesPalabraMasUsada = numVeces;
      } else if( numVecesPalabraMasUsada < numVeces ) {
        palabraMasUsada = palabra;
        numVecesPalabraMasUsada = numVeces;
      }
    }
    // Imprime resultado.
    System.out.print( "( Palabra: '" + palabraMasUsada + "' " + 
                         "veces: " + numVecesPalabraMasUsada + " )" );
  }

  // --------------------------------------------------------------------------
  static void imprimePalabraMasUsadaYVecesSincro(
          Map<String,AtomicInteger> cuentaPalabras ) {
    Vector<Map.Entry> lista =
            new Vector<Map.Entry>( cuentaPalabras.entrySet() );

    String palabraMasUsada = "";
    int    numVecesPalabraMasUsada = 0;
    // Calcula la palabra mas usada.
    for( int i = 0; i < lista.size(); i++ ) {
      String palabra = ( String ) lista.get( i ).getKey();
      AtomicInteger numVeces = ( AtomicInteger ) lista.get( i ).getValue();
      if( i == 0 ) {
        palabraMasUsada = palabra;
        numVecesPalabraMasUsada = numVeces.get();
      } else if( numVecesPalabraMasUsada < numVeces.get() ) {
        palabraMasUsada = palabra;
        numVecesPalabraMasUsada = numVeces.get();
      }
    }
    // Imprime resultado.
    System.out.print( "( Palabra: '" + palabraMasUsada + "' " +
            "veces: " + numVecesPalabraMasUsada + " )" );
  }

  // --------------------------------------------------------------------------
  static void imprimePalabraMasUsadaYVecesStream(
          Map<String,Long> cuentaPalabras ) {
    Vector<Map.Entry> lista =
            new Vector<Map.Entry>( cuentaPalabras.entrySet() );

    String palabraMasUsada = "";
    long    numVecesPalabraMasUsada = 0;
    // Calcula la palabra mas usada.
    for( int i = 0; i < lista.size(); i++ ) {
      String palabra = ( String ) lista.get( i ).getKey();
      long numVeces = ( Long ) lista.get( i ).getValue();
      if( i == 0 ) {
        palabraMasUsada = palabra;
        numVecesPalabraMasUsada = numVeces;
      } else if( numVecesPalabraMasUsada < numVeces ) {
        palabraMasUsada = palabra;
        numVecesPalabraMasUsada = numVeces;
      }
    }
    // Imprime resultado.
    System.out.print( "( Palabra: '" + palabraMasUsada + "' " +
            "veces: " + numVecesPalabraMasUsada + " )" );
  }

  // --------------------------------------------------------------------------
  static void printCuentaPalabrasOrdenadas(
                  HashMap<String,Integer> cuentaPalabras ) {
    int             i, numVeces;
    List<Map.Entry> list = new Vector<Map.Entry>( cuentaPalabras.entrySet() );

    // Ordena por valor.
    Collections.sort( 
        list,
        new Comparator<Map.Entry>() {
            public int compare( Map.Entry e1, Map.Entry e2 ) {
              Integer i1 = ( Integer ) e1.getValue();
              Integer i2 = ( Integer ) e2.getValue();
              return i2.compareTo( i1 );
            }
        }
    );
    // Muestra contenido.
    i = 1;
    System.out.println( "Veces Palabra" );
    System.out.println( "-----------------" );
    for( Map.Entry e : list ) {
      numVeces = ( ( Integer ) e.getValue () ).intValue();
      System.out.println( i + " " + e.getKey() + " " + numVeces );
      i++;
    }
    System.out.println( "-----------------" );
  }
}

class MiHebra_1 extends Thread {
  Map<String, Integer> maCuentaPalabras;
  int numHebras, miId;
  Vector<String> vectorLineas;

  public MiHebra_1(int numHebras, int miId, Map<String, Integer> maCuentaPalabras,
                   Vector<String> vectorLineas) {
    this.numHebras = numHebras;
    this.miId = miId;
    this.maCuentaPalabras = maCuentaPalabras;
    this.vectorLineas = vectorLineas;
  }

  @Override
  public void run() {
    String palabraActual;
    for( int i = miId; i < vectorLineas.size(); i += numHebras ) {
      // Procesa la linea "i".
      String[] palabras = vectorLineas.get( i ).split( "\\W+" );
      for( int j = 0; j < palabras.length; j++ ) {
        // Procesa cada palabra de la linea "i", si es distinta de blanco.
        palabraActual = palabras[ j ].trim();
        if( palabraActual.length() > 0 ) {
          EjemploPalabraMasUsada.contabilizaPalabraSincro( maCuentaPalabras, palabraActual );
        }
      }
    }
  }
}

class MiHebra_4 extends Thread {
  ConcurrentHashMap<String, Integer> mdCuentaPalabras;
  int numHebras, miId;
  Vector<String> vectorLineas;

  public MiHebra_4(int numHebras, int miId, ConcurrentHashMap<String, Integer> mdCuentaPalabras,
                   Vector<String> vectorLineas) {
    this.numHebras = numHebras;
    this.miId = miId;
    this.mdCuentaPalabras = mdCuentaPalabras;
    this.vectorLineas = vectorLineas;
  }

  @Override
  public void run() {
    String palabraActual;
    for( int i = miId; i < vectorLineas.size(); i += numHebras ) {
      // Procesa la linea "i".
      String[] palabras = vectorLineas.get( i ).split( "\\W+" );
      for( int j = 0; j < palabras.length; j++ ) {
        // Procesa cada palabra de la linea "i", si es distinta de blanco.
        palabraActual = palabras[ j ].trim();
        if( palabraActual.length() > 0 ) {
          EjemploPalabraMasUsada.contabilizaPalabraSincro2( mdCuentaPalabras, palabraActual );
        }
      }
    }
  }
}

class MiHebra_5 extends Thread {
  ConcurrentHashMap<String, AtomicInteger> meCuentaPalabras;
  int numHebras, miId;
  Vector<String> vectorLineas;

  public MiHebra_5(int numHebras, int miId, ConcurrentHashMap<String, AtomicInteger> meCuentaPalabras,
                   Vector<String> vectorLineas) {
    this.numHebras = numHebras;
    this.miId = miId;
    this.meCuentaPalabras = meCuentaPalabras;
    this.vectorLineas = vectorLineas;
  }

  @Override
  public void run() {
    String palabraActual;
    for( int i = miId; i < vectorLineas.size(); i += numHebras ) {
      // Procesa la linea "i".
      String[] palabras = vectorLineas.get( i ).split( "\\W+" );
      for( int j = 0; j < palabras.length; j++ ) {
        // Procesa cada palabra de la linea "i", si es distinta de blanco.
        palabraActual = palabras[ j ].trim();
        if( palabraActual.length() > 0 ) {
          EjemploPalabraMasUsada.contabilizaPalabraSincro3( meCuentaPalabras, palabraActual );
        }
      }
    }
  }
}

