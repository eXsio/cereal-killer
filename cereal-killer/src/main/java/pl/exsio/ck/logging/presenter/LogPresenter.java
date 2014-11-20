package pl.exsio.ck.logging.presenter;

import pl.exsio.ck.logging.view.AbstractLogPanel;
import pl.exsio.ck.presenter.Presenter;

/**
 *
 * @author exsio
 */
public interface LogPresenter extends Presenter {

    void setView(AbstractLogPanel view);
    
    void log(String msg);
    
    void logThrowable(Throwable t);

    void clean();
}
