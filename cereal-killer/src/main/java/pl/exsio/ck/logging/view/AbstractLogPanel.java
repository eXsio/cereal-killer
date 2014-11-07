package pl.exsio.ck.logging.view;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import pl.exsio.ck.logging.presenter.LogPresenter;

/**
 *
 * @author exsio
 */
public abstract class AbstractLogPanel extends JPanel {

    protected LogPresenter presenter;

    public void setPresenter(LogPresenter presenter) {
        this.presenter = presenter;
    }

    public abstract JTextArea getLogArea();
}
