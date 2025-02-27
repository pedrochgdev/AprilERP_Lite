package aprilbutiquestore.componentes;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;

// ✅ Corrección: Extender de DefaultCellEditor (no DefaultTableCellEditor)
public abstract class AbstractButtonEditor extends DefaultCellEditor {
    protected JTable table;
    
    public AbstractButtonEditor() {
        super(new JCheckBox());
    }
    
    protected String getRowId(int row) {
        return table.getModel().getValueAt(row, 0).toString();
    }
    
    protected abstract void handleViewAction(int row);
    protected abstract void handleEditAction(int row);
    protected abstract void handleDeleteAction(int row);
    protected abstract void handleUndoAction(int row);
}