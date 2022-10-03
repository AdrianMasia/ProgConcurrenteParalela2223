package la01;

class MiRun implements Runnable {
  final int miId;

  public MiRun(int miId) {
    this.miId = miId;
  }

  public void run() {
    for( int i = 0; i < 100; i++ ) {
      System.out.println( "Ejecutando Hebra Auxiliar " + miId );
    }
  }
}

class EjemploCreacionRunnableSimple {
  public static void main( String args[] ) {
    Thread t = new Thread(new MiRun(0));
    Thread t1 = new Thread(new MiRun(1));
    /*t.start();
    t1.start();*/
    t.run( );
    t1.run();
    for( int i = 0; i < 100; i++ ) {
      System.out.println( "Ejecutando Hebra Principal" );
    }
  }
}
