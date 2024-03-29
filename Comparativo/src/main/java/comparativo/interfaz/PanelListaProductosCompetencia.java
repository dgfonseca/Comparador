package comparativo.interfaz;
import java.awt.*;
import java.util.ArrayList;

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

import comparativo.mundo.model.ProductoCompetencia;
import comparativo.mundo.persistence.ProductosMarpico;
@SuppressWarnings("rawtypes")
public class PanelListaProductosCompetencia extends JPanel implements ListSelectionListener {

    private InterfazComparativo interfaz;
    private JList listaProductos;
    private JScrollPane scroll;
    private String competencia;

    public PanelListaProductosCompetencia(InterfazComparativo pInterfaz){
        interfaz=pInterfaz;
        setBorder(new TitledBorder("Productos Competencia"));
        setLayout(new BorderLayout());
        listaProductos = new JList<>();
        listaProductos.addListSelectionListener(this);
        scroll = new JScrollPane(listaProductos);
        add(scroll,BorderLayout.CENTER);

        JPanel p1 = new JPanel(new GridLayout(2,1));
        p1.add(new JLabel("Buscar Producto"));
        p1.add(createTextField());
        add(p1,BorderLayout.SOUTH);
    }

    public void refrescar(ArrayList<ProductoCompetencia> productos, ArrayList<ProductosMarpico> productosMarpico){
        if(productos!=null){
            DefaultListModel<ProductoCompetencia> data = new DefaultListModel<>();
            for(ProductoCompetencia prod : productos){
                data.addElement(prod);
            }
            listaProductos.setModel(data);
            competencia="Promopciones";
        }else{
            DefaultListModel<ProductosMarpico> data = new DefaultListModel<>();
            for(ProductosMarpico prod : productosMarpico){
                data.addElement(prod);
            }
            listaProductos.setModel(data);
            competencia="Marpico";
        }
    }

    private JTextField createTextField() {
        final JTextField field = new JTextField(20);
        field.getDocument().addDocumentListener(new DocumentListener(){
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) {}
            private void filter() {
                String filter = field.getText();
                filterModel((DefaultListModel)listaProductos.getModel(), filter);
            }
        });
        return field;
    }

    public void filterModel(DefaultListModel model, String filter) {
        if(competencia.equals("Promopciones")){
            for (ProductoCompetencia s : interfaz.obtenerProductosCompetencia()) {
                if (!s.getCodigoHijo().toLowerCase().contains(filter.toLowerCase()) && !s.getNombre().toLowerCase().contains(filter.toLowerCase())) {
                    if (model.contains(s)) {
                        model.removeElement(s);
                    }
                }
                 else {
                    if (!model.contains(s)) {
                        model.addElement(s);
                    }
                }
            }
        }else{
            for (ProductosMarpico s : interfaz.obtenerProductosMarpico()) {
                if (!s.getFamilia().toLowerCase().contains(filter.toLowerCase())) {
                    if (model.contains(s)) {
                        model.removeElement(s);
                    }
                }
                 else {
                    if (!model.contains(s)) {
                        model.addElement(s);
                    }
                }
            }

        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(competencia.equalsIgnoreCase("Marpico")){
            interfaz.setProductoCompetenciaMarpico((ProductosMarpico) listaProductos.getSelectedValue());
        }else{
            interfaz.setProductoCompetencia((ProductoCompetencia) listaProductos.getSelectedValue());
        }
    }
    
}
