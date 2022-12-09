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
  int suma, datoCondicional;

  printf( "Soy el proceso %d y mi dato es: %d\n", miId, dato );

  datoCondicional = ( miId % 2 != 0 ? 0 : dato );

  MPI_Reduce ( &datoCondicional, &suma, 1, MPI_INT, MPI_SUM, 0, MPI_COMM_WORLD );

  if( miId == 0 ) {
        printf( "\n La suma total de todos los datos pares es: %d\n\n", suma );
  }
  // --------------------------------------------------------------------------

  // Finalización de MPI.
  MPI_Finalize();

  // Fin de programa.
  printf( "Fin de programa (%d) \n", miId );
  return 0;
}
