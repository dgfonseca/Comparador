package comparativo.interfaz;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import comparativo.mundo.model.Categoria;
import comparativo.mundo.model.Producto;
import comparativo.mundo.model.ProductoCompetencia;

import java.awt.*;

public class PanelProductosSeleccionados extends JPanel {


    private final JLabel etiquetaCodigoHijo = new JLabel("Codigo Hijo:");
    private JTextField txtCodigoHijo;
    private final JLabel etiquetaCodigoPadre = new JLabel("Codigo Padre:");
    private JTextField txtCodigoPadre;
    private final JLabel etiquetaNombreComp = new JLabel("Nombre:");
    private JTextField txtNombreComp;
    private final JLabel etiquetaPrecioComp = new JLabel("Precio:");
    private JTextField txtPrecioComp;

    private final JLabel etiquetaReferencia = new JLabel("Referencia:");
    private JTextField txtReferencia;
    private final JLabel etiquetaCategoria = new JLabel("Categoria:");
    private JTextField txtCategoria;
    private final JLabel etiquetaNombre = new JLabel("Nombre:");
    private JTextField txtNombre;
    private final JLabel etiquetaPrecio = new JLabel("Precio:");
    private JTextField txtPrecio;
    private final JLabel etiquetaPrecio2 = new JLabel("Precio 2:");
    private JTextField txtPrecio2;
    private final JLabel etiquetaPrecio3 = new JLabel("Precio 3:");
    private JTextField txtPrecio3;
    private final JLabel etiquetaPrecio4 = new JLabel("Precio 4:");
    private JTextField txtPrecio4;
    private final JLabel etiquetaPrecio5 = new JLabel("Precio 5:");
    private JTextField txtPrecio5;

    public PanelProductosSeleccionados(){
        setLayout(new GridLayout(1,2,1,1));
        setBorder(new TitledBorder("Productos Seleccionados"));
        JPanel p1 = new JPanel(new GridLayout(4,4,5,5));
        p1.setBorder(new TitledBorder("Producto Propio Seleccionado"));
        txtReferencia = new JTextField("");
        txtReferencia.setEditable(false);
        txtReferencia.setBackground(Color.WHITE);
        p1.add(etiquetaReferencia);
        p1.add(txtReferencia);

        txtCategoria = new JTextField("");
        txtCategoria.setEditable(false);
        txtCategoria.setBackground(Color.WHITE);

        p1.add(etiquetaCategoria);
        p1.add(txtCategoria);

        txtNombre = new JTextField("");
        txtNombre.setEditable(false);
        txtNombre.setBackground(Color.WHITE);

        p1.add(etiquetaNombre);
        p1.add(txtNombre);

        txtPrecio = new JTextField("");
        txtPrecio.setEditable(false);
        txtPrecio.setBackground(Color.WHITE);

        p1.add(etiquetaPrecio);
        p1.add(txtPrecio);

        txtPrecio2 = new JTextField("");
        txtPrecio2.setEditable(false);
        txtPrecio2.setBackground(Color.WHITE);

        p1.add(etiquetaPrecio2);
        p1.add(txtPrecio2);

        txtPrecio3 = new JTextField("");
        txtPrecio3.setEditable(false);
        txtPrecio3.setBackground(Color.WHITE);

        p1.add(etiquetaPrecio3);
        p1.add(txtPrecio3);

        txtPrecio4 = new JTextField("");
        txtPrecio4.setEditable(false);
        txtPrecio4.setBackground(Color.WHITE);

        p1.add(etiquetaPrecio4);
        p1.add(txtPrecio4);

        txtPrecio5 = new JTextField("");
        txtPrecio5.setEditable(false);
        txtPrecio5.setBackground(Color.WHITE);

        p1.add(etiquetaPrecio5);
        p1.add(txtPrecio5);
        
        add(p1);

        JPanel p2 = new JPanel(new GridLayout(4,2,5,5));
        p2.setBorder(new TitledBorder("Producto Competencia Seleccionado"));

        txtCodigoHijo = new JTextField("");
        txtCodigoHijo.setEditable(false);
        txtCodigoHijo.setBackground(Color.WHITE);

        p2.add(etiquetaCodigoHijo);
        p2.add(txtCodigoHijo);

        txtCodigoPadre = new JTextField("");
        txtCodigoPadre.setEditable(false);
        txtCodigoPadre.setBackground(Color.WHITE);

        p2.add(etiquetaCodigoPadre);
        p2.add(txtCodigoPadre);

        txtNombreComp = new JTextField("");
        txtNombreComp.setEditable(false);
        txtNombreComp.setBackground(Color.WHITE);

        p2.add(etiquetaNombreComp);
        p2.add(txtNombreComp);

        txtPrecioComp = new JTextField("");
        txtPrecioComp.setEditable(false);
        txtPrecioComp.setBackground(Color.WHITE);

        p2.add(etiquetaPrecioComp);
        p2.add(txtPrecioComp);
        add(p2);  
    }

    public void refrescar(Producto propio, ProductoCompetencia competencia, Categoria categoria){
        if(propio!=null){
            txtReferencia.setText(propio.getReferencia());
            txtNombre.setText(propio.getNombre());
            txtCategoria.setText(categoria.getNombre());
            txtPrecio.setText(propio.getPrecio1()+"$");
            txtPrecio2.setText(propio.getPrecio2()+"$");
            txtPrecio3.setText(propio.getPrecio3()+"$");
            txtPrecio4.setText(propio.getPrecio4()+"$");
            txtPrecio5.setText(propio.getPrecio5()+"$");
        }if(competencia!=null){
            txtCodigoHijo.setText(competencia.getCodigoHijo());
            txtCodigoPadre.setText(competencia.getCodigoPadre());
            txtNombreComp.setText(competencia.getNombre());
            txtPrecioComp.setText(competencia.getPrecioBase()+"$");
        }if(propio==null && competencia==null){
            txtReferencia.setText("");
            txtNombre.setText("");
            txtCategoria.setText("");
            txtPrecio.setText("");

            txtCodigoHijo.setText("");
            txtCodigoPadre.setText("");
            txtNombreComp.setText("");
            txtPrecioComp.setText("");
        }

    }
}
