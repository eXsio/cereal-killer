package pl.exsio.ck.main.view;

import pl.exsio.ck.logging.view.AbstractLogPanel;
import pl.exsio.ck.logging.view.LogPanel;
import pl.exsio.ck.main.presenter.MainPresenter;
import pl.exsio.ck.view.AbstractFrame;

/**
 *
 * @author exsio
 */
public class AbstractMainFrame extends AbstractFrame {

    protected MainPresenter presenter;

    protected AbstractLogPanel logPanel;

    public void setPresenter(MainPresenter presenter) {
        this.presenter = presenter;
    }

    public void setLogPanel(final LogPanel logPanel) {
        this.logPanel = logPanel;
    }
}
