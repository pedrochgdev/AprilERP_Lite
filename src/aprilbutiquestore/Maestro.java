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
import java.util.List;
import java.awt.event.*;
import java.util.Map;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class Maestro extends JFrame {
    private static final int ACCIONES_COLUMN_INDEX = 11;    
    private static final int ACCIONES_COLUMN_WIDTH = 110; // Antes 120
    
    private JTable table;
    private DefaultTableModel model;
    private final ImageIcon editIcon;
    private final ImageIcon deleteIcon;
    private final ImageIcon viewIcon;
    
    // Colores corporativos
    private static final Color PRIMARY_COLOR = new Color(44, 62, 80);   // Azul oscuro
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219); // Azul claro

    private TableRowSorter<DefaultTableModel> sorter;
    private Connection conexion;
    private DBHelper dbHelper;
    
    private JLabel lblSubtotal;
    private JLabel lblImpuestos;
    private JLabel lblTotalDolares;
    private JLabel lblTotalSoles;
    
   public Maestro() {
        
       try {
            conexion = BDConnection.getConnection();
            dbHelper = new DBHelper(conexion); 
            conexion.setAutoCommit(true); // Transacciones automáticas por defecto
        } catch (SQLException ex) {
            showError("Error crítico: No se pudo conectar a la base de datos\n" + ex.getMessage());
            System.exit(1);
        }

        // 1. Carga inicial de iconos
        this.viewIcon = loadImageIcon("eye.png", 20, 20);
        this.editIcon = loadImageIcon("edit.png", 20, 20);
        this.deleteIcon = loadImageIcon("delete.png", 20, 20);
        // 2. Configuración básica de la ventana
        configureWindow();

        // 3. Inicialización en orden correcto
        initializeTableModel();  // Modelo primero
        setupUIComponents();     // Componentes de UI después
        loadTableData();         // Carga de datos al final  
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
    
    private void initializeTableModel() {
        model = new DefaultTableModel();
        String[] columns = {
            "ID", 
            "Descripción", 
            "Color", 
            "Talla", 
            "Precio Compra",  // Nueva columna
            "Subtotal",        
            "Total USD",       
            "Total PEN",       
            "Precio Venta", 
            "Stock", 
            "Cantidad",       // Nueva columna
            ""                 // Acciones
        };
        model.setColumnIdentifiers(columns);
    }
    
    private void configureWindow() {
        setTitle("Gestion de Inventario");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustColumnWidths();
            }
        });
    }

    private void setupUIComponents() {
        configureTable();
        configureTableStyle();
        configureActionsColumn();
        
        add(createHeader(), BorderLayout.NORTH);
        JPanel mainContent = new JPanel(new BorderLayout(10, 10));
        mainContent.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
//
        mainContent.add(createToolbar(), BorderLayout.NORTH);
        mainContent.add(createTablePanel(), BorderLayout.CENTER);
        mainContent.add(createTotalsPanel(), BorderLayout.SOUTH);
//        mainContent.add(createActionButtons(), BorderLayout.SOUTH);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        add(mainContent, BorderLayout.CENTER);
    }      
    
    private JPanel createTotalsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblSubtotal = new JLabel("Subtotal: $0.00");
        lblImpuestos = new JLabel("Impuestos (7%): $0.00");
        lblTotalDolares = new JLabel("Total (USD): $0.00");
        lblTotalSoles = new JLabel("Total (PEN): S/0.00");

        // Estilos
        Font boldFont = new Font("Segoe UI", Font.BOLD, 14);
        List.of(lblSubtotal, lblImpuestos, lblTotalDolares, lblTotalSoles).forEach(l -> {
            l.setFont(boldFont);
            l.setHorizontalAlignment(SwingConstants.CENTER);
        });

        panel.add(lblSubtotal);
        panel.add(lblImpuestos);
        panel.add(lblTotalDolares);
        panel.add(lblTotalSoles);

        return panel;
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
    
    // Nuevo método para barra de herramientas
   private JPanel createToolbar() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panel superior (botones principales y registrar venta)
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.add(createCompactButton("Agregar", "add.png", e -> agregarRegistro()));
        leftPanel.add(createCompactButton("Actualizar", "refresh.png", e -> loadTableData()));

        JPanel rightUpPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnRegistrar = createCompactButton("Ventas", "sales.png", e -> abrirVentanaVentas());
        btnRegistrar.setPreferredSize(new Dimension(140, 30)); // Ajustar tamaño
        rightUpPanel.add(btnRegistrar);

        toolbar.add(leftPanel, BorderLayout.WEST);
        toolbar.add(rightUpPanel, BorderLayout.EAST);

        // Panel inferior (búsqueda y botones de acciones)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel searchPanel = new JPanel(new GridBagLayout()); // Usar GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        JTextField searchField = new JTextField();
        searchField.setText("Buscar prendas...");
        searchField.setForeground(Color.GRAY);
        searchField.setPreferredSize(new Dimension(250, 30));
        
        searchField.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            filtrar();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            filtrar();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            filtrar();
        }

        private void filtrar() {
            String textoBusqueda = searchField.getText();

            // Ignorar si es el placeholder o está vacío
            if (textoBusqueda.equals("Buscar prendas...") || textoBusqueda.trim().isEmpty()) {
                ((TableRowSorter<?>) table.getRowSorter()).setRowFilter(null);
            } else {
                // Filtrar solo en la columna 0 (asumiendo que es la primera columna)
                RowFilter<Object, Object> filtro = RowFilter.regexFilter("(?i)" + Pattern.quote(textoBusqueda), 0, 1, 2);
                ((TableRowSorter<?>) table.getRowSorter()).setRowFilter(filtro);
            }
        }
    });

    // Agregar manejo del placeholder (si no lo tienes)
    searchField.addFocusListener(new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (searchField.getText().equals("Buscar prendas...")) {
                searchField.setText("");
                searchField.setForeground(Color.BLACK);
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (searchField.getText().isEmpty()) {
                searchField.setForeground(Color.GRAY);
                searchField.setText("Buscar prendas...");
            }
        }
    });

        JLabel searchIcon = new JLabel(loadImageIcon("search.png", 20, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 5); // Margen de 5px a la derecha
        searchPanel.add(searchIcon, gbc);

        // Configurar JTextField para que ocupe el espacio restante
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        searchPanel.add(searchField, gbc); // Hace que se expanda

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.add(createIconButton("filter.png", "Filtrar", e -> showFilterDialog()));
        rightPanel.add(createIconButton("pdf_icon.png", "Exportar PDF", e -> mostrarDialogoExportacion()));
        rightPanel.add(createIconButton("print_icon.png", "Imprimir"));

        bottomPanel.add(searchPanel, BorderLayout.CENTER); // Ocupar espacio disponible
        bottomPanel.add(rightPanel, BorderLayout.EAST);

        mainPanel.add(toolbar, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        return mainPanel;
    }



    
    private void abrirVentanaVentas() {
        new VentanaVentas().setVisible(true);
        this.dispose(); // Cierra la ventana actual
    }
    
    // Botones compactos
    private JButton createCompactButton(String text, String iconPath, ActionListener listener) {
        JButton btn = new JButton(text, loadImageIcon(iconPath, 16, 16));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setBackground(SECONDARY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(2, 8, 2, 8));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(SECONDARY_COLOR.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(SECONDARY_COLOR);
            }
        });

        btn.addActionListener(listener);
        return btn;
    }
    
    private void mostrarDialogoExportacion() {
        JDialog configDialog = new JDialog(this, "Configurar Exportación PDF", true);
        configDialog.setLayout(new BorderLayout());
        JPanel filterDialog = new JPanel(new GridLayout(0, 2, 10, 10));

        Calendar calDefaultDesde = Calendar.getInstance();
        calDefaultDesde.set(2024, Calendar.JANUARY, 1);

        JSpinner desdePicker = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor desdeEditor = new JSpinner.DateEditor(desdePicker, "dd/MM/yyyy");
        desdePicker.setEditor(desdeEditor);
        desdePicker.setValue(calDefaultDesde.getTime());

        JSpinner hastaPicker = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor hastaEditor = new JSpinner.DateEditor(hastaPicker, "dd/MM/yyyy");
        hastaPicker.setEditor(hastaEditor);

        // Panel de columnas usando el mapeo oficial
        JPanel columnPanel = new JPanel(new GridLayout(0, 2));
        Map<String, JCheckBox> columnCheckboxes = new LinkedHashMap<>();
        Map<String, String> mapeoOficial = crearMapeoColumnas();

        for(String nombreVisual : mapeoOficial.keySet()) {
            JCheckBox checkBox = new JCheckBox(nombreVisual, true);
            columnCheckboxes.put(nombreVisual, checkBox);
            columnPanel.add(checkBox);
        }

        filterDialog.add(new JLabel("Desde:"));
        filterDialog.add(desdePicker);
        filterDialog.add(new JLabel("Hasta:"));
        filterDialog.add(hastaPicker);

        JButton btnExportar = new JButton("Exportar");
        JButton btnCancelar = new JButton("Cancelar");

        btnExportar.addActionListener(e -> {
            try {
                java.util.Date utilDesde = (java.util.Date) desdePicker.getValue();
                java.util.Date utilHasta = (java.util.Date) hastaPicker.getValue();

                Calendar cal = Calendar.getInstance();

                cal.setTime(utilDesde);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                java.sql.Timestamp sqlDesde = new java.sql.Timestamp(cal.getTimeInMillis());

                cal.setTime(utilHasta);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                java.sql.Timestamp sqlHasta = new java.sql.Timestamp(cal.getTimeInMillis());

                if (sqlDesde.after(sqlHasta)) {
                    JOptionPane.showMessageDialog(filterDialog, 
                        "La fecha 'Desde' no puede ser mayor que 'Hasta'", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                generarPDF(columnCheckboxes, sqlDesde, sqlHasta);
                configDialog.dispose();
            } catch (HeadlessException ex) {
                JOptionPane.showMessageDialog(filterDialog, "Error en fechas: " + ex.getMessage());
            }  
        });

        btnCancelar.addActionListener(e -> configDialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnExportar);
        buttonPanel.add(btnCancelar);

        configDialog.add(new JScrollPane(filterDialog), BorderLayout.NORTH);
        configDialog.add(new JScrollPane(columnPanel), BorderLayout.CENTER);
        configDialog.add(buttonPanel, BorderLayout.SOUTH);
        configDialog.setSize(500, 400);
        configDialog.setLocationRelativeTo(this);
        configDialog.setVisible(true);
    }
    
    private void agregarMetadatos(Document document) {
        document.addTitle("Reporte de Inventario");
        document.addSubject("Generado desde April Boutique Store");
        document.addKeywords("inventario, reporte, pdf");
        document.addAuthor("Sistema April Boutique");
        document.addCreator("April Boutique Store v1.0");
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




    private void generarPDF(Map<String, JCheckBox> columnCheckboxes, java.sql.Timestamp desde, java.sql.Timestamp hasta) {
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
                agregarEncabezado(document, desde, hasta);

                try (ResultSet rs = dbHelper.ejecutarProcedimientoCursor("FILTRAR_REGISTROS", desde, hasta, null, null, null)) {
        
                    ResultSetMetaData metaData = rs.getMetaData();
                    List<ColumnaPDF> columnasVisibles = obtenerColumnasVisibles(metaData, columnCheckboxes);

                    if(columnasVisibles.isEmpty()) {
                        JOptionPane.showMessageDialog(this, 
                            "Seleccione al menos una columna", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    PdfPTable pdfTable = crearTablaPDF(columnasVisibles);
                    
                    
                    agregarTotalesDesdeResultSet(document, rs, pdfTable, columnasVisibles); // Método modificado    
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
    
    private String obtenerNombreVisual(String nombreBD, Map<String, String> mapeoColumnas) {
        String nombreBDBusqueda = nombreBD.trim().toUpperCase();

        return mapeoColumnas.entrySet().stream()
            .filter(entry -> entry.getValue().toUpperCase().equals(nombreBDBusqueda))
            .map(Map.Entry::getKey)
            .findFirst()
            .orElse(null);
    }
    
    private List<ColumnaPDF> obtenerColumnasVisibles(ResultSetMetaData metaData, 
                                                Map<String, JCheckBox> checkboxes) 
                                                throws SQLException {
        List<ColumnaPDF> columnas = new ArrayList<>();
        Map<String, String> mapeoColumnas = crearMapeoColumnas();

        for(int i = 1; i <= metaData.getColumnCount(); i++) {
            String nombreBD = metaData.getColumnLabel(i).toUpperCase();
            String nombreVisual = obtenerNombreVisual(nombreBD, mapeoColumnas);

            if(nombreVisual != null) {
                JCheckBox checkbox = checkboxes.get(nombreVisual);

                // Verificación robusta contra null
                if(checkbox != null && checkbox.isSelected()) {
                    columnas.add(new ColumnaPDF(i, nombreVisual, nombreBD));
                }
            }
        }
        return columnas;
    }
    
    private PdfPTable crearTablaPDF(List<ColumnaPDF> columnas) {
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

    // Método auxiliar actualizado para nuevo orden de columnas
    private String formatearValor(Object valor, String nombreColumna) {
        if(valor == null) return "";

        try {
            if(nombreColumna.contains("PRECIO") || nombreColumna.contains("TOTAL")) {
                double monto = Double.parseDouble(valor.toString());
                
                if(nombreColumna.equalsIgnoreCase("PRECIO_VENTA")) {
                    return String.format("S/ %,.2f", monto);
                }
                
                return nombreColumna.contains("PEN") ? 
                    String.format("S/ %,.2f", monto) : 
                    String.format("$ %,.2f", monto);
            }

            if(nombreColumna.matches("STOCK|CANTIDAD")) {
                return String.format("%,d", Long.valueOf(valor.toString()));
            }
        } catch(NumberFormatException e) {
            return valor.toString();
        }
        return valor.toString();
    }
    
    private void agregarTotalesDesdeResultSet(Document document, ResultSet rs, 
                                        PdfPTable pdfTable, List<ColumnaPDF> columnas) 
                                        throws SQLException, DocumentException {
        double subtotal = 0;
        double impuestos = 0;
        double totalUSD = 0;
        double totalPEN = 0;
        boolean primeraFila = true;

        while(rs.next()) {
            if(primeraFila) {
                // Obtener totales de la primera fila
                subtotal = rs.getDouble("TOTAL_SUBTOTAL");
                totalUSD = rs.getDouble("TOTAL_USD_GEN");
                totalPEN = rs.getDouble("TOTAL_PEN_GEN");
                impuestos = totalUSD - subtotal;
                primeraFila = false;
            }

            procesarFila(rs, pdfTable, columnas);
        }

        Paragraph totales = new Paragraph("\n");
        totales.add(new Phrase(String.format("Subtotal: $%,.2f\n", subtotal)));
        totales.add(new Phrase(String.format("Impuestos (7%%): $%,.2f\n", impuestos)));
        totales.add(new Phrase(String.format("Total USD: $%,.2f\n", totalUSD)));
        totales.add(new Phrase(String.format("Total PEN: S/%,.2f\n", totalPEN)));
        totales.add(new Phrase("\n\n"));
        document.add(totales);
    }
    
    private void procesarFila(ResultSet rs, PdfPTable tabla, List<ColumnaPDF> columnas) throws SQLException {
        for(ColumnaPDF columna : columnas) {
            Object valor = rs.getObject(columna.indice);
            PdfPCell celda = new PdfPCell(new Phrase(
                formatearValor(valor, columna.nombreBD), 
                FontFactory.getFont(FontFactory.HELVETICA, 9)
            ));

            if(esColumnaNumerica(columna.nombreBD)) {
                celda.setHorizontalAlignment(Element.ALIGN_RIGHT);
            }
            tabla.addCell(celda);
        }
    }
    
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
    
    private boolean esColumnaNumerica(String nombreColumna) {
        return nombreColumna.matches("(PRECIO|TOTAL|STOCK|CANTIDAD)");
    }

    private void agregarEncabezado(Document document, Timestamp desde, Timestamp hasta) 
                                 throws DocumentException {
        Paragraph header = new Paragraph();
        header.add(new Phrase("Reporte de Inventario\n", 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fechaDesde = (desde != null) ? sdf.format(desde) : "N/A";
        String fechaHasta = (hasta != null) ? sdf.format(hasta) : "N/A";

        header.add(new Phrase("Período: " + fechaDesde + " - " + fechaHasta + "\n\n", 
            FontFactory.getFont(FontFactory.HELVETICA, 12)));
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);
    }
    
    private Map<String, String> crearMapeoColumnas() {
        Map<String, String> mapeo = new LinkedHashMap<>();
        mapeo.put("ID", "ID_PRENDA");
        mapeo.put("Descripción", "NOMBRE");
        mapeo.put("Color", "COLOR");
        mapeo.put("Talla", "TALLA");
        mapeo.put("Precio Compra", "PRECIO_COMPRA");
        mapeo.put("Subtotal", "SUBTOTAL");
        mapeo.put("Total USD", "TOTAL_USD");
        mapeo.put("Total PEN", "TOTAL_PEN");
        mapeo.put("Precio Venta", "PRECIO_VENTA");
        mapeo.put("Stock", "STOCK");
        mapeo.put("Cantidad Comprada", "CANTIDAD_COMPRADA");
        return mapeo;
    }

    
    // Método para mostrar diálogo de filtros
   private void showFilterDialog() {
        JDialog filterDialog = new JDialog(this, "Filtros Avanzados", true);
        filterDialog.setLayout(new GridLayout(0, 2, 10, 10));
        
        
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
        // ComboBox para tallas con datos de la BD
        JComboBox<String> tallaFilter = new JComboBox<>();
        tallaFilter.addItem("Todas"); // Opción por defecto
        cargarDatosUnicos(" M.TALLA", tallaFilter); // Método que carga datos

        // ComboBox para colores con datos de la BD
        JComboBox<String> colorFilter = new JComboBox<>();
        colorFilter.addItem("Todos");
        cargarDatosUnicos("I.COLOR", colorFilter);

//         ComboBox para precios (ejemplo adicional)
        JComboBox<String> precioFilter = new JComboBox<>();
        precioFilter.addItem("Todos");
        cargarDatosUnicos("i.PRECIO_VENTA", precioFilter);

        // Añadir componentes al diálogo
        filterDialog.add(new JLabel("Desde:"));
        filterDialog.add(desdePicker);
        filterDialog.add(new JLabel("Hasta:"));
        filterDialog.add(hastaPicker);
        filterDialog.add(new JLabel(" Talla:"));
        filterDialog.add(tallaFilter);
        filterDialog.add(new JLabel(" Color:"));
        filterDialog.add(colorFilter);
        filterDialog.add(new JLabel(" Precio:"));
        filterDialog.add(precioFilter);

        // Botones
        JButton aplicar = new JButton("Aplicar Filtros");
        JButton cancelar = new JButton("Cancelar");

        aplicar.addActionListener(e -> {
            try{
                String talla = tallaFilter.getSelectedItem().toString().toUpperCase();
                String color = colorFilter.getSelectedItem().toString().toUpperCase();
                String precio = precioFilter.getSelectedItem().toString().toUpperCase();

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

                aplicarFiltros(sqlDesde, sqlHasta, talla, color, precio);
                filterDialog.dispose();
            }catch (HeadlessException ex) {
                JOptionPane.showMessageDialog(filterDialog, "Error en fechas: " + ex.getMessage());
                System.out.println("Error en fechas: " + ex.getMessage());
            }   
        });

        cancelar.addActionListener(e -> filterDialog.dispose());

        filterDialog.add(aplicar);
        filterDialog.add(cancelar);

        filterDialog.pack();
        filterDialog.setLocationRelativeTo(this);
        filterDialog.setVisible(true);
    }

    // Método para cargar datos únicos desde la BD
    private void cargarDatosUnicos(String columna, JComboBox<String> comboBox) {
        String query = "SELECT DISTINCT " + columna + " FROM PRENDAS I JOIN TALLAS M ON M.ID_TALLA = I.ID_TALLA  ORDER BY " + columna ;

        try (ResultSet rs = dbHelper.ejecutarConsulta(query)) {
            while (rs.next()) {
                comboBox.addItem(rs.getString(1));
            }
        } catch (SQLException ex) {
            showError("Error cargando datos: " + ex.getMessage());
        }
    }

    // Método para aplicar los filtros (ejemplo básico)
    private void aplicarFiltros(java.sql.Timestamp desde, java.sql.Timestamp hasta, String talla, String color, String precio) {
        Double precioFiltro = ("TODOS".equals(precio) || precio.isEmpty()) ? null : Double.valueOf(precio);

        try (ResultSet rs = dbHelper.ejecutarProcedimientoCursor(
                "FILTRAR_REGISTROS", 
                desde,
                hasta,
                "TODAS".equals(talla) ? null : talla, 
                "TODOS".equals(color) ? null : color, 
                precioFiltro)) { 

            model.setRowCount(0);
            final double[] totales = new double[3]; // [0: subtotal, 1: totalUSD, 2: totalPEN]
            boolean firstRow = true;

            while (rs.next()) {
                if (firstRow) { 
                    // Guardar los totales generales de la primera fila
                    totales[0] = rs.getDouble("TOTAL_SUBTOTAL");
                    totales[1] = rs.getDouble("TOTAL_USD_GEN");
                    totales[2] = rs.getDouble("TOTAL_PEN_GEN");
                    firstRow = false;
                }
                model.addRow(createTableRow(rs));
            }

            // Actualizar los labels con los totales generales
            SwingUtilities.invokeLater(() -> {
                lblSubtotal.setText(String.format("Subtotal: $%,.2f", totales[0]));
                lblImpuestos.setText(String.format("Impuestos (7%%): $%,.2f", totales[1] - totales[0]));
                lblTotalDolares.setText(String.format("Total (USD): $%,.2f", totales[1]));
                lblTotalSoles.setText(String.format("Total (PEN): S/%,.2f", totales[2]));
            });

        } catch (SQLException ex) {
            showError("Error al filtrar: " + ex.getMessage());
        }
    }


    // Método createTableRow actualizado para usar alias correctos
    private Object[] createTableRow(ResultSet rs) throws SQLException {
        return new Object[]{
            rs.getString("ID_PRENDA"),
            rs.getString("NOMBRE"),
            rs.getString("COLOR"),
            rs.getString("TALLA"),
            String.format("%,.2f", rs.getDouble("PRECIO_COMPRA")), // Precio Compra
            String.format("%,.2f", rs.getDouble("SUBTOTAL")),  
            String.format("%,.2f", rs.getDouble("TOTAL_USD")), 
            String.format("%,.2f", rs.getDouble("TOTAL_PEN")), 
            String.format("%,.2f", rs.getDouble("PRECIO_VENTA")),
            rs.getInt("STOCK"),
            rs.getInt("CANTIDAD_COMPRADA"),  // Cantidad
            ""
        };
    }

    

    // Dentro de la clase Maestro
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

    // Sobrecarga para casos sin ActionListener
    private JButton createIconButton(String iconPath, String tooltip) {
        return createIconButton(iconPath, tooltip, null);
    }
    
    private void configureTableStyle() {
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        // Renderizado de filas
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Color de hover
                if (table.isRowSelected(row)) {
                    c.setBackground(new Color(240, 240, 240)); // Color selección
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245)); // Alternar colores
                }

                // Tooltip para descripción
                if (column == 1) {
                    setToolTipText(value.toString());
                }
                
                switch (column) {
                    case 4, 5, 6, 7, 8, 9, 10 -> ((JLabel) c).setHorizontalAlignment(SwingConstants.RIGHT);
                    case 1 -> ((JLabel) c).setHorizontalAlignment(SwingConstants.LEFT);
                    default -> ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                }

                return c;
            }
        });
    }

    // En el método createTablePanel()
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // En el método configureTable()
    private void configureTable() {
        table = new JTable(model);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, 
                    int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                // Alinear y formatear columnas monetarias
                if (column == 4 || column == 5 || column == 6 || column == 7 || column == 8) {
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.RIGHT);
                    String formattedValue = (column == 7 && column == 8) ? "S/ " + value : "$ " + value;
                    ((JLabel) c).setText(formattedValue);
                } 
                // Alinear columnas numéricas
                else if (column == 9 || column == 10) {
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.RIGHT);
                }
                return c;
            }
        });
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Desactivar autoajuste automático
        table.setFillsViewportHeight(true);
    }
    private void configureActionsColumn() {
        TableColumn column = table.getColumnModel().getColumn(ACCIONES_COLUMN_INDEX);
        column.setPreferredWidth(ACCIONES_COLUMN_WIDTH);
        column.setMaxWidth(ACCIONES_COLUMN_WIDTH);
        column.setMinWidth(ACCIONES_COLUMN_WIDTH);
        column.setResizable(false);
        
        ButtonRenderer renderer = new ButtonRenderer();
        column.setCellRenderer(renderer);
        column.setCellEditor(new ButtonEditor(table));
//        ((ButtonRenderer) column.getCellRenderer()).setButtonSpacing(5); 

    }

    private void loadTableData() {
        try (ResultSet rs = dbHelper.ejecutarFuncionCursor("LISTADO_TABLA")) {
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(createTableRow(rs));
            }
            SwingUtilities.invokeLater(() -> {
                adjustColumnWidths();
                table.revalidate();
                table.repaint();
            });
            actualizarTotalesGenerales();
        } catch (SQLException e) {
            showError("Error cargando datos: " + e.getMessage());
        }
    }
    
    private void actualizarTotalesGenerales() {
        try (ResultSet rs = dbHelper.ejecutarFuncionCursor("CALCULAR_TOTALES_GENERALES")) {
            if (rs.next()) {
                double subtotal = rs.getDouble("SUBTOTAL");
                double totalUSD = rs.getDouble("TOTAL_USD");
                double totalPEN = rs.getDouble("TOTAL_PEN");

                // Actualizar labels
                lblSubtotal.setText(String.format("Subtotal: $%.2f", subtotal));
                lblImpuestos.setText(String.format("Impuestos (7%%): $%.2f", totalUSD - subtotal));
                lblTotalDolares.setText(String.format("Total (USD): $%.2f", totalUSD));
                lblTotalSoles.setText(String.format("Total (PEN): S/%.2f", totalPEN));
            }
        } catch (SQLException ex) {
            showError("Error obteniendo totales: " + ex.getMessage());
        }
    }
    

    private void adjustColumnWidths() {
        if (table.getParent() == null) return;

        TableColumnModel columnModel = table.getColumnModel();
        int totalWidth = table.getParent().getWidth();

        // 1. Establecer anchos mínimos específicos
        int[] minWidths = {
            80,   // 0: ID 
            200,  // 1: Descripción (mínimo recomendado)
            100,  // 2: Color
            80,   // 3: Talla
            110,  // 4: Precio Compra
            110,  // 5: Subtotal
            110,  // 6: Total USD
            110,  // 7: Total PEN
            110,  // 8: Precio Venta
            80,   // 9: Stock
            90,   // 10: Cantidad
            120   // 11: Acciones
        };

        // 2. Aplicar anchos mínimos
        for(int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setMinWidth(minWidths[i]);
        }

        // 3. Calcular espacio restante para Descripción
        int usedWidth = 0;
        for(int i = 0; i < columnModel.getColumnCount(); i++) {
            if(i != 1) { // Excluir columna de Descripción
                usedWidth += columnModel.getColumn(i).getMinWidth();
            }
        }

        // 4. Ajustar ancho de Descripción dinámicamente
        int descWidth = Math.max(totalWidth - usedWidth, minWidths[1]);
        columnModel.getColumn(1).setPreferredWidth(descWidth);

        // 5. Asegurar que las acciones mantengan su ancho
        columnModel.getColumn(ACCIONES_COLUMN_INDEX).setPreferredWidth(minWidths[ACCIONES_COLUMN_INDEX]);
    }

    // Métodos utilitarios
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

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Clases internas
    // En la clase ButtonRenderer - Maestro.java
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private final JButton btnView;
        private final JButton btnEdit;
        private final JButton btnDelete;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0)); // 10px de espacio
            setOpaque(true);
            
            btnView = createIconButton(viewIcon, "Ver Detalle");
            btnEdit = createIconButton(editIcon, "Editar");
            btnDelete = createIconButton(deleteIcon, "Eliminar");
            
            add(btnView);
            add(btnEdit);
            add(btnDelete);
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

    class ButtonEditor extends AbstractButtonEditor {
        private int selectedRow;
        private final JPanel panel; // ✅ Variable declarada aquí

        public ButtonEditor(JTable table) {
            super();
            this.table = table;
            this.panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            setClickCountToStart(1);
        }
        
        @Override
        protected void handleViewAction(int row) {
            selectedRow = table.convertRowIndexToModel(row);
            viewAction();
        }
        
        @Override
        protected void handleEditAction(int row) {
            selectedRow = table.convertRowIndexToModel(row);
            editAction();
        }
        
        @Override
        protected void handleUndoAction(int row) {
            
        }

        @Override
        protected void handleDeleteAction(int row) {
            selectedRow = table.convertRowIndexToModel(row);
            deleteAction();
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
            JButton btnEdit = createActionButton(editIcon, "Editar", e -> handleEditAction(row));
            JButton btnDelete = createActionButton(deleteIcon, "Eliminar", e -> handleDeleteAction(row));
            
            panel.add(btnView);
            panel.add(btnEdit);
            panel.add(btnDelete);
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
        public Object getCellEditorValue() {
            return ""; // Valor no relevante para botones
        }

        @Override
        public boolean stopCellEditing() {
            super.stopCellEditing();
            return true;
        }
        
        private void viewAction() {
            if (selectedRow == -1) return;

            // Obtener datos de la fila
            String idPrenda = model.getValueAt(selectedRow, 0).toString();
            String descripcion = model.getValueAt(selectedRow, 1).toString();
            String color = model.getValueAt(selectedRow, 2).toString();
            String talla = model.getValueAt(selectedRow, 3).toString();
            String precioCompra = model.getValueAt(selectedRow, 4).toString();
            String precioV = model.getValueAt(selectedRow, 8).toString();
            String stock = model.getValueAt(selectedRow, 9).toString();
            String cantidad = model.getValueAt(selectedRow, 10).toString();

            // Obtener datos adicionales de la BD
            String sql = "SELECT c.nombre AS categoria, t.nombre as tienda " +
                         "FROM prendas p " +
                         "JOIN categorias c ON p.id_categoria = c.id_categoria " +
                         "JOIN TIENDAS T ON P.ID_TIENDA = T.ID_TIENDA " +
                         "WHERE p.id_prenda = ?";

            try (ResultSet rs = dbHelper.ejecutarConsulta(sql, idPrenda)) {
                String categoria = " ", tienda = " ";
                while(rs.next()){
                    categoria = rs.getString("categoria");
                    tienda = rs.getString("tienda");
                }

                // Mostrar diálogo de solo lectura
                JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(table), "Detalle de Prenda", true);
                dialog.setLayout(new GridLayout(0, 2, 10, 10));

                addReadOnlyField(dialog, "ID Prenda:", idPrenda);
                addReadOnlyField(dialog, "Categoría:", categoria);
                addReadOnlyField(dialog, "Descripción:", descripcion);
                addReadOnlyField(dialog, "Color:", color);
                addReadOnlyField(dialog, "Talla:", talla);
                addReadOnlyField(dialog, "Precio Compra:", precioCompra);
                addReadOnlyField(dialog, "Precio Venta:", precioV);
                addReadOnlyField(dialog, "Stock:", stock);
                addReadOnlyField(dialog, "Cantidad:", cantidad);
                addReadOnlyField(dialog, "Tienda:", tienda);

                JButton btnCerrar = new JButton("Cerrar");
                btnCerrar.addActionListener(e -> dialog.dispose());

                dialog.add(new JLabel());
                dialog.add(btnCerrar);

                dialog.pack();
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(table, "Error obteniendo datos: " + ex.getMessage());
            }
        }
        
        private void addReadOnlyField(JDialog dialog, String label, String value) {
            dialog.add(new JLabel(" " + label));
            JTextField field = new JTextField(value);
            field.setEditable(false);
            field.setBorder(BorderFactory.createEmptyBorder());
            field.setBackground(dialog.getBackground());
            dialog.add(field);
        }

        private void editAction() {
           if (selectedRow == -1) return;

            // Obtener datos de la fila
            String idPrenda = model.getValueAt(selectedRow, 0).toString();
            System.out.println("idPrenda " + idPrenda);
            String descripcion = model.getValueAt(selectedRow, 1).toString();
            String color = model.getValueAt(selectedRow, 2).toString();
            String talla = model.getValueAt(selectedRow, 3).toString();
            // En ButtonEditor.editAction()
            String precioCompra = model.getValueAt(selectedRow, 4).toString();
            String precioV = model.getValueAt(selectedRow, 8).toString();
            String stock = model.getValueAt(selectedRow, 9).toString();
            String cantidad = model.getValueAt(selectedRow, 10).toString();

            String sql = "SELECT c.nombre AS categoria, t.nombre as tienda " +
             "FROM prendas p " +
             "JOIN categorias c ON p.id_categoria = c.id_categoria " +
             "JOIN TIENDAS T ON P.ID_TIENDA = T.ID_TIENDA " +
             "WHERE p.id_prenda = ?";

            try (ResultSet rs = dbHelper.ejecutarConsulta(sql, idPrenda)) {
                String categoria =" ", tienda =" ";
                while(rs.next()){
                    categoria = rs.getString("categoria");
                    tienda = rs.getString("tienda");
                }

                // Mostrar diálogo de edición
                JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(table), "Editar Prenda", true);
                dialog.setLayout(new GridLayout(0, 2, 10, 10));
                JTextField txtDescripcion = new JTextField(20);
                JTextField txtColor = new JTextField(15);
                JComboBox<String> cmbTalla = new JComboBox<>();
                cargarDatosUnicos("M.TALLA", cmbTalla);
                JTextField txtPrecioV = new JTextField(10);
                JTextField txtPrecioC = new JTextField(10);
                JTextField txtStock = new JTextField(5);
                JTextField txtCantidad = new JTextField(5);
                JComboBox<String> cmbCategoria = new JComboBox<>();
                JComboBox<String> cmbTienda = new JComboBox<>();
                txtDescripcion.setText(descripcion);
                txtColor.setText(color);    
                txtPrecioC.setText(precioCompra);
                txtPrecioV.setText(precioV);
                txtStock.setText(stock);
                txtCantidad.setText(cantidad);
                cargarComboboxConSeleccion(cmbCategoria, "SELECT NOMBRE FROM categorias", categoria);
                cargarComboboxConSeleccion(cmbTienda, "SELECT NOMBRE FROM tiendas", tienda);
                seleccionarEnCombo(cmbTalla, talla);
                
                dialog.add(new JLabel(" Categoría:"));
                dialog.add(cmbCategoria);
                dialog.add(new JLabel(" Descripción:"));
                dialog.add(txtDescripcion);
                dialog.add(new JLabel(" Color:"));
                dialog.add(txtColor);
                dialog.add(new JLabel(" Talla:"));
                dialog.add(cmbTalla);
                dialog.add(new JLabel(" Precio Compra:"));
                dialog.add(txtPrecioC);
                dialog.add(new JLabel(" Precio Venta:"));
                dialog.add(txtPrecioV);
                dialog.add(new JLabel(" Stock:"));
                dialog.add(txtStock);
                dialog.add(new JLabel(" Cantidad:"));
                dialog.add(txtCantidad);
                dialog.add(new JLabel(" Tienda:"));
                dialog.add(cmbTienda);
                
                JButton btnGuardar = new JButton("Guardar Cambios");
                JButton btnCancelar = new JButton("Cancelar");
                // Actualizar llamada al procedimiento
                btnGuardar.addActionListener(e ->actualizarRegistro(
                    idPrenda,
                    txtDescripcion.getText(),
                    txtColor.getText(),
                    cmbTalla.getSelectedItem().toString(),
                    Double.parseDouble(txtPrecioC.getText()),
                    Double.parseDouble(txtPrecioV.getText()),
                    Integer.parseInt(txtStock.getText()),
                    Integer.parseInt(txtCantidad.getText()),
                    cmbCategoria.getSelectedItem().toString(),
                    cmbTienda.getSelectedItem().toString(), 
                    dialog
                ));
                btnCancelar.addActionListener(e -> dialog.dispose());
                dialog.add(btnGuardar);
                dialog.add(btnCancelar); 

                dialog.pack();
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(table, "Error obteniendo datos: " + ex.getMessage());
            }
        }

        private void cargarComboboxConSeleccion(JComboBox<String> combo, String query, String valorActual) {
            combo.removeAllItems();
            try (ResultSet rs = dbHelper.ejecutarConsulta(query)) {
                while (rs.next()) {
                    String valor = rs.getString(1);
                    combo.addItem(valor);
                    if (valor.equals(valorActual)) {
                        combo.setSelectedItem(valor);
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(panel, "Error cargando datos: " + e.getMessage());
            }
        }

        private void seleccionarEnCombo(JComboBox<String> combo, String valor) {
            for (int i = 0; i < combo.getItemCount(); i++) {
                if (combo.getItemAt(i).equals(valor)) {
                    combo.setSelectedIndex(i);
                    break;
                }
            }
        }

        private void actualizarRegistro(String idPrenda, String descripcion, String color, 
                              String talla, double precioC, double precioV, int stock, int cantidad, String categoria, String tienda, JDialog dialog) {
            try {
                dbHelper.ejecutarProcedimiento("ACTUALIZAR_INVENTARIO(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", 
                    idPrenda, descripcion, color, talla, precioC, precioV, stock, cantidad, categoria, tienda);

                JOptionPane.showMessageDialog(null, "¡Prenda actualizada!");
                dialog.dispose();
                loadTableData();

            } catch (SQLException ex) {
                showError("Error actualizando: " + ex.getMessage());
            }
        }

        private void deleteAction() {
            String idInventario = model.getValueAt(selectedRow, 0).toString();
            if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
                panel, "¿Eliminar registro " +idInventario+ "?", "Confirmar", JOptionPane.YES_NO_OPTION)) {

                try {
                    dbHelper.ejecutarProcedimiento("ELIMINAR_REGISTRO_COMPLETO(?)", Integer.valueOf(idInventario));
                    JOptionPane.showMessageDialog((Frame) SwingUtilities.getWindowAncestor(table), "Registro eliminado");
                    loadTableData();
                } catch (SQLException ex) {
                    showError("Error eliminando: " + ex.getMessage());
                }
            }
        }
    }
   
   private void cargarCombo(JComboBox<String> combo, String consulta, String mensajeError) {
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(consulta)) {

            combo.removeAllItems();
            while (rs.next()) {
                combo.addItem(rs.getString(1).toUpperCase());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, mensajeError + e.getMessage());
        }
    }

    

    // Métodos no implementados (mantenidos como ejemplo)
    private void agregarRegistro() {
        JDialog dialog = new JDialog(this, "Agregar Nuevo Registro", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));

        // Campos del formulario
        JTextField txtDescripcion = new JTextField(20);
        JTextField txtColor = new JTextField(15);
        JComboBox<String> cmbTalla = new JComboBox<>();
        JTextField txtPrecioC = new JTextField(10);
        JTextField txtPrecioV = new JTextField(10);
        JTextField txtStock = new JTextField(5);
        JComboBox<String> cmbCategoria = new JComboBox<>();
        JComboBox<String> cmbTienda = new JComboBox<>();

        // Cargar categorías desde la BD
        cargarCombo(cmbCategoria, 
                    "SELECT nombre FROM categorias", 
                    "Error cargando categorías: ");
    
        cargarCombo(cmbTalla, 
                   "SELECT talla FROM tallas", 
                   "Error cargando tallas: ");

        cargarCombo(cmbTienda, 
                   "SELECT nombre FROM tiendas", 
                   "Error cargando tiendas: ");

        // Validación numérica
        txtPrecioC.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == '.' || c == KeyEvent.VK_BACK_SPACE)) {
                    e.consume();
                }
            }
        });
        txtPrecioV.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!(Character.isDigit(c) || c == '.' || c == KeyEvent.VK_BACK_SPACE)) {
                    e.consume();
                }
            }
        });

        txtStock.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });

        // Agregar componentes al diálogo
        dialog.add(new JLabel(" Categoría:"));
        dialog.add(cmbCategoria);
        dialog.add(new JLabel(" Descripción:"));
        dialog.add(txtDescripcion);
        dialog.add(new JLabel(" Color:"));
        dialog.add(txtColor);
        dialog.add(new JLabel(" Talla:"));
        dialog.add(cmbTalla);
        dialog.add(new JLabel(" Precio Compra:"));
        dialog.add(txtPrecioC);
//        dialog.add(new JLabel(" Precio Venta:"));
//        dialog.add(txtPrecioV);
        dialog.add(new JLabel(" Stock:"));
        dialog.add(txtStock);
        dialog.add(new JLabel(" Tienda:"));
        dialog.add(cmbTienda);

        // Botones
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> {
            try {
                String categoria = cmbCategoria.getSelectedItem().toString();
                String descripcion = txtDescripcion.getText();
                String color = txtColor.getText();
                String talla = cmbTalla.getSelectedItem().toString();
                double precioC = Double.parseDouble(txtPrecioC.getText());
//                double precioV = Double.parseDouble(txtPrecioV.getText());
                int stock = Integer.parseInt(txtStock.getText());
                String tienda = cmbTienda.getSelectedItem().toString();

                try {
                    dbHelper.ejecutarProcedimiento("INSERTAR_INVENTARIO(?, ?, ?, ?, ?, ?, ?, ?)", 
                        descripcion, 
                        color, 
                        talla, 
                        precioC, 
                        precioC * 8, // precioV calculado
                        stock, 
                        categoria, 
                        tienda);

                    JOptionPane.showMessageDialog(this, "¡Registro agregado!");
                    dialog.dispose();
                    loadTableData(); // Actualizar tabla

                } catch (SQLException ex) {
                    if (ex.getErrorCode() == 20001) {
                        JOptionPane.showMessageDialog(this, 
                            "Error: La categoría no existe", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE
                        );
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Error en BD: " + ex.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Formato numérico inválido", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.add(btnGuardar);
        dialog.add(btnCancelar);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
