package comparativo.interfaz;

import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class PanelMenuOpcionesComparacion extends JPanel implements ActionListener{

    private static InterfazComparativo interfaz;
    private JButton bntEliminarComparacion;
    private JButton btnModificarDescuento;
    private JButton btnExportarComparaciones;
    private JButton btnCargarHistorico;
    private JButton btnComparacionesRecientes;
    private JButton btnExportarHistorico;
    private JButton btnCalcularNuevaComparacion;
    private JFormattedTextField field;
    private JFormattedTextField field2;
    private JFormattedTextField field3;
    private JFormattedTextField field4;
    private JDialog dialog;

    public PanelMenuOpcionesComparacion(InterfazComparativo pInterfaz){
        interfaz = pInterfaz;
        setBorder(BorderFactory.createLineBorder(Color.black));
        setLayout(new GridLayout(1,6,3,3));
        
        btnModificarDescuento = new JButton("Modificar Descuento");
        btnModificarDescuento.setActionCommand("MODIFICAR");
        btnModificarDescuento.addActionListener(this);
        add(btnModificarDescuento);
        
        bntEliminarComparacion = new JButton("Eliminar Comparacion");
        bntEliminarComparacion.setActionCommand("ELIMINARC");
        bntEliminarComparacion.addActionListener(this);
        add(bntEliminarComparacion);

        btnCargarHistorico = new JButton("Cargar Historico Comparaciones");
        btnCargarHistorico.setActionCommand("CARGARH");
        btnCargarHistorico.addActionListener(this);
        add(btnCargarHistorico);

        btnComparacionesRecientes = new JButton("Comparaciones");
        btnComparacionesRecientes.setActionCommand("COMPARACIONESR");
        btnComparacionesRecientes.addActionListener(this);
        add(btnComparacionesRecientes);

        btnExportarComparaciones = new JButton("Exportar Comparaciones");
        btnExportarComparaciones.setActionCommand("EXPORTAR");
        btnExportarComparaciones.addActionListener(this);
        add(btnExportarComparaciones);

        btnExportarHistorico = new JButton("Exportar Historico");
        btnExportarHistorico.setActionCommand("EXPORTARH");
        btnExportarHistorico.addActionListener(this);
        add(btnExportarHistorico);

        btnCalcularNuevaComparacion = new JButton("Calcular Comparacion");
        btnCalcularNuevaComparacion.setActionCommand("CALCULARC");
        btnCalcularNuevaComparacion.addActionListener(this);
        add(btnCalcularNuevaComparacion);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if("MODIFICAR".equals(e.getActionCommand())){
            dialog = new JDialog();
            dialog.setSize(450,125);
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
            DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
            decimalFormat.setGroupingUsed(false);
            field = new JFormattedTextField(decimalFormat);
            field2 = new JFormattedTextField(decimalFormat);
            field3 = new JFormattedTextField(decimalFormat);
            field4 = new JFormattedTextField(decimalFormat);
    
            field.setColumns(5);
            field2.setColumns(5);
            field3.setColumns(5);
            field4.setColumns(5);
            


            dialog.setTitle("Establecer Descuentos");
            JPanel p3 = new JPanel(new GridLayout(2,1));
            p3.add(new JLabel("Descuento Propio: "));
            p3.add(new JLabel("Descuento Competencia: "));
            JPanel p4 = new JPanel(new GridLayout(2,1));
            p4.add(field);
            p4.add(field2);
            
            JPanel p6 = new JPanel(new GridLayout(2,1));
            p6.add(field3);
            p6.add(field4);

            JPanel p1 = new JPanel();
            p1.add(p3);
            p1.add(p4);
            p1.add(p6);

            JPanel p2 = new JPanel(new GridLayout(1,2));
            JButton boton = new JButton("Cambiar Descuentos");
            
            ActionListener actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    
                    double descuentoPropio = field.getValue() == null ?-1:((Long) field.getValue()).doubleValue();
                    double descuentoCompetencia = field2.getValue() == null ?-1:((Long) field2.getValue()).doubleValue();
                    double descuentoPropio2 = field3.getValue() == null ?-1:((Long) field3.getValue()).doubleValue();
                    double descuentoCompetencia2 = field4.getValue() == null ?-1:((Long) field4.getValue()).doubleValue();
                    if(descuentoPropio<=100 && descuentoCompetencia <=100 && descuentoPropio2<=100 && descuentoCompetencia2<=100){
                        interfaz.actualizarDescuentos(descuentoPropio,descuentoCompetencia, descuentoPropio2, descuentoCompetencia2);
                    }
                    dialog.dispose();
                
                }
            };
            boton.setActionCommand("command");
            boton.addActionListener(actionListener);
            p2.add(boton);

            JPanel p5 = new JPanel(new BorderLayout());
            p5.add(p2,BorderLayout.CENTER);
            dialog.setLayout(new BorderLayout());
            dialog.add(p1,BorderLayout.CENTER);
            dialog.add(p5, BorderLayout.SOUTH);
            dialog.setLocationRelativeTo(null);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        }
        if("EXPORTAR".equals(e.getActionCommand())){
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setDialogTitle( "Almacenar Archivo" );
            fc.setMultiSelectionEnabled( false );

            int resultado = fc.showOpenDialog( this );
            if( resultado == JFileChooser.APPROVE_OPTION )
            {
                String path = fc.getSelectedFile( ).getAbsolutePath( );
                backgroundLoad(path,false);
                
            }
        }if("EXPORTARH".equals(e.getActionCommand())){
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setDialogTitle( "Almacenar Archivo" );
            fc.setMultiSelectionEnabled( false );

            int resultado = fc.showOpenDialog( this );
            if( resultado == JFileChooser.APPROVE_OPTION )
            {
                String path = fc.getSelectedFile( ).getAbsolutePath( );
                backgroundLoad(path,true);
                
            }
        }
        if("ELIMINARC".equals(e.getActionCommand())){
            int input = JOptionPane.showConfirmDialog(null, "¿Esta seguro que desea eliminar la comparacion?");
            if(input == 0){
                interfaz.eliminarComparacion();
            }
        }
        if("CARGARH".equals(e.getActionCommand())){
            interfaz.setEsHistorico(true);
            interfaz.obtenerHistoricoComparaciones();
            interfaz.limpiarComparacionSeleccionada();
        }
        if("COMPARACIONESR".equals(e.getActionCommand())){
            interfaz.setEsHistorico(false);
            interfaz.obtenerComparacionesGuardadas();
            interfaz.limpiarComparacionSeleccionada();
        }
        if("CALCULARC".equals(e.getActionCommand())){
            interfaz.actualizarListaComparaciones();
        }
        
    }

    private static void backgroundLoad(String path, boolean esHistorico){
        final String ppath = path;
        final boolean historico = esHistorico;

        SwingWorker sw1 = new SwingWorker() {

            @Override
            protected String doInBackground() throws Exception {
                interfaz.exportarComparaciones(ppath,historico);
                String termino = "Termino la exportacion de comparaciones";
                return termino;
            }

            @Override
            protected void done(){
                try {
                    JOptionPane.showMessageDialog(new JFrame(), get(), "Informacion", JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(new JFrame(), "Error al cargar excel", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        sw1.execute();
    }
    
}
