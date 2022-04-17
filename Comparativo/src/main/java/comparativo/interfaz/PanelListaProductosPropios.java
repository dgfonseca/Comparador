package comparativo.interfaz;
import java.awt.*;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import comparativo.mundo.model.Categoria;
import comparativo.mundo.model.Producto;

public class PanelListaProductosPropios extends JPanel implements ListSelectionListener{

    private InterfazComparativo interfaz;
    private JList<Producto> listaProductos;
    private JScrollPane scroll;

    public PanelListaProductosPropios(InterfazComparativo pInterfaz){
        interfaz=pInterfaz;
        setBorder(new TitledBorder("Productos"));
        setLayout(new BorderLayout());
        listaProductos = new JList<>();
        listaProductos.addListSelectionListener(this);
        scroll = new JScrollPane(listaProductos);
        add(scroll,BorderLayout.CENTER);

        JPanel p1 = new JPanel(new GridLayout(2,1));
        p1.add(new JLabel("Buscar producto"));
        p1.add(createTextField());
        add(p1, BorderLayout.SOUTH);

    }

    public void refrescar(Categoria categoria){
        DefaultListModel<Producto> data = new DefaultListModel<>();
        for(Producto prod : categoria.getProductos()){
            data.addElement(prod);
        }
        listaProductos.setModel(data);
    }

    private JTextField createTextField() {
        final JTextField field = new JTextField(20);
        field.getDocument().addDocumentListener(new DocumentListener(){
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) {}
            private void filter() {
                String filter = field.getText();
                filterModel((DefaultListModel<Producto>)listaProductos.getModel(), filter);
            }
        });
        return field;
    }

    public void filterModel(DefaultListModel<Producto> model, String filter) {
        for (Producto s : interfaz.obtenerCategoriaActual().getProductos()) {
            if (!s.getNombre().toLowerCase().contains(filter.toLowerCase()) && !s.getReferencia().toLowerCase().contains(filter.toLowerCase())) {
                if (model.contains(s)) {
                    model.removeElement(s);
                }
            } else {
                if (!model.contains(s)) {
                    model.addElement(s);
                }
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        interfaz.setProductoPropio(listaProductos.getSelectedValue());
    }
    
}
