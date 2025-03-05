/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aprilbutiquestore;

import aprilbutiquestore.componentes.AbstractButtonEditor;
import com.itextpdf.text.BaseColor;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import oracle.jdbc.OracleTypes;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
/**
 *
 * @author gabri
 */
public class VentanaVentas extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(44, 62, 80);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private DBHelper dbHelper;
    private Connection conexion;
    
    private final ImageIcon viewIcon;
    private final ImageIcon undoIcon;
    
    private JLabel lblTotalPagado;
    private JLabel lblTotalVentas;
    private JLabel lblPorCobrar;

    public VentanaVentas() {
        try {
            conexion = BDConnection.getConnection();
            dbHelper = new DBHelper(conexion); 
            conexion.setAutoCommit(true); // Transacciones automáticas por defecto
        } catch (SQLException ex) {
            showError("Error crítico: No se pudo conectar a la base de datos\n" + ex.getMessage());
            System.exit(1);
        }
        
        this.viewIcon = loadImageIcon("eye.png", 20, 20);
        this.undoIcon = loadImageIcon("undo.png", 20, 20);
        
        configureWindow();
        initializeTableModel();
        setupUIComponents();
        loadTableData();
    }
    
    @Override
    public void dispose() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException ex) {
            System.err.println("Error cerrando conexión: " + ex.getMessage());
        }
        super.dispose();
    }
    
    private void nuevaVenta() {
        JDialog dialog = new JDialog(this, "Nueva Venta", true);
        dialog.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout());
        JTextField txtCodigo = new JTextField(15);
        JButton btnAgregar = new JButton("Agregar");

        inputPanel.add(new JLabel("Código de Prenda:"));
        inputPanel.add(txtCodigo);
        inputPanel.add(btnAgregar);

        // Modificar la creación del modelo para incluir formato
        DefaultTableModel detalleModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 3 || column == 4 ? Double.class : Object.class;
            }
        };

        JLabel lblTotal = new JLabel("Total:");
        // Cambiar la creación del campo total
        JFormattedTextField txtTotal = new JFormattedTextField(NumberFormat.getCurrencyInstance());
        txtTotal.setEditable(false);
        txtTotal.setValue(0.00);
        txtTotal.setHorizontalAlignment(JTextField.RIGHT);
        txtTotal.setPreferredSize(new Dimension(150, 25)); // Añadir este tamaño
        
        // En el método nuevaVenta()
        detalleModel.addTableModelListener(e -> {
            // Manejar eventos de eliminación de filas
            if (e.getType() == TableModelEvent.DELETE || e.getFirstRow() == TableModelEvent.ALL_COLUMNS) {
                recalcularTotal(detalleModel, txtTotal);
                return;
            }

            // Manejar actualizaciones de celdas
            int row = e.getFirstRow();
            int col = e.getColumn();

            // Validar índices válidos
            if (row < 0 || row >= detalleModel.getRowCount() || col < 0) return;

            if (col == 2 || col == 3) { 
                actualizarSubtotal(detalleModel, row);
            }
            recalcularTotal(detalleModel, txtTotal);
        });
        
        detalleModel.setColumnIdentifiers(new String[]{"Código", "Prenda", "Cantidad", "Precio Unitario", "Subtotal", "Eliminar"});
        JTable detalleTable = new JTable(detalleModel);
        
        // En el método nuevaVenta() después de crear la tabla
        detalleTable.setDefaultRenderer(Double.class, new DefaultTableCellRenderer() {
            private final DecimalFormat df = new DecimalFormat("#,##0.00");

            @Override
            public Component getTableCellRendererComponent(
                JTable table, Object value, 
                boolean isSelected, boolean hasFocus, 
                int row, int column) 
            {
                if (value instanceof Double) {
                    value = df.format(value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });
        
        
        // Configurar spinner para cantidad (existente)
        TableColumn cantidadColumna = detalleTable.getColumnModel().getColumn(2);
        cantidadColumna.setCellEditor(new SpinnerEditor(detalleTable, dbHelper, 0)); // ID en columna 0

        // Nuevo editor para precio unitario
        TableColumn precioColumna = detalleTable.getColumnModel().getColumn(3);
        precioColumna.setCellEditor(new PriceEditor());
        precioColumna.setCellRenderer(new PriceRenderer());

        // Resto de configuraciones existentes
        TableColumn eliminarColumna = detalleTable.getColumnModel().getColumn(5);
        eliminarColumna.setCellRenderer(new aprilbutiquestore.ButtonRenderer());
        eliminarColumna.setCellEditor(new aprilbutiquestore.ButtonEditor(detalleTable, detalleModel, txtTotal, dbHelper)); 

        btnAgregar.addActionListener(e -> agregarPrendaACarrito(txtCodigo.getText().trim(), detalleModel));
        txtCodigo.addActionListener(e -> agregarPrendaACarrito(txtCodigo.getText().trim(), detalleModel));

        JButton btnFinalizar = new JButton("Finalizar Venta");
        btnFinalizar.addActionListener(e -> registrarVentaEnBD(detalleModel, dialog));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(btnFinalizar, BorderLayout.EAST);
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dialog.dispose());

        // Panel para el total
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        totalPanel.add(lblTotal);
        totalPanel.add(txtTotal);

        // Panel para botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnFinalizar);

        bottomPanel.add(totalPanel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        // ... (resto del código existente)

        dialog.add(inputPanel, BorderLayout.NORTH);
        dialog.add(new JScrollPane(detalleTable), BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH); // Cambiado de .add(bottomPanel, BorderLayout.SOUTH)
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void actualizarSubtotal(DefaultTableModel model, int row) {
        try {
            int cantidad = Integer.parseInt(model.getValueAt(row, 2).toString());
            double precio = Double.parseDouble(model.getValueAt(row, 3).toString());
            model.setValueAt(cantidad * precio, row, 4);
        } catch (NumberFormatException ex) {
            model.setValueAt(0.00, row, 4);
        }
    }

    private void recalcularTotal(DefaultTableModel model, JFormattedTextField txtTotal) {
        double total = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object valor = model.getValueAt(i, 4);
            if (valor instanceof Number number) {
                total += number.doubleValue();
            }
        }
        txtTotal.setValue(total);
    }



    private void registrarVentaEnBD(DefaultTableModel detalleModel, JDialog dialog) {
        if (detalleModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "No hay productos en la venta.");
            return;
        }

        // Diálogo ampliado para datos del cliente
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblTotalVenta = new JLabel("Total Venta:");
        JFormattedTextField txtTotalVenta = new JFormattedTextField(NumberFormat.getCurrencyInstance());
        txtTotalVenta.setEditable(false);
        txtTotalVenta.setValue(calcularTotal(detalleModel));
        txtTotalVenta.setPreferredSize(new Dimension(150, 25));

        JLabel lblEstado = new JLabel("Estado:");
        JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"SEPARADO", "PAGADO", "ENTREGADO", "DEVUELTO", "DEV PARCIAL"});
        // Componentes para cliente
        JLabel lblDNI = new JLabel("DNI Cliente:");
        JTextField txtDNI = new JTextField(15);
        JButton btnVerificar = new JButton("Verificar");
        JLabel lblNombre = new JLabel("Nombre:");
        JTextField txtNombre = new JTextField(15);
        JLabel lblTelefono = new JLabel("Teléfono:");
        JTextField txtTelefono = new JTextField(15);
        JLabel lblCorreo = new JLabel("Correo:");
        JTextField txtCorreo = new JTextField(15);
        JLabel lblMetodo = new JLabel("Método de Pago:");
        JComboBox<String> cmbMetodo = new JComboBox<>(new String[]{"EFECTIVO", "TRANSFERENCIA", "YAPE VALERIE", "YAPE ALDAIR"});
        JLabel lblMonto = new JLabel("Monto Pagado:");
        JFormattedTextField txtMonto = new JFormattedTextField(NumberFormat.getNumberInstance());

        // Configurar verificación de DNI
        btnVerificar.addActionListener(e -> verificarCliente(
            txtDNI.getText().trim(), 
            txtTelefono.getText().trim(), 
            txtDNI, 
            txtNombre, 
            txtTelefono, 
            txtCorreo
        ));

        // Resto de componentes
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(lblDNI, gbc);
        gbc.gridx = 1;
        formPanel.add(txtDNI, gbc);
        gbc.gridx = 2;
        formPanel.add(btnVerificar, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(lblNombre, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        formPanel.add(txtNombre, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(lblTelefono, gbc);
        gbc.gridx = 1;
        formPanel.add(txtTelefono, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(lblCorreo, gbc);
        gbc.gridx = 1;
        formPanel.add(txtCorreo, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(lblMetodo, gbc);
        gbc.gridx = 1;
        formPanel.add(cmbMetodo, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(lblMonto, gbc);
        gbc.gridx = 1;
        formPanel.add(txtMonto, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(lblEstado, gbc);
        gbc.gridx = 1;
        formPanel.add(cmbEstado, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(lblTotalVenta, gbc);
        gbc.gridx = 1;
        formPanel.add(txtTotalVenta, gbc);

        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(lblMonto, gbc);
        gbc.gridx = 1;
        txtMonto.setPreferredSize(new Dimension(150, 25)); // Ajustar tamaño
        formPanel.add(txtMonto, gbc);
        
        // En el método registrarVentaEnBD, agregar estos estilos:
        txtTotalVenta.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtMonto.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Para los combobox:
        cmbEstado.setPreferredSize(new Dimension(150, 25));
        cmbMetodo.setPreferredSize(new Dimension(150, 25));
        
        int result = JOptionPane.showConfirmDialog(
            null, 
            formPanel, 
            "Datos del Cliente y Pago", 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        // Validaciones
        if (txtDNI.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "DNI es obligatorio");
            return;
        }

        try (Connection conn = BDConnection.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Registrar/Actualizar cliente
            if (txtNombre.isVisible()) { // Si los campos están visibles, es cliente nuevo
                registrarOActualizarCliente(
                    txtDNI.getText().trim(),
                    txtNombre.getText().trim(),
                    txtTelefono.getText().trim(),
                    txtCorreo.getText().trim()
                );
            }

            // 2. Registrar venta
            int idVenta = registrarVenta(
                txtDNI.getText().trim(),
                txtTelefono.getText().trim(),
                calcularTotal(detalleModel),
                cmbMetodo.getSelectedItem().toString(),
                ((Number) txtMonto.getValue()).doubleValue(),
                cmbEstado.getSelectedItem().toString()  // Nuevo parámetro
            );

            // 3. Registrar detalles
            registrarDetallesVenta(idVenta, detalleModel);

            conn.commit();
            JOptionPane.showMessageDialog(null, "Venta registrada exitosamente!");
            dialog.dispose();
            loadTableData();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }
    
    private double calcularTotal(DefaultTableModel detalleModel) {
        double total = 0;
        for (int i = 0; i < detalleModel.getRowCount(); i++) {
            total += Double.parseDouble(detalleModel.getValueAt(i, 4).toString());
        }
        return total;
    }
    
    private void verificarCliente(String dni, String telefono, JTextField txtDNI, JTextField txtNombre, JTextField txtTelefono, JTextField txtCorreo) {
    if (dni.isEmpty() && telefono.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Ingrese un DNI o Teléfono para verificar");
        return;
    }

    String sql = "SELECT DNI, NOMBRE, TELEFONO, CORREO FROM CLIENTES WHERE DNI = ? OR TELEFONO = ?";
    try (ResultSet rs = dbHelper.ejecutarConsulta(sql, dni, telefono)) {

        if (rs.next()) { // Cliente encontrado
            txtDNI.setText(rs.getString("DNI"));  // Autocompleta el DNI
            txtNombre.setText(rs.getString("NOMBRE").toUpperCase());
            txtTelefono.setText(rs.getString("TELEFONO"));
            txtCorreo.setText(rs.getString("CORREO"));
            
            txtDNI.setEditable(true);  // Bloquea el DNI si ya existe
            txtNombre.setEditable(true);
            txtTelefono.setEditable(true);
            txtCorreo.setEditable(true);
        } else { // Cliente nuevo
            JOptionPane.showMessageDialog(null, "No se encontró el cliente");
            txtNombre.setText("");
            txtDNI.setText(dni);  // Mantiene el DNI ingresado
            txtTelefono.setText(telefono); // Mantiene el teléfono ingresado
            txtCorreo.setText("");

            txtDNI.setEditable(true);
            txtNombre.setEditable(true);
            txtTelefono.setEditable(true);
            txtCorreo.setEditable(true);
        }

        // Mostrar campos
        txtDNI.setVisible(true);
        txtNombre.setVisible(true);
        txtTelefono.setVisible(true);
        txtCorreo.setVisible(true);

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null, "Error verificando cliente: " + ex.getMessage());
    }
}

    
    private void registrarOActualizarCliente(String dni, String nombre, String telefono, String correo) 
    throws SQLException {
    
        dbHelper.ejecutarProcedimiento( // ✅ Usar DBHelper
            "REGISTRAR_CLIENTE(?, ?, ?, ?)", 
            dni, nombre, telefono, correo
        );
    }
    
    private int registrarVenta(String dni, String telefono, double total, 
        String metodoPago, double montoPagado, String estado) throws SQLException {

        Object[] parametrosIn = {dni, telefono, total, metodoPago, montoPagado, estado};
        int[] posicionesOut = {7}; // Posición del parámetro OUT
        int[] tiposOut = {Types.INTEGER};

        Map<Integer, Object> resultados = dbHelper.ejecutarProcedimientoConOut(
            "REGISTRAR_VENTA",
            parametrosIn,
            posicionesOut,
            tiposOut
        );

        return (int) resultados.get(7);
    }
    
    private void registrarDetallesVenta(int idVenta, DefaultTableModel detalleModel) 
    throws SQLException {
        for (int i = 0; i < detalleModel.getRowCount(); i++) {
            // Obtener datos del modelo
            int idPrenda = Integer.parseInt(detalleModel.getValueAt(i, 0).toString());
            int cantidad = Integer.parseInt(detalleModel.getValueAt(i, 2).toString());
            double precioFinal = Double.parseDouble(detalleModel.getValueAt(i, 3).toString());

            // Registrar detalle
            dbHelper.ejecutarProcedimiento( // ✅ Usar DBHelper en cada iteración
                "REGISTRAR_DETALLE_VENTA(?, ?, ?, ?)",
                idVenta, idPrenda, cantidad, precioFinal
            );
        }
    }

    private void agregarPrendaACarrito(String codigo, DefaultTableModel detalleModel) {
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese un código de prenda.");
            return;
        }

        String sql = "SELECT NOMBRE, stock FROM ADMIN.PRENDAS WHERE ID_PRENDA = ?";
        try (ResultSet rs = dbHelper.ejecutarConsulta(sql, codigo)) { // ✅ Usar DBHelper
            if (rs.next()) {
                String nombre = rs.getString("NOMBRE");
                int stock = Integer.parseInt(rs.getString("stock"));
                // Verificar si ya existe
                if(stock>0){
                    for (int i = 0; i < detalleModel.getRowCount(); i++) {
                        if (detalleModel.getValueAt(i, 0).equals(codigo)) {
                            JOptionPane.showMessageDialog(null, "La prenda ya está en el carrito");
                            return;
                        }
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Stock Insuficiente");
                    return;
                }

                // Agregar nueva fila con precio vacío
                detalleModel.addRow(new Object[]{
                    codigo, 
                    nombre, 
                    1,    // Cantidad inicial
                    "",   // Precio vacío
                    0.00, // Subtotal inicial
                    "X"   // Botón eliminar
                });

            } else {
                JOptionPane.showMessageDialog(null, "Prenda no encontrada");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar prenda: " + e.getMessage());
        }
    }
    
    private void loadTableData() {
        try (ResultSet rs = dbHelper.ejecutarFuncionCursor("LISTAR_VENTAS")) { // ✅ Usar DBHelper
            model.setRowCount(0);
            while (rs.next()) {
                    model.addRow(createTableRow(rs));
            }
            actualizarTotalesVentas();
        } catch (SQLException e) {
            showError("""
                      Error conectando a la base de datos:
                      C\u00f3digo: """ + e.getErrorCode() + "\n"
                + "Mensaje: " + e.getMessage());
        }
    }
    
    private void actualizarTotalesVentas() {
        try (ResultSet rs = dbHelper.ejecutarFuncionCursor("CALCULAR_TOTALES_VENTAS")) {
            if (rs.next()) {
                double totalPagado = rs.getDouble("TOTAL_PAGADO");
                double totalVentas = rs.getDouble("TOTAL_VENTAS");

                // Actualizar labels
                lblTotalPagado.setText(String.format("Total Pagado: S/%.2f", totalPagado));
                lblTotalVentas.setText(String.format("Total Ventas: S/%.2f", totalVentas));
                lblPorCobrar.setText(String.format("Por Cobrar: S/%.2f", totalVentas-totalPagado));
            }
        } catch (SQLException ex) {
            showError("Error obteniendo totales: " + ex.getMessage());
        }
    }
    
    private JPanel createTotalsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        lblTotalPagado = new JLabel("Total Pagado: S/0.00");
        lblTotalVentas = new JLabel("Total Ventas: S/0.00");
        lblPorCobrar = new JLabel("Por Cobrar: S/0.00");

        // Estilos
        Font boldFont = new Font("Segoe UI", Font.BOLD, 14);
        java.util.List.of(lblTotalPagado, lblTotalVentas, lblPorCobrar).forEach(l -> {
            l.setFont(boldFont);
            l.setHorizontalAlignment(SwingConstants.CENTER);
        });
        
        panel.add(lblTotalPagado);
        panel.add(lblPorCobrar);
        panel.add(lblTotalVentas);
        

        return panel;
    }
    
    private Object[] createTableRow(ResultSet rs) throws SQLException {
        return new Object[]{
            rs.getString("ID_VENTA"),
            rs.getString("FECHA_FORMATEADA"),
            String.format("%.2f", rs.getDouble("TOTAL_VENTA")), // Formatear aquí
            rs.getString("DNI_CLIENTE"),
            rs.getString("ESTADO"),
            String.format("%.2f", rs.getDouble("MONTO_PAGADO")), 
            String.format("%.2f", rs.getDouble("MONTO_RESTANTE")),
            rs.getString("METODO_PAGO"),
            rs.getString("FECHA_ENTREGA"),
            ""
        };
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void configureWindow() {
        setTitle("Gestión de Ventas");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initializeTableModel() {
        model = new DefaultTableModel();
        String[] columns = {
            "ID", 
            "Fecha", 
            "Total", 
            "Ciente", 
            "Estado", 
            "Pagado", 
            "Restante", 
            "Método Pago", 
            "Fecha Entrega", 
            ""
        };
        model.setColumnIdentifiers(columns);
    }

    private void setupUIComponents() {
        configureTable();
        configureTableStyle();
        configureEstadoColumn();
        configureActionsColumn();

        add(createHeader(), BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        mainContent.add(createSalesToolbar(), BorderLayout.NORTH);
        mainContent.add(createTablePanel(), BorderLayout.CENTER);
        mainContent.add(createTotalsPanel(), BorderLayout.SOUTH);
        
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);


        add(mainContent);
    }
    
    private void configureEstadoColumn() {
        TableColumn estadoColumn = table.getColumnModel().getColumn(4);
        JComboBox<String> comboBox = new JComboBox<>(new String[]{"SEPARADO", "PAGADO", "ENTREGADO", "DEVUELTO", "DEV PARCIAL"});

        // Usar CellEditor personalizado para manejar cambios
        estadoColumn.setCellEditor(new DefaultCellEditor(comboBox) {
            @Override
            public Component getTableCellEditorComponent(
                JTable table, Object value, 
                boolean isSelected, int row, int column
            ) {
                JComboBox<String> cb = (JComboBox<String>) super.getTableCellEditorComponent(
                    table, value, isSelected, row, column
                );
                cb.setSelectedItem(value);
                return cb;
            }
        });

        // Listener para cambios
        comboBox.addActionListener(e -> actualizarEstadoEnBD(comboBox));
    }

    private void actualizarEstadoEnBD(JComboBox<String> comboBox) {
        int viewRow = table.getEditingRow();

        // Verificar si la fila es válida
        if (viewRow == -1) return;

        // Convertir índice de la vista al modelo (necesario si hay ordenamiento/filtrado)
        int modelRow = table.convertRowIndexToModel(viewRow);

        // Obtener datos del modelo (no de la vista)
        String idVenta = model.getValueAt(modelRow, 0).toString();
        String nuevoEstado = (String) comboBox.getSelectedItem();

        try {
            dbHelper.ejecutarProcedimiento( // ✅ Usar DBHelper
                "ACTUALIZAR_ESTADO_VENTA(?, ?)", 
                idVenta, nuevoEstado
            );
            model.setValueAt(nuevoEstado, modelRow, 4);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error actualizando estado: " + ex.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
        }
        loadTableData();
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);

        // Logo
        JLabel logo = new JLabel(loadImageIcon("logo.png", 81, 50));

        // Panel derecha (usuario + acciones)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        rightPanel.setBackground(PRIMARY_COLOR);

        // Obtener usuario de la BD
        String usuario = BDConnection.getCurrentUser(); // Método a implementar
        JLabel userLabel = new JLabel("Usuario: " + usuario.toUpperCase());
        userLabel.setForeground(Color.WHITE);
        userLabel.setVerticalAlignment(SwingConstants.CENTER); 
        userLabel.setHorizontalAlignment(SwingConstants.RIGHT); 
    
        userLabel.setPreferredSize(new Dimension(150, 40)); // Altura fija
        rightPanel.add(userLabel);
        
        

        header.add(logo, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }
    
    private ImageIcon loadImageIcon(String filename, int width, int height) {
        try {
            // Cargar desde la carpeta resources usando ruta relativa
            BufferedImage img = ImageIO.read(new File("resources/" + filename));
            return new ImageIcon(img.getScaledInstance(width, height, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error cargando ícono: " + filename);
            return new ImageIcon(); // Fallback
        }
    }
    private void mostrarDialogoExportacion() {
        JDialog configDialog = new JDialog(this, "Configurar Exportación PDF", true);
        configDialog.setLayout(new BorderLayout());

        // Panel de selección de columnas
        JPanel columnPanel = new JPanel(new GridLayout(0, 2));
        Map<String, JCheckBox> columnCheckboxes = new LinkedHashMap<>();
        Map<String, String> mapeoOficial = crearMapeoColumnasVentas();

        for(String nombreVisual : mapeoOficial.keySet()) {
            JCheckBox checkBox = new JCheckBox(nombreVisual, true);
            columnCheckboxes.put(nombreVisual, checkBox);
            columnPanel.add(checkBox);
        }
        
        JPanel filterDialog = new JPanel(new GridLayout(0, 2, 10, 10));

        // Configurar fechas predeterminadas
        Calendar calDefaultDesde = Calendar.getInstance();
        calDefaultDesde.set(2024, Calendar.JANUARY, 1); // 1 de Enero 2024

        // JSpinner para fecha desde
        JSpinner desdePicker = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor desdeEditor = new JSpinner.DateEditor(desdePicker, "dd/MM/yyyy");
        desdePicker.setEditor(desdeEditor);
        desdePicker.setValue(calDefaultDesde.getTime());

        // JSpinner para fecha hasta (valor predeterminado = hoy)
        JSpinner hastaPicker = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor hastaEditor = new JSpinner.DateEditor(hastaPicker, "dd/MM/yyyy");
        hastaPicker.setEditor(hastaEditor);
       
        // Campos de precio con formato numérico
        JFormattedTextField precioMinField = new JFormattedTextField(NumberFormat.getNumberInstance());
        JFormattedTextField precioMaxField = new JFormattedTextField(NumberFormat.getNumberInstance());
        precioMinField.setValue(0.00);
        precioMaxField.setValue(0.00);

        // Autocompletado para DNI
        JComboBox<String> dniClienteField = new JComboBox<>();
        dniClienteField.setEditable(true);
        cargarDatosUnicos("NOMBRE", dniClienteField);

        // Combos para selecciones
        JComboBox<String> estadoFilter = new JComboBox<>(new String[]{"Todos", "SEPARADO", "PAGADO", "ENTREGADO", "DEVUELTO", "DEV PARCIAL"});
        JComboBox<String> metodoPagoFilter = new JComboBox<>(new String[]{"Todos", "EFECTIVO", "TRANSFERENCIA", "YAPE VALERIE", "YAPE ALDAIR"});
        JComboBox<String> faltaPagarCombo = new JComboBox<>(new String[]{"Todos", "Falta Pagar", "Pagado Completo"});

        // Añadir componentes
        filterDialog.add(new JLabel("Desde:"));
        filterDialog.add(desdePicker);
        filterDialog.add(new JLabel("Hasta:"));
        filterDialog.add(hastaPicker);
        filterDialog.add(new JLabel("Precio Min:"));
        filterDialog.add(precioMinField);
        filterDialog.add(new JLabel("Precio Max:"));
        filterDialog.add(precioMaxField);
        filterDialog.add(new JLabel("DNI Cliente:"));
        filterDialog.add(dniClienteField);
        filterDialog.add(new JLabel("Estado:"));
        filterDialog.add(estadoFilter);
        filterDialog.add(new JLabel("Método de Pago:"));
        filterDialog.add(metodoPagoFilter);
        filterDialog.add(new JLabel("Estado de Pago:"));
        filterDialog.add(faltaPagarCombo);

        // Botones
        JButton btnExportar = new JButton("Exportar");
        JButton btnCancelar = new JButton("Cancelar");

        btnExportar.addActionListener(e -> {
            try {
                java.util.Date utilDesde = (java.util.Date) desdePicker.getValue();
                java.util.Date utilHasta = (java.util.Date) hastaPicker.getValue();

                // Crear objetos Calendar para ajustar horas
                Calendar cal = Calendar.getInstance();

                // Para fecha DESDE (00:00:00)
                cal.setTime(utilDesde);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                java.sql.Timestamp sqlDesde = new java.sql.Timestamp(cal.getTimeInMillis());

                // Para fecha HASTA (23:59:59)
                cal.setTime(utilHasta);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                java.sql.Timestamp sqlHasta = new java.sql.Timestamp(cal.getTimeInMillis());


                // Validar rango de fechas
                if (sqlDesde.after(sqlHasta)) {
                    JOptionPane.showMessageDialog(filterDialog, 
                        "La fecha 'Desde' no puede ser mayor que 'Hasta'", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Obtener precios
                double precioMin = ((Number) precioMinField.getValue()).doubleValue();
                double precioMax = ((Number) precioMaxField.getValue()).doubleValue();
                // Validar precios
                if (precioMin < 0 || precioMax < 0) {
                    JOptionPane.showMessageDialog(filterDialog,
                            "Los precios no pueden ser negativos",
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Obtener otros valores
                String dniCliente = dniClienteField.getSelectedItem() != "Todos" ? 
                        dniClienteField.getSelectedItem().toString().trim() : "";
                String estado = estadoFilter.getSelectedItem().toString();
                String metodoPago = metodoPagoFilter.getSelectedItem().toString();
                int faltaPagarValue = faltaPagarCombo.getSelectedIndex();
                generarPDF(columnCheckboxes, sqlDesde, sqlHasta, precioMin > 0 ? precioMin : 0,
                        precioMax > 0 ? precioMax : 0, dniCliente, estado, metodoPago, faltaPagarValue);
                configDialog.dispose();
            } catch (HeadlessException ex) {
                JOptionPane.showMessageDialog(filterDialog, "Error en fechas: " + ex.getMessage());
                System.out.println("Error en fechas: " + ex.getMessage());
            }   
            
            
        });

        btnCancelar.addActionListener(e -> configDialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnExportar);
        buttonPanel.add(btnCancelar);

        configDialog.add(new JScrollPane(filterDialog), BorderLayout.NORTH);
        configDialog.add(new JScrollPane(columnPanel), BorderLayout.CENTER);
        configDialog.add(buttonPanel, BorderLayout.SOUTH);
        configDialog.setSize(500, 700);
        configDialog.setLocationRelativeTo(this);
        configDialog.setVisible(true);
    }

    private void generarPDF(Map<String, JCheckBox> columnCheckboxes, java.sql.Timestamp desde, java.sql.Timestamp hasta, 
        double precioMin, double precioMax, String dniCliente, String estado, 
        String metodoPago, int faltaPagar) {
    
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar PDF");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos PDF", "pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                file = new File(file.getParentFile(), file.getName() + ".pdf");
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                Document document = new Document(PageSize.A4.rotate());
                PdfWriter writer = PdfWriter.getInstance(document, fos);

                document.open();
                agregarMetadatos(document);
                agregarEncabezadoVentas(document, desde, hasta);

                // Ejecutar procedimiento
                Object[] parametrosIn = {
                    desde,
                    hasta,
                    precioMin > 0 ? precioMin : null,
                    precioMax > 0 ? precioMax : null,
                    dniCliente.isEmpty() ? null : dniCliente,
                    estado.equals("Todos") ? null : estado,
                    metodoPago.equals("Todos") ? null : metodoPago,
                    faltaPagar
                };

                Map<Integer, Object> resultados = dbHelper.ejecutarProcedimientoConOut(
                    "filtrar_ordenes",
                    parametrosIn,
                    new int[]{9}, 
                    new int[]{OracleTypes.CURSOR}
                );

                try (ResultSet rs = (ResultSet) resultados.get(9)) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    List<ColumnaPDF> columnasVisibles = obtenerColumnasVisiblesVentas(metaData, columnCheckboxes);

                    if(columnasVisibles.isEmpty()) {
                        JOptionPane.showMessageDialog(this, 
                            "Seleccione al menos una columna", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    PdfPTable pdfTable = crearTablaPDFVentas(columnasVisibles);
                    final double[] valorTotales = new double[2]; // [0: subtotal, 1: totalUSD, 2: totalPEN]
                    boolean primeraFila = true;

                    while(rs.next()) {
                        if(primeraFila) {
                            valorTotales[0] = rs.getDouble("TOTAL_PAGADO");
                            valorTotales[1] = rs.getDouble("TOTAL_VENTAS");
                            primeraFila = false;
                        }
                        procesarFilaVentas(rs, pdfTable, columnasVisibles);
                    }

                    // Agregar totales
                    Paragraph totales = new Paragraph("\n");
                    totales.add(new Phrase(String.format("Total Pagado: S/%,.2f\n", valorTotales[0])));
                    totales.add(new Phrase(String.format("Por Cobrar: S/%,.2f\n", valorTotales[1] - valorTotales[0])));
                    totales.add(new Phrase(String.format("Total Ventas: S/%,.2f\n", valorTotales[1])));
                    totales.add(new Phrase("\n\n"));
                    document.add(totales);

                    document.add(pdfTable);
                }

                agregarPiePagina(writer, document);
                document.close();

                JOptionPane.showMessageDialog(this, 
                    "PDF generado exitosamente!", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error generando PDF: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Dentro de tu clase de la ventana de ventas (mismo nivel que los demás métodos)
    private static class ColumnaPDF {
        int indice;
        String nombreVisual;
        String nombreBD;

        public ColumnaPDF(int indice, String nombreVisual, String nombreBD) {
            this.indice = indice;
            this.nombreVisual = nombreVisual;
            this.nombreBD = nombreBD;
        }
    }

    // Métodos auxiliares específicos para Ventas
    private void agregarEncabezadoVentas(Document document, Timestamp desde, Timestamp hasta) 
            throws DocumentException {
        Paragraph header = new Paragraph();
        header.add(new Phrase("Reporte de Ventas\n", 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fechaDesde = (desde != null) ? sdf.format(desde) : "N/A";
        String fechaHasta = (hasta != null) ? sdf.format(hasta) : "N/A";

        header.add(new Phrase("Período: " + fechaDesde + " - " + fechaHasta + "\n\n", 
            FontFactory.getFont(FontFactory.HELVETICA, 12)));
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);
    }

    private List<ColumnaPDF> obtenerColumnasVisiblesVentas(ResultSetMetaData metaData, 
            Map<String, JCheckBox> checkboxes) throws SQLException {

        List<ColumnaPDF> columnas = new ArrayList<>();
        Map<String, String> mapeoColumnas = crearMapeoColumnasVentas();

        for(int i = 1; i <= metaData.getColumnCount(); i++) {
            String nombreBD = metaData.getColumnLabel(i).toUpperCase();
            String nombreVisual = obtenerNombreVisual(nombreBD, mapeoColumnas);

            if(nombreVisual != null) {
                JCheckBox checkbox = checkboxes.get(nombreVisual);
                if(checkbox != null && checkbox.isSelected()) {
                    columnas.add(new ColumnaPDF(i, nombreVisual, nombreBD));
                }
            }
        }
        return columnas;
    }
    
    private String obtenerNombreVisual(String nombreBD, Map<String, String> mapeoColumnas) {
        String nombreBDBusqueda = nombreBD.trim().toUpperCase();

        return mapeoColumnas.entrySet().stream()
            .filter(entry -> entry.getValue().toUpperCase().equals(nombreBDBusqueda))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(null);
    }

    private PdfPTable crearTablaPDFVentas(List<ColumnaPDF> columnas) {
        PdfPTable tabla = new PdfPTable(columnas.size());
        tabla.setWidthPercentage(100);

        columnas.forEach(col -> {
            PdfPCell celda = new PdfPCell(new Phrase(col.nombreVisual, 
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            celda.setBackgroundColor(new BaseColor(240, 240, 240));
            tabla.addCell(celda);
        });

        return tabla;
    }

    private void procesarFilaVentas(ResultSet rs, PdfPTable tabla, 
            List<ColumnaPDF> columnas) throws SQLException {

        for(ColumnaPDF columna : columnas) {
            Object valor = rs.getObject(columna.indice);
            String valorFormateado = formatearValorVentas(valor, columna.nombreBD);

            PdfPCell celda = new PdfPCell(new Phrase(valorFormateado, 
                FontFactory.getFont(FontFactory.HELVETICA, 9)));

            if(esColumnaNumericaVentas(columna.nombreBD)) {
                celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            }
            tabla.addCell(celda);
        }
    }

    private String formatearValorVentas(Object valor, String nombreColumna) {
        if(valor == null) return "";

        try {
            if(nombreColumna.matches("TOTAL_VENTA|MONTO_PAGADO|MONTO_RESTANTE")) {
                double monto = Double.parseDouble(valor.toString());
                return String.format("S/ %,.2f", monto);
            }

            if(nombreColumna.equals("FECHA_VENTA") || nombreColumna.equals("FECHA_ENTREGA")) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                return sdf.format((Timestamp) valor);
            }

        } catch(NumberFormatException e) {
            return valor.toString();
        }
        return valor.toString();
    }

    private boolean esColumnaNumericaVentas(String nombreColumna) {
        return nombreColumna.matches("TOTAL_VENTA|MONTO_PAGADO|MONTO_RESTANTE");
    }

    private Map<String, String> crearMapeoColumnasVentas() {
        Map<String, String> mapeo = new LinkedHashMap<>();
        mapeo.put("ID Venta", "ID_VENTA");
        mapeo.put("Fecha Venta", "FECHA_FORMATEADA");
        mapeo.put("Total Venta", "TOTAL_VENTA");
        mapeo.put("DNI Cliente", "DNI_CLIENTE");
        mapeo.put("Estado", "ESTADO");
        mapeo.put("Monto Pagado", "MONTO_PAGADO");
        mapeo.put("Monto Restante", "MONTO_RESTANTE");
        mapeo.put("Método Pago", "METODO_PAGO");
        mapeo.put("Fecha Entrega", "FECHA_ENTREGA");
        return mapeo;
    }

    
    private void agregarMetadatos(Document document) {
        document.addTitle("Reporte de Ventas");
        document.addSubject("Generado desde April Boutique Store");
        document.addKeywords("ventas, reporte, pdf");
        document.addAuthor("Sistema April Boutique");
        document.addCreator(BDConnection.getCurrentUser());
    }

    private void agregarPiePagina(PdfWriter writer, Document document) throws DocumentException {
        PdfContentByte cb = writer.getDirectContent();

        // Obtener usuario
        String usuario = BDConnection.getCurrentUser();

        // Obtener fecha y hora actual
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaGeneracion = now.format(formatter);

        // Coordenadas del pie de página
        float x = document.right();  // Alineado a la derecha
        float y = document.bottom() - 20;  // Espacio desde el borde inferior

        // Agregar usuario
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, 
            new Phrase("Generado por: " + usuario, FontFactory.getFont(FontFactory.HELVETICA, 9)), 
            x, y, 0);

        // Agregar fecha debajo del usuario
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, 
            new Phrase("Fecha: " + fechaGeneracion, FontFactory.getFont(FontFactory.HELVETICA, 9)), 
            x, y - 12, 0);  // Bajamos 12 unidades para que no se superpongan
    }
    
    private void showFilterDialog() {
        JDialog filterDialog = new JDialog(this, "Filtros Avanzados", true);
        filterDialog.setLayout(new GridLayout(0, 2, 10, 10));

        // Configurar fechas predeterminadas
        Calendar calDefaultDesde = Calendar.getInstance();
        calDefaultDesde.set(2024, Calendar.JANUARY, 1); // 1 de Enero 2024

        // JSpinner para fecha desde
        JSpinner desdePicker = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor desdeEditor = new JSpinner.DateEditor(desdePicker, "dd/MM/yyyy");
        desdePicker.setEditor(desdeEditor);
        desdePicker.setValue(calDefaultDesde.getTime());

        // JSpinner para fecha hasta (valor predeterminado = hoy)
        JSpinner hastaPicker = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor hastaEditor = new JSpinner.DateEditor(hastaPicker, "dd/MM/yyyy");
        hastaPicker.setEditor(hastaEditor);
       
        // Campos de precio con formato numérico
        JFormattedTextField precioMinField = new JFormattedTextField(NumberFormat.getNumberInstance());
        JFormattedTextField precioMaxField = new JFormattedTextField(NumberFormat.getNumberInstance());
        precioMinField.setValue(0.00);
        precioMaxField.setValue(0.00);

        // Autocompletado para DNI
        JComboBox<String> dniClienteField = new JComboBox<>();
        dniClienteField.setEditable(true);
        cargarDatosUnicos("NOMBRE", dniClienteField);

        // Combos para selecciones
        JComboBox<String> estadoFilter = new JComboBox<>(new String[]{"Todos", "SEPARADO", "PAGADO", "ENTREGADO", "DEVUELTO", "DEV PARCIAL"});
        JComboBox<String> metodoPagoFilter = new JComboBox<>(new String[]{"Todos", "EFECTIVO", "TRANSFERENCIA", "YAPE VALERIE", "YAPE ALDAIR"});
        JComboBox<String> faltaPagarCombo = new JComboBox<>(new String[]{"Todos", "Falta Pagar", "Pagado Completo"});

        // Añadir componentes
        filterDialog.add(new JLabel("Desde:"));
        filterDialog.add(desdePicker);
        filterDialog.add(new JLabel("Hasta:"));
        filterDialog.add(hastaPicker);
        filterDialog.add(new JLabel("Precio Min:"));
        filterDialog.add(precioMinField);
        filterDialog.add(new JLabel("Precio Max:"));
        filterDialog.add(precioMaxField);
        filterDialog.add(new JLabel("Nombre Cliente:"));
        filterDialog.add(dniClienteField);
        filterDialog.add(new JLabel("Estado:"));
        filterDialog.add(estadoFilter);
        filterDialog.add(new JLabel("Método de Pago:"));
        filterDialog.add(metodoPagoFilter);
        filterDialog.add(new JLabel("Estado de Pago:"));
        filterDialog.add(faltaPagarCombo);

        // Botones
        JButton aplicar = new JButton("Aplicar Filtros");
        JButton cancelar = new JButton("Cancelar");

        aplicar.addActionListener(e -> {
            try {
                java.util.Date utilDesde = (java.util.Date) desdePicker.getValue();
                java.util.Date utilHasta = (java.util.Date) hastaPicker.getValue();

                // Crear objetos Calendar para ajustar horas
                Calendar cal = Calendar.getInstance();

                // Para fecha DESDE (00:00:00)
                cal.setTime(utilDesde);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                java.sql.Timestamp sqlDesde = new java.sql.Timestamp(cal.getTimeInMillis());

                // Para fecha HASTA (23:59:59)
                cal.setTime(utilHasta);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                java.sql.Timestamp sqlHasta = new java.sql.Timestamp(cal.getTimeInMillis());


                // Validar rango de fechas
                if (sqlDesde.after(sqlHasta)) {
                    JOptionPane.showMessageDialog(filterDialog, 
                        "La fecha 'Desde' no puede ser mayor que 'Hasta'", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Obtener precios
                double precioMin = ((Number) precioMinField.getValue()).doubleValue();
                double precioMax = ((Number) precioMaxField.getValue()).doubleValue();
                // Validar precios
                if (precioMin < 0 || precioMax < 0) {
                    JOptionPane.showMessageDialog(filterDialog,
                            "Los precios no pueden ser negativos",
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Obtener otros valores
                String dniCliente = dniClienteField.getSelectedItem() != "Todos" ? 
                        dniClienteField.getSelectedItem().toString().trim() : "";
                String estado = estadoFilter.getSelectedItem().toString();
                String metodoPago = metodoPagoFilter.getSelectedItem().toString();
                int faltaPagarValue = faltaPagarCombo.getSelectedIndex();
                aplicarFiltros(
                        sqlDesde,
                        sqlHasta,
                        precioMin > 0 ? precioMin : 0,
                        precioMax > 0 ? precioMax : 0,
                        dniCliente,
                        estado,
                        metodoPago, 
                        faltaPagarValue
                );
                filterDialog.dispose();
            } catch (HeadlessException ex) {
                JOptionPane.showMessageDialog(filterDialog, "Error en fechas: " + ex.getMessage());
                System.out.println("Error en fechas: " + ex.getMessage());
            }   
        });

        cancelar.addActionListener(ev -> filterDialog.dispose());

        filterDialog.add(aplicar);
        filterDialog.add(cancelar);

        filterDialog.pack();
        filterDialog.setLocationRelativeTo(this);
        filterDialog.setVisible(true);
    }
    
    private void aplicarFiltros(java.sql.Timestamp desde, java.sql.Timestamp hasta, double precioMin, 
        double precioMax, String dniCliente, String estado, String metodoPago, 
        int faltaPagarValue) {
        ResultSet rs = null;
        CallableStatement cstmt = null;
        try {
            // 1. Preparar parámetros IN
            Object[] parametrosIn = {
                desde,
                hasta,
                precioMin > 0 ? precioMin : null,
                precioMax > 0 ? precioMax : null,
                dniCliente.isEmpty() ? null : dniCliente,
                estado.equals("Todos") ? null : estado,
                metodoPago.equals("Todos") ? null : metodoPago,
                faltaPagarValue
            };
            int[] posicionesOut = {9};
            int[] typesOut={OracleTypes.CURSOR};

            // 2. Ejecutar procedimiento
            Map<Integer, Object> resultados = dbHelper.ejecutarProcedimientoConOut(
                "filtrar_ordenes",
                parametrosIn,
                posicionesOut,
                typesOut
            );

            // 3. Procesar resultados
            model.setRowCount(0);
            rs = (ResultSet) resultados.get(9);

            // Variables para totales
            final double[] totales = new double[2]; // [0: subtotal, 1: totalUSD, 2: totalPEN]
            boolean firstRow = true;

            while (rs.next()) {
                if (firstRow) {
                    // Capturar los totales de la primera fila
                    totales[0] = rs.getDouble("TOTAL_PAGADO");
                    totales[1] = rs.getDouble("TOTAL_VENTAS");
                    firstRow = false;
                }
                model.addRow(createTableRow(rs));
            }

            // Actualizar los labels con los totales
            SwingUtilities.invokeLater(() -> {
                lblTotalPagado.setText(String.format("Total Pagado: S/%,.2f", totales[0]));
                lblTotalVentas.setText(String.format("Total Ventas: S/%,.2f", totales[1]));
                lblPorCobrar.setText(String.format("Por Cobrar: S/%,.2f", (totales[1] - totales[0])));
            });

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al aplicar filtros:\n" + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        finally {
            try {
                if (rs != null) rs.close();
                if (cstmt != null) cstmt.close();
            } catch (SQLException e) {
                 showError("Error: " + e.getMessage());
            }
        }
    }
    
    private void cargarDatosUnicos(String columna, JComboBox<String> comboBox) {
        String query = "SELECT DISTINCT " + columna + " FROM Clientes order by " + columna + " desc";

        try (ResultSet rs = dbHelper.ejecutarConsulta(query)) {
            comboBox.addItem("Todos");
            while (rs.next()) {
                comboBox.addItem(rs.getString(1));
            }
        } catch (SQLException ex) {
            showError("Error cargando datos: " + ex.getMessage());
        }
    }

    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }   
    
    private void configureTable() {
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // ✅ Modo clave
        table.setFillsViewportHeight(true);

        // Opcional: Ajustes específicos para columnas
        table.getColumnModel().getColumn(0).setPreferredWidth(30);  // ID
        table.getColumnModel().getColumn(9).setPreferredWidth(120);  // ID
        table.getColumnModel().getColumn(1).setMinWidth(100);       // Estado
        table.getColumnModel().getColumn(3).setMinWidth(100);       // Estado
        table.getColumnModel().getColumn(8).setMinWidth(100);       // Estado
        table.getColumnModel().getColumn(4).setMinWidth(80);       // Estado
        table.getColumnModel().getColumn(7).setMinWidth(80);       // Estado
    }
    
    private void configureTableStyle() {
        // Estilo de encabezados
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);

        // Renderizado de filas con colores alternados
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                JTable table, Object value, 
                boolean isSelected, boolean hasFocus, 
                int row, int column
            ) {
                Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column
                );

                // Colores alternados
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                }

                // Alineación derecha para columnas numéricas
                switch (column) {
                    case 0, 2, 5, 6 -> ((JLabel) c).setHorizontalAlignment(SwingConstants.RIGHT);
                    case 3 -> ((JLabel) c).setHorizontalAlignment(SwingConstants.LEFT);
                    default -> ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                }
                return c;
            }
        });
    }

    private JPanel createSalesToolbar() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panel superior (botones principales)
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Panel izquierdo (Botones principales)
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.add(createStyledButton("Nueva Venta", "add.png", e -> nuevaVenta()));
        leftPanel.add(createStyledButton("Refrescar", "refresh.png", e -> loadTableData()));

        // Panel derecha superior (Botón Inventario)
        JPanel rightUpPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnInv = createStyledButton("Inventario", "truck.png", e -> volverInventario());
        btnInv.setPreferredSize(new Dimension(140, 30));
        if(!BDConnection.getCurrentRole().equalsIgnoreCase("rol_vendedor")){
            rightUpPanel.add(btnInv);
        }

        toolbar.add(leftPanel, BorderLayout.WEST);
        toolbar.add(rightUpPanel, BorderLayout.EAST);

        // Panel inferior (búsqueda y botones de acciones)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JTextField searchField = new JTextField();
        searchField.setText("Buscar ventas...");  // Texto modificado para ventas
        searchField.setForeground(Color.GRAY);
        searchField.setPreferredSize(new Dimension(250, 30));

        // Configurar DocumentListener para búsqueda en vivo
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filtrar(); }

            private void filtrar() {
                String texto = searchField.getText();
                if (texto.equals("Buscar ventas...") || texto.trim().isEmpty()) {
                    ((TableRowSorter<?>) table.getRowSorter()).setRowFilter(null);
                } else {
                    RowFilter<Object, Object> filtro = RowFilter.regexFilter("(?i)" + Pattern.quote(texto), 0, 1, 2, 3, 4, 5, 6, 7, 8);
                    ((TableRowSorter<?>) table.getRowSorter()).setRowFilter(filtro);
                }
            }
        });

        // Manejo del placeholder
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Buscar ventas...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("Buscar ventas...");
                }
            }
        });

        // Icono de búsqueda
        JLabel searchIcon = new JLabel(loadImageIcon("search.png", 20, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 5);
        searchPanel.add(searchIcon, gbc);

        // Campo de búsqueda
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        searchPanel.add(searchField, gbc);

        // Botones derecha inferior
        JPanel rightBtmPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightBtmPanel.add(createIconButton("filter.png", "Filtrar", e -> showFilterDialog()));
        rightBtmPanel.add(createIconButton("pdf_icon.png", "Exportar PDF", e -> mostrarDialogoExportacion()));
        rightBtmPanel.add(createIconButton("print_icon.png", "Imprimir", e -> {}));

        bottomPanel.add(searchPanel, BorderLayout.CENTER);
        bottomPanel.add(rightBtmPanel, BorderLayout.EAST);

        mainPanel.add(toolbar, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        return mainPanel;  // Cambiar para retornar el panel completo
    }
    
    // Dentro de la clase VentanaVentas
    private JButton createIconButton(String iconPath, String tooltip, ActionListener listener) {
        JButton btn = new JButton(loadImageIcon(iconPath, 20, 20));
        btn.setToolTipText(tooltip);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btn.setContentAreaFilled(false); // ✅ Quitar fondo
        btn.setBorderPainted(false); // ✅ Quitar borde por defecto
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (listener != null) {
            btn.addActionListener(listener);
        }
        return btn;
    }
    
    private JButton createStyledButton(String text, String iconPath, ActionListener listener) {
        JButton button = new JButton(text, loadImageIcon(iconPath, 20, 20));
        button.addActionListener(listener);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setBackground(SECONDARY_COLOR); // Azul corporativo
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
            }
            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(SECONDARY_COLOR);
            }
        });

        return button;
    }

    private void volverInventario() {
        new Maestro().setVisible(true);
        this.dispose();
    }
    private void configureActionsColumn() {
        TableColumn accionesColumn = table.getColumnModel().getColumn(9);
        accionesColumn.setPreferredWidth(120);
        accionesColumn.setCellRenderer(new ButtonRenderer());
        accionesColumn.setCellEditor(new ButtonEditor(table)); // Sin parámetros
    }

    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton btnView;
        private final JButton btnUndo;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0)); // 10px de espacio
            setOpaque(true);
            
            btnView = createIconButton(viewIcon, "Ver Detalle");
            btnUndo = createIconButton(undoIcon, "Devolucion");
            
            add(btnView);
            add(btnUndo);
        }
        
        private JButton createIconButton(ImageIcon icon, String tooltip) {
            JButton button = new JButton(icon);
            button.setToolTipText(tooltip);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setPreferredSize(new Dimension(24, 24)); // Tamaño fijo
            return button;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            return this;
        }
    }


    class ButtonEditor extends AbstractButtonEditor  {
        private int selectedRow;
        private final JPanel panel; 
        
        public ButtonEditor(JTable table) {
            super();
            this.table = table;
            this.panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            setClickCountToStart(1);
        }
        
        @Override
        protected void handleUndoAction(int row) {
            // Convertir la fila de la vista al modelo
            int modelRow = table.convertRowIndexToModel(row);
            String idVenta = model.getValueAt(modelRow, 0).toString(); // Usar el modelo directamente
            String estado = (String) model.getValueAt(modelRow, 4);

            if (!estado.equals("ENTREGADO") && !estado.equals("DEV PARCIAL")) {
                JOptionPane.showMessageDialog((Frame) SwingUtilities.getWindowAncestor(table), 
                    "Solo se pueden devolver ventas en estado ENTREGADO O DEVOLUCION PARCIAL", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            mostrarDialogoDevolucion(idVenta);
        }
        
        @Override
        protected void handleEditAction(int row) {            
        }
        
        @Override
        protected void handleViewAction(int row) {
            selectedRow = table.convertRowIndexToModel(row);
            viewAction();
        }
        
        @Override
        protected void handleDeleteAction(int row) {
            // No aplicable para ventas
        }
        
        
        
        @Override
        public Component getTableCellEditorComponent(
            JTable table, Object value, 
            boolean isSelected, int row, int column) 
        {
            this.table = table;

            panel.removeAll(); // Limpiar componentes
            panel.setOpaque(true);
            
            JButton btnView = createActionButton(viewIcon, "Ver Detalle", e -> handleViewAction(row));
            JButton btnUndo = createActionButton(undoIcon, "Devolucion", e -> handleUndoAction(row));
            
            panel.add(btnView);
            panel.add(btnUndo);
            return panel;
        }
        
        private JButton createActionButton(ImageIcon icon, String tooltip, ActionListener action) {
            JButton button = new JButton(icon);
            button.setToolTipText(tooltip);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setPreferredSize(new Dimension(24, 24));
            button.addActionListener(action);
            return button;
        }
        
        @Override
        public boolean stopCellEditing() {
            super.stopCellEditing();
            return true;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "";
        }
        
        private void mostrarDialogoDevolucion(String idVenta) {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(table), "Registrar Devolución", true);
            dialog.setLayout(new BorderLayout(10, 10));

            // Modelo de tabla
            DefaultTableModel modelSeleccion = new DefaultTableModel(
                new Object[]{"Código", "Prenda", "Disponible", "Devolver", "Total", "Seleccionar", "P. Unitario"}, 0) {

                @Override
                public Class<?> getColumnClass(int column) {
                    return switch (column) {
                        case 5 -> Boolean.class;
                        case 3 -> Integer.class;
                        case 4 -> Double.class;
                        case 6 -> Double.class;
                        default -> String.class;
                    };
                }

                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 3 || column == 5;
                }
            };

            // Consulta de datos
            String sql = "SELECT p.ID_PRENDA, p.NOMBRE, dv.CANTIDAD - dv.CANTIDAD_DEVUELTA AS disponible, "
                       + "dv.PRECIO_UNITARIO FROM DETALLE_VENTAS dv "
                       + "JOIN PRENDAS p ON dv.ID_PRENDA = p.ID_PRENDA WHERE dv.ID_VENTA = ?";

            try (ResultSet rs = dbHelper.ejecutarConsulta(sql, idVenta)) {
                while (rs.next()) {
                    modelSeleccion.addRow(new Object[]{
                        rs.getString("ID_PRENDA"),
                        rs.getString("NOMBRE"),
                        rs.getInt("disponible"),
                        0,    // Devolver
                        0.00, // Total
                        false,
                        rs.getDouble("PRECIO_UNITARIO")
                    });
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error obteniendo detalle: " + ex.getMessage());
                return;
            }

            JTable tablaDetalle = new JTable(modelSeleccion);
            tablaDetalle.removeColumn(tablaDetalle.getColumnModel().getColumn(6)); // Ocultar precio unitario

            // Configurar spinner
            TableColumn cantidadCol = tablaDetalle.getColumnModel().getColumn(3);
            cantidadCol.setCellEditor(new SpinnerEditor(tablaDetalle, dbHelper, 0, false));

            // Componentes de interfaz
            JTextArea txtRazon = new JTextArea(3, 40);
            txtRazon.setLineWrap(true);

            JLabel lblTotal = new JLabel("Total a devolver: S/0.00");
            lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 12));

            // Configurar actualización de totales
            modelSeleccion.addTableModelListener(e -> {
                int col = e.getColumn();
                if (col == 3 || col == 5) { // Si cambia la cantidad o el checkbox de selección
                    double totalGeneral = 0;

                    for (int i = 0; i < modelSeleccion.getRowCount(); i++) {
                        boolean seleccionado = (Boolean) modelSeleccion.getValueAt(i, 5);
                        int cantidad = (Integer) modelSeleccion.getValueAt(i, 3);
                        double precio = (Double) modelSeleccion.getValueAt(i, 6);
                        double totalFila = cantidad * precio;

                        // Actualizar columna "Total" en la tabla
                        modelSeleccion.setValueAt(totalFila, i, 4);

                        if (seleccionado) {
                            totalGeneral += totalFila;
                        }
                    }

                    lblTotal.setText(String.format("Total a devolver: S/%,.2f", totalGeneral));
                }
            });

            // Botones rediseñados
            JButton btnConfirmar = new JButton("Confirmar");
            JButton btnCancelar = new JButton("Cancelar");

            // Configurar tamaño de botones
            Dimension btnSize = new Dimension(100, 30);
            btnConfirmar.setPreferredSize(btnSize);
            btnCancelar.setPreferredSize(btnSize);

            // Panel de controles inferiores
            JPanel panelInferior = new JPanel(new BorderLayout(10, 10));
            panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Panel para total
            JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panelTotal.add(lblTotal);

            // Panel para botones
            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            panelBotones.add(btnCancelar);
            panelBotones.add(btnConfirmar);

            // Ensamblar componentes
            panelInferior.add(panelTotal, BorderLayout.NORTH);
            panelInferior.add(panelBotones, BorderLayout.SOUTH);

            // Acciones de botones
            btnConfirmar.addActionListener(e -> procesarDevolucion(dialog, idVenta, modelSeleccion, txtRazon));
            btnCancelar.addActionListener(e -> dialog.dispose());

            // Construcción final del diálogo
            JPanel panelSuperior = new JPanel(new BorderLayout());
            panelSuperior.add(new JLabel("Razón de la devolución:"), BorderLayout.NORTH);
            panelSuperior.add(new JScrollPane(txtRazon), BorderLayout.CENTER);

            dialog.add(panelSuperior, BorderLayout.NORTH);
            dialog.add(new JScrollPane(tablaDetalle), BorderLayout.CENTER);
            dialog.add(panelInferior, BorderLayout.SOUTH);

            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setMinimumSize(new Dimension(600, 400));
            dialog.setVisible(true);
        }
        
        private void procesarDevolucion(JDialog dialog, String idVenta, DefaultTableModel modelSeleccion, JTextArea txtRazon) {
            if (!validarDevolucion(modelSeleccion, txtRazon.getText())) return;

            try {
                Map<Integer, Integer> productos = new HashMap<>();
                for (int i = 0; i < modelSeleccion.getRowCount(); i++) {
                    if ((Boolean) modelSeleccion.getValueAt(i, 5)) { // Columna 5 = Checkbox
                        // Obtener ID_PRENDA desde la columna "Código" (String) y convertir a Integer
                        int idPrenda = Integer.parseInt((String) modelSeleccion.getValueAt(i, 0));
                        int cantidad = (Integer) modelSeleccion.getValueAt(i, 3); // Columna 3 = Devolver
                        productos.put(idPrenda, cantidad);
                    }
                }

                dbHelper.registrarDevolucion(
                    Integer.parseInt(idVenta),
                    txtRazon.getText().trim(),
                    productos
                );

                JOptionPane.showMessageDialog(dialog, "Devolución registrada exitosamente!");
                loadTableData();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error en el formato del código de prenda", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private boolean validarDevolucion(DefaultTableModel model, String razon) {
            if (razon.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, 
                    "Debe ingresar una razón para la devolución", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            boolean alMenosUnaSeleccion = false;
            for (int i = 0; i < model.getRowCount(); i++) {
                if ((Boolean) model.getValueAt(i, 5)) { // Columna 5 = Checkbox
                    alMenosUnaSeleccion = true;
                    int cantidadDevolver = (Integer) model.getValueAt(i, 3); // Columna 3 = Devolver
                    int cantidadDisponible = (Integer) model.getValueAt(i, 2); 

                    if (cantidadDevolver <= 0) {
                        JOptionPane.showMessageDialog(null, 
                            "La cantidad a devolver debe ser mayor a 0", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }

                    if (cantidadDevolver > cantidadDisponible) {
                        JOptionPane.showMessageDialog(null, 
                            "No puede devolver más unidades de las vendidas", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
            }

            if (!alMenosUnaSeleccion) {
                JOptionPane.showMessageDialog(null, 
                    "Seleccione al menos un producto para devolver", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        }
        
        private void viewAction() {
            if (selectedRow == -1) return;

            // Obtener datos principales de la venta
            String idVenta = model.getValueAt(selectedRow, 0).toString();
            String fechaVenta = model.getValueAt(selectedRow, 1).toString();
            String totalVenta = model.getValueAt(selectedRow, 2).toString();
            String nombre = model.getValueAt(selectedRow, 3).toString();
            String estado = model.getValueAt(selectedRow, 4).toString();
            String montoPagado = model.getValueAt(selectedRow, 5).toString();
            String montoRestante = model.getValueAt(selectedRow, 6).toString();
            String metodoPago = model.getValueAt(selectedRow, 7).toString();
            String fechaEntrega = model.getValueAt(selectedRow, 8) != null ? 
                                 model.getValueAt(selectedRow, 8).toString() : "Pendiente";

            // Obtener detalle de la venta desde BD
            String sqlDetalle = "SELECT p.nombre AS prenda, dv.precio_unitario, "
                 + "dv.cantidad, dv.cantidad_devuelta, "
                 + "(dv.cantidad - dv.cantidad_devuelta) AS cantidad_actual, "
                 + "(dv.precio_unitario * (dv.cantidad - dv.cantidad_devuelta)) AS total "
                 + "FROM detalle_ventas dv "
                 + "JOIN prendas p ON dv.id_prenda = p.id_prenda "
                 + "WHERE dv.id_venta = ?";

            DefaultTableModel modelDetalle = new DefaultTableModel(
                new Object[]{"Prenda", "Precio Unitario", "Cantidad Vendida", 
                             "Devuelto", "Actual", "Total"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            try (ResultSet rs = dbHelper.ejecutarConsulta(sqlDetalle, idVenta)) {
                while(rs.next()) {
                    modelDetalle.addRow(new Object[]{
                        rs.getString("prenda"),
                        String.format("S/ %,.2f", rs.getDouble("precio_unitario")),
                        rs.getInt("cantidad"),
                        rs.getInt("cantidad_devuelta"),
                        rs.getInt("cantidad_actual"),
                        String.format("S/ %,.2f", rs.getDouble("total"))
                    });
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error obteniendo detalle: " + ex.getMessage());
                return;
            }
            
            String sqlCliente = "SELECT telefono, correo FROM clientes WHERE nombre = ?";

            String telefono = "No disponible";
            String correo = "No disponible";

            try (ResultSet rs = dbHelper.ejecutarConsulta(sqlCliente, nombre)) {
                if (rs.next()) {
                    telefono = rs.getString("telefono") != null ? rs.getString("telefono") : "No disponible";
                    correo = rs.getString("correo") != null ? rs.getString("correo") : "No disponible";
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error obteniendo datos del cliente: " + ex.getMessage());
            }

            // Configurar diálogo
            JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(table), 
                                       "Detalle de Venta", true);
            dialog.setLayout(new BorderLayout(10, 10));
            JPanel panelSuperior = new JPanel(new GridLayout(0, 2, 10, 10));

            // Campos de solo lectura
            addReadOnlyField(panelSuperior, "ID Venta:", idVenta);
            addReadOnlyField(panelSuperior, "Fecha Venta:", fechaVenta);
            addReadOnlyField(panelSuperior, "Nombre Cliente:", nombre);
            addReadOnlyField(panelSuperior, "Teléfono:", telefono);
            addReadOnlyField(panelSuperior, "Correo:", correo);
            addReadOnlyField(panelSuperior, "Estado:", estado);
            addReadOnlyField(panelSuperior, "Monto Pagado:", String.format("S/ %,.2f", Double.valueOf(montoPagado)));
            addReadOnlyField(panelSuperior, "Monto Restante:", String.format("S/ %,.2f", Double.valueOf(montoRestante)));
            addReadOnlyField(panelSuperior, "Método Pago:", metodoPago);
            addReadOnlyField(panelSuperior, "Fecha Entrega:", fechaEntrega);
            addReadOnlyField(panelSuperior, "Total Venta:", String.format("S/ %,.2f", Double.valueOf(totalVenta)));

            // Tabla de detalle
            JTable tablaDetalle = new JTable(modelDetalle);
            tablaDetalle.setAutoCreateRowSorter(true);
            JScrollPane scroll = new JScrollPane(tablaDetalle);
            scroll.setPreferredSize(new Dimension(600, 200));
            tablaDetalle.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
            tablaDetalle.setRowHeight(25);
            tablaDetalle.setShowGrid(true);
            tablaDetalle.setGridColor(Color.LIGHT_GRAY);
            // Botón cerrar
            JButton btnCerrar = new JButton("Cerrar");
            btnCerrar.addActionListener(e -> dialog.dispose());

            // Ensamblar componentes
            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panelBotones.add(btnCerrar);

            dialog.add(panelSuperior, BorderLayout.NORTH);
            dialog.add(scroll, BorderLayout.CENTER);
            dialog.add(panelBotones, BorderLayout.SOUTH);

            dialog.pack();
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }

        private void addReadOnlyField(JPanel panel, String label, String value) {
            panel.add(new JLabel(" " + label));
            JTextField field = new JTextField(value);
            field.setEditable(false);
            field.setBorder(BorderFactory.createEmptyBorder());
            field.setBackground(panel.getBackground());
            panel.add(field);
        }
    }
}

// Editor para precios con validación de formato
class PriceEditor extends AbstractCellEditor implements TableCellEditor {
    private final JTextField textField = new JTextField();
    
    public PriceEditor() {
        textField.setHorizontalAlignment(SwingConstants.RIGHT);
        
        // Validación de entrada
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == '.' || c == KeyEvent.VK_BACK_SPACE)) {
                    e.consume();
                }
            }
        });
    }

    @Override
    public Object getCellEditorValue() {
        try {
            return Double.valueOf(textField.getText());
        } catch (NumberFormatException e) {
            return 0.00; // Valor por defecto
        }
    }

    @Override
    public Component getTableCellEditorComponent(
        JTable table, Object value, 
        boolean isSelected, int row, int column) 
    {
        textField.setText(value.toString());
        return textField;
    }
}

// Renderizador para precios
class PriceRenderer extends DefaultTableCellRenderer {
    private final DecimalFormat format = new DecimalFormat("#,##0.00");

    public PriceRenderer() {
        setHorizontalAlignment(SwingConstants.LEFT); // Cambiar de RIGHT a LEFT
    }

    @Override
    protected void setValue(Object value) {
        if (value instanceof Number) {
            setText(format.format(value));
        } else {
            super.setValue(value);
        }
    }
}