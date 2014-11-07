package pl.exsio.ck.serialtable.view;

import javax.swing.JPanel;
import javax.swing.JTable;
import pl.exsio.ck.serialtable.presenter.SerialTablePresenter;

/**
 *
 * @author exsio
 */
public abstract class AbstractSerialTablePanel extends JPanel {

    protected SerialTablePresenter presenter;

    public void setPresenter(SerialTablePresenter presenter) {
        this.presenter = presenter;
    }

    public abstract JTable getEntryTable();
}
