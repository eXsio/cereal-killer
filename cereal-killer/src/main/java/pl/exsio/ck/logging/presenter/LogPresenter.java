
package pl.exsio.ck.logging.presenter;

import pl.exsio.ck.presenter.Presenter;

/**
 *
 * @author exsio
 */
public interface LogPresenter extends Presenter {

    void log(String msg);

    void clean();
}
