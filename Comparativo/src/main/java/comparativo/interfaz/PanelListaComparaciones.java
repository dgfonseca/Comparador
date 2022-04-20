package comparativo.interfaz;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import comparativo.mundo.model.Comparacion;
import comparativo.mundo.model.ListaComparaciones;

import java.awt.*;
import java.awt.event.*;


class MyTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Color getBackground() {
        return super.getBackground();
    }
}

public class PanelListaComparaciones extends JPanel{

    private InterfazComparativo interfaz;
    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private TableRowSorter<TableModel> rowSorter;

    public PanelListaComparaciones(InterfazComparativo pInterfaz){
        interfaz=pInterfaz;
        setBorder(BorderFactory.createTitledBorder("Comparaciones"));
        setLayout(new BorderLayout());
        modeloTabla = new DefaultTableModel();
        tabla = new JTable(modeloTabla){
            public boolean isCellEditable( int col, int row )
            {
                return false;
            }

        };
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Codigo Promos");
        modeloTabla.addColumn("Descuento Promos");
        modeloTabla.addColumn("Codigo Competencia");
        modeloTabla.addColumn("Descuento Competencia");
        modeloTabla.addColumn("Precio Promos");
        modeloTabla.addColumn("Precio Competencia ");
        modeloTabla.addColumn("Precio Promos Descuento");
        modeloTabla.addColumn("Precio Competencia Descuento");

        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tabla.setAutoCreateRowSorter(true);
        tabla.getTableHeader().setBackground(Color.lightGray);
        tabla.addMouseListener(new MouseAdapter(){
            
            public void mouseClicked(MouseEvent event) {
                String referencia=tabla.getValueAt(tabla.getSelectedRow(), 1).toString();
                String codigoHijo=tabla.getValueAt(tabla.getSelectedRow(), 3).toString();
                interfaz.actualizarComparacionSeleccionada(referencia,codigoHijo);
            }
        });
        JScrollPane panelScroll = new JScrollPane( tabla );

        add(panelScroll, BorderLayout.CENTER);
        JPanel p1 = new JPanel(new GridLayout(2,1));
        p1.add(new JLabel("Buscar Comparacion"));
        p1.add(createTextField());
        add(p1,BorderLayout.SOUTH);
    }

    public void changeTable(JTable table) {
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                double precioPromos = Double.parseDouble(table.getValueAt(row, 5).toString());
                double precioComp = Double.parseDouble(table.getValueAt(row, 6).toString());
                if (precioPromos > precioComp) {
                    c.setBackground(Color.RED);
                } else {
                    c.setBackground(Color.GREEN);
                }
                return c;
            }
        });
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                double precioPromos = Double.parseDouble(table.getValueAt(row, 5).toString());
                double precioComp = Double.parseDouble(table.getValueAt(row, 6).toString());
                if (precioPromos > precioComp) {
                    c.setBackground(Color.GREEN);
                } else {
                    c.setBackground(Color.RED);
                }
                return c;
            }
        });
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                double precioPromos = Double.parseDouble(table.getValueAt(row, 7).toString());
                double precioComp = Double.parseDouble(table.getValueAt(row, 8).toString());
                if (precioPromos > precioComp) {
                    c.setBackground(Color.RED);
                } else {
                    c.setBackground(Color.GREEN);
                }
                return c;
            }
        });
        table.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                double precioPromos = Double.parseDouble(table.getValueAt(row, 7).toString());
                double precioComp = Double.parseDouble(table.getValueAt(row, 8).toString());
                if (precioPromos > precioComp) {
                    c.setBackground(Color.GREEN);
                } else {
                    c.setBackground(Color.RED);
                }
                return c;
            }
        });
    }


    public void refrescar(ListaComparaciones lista){
        Comparacion comp = null;
        modeloTabla.setRowCount(0);
        for(int i =0; i<lista.getListaComparaciones().size();i++){
            comp=lista.getListaComparaciones().get(i);
            comp.getProductoPropio().getNombre();
            modeloTabla.addRow(new Object[]{comp.getProductoPropio().getNombre(), comp.getProductoPropio().getReferencia(), comp.getProductoPropio().getDescuento(),
                comp.getProductoCompetencia().getCodigoHijo(),comp.getProductoCompetencia().getDescuento(),comp.getProductoPropio().getPrecio1(), comp.getProductoCompetencia().getPrecioBase(),
                comp.getProductoPropio().getPrecioDescuento(),comp.getProductoCompetencia().getPrecioDescuento()
            });;
        }
        changeTable(tabla);
        rowSorter = new TableRowSorter<>(tabla.getModel());
        tabla.setRowSorter(rowSorter);
    }

    private JTextField createTextField() {
        final JTextField field = new JTextField(20);
        field.getDocument().addDocumentListener(new DocumentListener(){
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) {}
            private void filter() {
                String filter = field.getText();
                if (filter.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + filter));
                }
            }
        });
        return field;
    }

    
    
}
