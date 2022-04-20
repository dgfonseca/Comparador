package comparativo.interfaz;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.*;
import java.awt.event.*;

public class PanelMenuOpcionesProductosSeleccionados extends JPanel implements ActionListener{

    private InterfazComparativo interfaz;
    private JButton btnAgregarComparacion;
    private JButton btnEliminarComparacion;
    private JButton btnBuscarProducto;
    private JTextField field;
    private JDialog dialog;

    public PanelMenuOpcionesProductosSeleccionados(InterfazComparativo pinterfaz){
        interfaz=pinterfaz;
        setBorder(BorderFactory.createLineBorder(Color.black));
        setLayout(new GridLayout(1,3,5,5));
        btnBuscarProducto = new JButton("Buscar Producto");
        btnBuscarProducto.setActionCommand("BUSCAR");
        btnBuscarProducto.addActionListener(this);
        add(btnBuscarProducto);
        btnAgregarComparacion = new JButton("Agregar Comparacion");
        btnAgregarComparacion.setActionCommand("AGREGAR");
        btnAgregarComparacion.addActionListener(this);
        add(btnAgregarComparacion);
        btnEliminarComparacion = new JButton("Limpiar Productos Seleccionados");
        btnEliminarComparacion.setActionCommand("ELIMINAR");
        btnEliminarComparacion.addActionListener(this);
        add(btnEliminarComparacion);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if("AGREGAR".equals(e.getActionCommand())){
            interfaz.crearComparacion();
        }if("ELIMINAR".equals(e.getActionCommand())){
            int input = JOptionPane.showConfirmDialog(null, "¿Esta seguro que desea limpiar los productos seleccionados?");
            if(input == 0){
                interfaz.limpiarProductosSeleccionados();
            }
        }if("BUSCAR".equals(e.getActionCommand())){
            dialog = new JDialog();
            dialog.setSize(200,125);
    
            field = new JTextField();
    
            field.setColumns(10);


            dialog.setTitle("Buscar Producto");
            JPanel p3 = new JPanel(new GridLayout(2,1));
            p3.add(new JLabel("Ingresar Referencia: "));
            p3.add(field);

            JPanel p1 = new JPanel();
            p1.add(p3);

            JPanel p2 = new JPanel();
            JButton boton = new JButton("Buscar Referencia");
            
            ActionListener actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    
                    String referencia = field.getText();
                    interfaz.buscarProductoPorReferencia(referencia);  
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
        
    }
    
}
