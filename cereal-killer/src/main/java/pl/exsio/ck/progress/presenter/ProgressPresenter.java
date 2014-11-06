
package pl.exsio.ck.progress.presenter;

import pl.exsio.ck.presenter.Presenter;

/**
 *
 * @author exsio
 */
public interface ProgressPresenter extends Presenter {

    void setProgressName(String name);
    
    void setProgress(int percent);
}
