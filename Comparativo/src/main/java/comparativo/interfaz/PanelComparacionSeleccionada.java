package comparativo.interfaz;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import comparativo.mundo.model.Comparacion;

import java.awt.*;


public class PanelComparacionSeleccionada extends JPanel{


    private final JLabel etNombre = new JLabel("Nombre: ");
    private JTextField txtNombre;
    private final JLabel etCodigoPromos = new JLabel("Codigo Promos:");
    private JTextField txtCodigoPromos;
    private final JLabel etDescuentoPromos = new JLabel("Descuento Promos:");
    private JTextField txtDescuentoPromos;
    private final JLabel etCodigoCompetencia = new JLabel("Codigo Competencia:");
    private JTextField txtCodigoCompetencia;

    private final JLabel etDescuentoCompetencia = new JLabel("Descuento Competencia:");
    private JTextField txtDescuentoCompetencia;
    private final JLabel etPrecioPromos = new JLabel("Precio Promos:");
    private JTextField txtPrecioPromos;
    private final JLabel etPrecioCompetencia = new JLabel("Precio Competencia:");
    private JTextField txtPrecioCompetencia;
    private final JLabel etPrecioPromosDescuento = new JLabel("Precio Promos Descuento:");
    private JTextField txtPrecioPromosDescuento;
    private final JLabel etPrecioCompetenciaDescuento = new JLabel("Precio Competencia Descuento:");
    private JTextField txtPrecioCompetenciaDescuento;

    private final JLabel etPrecioPromosDescuento2 = new JLabel("Precio Promos Descuento 2:");
    private JTextField txtPrecioPromosDescuento2;

    private final JLabel etPrecioCompetenciaDescuento2 = new JLabel("Precio Competencia Descuento 2");
    private JTextField txtPrecioCompetenciaDescuento2;

    private final JLabel etDescuento2Promos = new JLabel("Descuento 2 Promos:");
    private JTextField txtDescuento2Promos;

    private final JLabel etDescuento2Competencia = new JLabel("Descuento 2 Competencia:");
    private JTextField txttDescuento2Competencia;




    public PanelComparacionSeleccionada(InterfazComparativo pInterfaz){
        setLayout(new GridLayout(1,1));
        setBorder(new TitledBorder("Comparacion Seleccionada"));
        JPanel p1 = new JPanel(new GridLayout(5,3,2,5));
        txtNombre = new JTextField("");
        txtNombre.setEditable(false);
        txtNombre.setBackground(Color.WHITE);
        p1.add(etNombre);
        p1.add(txtNombre);
        
        txtDescuentoPromos = new JTextField("");
        txtDescuentoPromos.setEditable(false);
        txtDescuentoPromos.setBackground(Color.WHITE);
        p1.add(etDescuentoPromos);
        p1.add(txtDescuentoPromos);
        
        txtDescuentoCompetencia = new JTextField("");
        txtDescuentoCompetencia.setEditable(false);
        txtDescuentoCompetencia.setBackground(Color.WHITE);
        p1.add(etDescuentoCompetencia);
        p1.add(txtDescuentoCompetencia);


        txtCodigoCompetencia = new JTextField("");
        txtCodigoCompetencia.setEditable(false);
        txtCodigoCompetencia.setBackground(Color.WHITE);
        p1.add(etCodigoCompetencia);
        p1.add(txtCodigoCompetencia);

        txtDescuento2Promos = new JTextField("");
        txtDescuento2Promos.setEditable(false);
        txtDescuento2Promos.setBackground(Color.WHITE);
        p1.add(etDescuento2Promos);
        p1.add(txtDescuento2Promos);
        

        txttDescuento2Competencia = new JTextField("");
        txttDescuento2Competencia.setEditable(false);
        txttDescuento2Competencia.setBackground(Color.WHITE);
        p1.add(etDescuento2Competencia);
        p1.add(txttDescuento2Competencia);
        
        txtCodigoPromos = new JTextField("");
        txtCodigoPromos.setEditable(false);
        txtCodigoPromos.setBackground(Color.WHITE);
        p1.add(etCodigoPromos);
        p1.add(txtCodigoPromos);


        txtPrecioPromos = new JTextField("");
        txtPrecioPromos.setEditable(false);
        txtPrecioPromos.setBackground(Color.WHITE);
        p1.add(etPrecioPromos);
        p1.add(txtPrecioPromos);


        txtPrecioCompetencia = new JTextField("");
        txtPrecioCompetencia.setEditable(false);
        txtPrecioCompetencia.setBackground(Color.WHITE);
        p1.add(etPrecioCompetencia);
        p1.add(txtPrecioCompetencia);

        p1.add(new JLabel());
        p1.add(new JLabel());

        txtPrecioPromosDescuento = new JTextField("");
        txtPrecioPromosDescuento.setEditable(false);
        txtPrecioPromosDescuento.setBackground(Color.WHITE);
        p1.add(etPrecioPromosDescuento);
        p1.add(txtPrecioPromosDescuento);

        txtPrecioCompetenciaDescuento = new JTextField("");
        txtPrecioCompetenciaDescuento.setEditable(false);
        txtPrecioCompetenciaDescuento.setBackground(Color.WHITE);
        p1.add(etPrecioCompetenciaDescuento);
        p1.add(txtPrecioCompetenciaDescuento);
        
        

        p1.add(new JLabel());
        p1.add(new JLabel());

        txtPrecioPromosDescuento2 = new JTextField("");
        txtPrecioPromosDescuento2.setEditable(false);
        txtPrecioPromosDescuento2.setBackground(Color.WHITE);
        p1.add(etPrecioPromosDescuento2);
        p1.add(txtPrecioPromosDescuento2);

        txtPrecioCompetenciaDescuento2 = new JTextField("");
        txtPrecioCompetenciaDescuento2.setEditable(false);
        txtPrecioCompetenciaDescuento2.setBackground(Color.WHITE);
        p1.add(etPrecioCompetenciaDescuento2);
        p1.add(txtPrecioCompetenciaDescuento2);

        add(p1);
    }

    public void refrescar(Comparacion comparacion){
        if(comparacion!=null){
            txtNombre.setText(comparacion.getProductoPropio().getNombre());
            txtCodigoPromos.setText(comparacion.getProductoPropio().getReferencia());
            txtDescuentoPromos.setText(comparacion.getProductoPropio().getDescuento()+"%");
            txtCodigoCompetencia.setText(comparacion.getProductoCompetencia().getCodigoHijo());
            txtDescuentoCompetencia.setText(comparacion.getProductoCompetencia().getDescuento()+"%");
            txtPrecioPromos.setText(comparacion.getProductoPropio().getPrecio1()+"$");
            txtPrecioCompetencia.setText(comparacion.getProductoCompetencia().getPrecioBase()+"$");
            txtPrecioPromosDescuento.setText(comparacion.getProductoPropio().getPrecioDescuento()+"$");
            txtPrecioCompetenciaDescuento.setText(comparacion.getProductoCompetencia().getPrecioDescuento()+"$");
            txtPrecioPromosDescuento2.setText(comparacion.getProductoPropio().getPrecioDescuento2()+"$");
            txtPrecioCompetenciaDescuento2.setText(comparacion.getProductoCompetencia().getPrecioDescuento2()+"$");
            txttDescuento2Competencia.setText(comparacion.getProductoCompetencia().getDescuento2()+"%");
            txtDescuento2Promos.setText(comparacion.getProductoPropio().getDescuento2()+"%");
            if(comparacion.getProductoPropio().getPrecio1()>comparacion.getProductoCompetencia().getPrecioBase()){
                txtPrecioPromos.setBackground(Color.RED);
                txtPrecioCompetencia.setBackground(Color.GREEN);
            }
            else{
                txtPrecioPromos.setBackground(Color.GREEN);
                txtPrecioCompetencia.setBackground(Color.RED);
            }
            if(comparacion.getProductoPropio().getPrecioDescuento()>comparacion.getProductoCompetencia().getPrecioDescuento()){
                txtPrecioPromosDescuento.setBackground(Color.RED);
                txtPrecioCompetenciaDescuento.setBackground(Color.GREEN);
            }else{
                txtPrecioPromosDescuento.setBackground(Color.GREEN);
                txtPrecioCompetenciaDescuento.setBackground(Color.RED);
            }if(comparacion.getProductoPropio().getPrecioDescuento2()>comparacion.getProductoCompetencia().getPrecioDescuento2()){
                txtPrecioPromosDescuento2.setBackground(Color.RED);
                txtPrecioCompetenciaDescuento2.setBackground(Color.GREEN);
            }else{
                txtPrecioPromosDescuento2.setBackground(Color.GREEN);
                txtPrecioCompetenciaDescuento2.setBackground(Color.RED);
            }
        }else{
            txtNombre.setText("");
            txtCodigoPromos.setText("");
            txtDescuentoPromos.setText("");
            txtCodigoCompetencia.setText("");
            txtDescuentoCompetencia.setText("");
            txtPrecioPromos.setText("");
            txtPrecioCompetencia.setText("");
            txtPrecioPromosDescuento.setText("");
            txtPrecioCompetenciaDescuento.setText("");
            txtPrecioCompetenciaDescuento2.setText("");
            txtPrecioPromosDescuento2.setText("");
            txtDescuento2Promos.setText("");
            txttDescuento2Competencia.setText("");

            txtPrecioPromos.setBackground(Color.WHITE);
            txtPrecioCompetencia.setBackground(Color.WHITE);
            txtPrecioPromosDescuento.setBackground(Color.WHITE);
            txtPrecioCompetenciaDescuento.setBackground(Color.WHITE);
            txtPrecioCompetenciaDescuento2.setBackground(Color.WHITE);
            txtPrecioPromosDescuento2.setBackground(Color.WHITE);
            txtDescuento2Promos.setBackground(Color.WHITE);
            txttDescuento2Competencia.setBackground(Color.WHITE);

        }
    }

    
}

