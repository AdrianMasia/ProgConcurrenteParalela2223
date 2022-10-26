package la04;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import javax.swing.*;
import javax.swing.event.*;


// ===========================================================================
class ZonaIntercambio {
// ===========================================================================
  AtomicLong tiempo = new AtomicLong(500);

  // -------------------------------------------------------------------------
  void setTiempo( long tiempoMilisegundos ) {
    tiempo.set(tiempoMilisegundos);
  }

  // -------------------------------------------------------------------------
  long getTiempo( ) {
    return tiempo.get();
  }
}

// ===========================================================================
public class GUISecuenciaPrimos {
// ===========================================================================
  JFrame      container;
  JPanel      jpanel;
  JTextField  txfMensajes;
  JButton     btnComienzaSecuencia, btnCancelaSecuencia;
  JSlider     sldEspera;
  HebraCalculadora  t; // Ejercicio 2.2
  ZonaIntercambio   z = new ZonaIntercambio(); // Ejercicio 2.3
  
  // -------------------------------------------------------------------------
  public static void main( String args[] ) {
    GUISecuenciaPrimos gui = new GUISecuenciaPrimos();
    SwingUtilities.invokeLater(new Runnable(){
      public void run(){
        gui.go();
      }
    });
  }

  // -------------------------------------------------------------------------
  public void go() {
    // Constantes.
    final int valorMaximo = 1000;
    final int valorMedio  = 500;

    // Variables.
    JPanel  tempPanel;

    // Crea el JFrame principal.
    container = new JFrame( "GUI Secuencia de Primos " );

    // Consigue el panel principal del Frame "container".
    jpanel = ( JPanel ) container.getContentPane();
    jpanel.setLayout( new GridLayout( 3, 1 ) );

    // Crea e inserta la etiqueta y el campo de texto para los mensajes.
    txfMensajes = new JTextField( 20 );
    txfMensajes.setEditable( false );
    tempPanel = new JPanel();
    tempPanel.setLayout( new FlowLayout() );
    tempPanel.add( new JLabel( "Secuencia: " ) );
    tempPanel.add( txfMensajes );
    jpanel.add( tempPanel );

    // Crea e inserta los botones de Comienza secuencia y Cancela secuencia.
    btnComienzaSecuencia = new JButton( "Comienza secuencia" );
    btnCancelaSecuencia = new JButton( "Cancela secuencia" );
    tempPanel = new JPanel();
    tempPanel.setLayout( new FlowLayout() );
    tempPanel.add( btnComienzaSecuencia );
    tempPanel.add( btnCancelaSecuencia );
    jpanel.add( tempPanel );

    // Crea e inserta el slider para controlar el tiempo de espera.
    sldEspera = new JSlider( JSlider.HORIZONTAL, 0, valorMaximo , valorMedio );
    tempPanel = new JPanel();
    tempPanel.setLayout( new BorderLayout() );
    tempPanel.add( new JLabel( "Tiempo de espera: " ) );
    tempPanel.add( sldEspera );
    jpanel.add( tempPanel );
    
    // Activa inicialmente los 2 botones.
    btnComienzaSecuencia.setEnabled( true );
    btnCancelaSecuencia.setEnabled( false );

    // Anyade codigo para procesar el evento del boton de Comienza secuencia.
    btnComienzaSecuencia.addActionListener( new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
          btnComienzaSecuencia.setEnabled( false );
          btnCancelaSecuencia.setEnabled( true );
          t = new HebraCalculadora(txfMensajes, z);
          t.start();
        }
    } );

    // Anyade codigo para procesar el evento del boton de Cancela secuencia.
    btnCancelaSecuencia.addActionListener( new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
          btnComienzaSecuencia.setEnabled( true );
          btnCancelaSecuencia.setEnabled( false );
          t.fin();
        }
    } );

    // Anyade codigo para procesar el evento del slider " Espera " .
    sldEspera.addChangeListener( new ChangeListener() {
      public void stateChanged( ChangeEvent e ) {
        JSlider sl = ( JSlider ) e.getSource();
        if ( ! sl.getValueIsAdjusting() ) {
          long tiempoMilisegundos = ( long ) sl.getValue();
          System.out.println( "JSlider value = " + tiempoMilisegundos );
          z.setTiempo(tiempoMilisegundos);
        }
      }
    } );

    // Fija caracteristicas del container.
    container.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    container.pack();
    container.setResizable( false );
    container.setVisible( true );

    System.out.println( "% End of routine: go.\n" );
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

class HebraCalculadora extends Thread {
  AtomicBoolean fin = new AtomicBoolean();
  JTextField txfMensajes;
  ZonaIntercambio z;
  AtomicLong tiempo = new AtomicLong();

  public HebraCalculadora(JTextField txfMensajes, ZonaIntercambio z) {
    this.fin.set(false);
    this.txfMensajes = txfMensajes;
    this.z = z;
  }

  public void fin() {
    fin.set(true);
  }

  @Override
  public void run() {
    long i = 1L;
    while (!fin.get()) {
      if(GUISecuenciaPrimos.esPrimo(i)) {
        long primo = i;
        tiempo.set(z.getTiempo());
        try {
          sleep(tiempo.get());
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            String mensaje = Long.toString(primo);
            txfMensajes.setText(mensaje);
          }
        });
      }
      i++;
    }
  }
}

