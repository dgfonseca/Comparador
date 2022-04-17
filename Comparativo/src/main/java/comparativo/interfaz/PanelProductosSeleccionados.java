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

    private InterfazComparativo interfaz;

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

    public PanelProductosSeleccionados(InterfazComparativo interfaz){
        setLayout(new GridLayout(1,2,1,1));
        setBorder(new TitledBorder("Productos Seleccionados"));
        JPanel p1 = new JPanel(new GridLayout(4,2,1,1));
        p1.setBorder(new TitledBorder("Producto Propio Seleccionado"));
        txtReferencia = new JTextField("");
        txtReferencia.setEditable(false);
        p1.add(etiquetaReferencia);
        p1.add(txtReferencia);

        txtCategoria = new JTextField("");
        txtCategoria.setEditable(false);
        p1.add(etiquetaCategoria);
        p1.add(txtCategoria);

        txtNombre = new JTextField("");
        txtNombre.setEditable(false);
        p1.add(etiquetaNombre);
        p1.add(txtNombre);

        txtPrecio = new JTextField("");
        txtPrecio.setEditable(false);
        p1.add(etiquetaPrecio);
        p1.add(txtPrecio);
        add(p1);

        JPanel p2 = new JPanel(new GridLayout(4,2,1,1));
        p2.setBorder(new TitledBorder("Producto Competencia Seleccionado"));

        txtCodigoHijo = new JTextField("");
        txtCodigoHijo.setEditable(false);
        p2.add(etiquetaCodigoHijo);
        p2.add(txtCodigoHijo);

        txtCodigoPadre = new JTextField("");
        txtCodigoPadre.setEditable(false);
        p2.add(etiquetaCodigoPadre);
        p2.add(txtCodigoPadre);

        txtNombreComp = new JTextField("");
        txtNombreComp.setEditable(false);
        p2.add(etiquetaNombreComp);
        p2.add(txtNombreComp);

        txtPrecioComp = new JTextField("");
        txtPrecioComp.setEditable(false);
        p2.add(etiquetaPrecioComp);
        p2.add(txtPrecioComp);
        add(p2);  
    }

    public void refrescar(Producto propio, ProductoCompetencia competencia, Categoria categoria){
        if(propio!=null&&categoria!=null){
            txtReferencia.setText(propio.getReferencia());
            txtNombre.setText(propio.getNombre());
            txtCategoria.setText(categoria.getNombre());
            txtPrecio.setText(propio.getPrecio1()+"");
        }
        if(competencia!=null){
            txtCodigoHijo.setText(competencia.getCodigoHijo());
            txtCodigoPadre.setText(competencia.getCodigoPadre());
            txtNombreComp.setText(competencia.getNombre());
            txtPrecioComp.setText(competencia.getPrecioBase()+"");
        }

    }
    



    
    
}
