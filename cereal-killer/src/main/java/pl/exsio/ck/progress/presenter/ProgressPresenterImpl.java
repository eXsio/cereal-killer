/* 
 * The MIT License
 *
 * Copyright 2014 exsio.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pl.exsio.ck.progress.presenter;

import java.awt.Container;
import javax.swing.SwingWorker;
import pl.exsio.ck.progress.view.AbstractProgressFrame;

/**
 *
 * @author exsio
 */
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
