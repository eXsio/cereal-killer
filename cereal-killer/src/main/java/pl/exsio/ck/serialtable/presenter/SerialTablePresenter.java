
package pl.exsio.ck.serialtable.presenter;

import java.util.Collection;
import pl.exsio.ck.presenter.Presenter;
import pl.exsio.ck.serialtable.view.AbstractSerialTablePanel;

/**
 *
 * @author exsio
 */
public interface SerialTablePresenter extends Presenter {
    
    void setView(AbstractSerialTablePanel view);
    
    void showSerials(Collection<String> serials);
}
