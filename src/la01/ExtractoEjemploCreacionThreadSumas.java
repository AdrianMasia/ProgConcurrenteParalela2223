package la01;

class MiHebra extends Thread {
  final int miId;
  final int n1;
  final int n2;
  public MiHebra( int miId, int n1, int n2 ) {
    this.miId = miId;
    this.n1 = n1;
    this.n2 = n2;
  }
  public void run() {
    long suma = 0;
    for( int i = n1; i <= n2; i++ ) {
      suma += i;
    }
    System.out.println( "Hebra Auxiliar " + miId + " , suma: " + suma);
  }
}

class EjemploCreacionThreadSumas {
  public static void main( String args[] ) {

    System.out.println( "Hebra Principal inicia" );

    // Crea y arranca hebra t0 sumando desde 1 hasta 1000000
    MiHebra t0 = new MiHebra(0, 1, 1000000);
//    t0.setDaemon(true);
    t0.start();
    // Crea y arranca hebra t1 sumando desde 1 hasta 1000000
    MiHebra t1 = new MiHebra(1, 1, 1000000);
//    t1.setDaemon(true);
    t1.start();
    try {
      t0.join();
      t1.join();
    } catch (InterruptedException ex) {ex.printStackTrace();}

    System.out.println( "Hebra Principal finaliza" );
  }
}

