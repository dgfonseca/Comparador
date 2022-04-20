package comparativo.interfaz;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;


public class PanelMenuOpciones extends JPanel implements ActionListener{

    private InterfazComparativo ventanaPrincipal;
    private JButton botonProductosCompetencia;
    private JButton botonProductos;
    private JButton botonComparaciones;
    
    public PanelMenuOpciones(InterfazComparativo pVentanaPrincipal){
        this.ventanaPrincipal=pVentanaPrincipal;
        setBorder(BorderFactory.createLineBorder(Color.black));
        setLayout(new GridLayout(1,4));

        
        botonProductosCompetencia = new JButton("Cargar Productos Competencia");
        botonProductosCompetencia.setActionCommand("A");
        botonProductosCompetencia.addActionListener(this);
        add(botonProductosCompetencia);

        botonProductos = new JButton("Seleccionar Productos");
        botonProductos.setActionCommand("C");
        botonProductos.addActionListener(this);
        add(botonProductos);

        botonComparaciones = new JButton("Comparaciones");
        botonComparaciones.setActionCommand("B");
        botonComparaciones.addActionListener(this);
        add(botonComparaciones);




    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if("A".equals(e.getActionCommand())){
            JFileChooser fc = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos Excel", "xlsx");
            fc.setFileFilter(filter);
            fc.setDialogTitle( "Buscar Archivo excel" );
            fc.setMultiSelectionEnabled( false );

            int resultado = fc.showOpenDialog( this );
            if( resultado == JFileChooser.APPROVE_OPTION )
            {
                String path = fc.getSelectedFile( ).getAbsolutePath( );
                ventanaPrincipal.cargarProductosCompetencia(path);
                
            }
        }
        if("B".equals(e.getActionCommand())){
            ventanaPrincipal.cambiarPanelComparaciones();
        }
        if("C".equals(e.getActionCommand())){
            ventanaPrincipal.cambiarPanelProductos();
            ventanaPrincipal.obtenerCategorias();
        }
        
        
    }

    
}
