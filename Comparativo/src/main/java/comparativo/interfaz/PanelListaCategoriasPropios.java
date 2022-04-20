package comparativo.interfaz;

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

import comparativo.mundo.model.Catalogo;
import comparativo.mundo.model.Categoria;

import java.awt.*;

public class PanelListaCategoriasPropios extends JPanel implements ListSelectionListener {

    private InterfazComparativo interfaz;
    private JList<Categoria> listaCategorias;
    private JScrollPane scroll;

    public PanelListaCategoriasPropios(InterfazComparativo pInterfaz){
        interfaz = pInterfaz;
        setBorder(new TitledBorder("Categorias"));
        setLayout(new BorderLayout());
        listaCategorias = new JList<>();
        listaCategorias.addListSelectionListener(this);
        scroll = new JScrollPane(listaCategorias);
        add(scroll,BorderLayout.CENTER);
        JPanel p1 = new JPanel(new GridLayout(2,1));
        p1.add(new JLabel("Buscar Categoria"));
        p1.add(createTextField());
        add(p1, BorderLayout.SOUTH);

    }

    public void refrescar(Catalogo catalogo){
        
        DefaultListModel<Categoria> data = new DefaultListModel<>();
        for(Categoria cat : catalogo.getCatalogo()){
            data.addElement(cat);
        }
        listaCategorias.setModel(data);
    }

    private JTextField createTextField() {
        final JTextField field = new JTextField(20);
        field.getDocument().addDocumentListener(new DocumentListener(){
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) {}
            private void filter() {
                String filter = field.getText();
                filterModel((DefaultListModel<Categoria>)listaCategorias.getModel(), filter);
            }
        });
        return field;
    }

    public void filterModel(DefaultListModel<Categoria> model, String filter) {
        for (Categoria s : interfaz.obtenerCategorias().getCatalogo()) {
            if (!s.getNombre().toLowerCase().contains(filter.toLowerCase())) {
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
    public void valueChanged( ListSelectionEvent e )
    {
        Categoria indice = listaCategorias.getSelectedValue();
        if( indice != null )
        {
            interfaz.refrescarCategoriaActual( indice );
            try{
                interfaz.refrescarProductos();
            }catch(NullPointerException ex){

            }
        }
    }
    
}
