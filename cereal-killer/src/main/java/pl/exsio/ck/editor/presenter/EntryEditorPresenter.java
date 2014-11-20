package pl.exsio.ck.editor.presenter;

import java.util.Collection;
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
