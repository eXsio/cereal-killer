
package pl.exsio.ck.logging.presenter;

import java.awt.Container;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.SwingWorker;
import pl.exsio.ck.logging.view.LogPanel;

public class LogPresenterImpl implements LogPresenter {

    private final LogPanel view;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public LogPresenterImpl(LogPanel view) {
        this.view = view;
    }

    @Override
    public void log(final String msg) {
        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {

                view.getLogArea().append(sdf.format(new Date()) + ": " + msg + "\n");
                return null;
            }

        };
        worker.execute();
    }

    @Override
    public void clean() {
        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                view.getLogArea().setText("");
                return null;
            }

        };
        worker.execute();
    }

    @Override
    public Container getView() {
        return this.view;
    }

}
