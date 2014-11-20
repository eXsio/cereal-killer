package pl.exsio.ck.editor.presenter;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
import pl.exsio.ck.editor.view.AbstractEntryEditorFrame;
import pl.exsio.ck.model.Entry;
import pl.exsio.ck.model.EntryImpl;

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
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                view.showOnScreen(0);
                view.setVisible(true);
            }
        });
    }

    private void initListeners(SaveListener saveListener, CancelListener cancelListener) {
        this.saveListeners = new LinkedHashSet<>();
        this.cancelListeners = new LinkedHashSet<>();
        this.cancelListeners.add(this.getCloseEditorListener());
        if (cancelListener != null) {
            this.cancelListeners.add(cancelListener);
        }
        if (saveListener != null) {
            this.saveListeners.add(saveListener);
        }
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
        Entry dataPattern = this.getDataPattern();
        if (dataPattern != null) {
            for (SaveListener listener : this.saveListeners) {
                listener.saveEntries(this.serials, dataPattern);
            }
            this.closeView();
        } else {
            JOptionPane.showMessageDialog(this.view, "Proszę wypełnić wszystkie dane", "Uwaga!", JOptionPane.ERROR_MESSAGE);
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

    protected Entry getDataPattern() {
        Map<String, Object> values = this.view.getValues();
        if (this.validateValues(values)) {
            Entry dataPattern = new EntryImpl();
            dataPattern.setBuyInvoiceNo((String) values.get(V_BUY_INVOICE_NO));
            dataPattern.setRecipient((String) values.get(V_RECIPIENT));
            dataPattern.setSellDate((Date) values.get(V_SELL_DATE));
            dataPattern.setSellInvoiceNo((String) values.get(V_SELL_INVOICE_NO));
            dataPattern.setSupplier((String) values.get(V_SUPPLIER));
            dataPattern.setSupplyDate((Date) values.get(V_SUPPLY_DATE));
            return dataPattern;
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

}
