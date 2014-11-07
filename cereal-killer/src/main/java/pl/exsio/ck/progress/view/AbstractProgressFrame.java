package pl.exsio.ck.progress.view;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import pl.exsio.ck.progress.presenter.ProgressPresenter;
import pl.exsio.ck.view.AbstractFrame;

/**
 *
 * @author exsio
 */
public abstract class AbstractProgressFrame extends AbstractFrame {

    protected ProgressPresenter presenter;

    public void setPresenter(ProgressPresenter presenter) {
        this.presenter = presenter;
    }

    public abstract JProgressBar getBar();

    public abstract JLabel getNameLabel();
}
