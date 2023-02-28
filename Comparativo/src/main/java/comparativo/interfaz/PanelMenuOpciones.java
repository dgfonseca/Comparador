package comparativo.interfaz;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.ExecutionException;


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

    @SuppressWarnings("rawtypes")
    private void backgroundLoad(String path){
        final String ppath = path;

        SwingWorker sw1 = new SwingWorker() {

            @Override
            protected String doInBackground() throws Exception {
                ventanaPrincipal.cargarProductosCompetencia(ppath);
                String termino = "Termino de cargar los productos";
                return termino;
            }

            @Override
            protected void done(){
                try {
                    JOptionPane.showMessageDialog(new JFrame(), get(), "Informacion", JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(new JFrame(), "Error al cargar excel", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        sw1.execute();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if("A".equals(e.getActionCommand())){
            if(ventanaPrincipal.getComparador().equalsIgnoreCase("Promopciones")){
                JFileChooser fc = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos Excel", "xlsx");
                fc.setFileFilter(filter);
                fc.setDialogTitle( "Buscar Archivo excel" );
                fc.setMultiSelectionEnabled( false );
    
                int resultado = fc.showOpenDialog( this );
                if( resultado == JFileChooser.APPROVE_OPTION )
                {
                    String path = fc.getSelectedFile( ).getAbsolutePath( );
                    backgroundLoad(path);                
                }
            }else{
                backgroundLoad("Marpico");
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
