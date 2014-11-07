
package pl.exsio.ck.entrytable.presenter;

import java.awt.Container;
import java.text.SimpleDateFormat;
import java.util.Collection;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import pl.exsio.ck.entrytable.view.EntryTablePanel;
import pl.exsio.ck.model.Entry;

public class EntryTablePresenterImpl implements EntryTablePresenter {

    private EntryTablePanel view;

    private final SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

    private final SimpleDateFormat datetime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public void setView(EntryTablePanel view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void showEntries(final Collection<Entry> entries) {

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
                    "Id", "Nr seryjny", "Dostawca", "Data dostawy",
                    "Nr faktury zakupu", "Odbiorca", "Data sprzedaży",
                    "Nr faktury sprzedaży", "Data importu"
                });
                for (Entry e : entries) {
                    tm.addRow(new Object[]{e.getId(), e.getSerialNo(), e.getSupplier(),
                        date.format(e.getSupplyDate()), e.getBuyInvoiceNo(),
                        e.getRecipient(), date.format(e.getSellDate()),
                        e.getSellInvoiceNo(), datetime.format(e.getImportedAt())});
                }
                tm.setRowCount(entries.size());
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
