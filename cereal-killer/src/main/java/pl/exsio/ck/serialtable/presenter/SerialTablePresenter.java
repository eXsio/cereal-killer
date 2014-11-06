
package pl.exsio.ck.serialtable.presenter;

import java.util.Collection;
import pl.exsio.ck.presenter.Presenter;

/**
 *
 * @author exsio
 */
public interface SerialTablePresenter extends Presenter {
    
    void showSerials(Collection<String> serials);
}
