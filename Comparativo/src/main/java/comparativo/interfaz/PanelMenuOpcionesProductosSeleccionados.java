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
    JButton boton;
    JButton boton1;
    JButton boton2;
    JButton boton3;
    JButton boton4;
    JButton boton5;

    public PanelMenuOpcionesProductosSeleccionados(InterfazComparativo pinterfaz){
        interfaz=pinterfaz;
        setBorder(BorderFactory.createLineBorder(Color.black));
        setLayout(new GridLayout(1,3,5,5));
        btnBuscarProducto = new JButton("Buscar Producto Propio");
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
            if(interfaz.getProductoPropioSeleccionado().getPrecio2()>0||interfaz.getProductoPropioSeleccionado().getPrecio3()>0
            ||interfaz.getProductoPropioSeleccionado().getPrecio4()>0 || interfaz.getProductoPropioSeleccionado().getPrecio5()>0){
            dialog = new JDialog();
    
            dialog.setSize(500,170);

            dialog.setTitle("Seleccionar precio a comparar");
            JPanel p3 = new JPanel(new GridLayout(1,5));
            ActionListener actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    
                    if("1".equals(event.getActionCommand())){
                        interfaz.crearComparacion(1);
                    }if("2".equals(event.getActionCommand())){
                        interfaz.crearComparacion(2);
                    }if("3".equals(event.getActionCommand())){
                        interfaz.crearComparacion(3);
                    }if("4".equals(event.getActionCommand())){
                        interfaz.crearComparacion(4);
                    }if("5".equals(event.getActionCommand())){
                        interfaz.crearComparacion(5);
                    }
                    dialog.dispose();
                }
            };

            boton1 = new JButton("Precio 1");
            boton2 = new JButton("Precio 2");
            boton3 = new JButton("Precio 3");
            boton4 = new JButton("Precio 4");
            boton5 = new JButton("Precio 5");
            boton1.setActionCommand("1");
            boton1.addActionListener(actionListener);

            boton2.setActionCommand("2");
            boton2.addActionListener(actionListener);

            boton3.setActionCommand("3");
            boton3.addActionListener(actionListener);

            boton4.setActionCommand("4");
            boton4.addActionListener(actionListener);

            boton5.setActionCommand("5");
            boton5.addActionListener(actionListener);
            p3.add(boton1);
            p3.add(boton2);
            p3.add(boton3);
            p3.add(boton4);
            p3.add(boton5);
            

            

            JPanel p1 = new JPanel();
            p1.add(p3);
            dialog.setLayout(new BorderLayout());
            dialog.add(p1,BorderLayout.CENTER);
            dialog.add(new JPanel(), BorderLayout.EAST);
            dialog.add(new JLabel("Seleccione el precio a comparar"), BorderLayout.NORTH);
            dialog.setLocationRelativeTo(null);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
            }else{
                interfaz.crearComparacion(1);
            }

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
            boton = new JButton("Buscar Referencia");
            
            ActionListener actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    
                    String referencia = field.getText();
                    boton.setText("Buscando producto, por favor espere");
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
