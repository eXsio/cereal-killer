package pl.exsio.ck.progress.presenter;

import java.awt.Container;
import javax.swing.SwingWorker;
import pl.exsio.ck.progress.view.AbstractProgressFrame;

public class ProgressPresenterImpl implements ProgressPresenter {

    private AbstractProgressFrame view;

    @Override
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

    @Override
    public void show(final boolean indeterminate) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                view.getBar().setIndeterminate(indeterminate);
                view.getBar().setStringPainted(!indeterminate);
                view.showOnScreen(0);
                view.setVisible(true);
            }
            
        });
    }

    @Override
    public void hide() {
        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                return null;
            }

            @Override
            protected void done() {
                view.setVisible(false);
                view.dispose();
            }
        };
        worker.execute();
    }

}
