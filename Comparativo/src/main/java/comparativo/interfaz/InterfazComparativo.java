package comparativo.interfaz;

import java.sql.Date;
import java.util.ArrayList;

import comparativo.mundo.ComparativoMundo;
import comparativo.mundo.model.Catalogo;
import comparativo.mundo.model.ProductoCompetencia;

public class InterfazComparativo {
	
	public static void main( String[] args ) throws Exception
    {
        System.out.println( "Hello World!" );
        ComparativoMundo comparativo = new ComparativoMundo("postgres","santafe","127.0.0.1");

    }

}
