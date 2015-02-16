/* 
 * The MIT License
 *
 * Copyright 2015 exsio.
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
package pl.exsio.ck.logging.presenter;

import java.awt.Container;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.SwingWorker;
import org.apache.commons.lang3.exception.ExceptionUtils;
import pl.exsio.ck.logging.view.AbstractLogPanel;

/**
 *
 * @author exsio
 */
public class LogPresenterImpl implements LogPresenter {
    
    private AbstractLogPanel view;
    
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    
    @Override
    public void setView(AbstractLogPanel view) {
        this.view = view;
        view.setPresenter(this);
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
    
    @Override
    public void logThrowable(Throwable t) {
        this.log(ExceptionUtils.getMessage(t));
        this.log(ExceptionUtils.getStackTrace(t));
    }
    
}
