package pl.exsio.ck.progress.presenter;

import java.awt.Container;
import javax.swing.SwingWorker;
import pl.exsio.ck.progress.view.AbstractProgressFrame;

public class ProgressPresenterImpl implements ProgressPresenter {

    private AbstractProgressFrame view;

    public void setView(AbstractProgressFrame view) {
        this.view = view;
        view.setPresenter(this);
        view.getBar().setIndeterminate(false);
        view.getBar().setStringPainted(true);
        view.getBar().setVisible(true);
        view.getNameLabel().setVisible(true);
    }

    @Override
    public void setProgressName(final String name) {
        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                view.getNameLabel().setText(name);
                return null;
            }
        };
        worker.execute();
    }

    @Override
    public void setProgress(final int percent) {
        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                view.getBar().setValue(percent);
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
