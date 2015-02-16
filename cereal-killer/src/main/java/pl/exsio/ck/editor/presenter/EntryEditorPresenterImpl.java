/* 
 * The MIT License
 *
 * Copyright 2015 exsio.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pl.exsio.ck.editor.presenter;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
import pl.exsio.ck.editor.view.AbstractEntryEditorFrame;
import pl.exsio.ck.model.Entry;
import pl.exsio.ck.model.EntryImpl;

/**
 *
 * @author exsio
 */
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
        java.awt.EventQueue.invokeLater(() -> {
            view.showOnScreen(0);
            view.setVisible(true);
        });
    }

    private void initListeners(SaveListener saveListener, CancelListener cancelListener) {
        this.saveListeners = new LinkedHashSet<>();
        this.cancelListeners = new LinkedHashSet<>();
        this.cancelListeners.add(() -> {
            closeView();
        });
        if (cancelListener != null) {
            this.cancelListeners.add(cancelListener);
        }
        if (saveListener != null) {
            this.saveListeners.add(saveListener);
        }
    }

    @Override
    public void save() {
        Entry dataPattern = this.getDataPattern();
        if (dataPattern != null) {
            this.saveListeners.stream().forEach((listener) -> {
                listener.saveEntries(this.serials, dataPattern);
            });
            this.closeView();
        } else {
            JOptionPane.showMessageDialog(this.view, "Proszę wypełnić wszystkie dane", "Uwaga!", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void cancel() {
        this.cancelListeners.stream().forEach((listener) -> {
            listener.cancelEdition();
        });
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
        return values.keySet().stream().noneMatch((key) -> (values.get(key) == null));
    }

}
