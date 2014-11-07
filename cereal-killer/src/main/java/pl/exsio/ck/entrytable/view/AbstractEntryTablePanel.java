package pl.exsio.ck.entrytable.view;

import javax.swing.JPanel;
import javax.swing.JTable;
import pl.exsio.ck.entrytable.presenter.EntryTablePresenter;

/**
 *
 * @author exsio
 */
public abstract class AbstractEntryTablePanel extends JPanel {

    protected EntryTablePresenter presenter;

    public void setPresenter(EntryTablePresenter presenter) {
        this.presenter = presenter;
    }

    public abstract JTable getEntryTable();
}
