package pl.exsio.ck.entrytable.presenter;

import java.util.Collection;
import pl.exsio.ck.entrytable.view.AbstractEntryTablePanel;
import pl.exsio.ck.presenter.Presenter;

/**
 *
 * @author exsio
 */
public interface EntryTablePresenter extends Presenter {

    void setView(AbstractEntryTablePanel view);
    
    void showEntries();

    void showEntries(String[] serials);

    void filter(String filterStr);
}
