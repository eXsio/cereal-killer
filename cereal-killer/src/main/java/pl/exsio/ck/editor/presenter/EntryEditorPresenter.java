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

import pl.exsio.ck.editor.view.AbstractEntryEditorFrame;
import pl.exsio.ck.model.Entry;

/**
 *
 * @author exsio
 */
public interface EntryEditorPresenter {

    public static final String V_SUPPLIER = "supplier";

    public static final String V_BUY_INVOICE_NO = "buy_invoice_no";

    public static final String V_RECIPIENT = "recipient";

    public static final String V_SUPPLY_DATE = "supply_date";

    public static final String V_SELL_DATE = "sell_date";

    public static final String V_SELL_INVOICE_NO = "sell_invoice_no";

    void setView(AbstractEntryEditorFrame view);

    void show(String[] serials, SaveListener saveListener);

    void show(String[] serials, SaveListener saveListener, CancelListener cancelListener);
    
    void save();
    
    void cancel();

    public interface SaveListener {

        void saveEntries(String[] serials, Entry dataPattern);
    }

    public interface CancelListener {

        void cancelEdition();
    }
}
