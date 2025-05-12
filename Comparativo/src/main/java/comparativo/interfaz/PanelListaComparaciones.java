package comparativo.interfaz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import comparativo.mundo.model.Comparacion;
import comparativo.mundo.model.ComparacionMarpico;
import comparativo.mundo.model.ListaComparaciones;


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
        modeloTabla.addColumn("Descuento Promos 2");
        modeloTabla.addColumn("Codigo Competencia");
        modeloTabla.addColumn("Descuento Competencia");
        modeloTabla.addColumn("Descuento Competencia 2");
        modeloTabla.addColumn("Precio Promos");
        modeloTabla.addColumn("Precio Competencia ");
        modeloTabla.addColumn("Precio Promos Descuento");
        modeloTabla.addColumn("Precio Competencia Descuento");
        modeloTabla.addColumn("Precio Promos Descuento 2");
        modeloTabla.addColumn("Precio Competencia Descuento 2");
        modeloTabla.addColumn("Fecha");
        modeloTabla.addColumn("Numero Precio");
        
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tabla.setAutoCreateRowSorter(true);
        tabla.getTableHeader().setBackground(Color.lightGray);
        tabla.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tabla.addMouseListener(new MouseAdapter(){
            
            public void mouseClicked(MouseEvent event) {
                String referencia=tabla.getValueAt(tabla.getSelectedRow(), 1).toString();
                String codigoHijo=tabla.getValueAt(tabla.getSelectedRow(), 4).toString();
                String fecha = tabla.getValueAt(tabla.getSelectedRow(), 13).toString();
                int numeroPrecio = Integer.parseInt(tabla.getValueAt(tabla.getSelectedRow(),14).toString());
                interfaz.actualizarComparacionSeleccionada(referencia,codigoHijo,fecha, numeroPrecio);
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
        table.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                double precioPromos = Double.parseDouble(table.getValueAt(row, 9).toString());
                double precioComp = Double.parseDouble(table.getValueAt(row, 10).toString());
                if (precioPromos > precioComp) {
                    c.setBackground(Color.RED);
                } else {
                    c.setBackground(Color.GREEN);
                }
                return c;
            }
        });
        table.getColumnModel().getColumn(10).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                double precioPromos = Double.parseDouble(table.getValueAt(row, 9).toString());
                double precioComp = Double.parseDouble(table.getValueAt(row, 10).toString());
                if (precioPromos > precioComp) {
                    c.setBackground(Color.GREEN);
                } else {
                    c.setBackground(Color.RED);
                }
                return c;
            }
        });
        table.getColumnModel().getColumn(11).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                double precioPromos = Double.parseDouble(table.getValueAt(row, 11).toString());
                double precioComp = Double.parseDouble(table.getValueAt(row, 12).toString());
                if (precioPromos > precioComp) {
                    c.setBackground(Color.RED);
                } else {
                    c.setBackground(Color.GREEN);
                }
                return c;
            }
        });

        table.getColumnModel().getColumn(12).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                double precioPromos = Double.parseDouble(table.getValueAt(row, 11).toString());
                double precioComp = Double.parseDouble(table.getValueAt(row, 12).toString());
                if (precioPromos > precioComp) {
                    c.setBackground(Color.GREEN);
                } else {
                    c.setBackground(Color.RED);
                }
                return c;
            }
        });
        
    }

    public void refrescar(ListaComparaciones lista, ArrayList<ComparacionMarpico> listaMarpico){
        Comparacion comp = null;
        ComparacionMarpico compMarpico=null;
        modeloTabla.setRowCount(0);
        if(interfaz.getEsHistorico()){
            this.setBorder(BorderFactory.createTitledBorder("Historico Comparaciones"));
        }else{
            this.setBorder(BorderFactory.createTitledBorder("Comparaciones"));
        }
        if(interfaz.getComparador().equalsIgnoreCase("Promopciones")){
            
            for(int i =0; i<lista.getListaComparaciones().size();i++){
                comp=lista.getSortedList().get(i);
                modeloTabla.addRow(new Object[]{comp.getProductoPropio().getNombre(), comp.getProductoPropio().getReferencia(), comp.getProductoPropio().getDescuento(),comp.getProductoPropio().getDescuento2(),
                    comp.getProductoCompetencia().getCodigoHijo(),comp.getProductoCompetencia().getDescuento(),comp.getProductoCompetencia().getDescuento2(),comp.getProductoPropio().getPrecio1(), comp.getProductoCompetencia().getPrecioBase(),
                    comp.getProductoPropio().getPrecioDescuento(),comp.getProductoCompetencia().getPrecioDescuento(),comp.getProductoPropio().getPrecioDescuento2(),comp.getProductoCompetencia().getPrecioDescuento2()
                    ,comp.getFechaComparacion()!=null? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(comp.getFechaComparacion()):new SimpleDateFormat("yyyy-MM-dd").format(new Date()),comp.getNumeroPrecio()
                });
            }
            changeTable(tabla);
            rowSorter = new TableRowSorter<>(tabla.getModel());
            tabla.setRowSorter(rowSorter);
        }else if(interfaz.getComparador().equalsIgnoreCase("Marpico")){
            ArrayList<ComparacionMarpico> sorted = quickSort(listaMarpico);

            for(int i =0; i<sorted.size();i++){
                compMarpico=sorted.get(i);
                modeloTabla.addRow(new Object[]{compMarpico.getProductoPropio().getNombre(), compMarpico.getProductoPropio().getReferencia(), compMarpico.getProductoPropio().getDescuento(),compMarpico.getProductoPropio().getDescuento2(),
                    compMarpico.getProductoCompetencia().getFamilia(),compMarpico.getProductoCompetencia().getDescuento1(),compMarpico.getProductoCompetencia().getDescuento2(),compMarpico.getProductoPropio().getPrecio1(), compMarpico.getProductoCompetencia().getPrecio(),
                    compMarpico.getProductoPropio().getPrecioDescuento(),compMarpico.getProductoCompetencia().getPrecioDescuento1(),compMarpico.getProductoPropio().getPrecioDescuento2(),compMarpico.getProductoCompetencia().getPrecioDescuento2()
                    ,compMarpico.getFechaComparacion()!=null? compMarpico.getFechaComparacion():new SimpleDateFormat("yyyy-MM-dd").format(new Date()),compMarpico.getNumeroPrecio()
                });
            }
            changeTable(tabla);
            rowSorter = new TableRowSorter<>(tabla.getModel());
            tabla.setRowSorter(rowSorter);
        }
    }
    
    public ArrayList<ComparacionMarpico> quickSort(ArrayList<ComparacionMarpico> comparacion) {
        if(!comparacion.isEmpty()) {
            ArrayList<ComparacionMarpico> sorted;
            ArrayList<ComparacionMarpico> smaller=new ArrayList<>();
            ArrayList<ComparacionMarpico> greater=new ArrayList<>();
            ComparacionMarpico pivot = comparacion.get(0);
            int i;
            ComparacionMarpico j;
            for(i=1;i<comparacion.size();i++) {
                j=comparacion.get(i);
                if(j.compareTo(pivot.getProductoPropio().getReferencia())<0) {
                    smaller.add(j);
                }else {
                    greater.add(j);
                }
            }
            smaller=quickSort(smaller);
            greater=quickSort(greater);
            smaller.add(pivot);
            smaller.addAll(greater);
            sorted=smaller;
            return sorted;
        }
        return comparacion;
        
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
