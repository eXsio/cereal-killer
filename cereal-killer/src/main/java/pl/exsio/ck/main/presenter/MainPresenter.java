
package pl.exsio.ck.main.presenter;

import pl.exsio.ck.comparator.EntryComparator;
import pl.exsio.ck.importer.EntryImporter;
import pl.exsio.ck.presenter.Presenter;

/**
 *
 * @author exsio
 */
public interface MainPresenter extends Presenter {

    void showImportWindow(boolean updateEnabled);

    void showCompareWindow();
    
    void showBrowseWindow();

    void exit();

    void setEntryImporter(EntryImporter importer);

    void setEntryComparator(EntryComparator comparator);
}
