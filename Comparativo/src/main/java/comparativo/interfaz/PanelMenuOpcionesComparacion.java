package comparativo.interfaz;

import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class PanelMenuOpcionesComparacion extends JPanel implements ActionListener{

    private InterfazComparativo interfaz;
    private JButton bntEliminarComparacion;
    private JButton btnModificarDescuento;
    private JButton btnExportarComparaciones;
    private JButton btnCargarHistorico;
    private JFormattedTextField field;
    private JFormattedTextField field2;
    private JDialog dialog;

    public PanelMenuOpcionesComparacion(InterfazComparativo pInterfaz){
        interfaz = pInterfaz;
        setBorder(BorderFactory.createLineBorder(Color.black));
        setLayout(new GridLayout(1,4,3,3));
        
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

        btnExportarComparaciones = new JButton("Exportar a Excel");
        btnExportarComparaciones.setActionCommand("EXPORTAR");
        btnExportarComparaciones.addActionListener(this);
        add(btnExportarComparaciones);


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if("MODIFICAR".equals(e.getActionCommand())){
            dialog = new JDialog();
            dialog.setSize(300,150);
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
            DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
            decimalFormat.setGroupingUsed(false);
            field = new JFormattedTextField(decimalFormat);
            field2 = new JFormattedTextField(decimalFormat);
    
            field.setColumns(10);
            field2.setColumns(10);


            dialog.setTitle("Establecer Descuentos");
            JPanel p3 = new JPanel(new GridLayout(2,1));
            p3.add(new JLabel("Descuento Propio: "));
            p3.add(new JLabel("Descuento Competencia: "));
            JPanel p4 = new JPanel(new GridLayout(2,1));
            p4.add(field);
            p4.add(field2);

            JPanel p1 = new JPanel();
            p1.add(p3);
            p1.add(p4);

            JPanel p2 = new JPanel();
            JButton boton = new JButton("Cambiar Descuentos");
            
            ActionListener actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    
                    double descuentoPropio = field.getValue() == null ?-1:((Long) field.getValue()).doubleValue();
                    double descuentoCompetencia = field2.getValue() == null ?-1:((Long) field2.getValue()).doubleValue();
                    if(descuentoPropio<=100 && descuentoCompetencia <=100){
                        interfaz.actualizarDescuentos(descuentoPropio,descuentoCompetencia);
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
                interfaz.exportarComparaciones(path);
                
            }
        }
        if("ELIMINARC".equals(e.getActionCommand())){
            int input = JOptionPane.showConfirmDialog(null, "¿Esta seguro que desea eliminar la comparacion?");
            if(input == 0){
                interfaz.eliminarComparacion();
            }
        }
        if("CARGARH".equals(e.getActionCommand())){
            interfaz.obtenerHistoricoComparaciones();
        }
        
    }
    
}
