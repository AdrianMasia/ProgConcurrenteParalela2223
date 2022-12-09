#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>

// ============================================================================
int main( int argc, char * argv[] ) {
  int  numProcs, miId;

  // Inicializa MPI.
  MPI_Init( & argc, & argv );
  MPI_Comm_size( MPI_COMM_WORLD, & numProcs );
  MPI_Comm_rank( MPI_COMM_WORLD, & miId );

  // --------------------------------------------------------------------------
  // Comprueba que hay al menos 2 procesos para la version paralela.
  // La version paralela necesita al menos 2 procesos:
  // 1 coordinador y al menos 1 trabajador.
  if( numProcs < 2 ) {
    fprintf( stderr, "ERROR: Debe haber al menos 2 procesos.\n" );
    MPI_Finalize();
    exit( -1 );
  }

  int dato = numProcs - miId + 1;
  int suma, procSig, procAnt;
  MPI_Status st;

  printf( "Soy el proceso %d y mi dato es: %d\n", miId, dato );

  procSig = ( miId + 2 ) % numProcs;
  procAnt = ( miId - 2 + numProcs ) % numProcs;

  if( miId == 0 ) {
        suma = dato;
        MPI_Send( &suma, 1, MPI_INT, procSig, 33, MPI_COMM_WORLD );
        MPI_Recv( &suma, 1, MPI_INT, procAnt, 33, MPI_COMM_WORLD, &st );
        printf( "\n La suma total de todos los datos pares es: %d\n\n", suma );
  } else if ( miId % 2 == 0 ) {
        MPI_Recv( &suma, 1, MPI_INT, procAnt, 33, MPI_COMM_WORLD, &st );
        suma += dato;
        MPI_Send( &suma, 1, MPI_INT, procSig, 33, MPI_COMM_WORLD );
  }
  // --------------------------------------------------------------------------

  // Finalización de MPI.
  MPI_Finalize();

  // Fin de programa.
  printf( "Fin de programa (%d) \n", miId );
  return 0;
}
