package pl.exsio.ck.progress.presenter;

import pl.exsio.ck.presenter.Presenter;
import pl.exsio.ck.progress.view.AbstractProgressFrame;

/**
 *
 * @author exsio
 */
public interface ProgressPresenter extends Presenter {

    void setView(AbstractProgressFrame view);

    void setProgressName(String name);

    void setProgress(int percent);
}
