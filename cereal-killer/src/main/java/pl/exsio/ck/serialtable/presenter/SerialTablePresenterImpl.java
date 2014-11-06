
package pl.exsio.ck.serialtable.presenter;

import java.awt.Container;
import java.util.Collection;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import pl.exsio.ck.serialtable.view.SerialTablePanel;

public class SerialTablePresenterImpl implements SerialTablePresenter {

    private SerialTablePanel view;

    public SerialTablePresenterImpl(SerialTablePanel view) {
        this.view = view;
    }

    @Override
    public void showSerials(final Collection<String> serials) {

        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                JTable table = view.getEntryTable();
                DefaultTableModel tm = new DefaultTableModel() {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                tm.setColumnIdentifiers(new String[]{
                    "Nr seryjny"
                });
                for (String s : serials) {
                    tm.addRow(new Object[]{s});
                }
                tm.setRowCount(serials.size());
                table.setModel(tm);
                return null;
            }

        };
        worker.execute();

    }

    @Override
    public Container getView() {
        return this.view;
    }

}
