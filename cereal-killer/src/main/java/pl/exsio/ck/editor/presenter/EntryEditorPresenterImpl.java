package pl.exsio.ck.editor.presenter;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
import pl.exsio.ck.editor.view.AbstractEntryEditorFrame;
import pl.exsio.ck.model.Entry;

public class EntryEditorPresenterImpl implements EntryEditorPresenter {

    protected AbstractEntryEditorFrame view;

    protected Set<SaveListener> saveListeners;

    protected Set<CancelListener> cancelListeners;

    protected String[] serials;

    @Override
    public void setView(AbstractEntryEditorFrame view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void show(String[] serials, SaveListener saveListener) {
        this.show(serials, saveListener, null);
    }

    @Override
    public void show(String[] serials, SaveListener saveListener, CancelListener cancelListener) {
        this.initListeners(saveListener, cancelListener);
        this.serials = serials;
    }

    private void initListeners(SaveListener saveListener, CancelListener cancelListener) {
        this.saveListeners = new LinkedHashSet<>();
        this.cancelListeners = new LinkedHashSet<>();
        this.cancelListeners.add(this.getCloseEditorListener());
        this.cancelListeners.add(cancelListener);
        this.saveListeners.add(saveListener);
    }

    private CancelListener getCloseEditorListener() {
        return new CancelListener() {

            @Override
            public void cancelEdition() {
                closeView();
            }
        };
    }

    @Override
    public void save() {
        Collection<Entry> entries = this.getEntries();
        if (entries != null) {
            for (SaveListener listener : this.saveListeners) {
                listener.saveEntries(entries);
            }
            this.closeView();
        } else {
            JOptionPane.showMessageDialog(null, "ALERT MESSAGE", "TITLE", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void cancel() {
        for (CancelListener listener : this.cancelListeners) {
            listener.cancelEdition();
        }
    }

    protected void closeView() {
        this.view.setVisible(false);
        this.view.dispose();
    }

    protected Collection<Entry> getEntries() {
        Map<String, Object> values = this.view.getValues();
        if (this.validateValues(values)) {
            return this.createEntries(values);
        } else {
            return null;
        }
    }

    protected boolean validateValues(Map<String, Object> values) {
        for (String key : values.keySet()) {
            if (values.get(key) == null) {
                return false;
            }
        }
        return true;
    }

    protected Collection<Entry> createEntries(Map<String, Object> values) {
        Set<Entry> entries = new LinkedHashSet<>();
        for (String serial : this.serials) {
            Entry e = new Entry();
            e.setSerialNo(serial);
            e.setBuyInvoiceNo((String) values.get(V_BUY_INVOICE_NO));
            e.setRecipient((String) values.get(V_RECIPIENT));
            e.setSellDate((Date) values.get(V_SELL_DATE));
            e.setSellInvoiceNo((String) values.get(V_SELL_INVOICE_NO));
            e.setSupplier((String) values.get(V_SUPPLIER));
            e.setSupplyDate((Date) values.get(V_SUPPLY_DATE));
            entries.add(e);
        }
        return entries;
    }

}
