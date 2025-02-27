/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aprilbutiquestore;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {
    private final JSpinner spinner;
    private final DBHelper dbHelper;
    private JTable table;
    private int idColumn; // Columna donde está el ID de la prenda
    private boolean validarStock; // Determina si valida stock o cantidad vendida

    // Constructor para Nueva Venta (valida stock)
    public SpinnerEditor(JTable table, DBHelper dbHelper, int idColumn) {
        this(table, dbHelper, idColumn, true);
    }

    // Constructor para Devolución (valida contra cantidad vendida)
    public SpinnerEditor(JTable table, DBHelper dbHelper, int idColumn, boolean validarStock) {
        this.table = table;
        this.dbHelper = dbHelper;
        this.idColumn = idColumn;
        this.validarStock = validarStock;
        
        int min = validarStock ? 0 : 1; // Mínimo 0 para devoluciones
        spinner = new JSpinner(new SpinnerNumberModel(min, 0, 100, 1)); // Valor inicial 0
        spinner.addChangeListener(e -> validarCantidad());
    }

    private void validarCantidad() {
        int viewRow = table.getEditingRow();
        if (viewRow == -1) return;

        int modelRow = table.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= table.getModel().getRowCount()) return;
        
        String codigo = (String) table.getModel().getValueAt(modelRow, idColumn);
        int nuevaCantidad = (int) spinner.getValue();

        if (validarStock) {
            validarStockDisponible(codigo, nuevaCantidad);
        } else {
            validarCantidadVendida(modelRow, nuevaCantidad);
        }
    }

    private void validarStockDisponible(String codigo, int nuevaCantidad) {
        String sql = "SELECT STOCK FROM PRENDAS WHERE ID_PRENDA = ?";
        try (ResultSet rs = dbHelper.ejecutarConsulta(sql, codigo)) {
            if (rs.next()) {
                int stock = rs.getInt("STOCK");
                if (nuevaCantidad > stock) {
                    JOptionPane.showMessageDialog(null, "Stock insuficiente");
                    spinner.setValue(Math.min(nuevaCantidad, stock));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error validando stock: " + e.getMessage());
        }
    }

    private void validarCantidadVendida(int modelRow, int nuevaCantidad) {
        int cantidadVendida = (int) table.getModel().getValueAt(modelRow, 2); // Columna de cantidad vendida
        if (nuevaCantidad > cantidadVendida) {
            JOptionPane.showMessageDialog(null, "No puede devolver más de " + cantidadVendida);
            spinner.setValue(cantidadVendida);
        }
    }

    @Override
    public Object getCellEditorValue() {
        return spinner.getValue();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, 
                                               boolean isSelected, int row, int column) {
        int modelRow = table.convertRowIndexToModel(row);
        spinner.setValue(value);
        
        if (!validarStock) { // Modo devolución
            // Obtener cantidad vendida de la columna 2
            int cantidadVendida = (int) table.getModel().getValueAt(modelRow, 2);
            SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
            
            // Ajustar máximo dinámicamente
            model.setMaximum(cantidadVendida);
        }

        return spinner;
    }
}

class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setText("X");
        setOpaque(true);
        setBackground(Color.RED);
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 12));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
}

// Clase ButtonEditor corregida para el carrito
class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
    private JButton button;
    private DefaultTableModel model;
    private JFormattedTextField txtTotal;
    // ✅ Inyectar DBHelper


   public ButtonEditor(JTable table, DefaultTableModel model, 
                       JFormattedTextField txtTotal, DBHelper dbHelper) {
        this.model = model;
        this.txtTotal = txtTotal;
        
        button = new JButton("X");
        button.setBackground(Color.RED);
        button.setForeground(Color.WHITE);
        
        button.addActionListener(e -> {
            // Obtener fila antes de detener la edición
            int viewRow = table.getEditingRow();
            if (viewRow == -1) return;
            
            // Convertir a índice del modelo
            int modelRow = table.convertRowIndexToModel(viewRow);
            
            // Detener edición
            if (table.isEditing()) {
                table.getCellEditor().stopCellEditing();
            }
            
            // Eliminar fila si es válida
            if (modelRow >= 0 && modelRow < model.getRowCount()) {
                model.removeRow(modelRow);
                actualizarTotal();
            }
            fireEditingStopped();
        });
    }

    private void actualizarTotal() {
        double total = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object valor = model.getValueAt(i, 4);
            if (valor instanceof Number number) {
                total += number.doubleValue();
            }
        }
        txtTotal.setValue(total);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, 
        boolean isSelected, int row, int column) {
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return "X";
    }
}