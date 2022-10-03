package la01;

class MiHebra3 extends Thread {
  final int miId;

  public MiHebra3(int miId) {
    this.miId = miId;
  }

  public void run() {
    for( int i = 0; i < 100; i++ ) {
      System.out.println( "Ejecutando Hebra Auxiliar " + miId );
    }
  }
}

class EjemploCreacionThreadSimple {
  public static void main( String args[] ) {
    MiHebra3 t = new MiHebra3(0);
    MiHebra3 u = new MiHebra3(1);
    /*t.start();
    u.start();*/
    t.run();
    u.run();
    for( int i = 0; i < 100; i++ ) {
      System.out.println( "Ejecutando Hebra Principal" );
    }
  }
}

