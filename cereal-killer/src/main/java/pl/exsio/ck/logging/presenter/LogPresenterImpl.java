package pl.exsio.ck.logging.presenter;

import java.awt.Container;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.SwingWorker;
import org.apache.commons.lang3.exception.ExceptionUtils;
import pl.exsio.ck.logging.view.AbstractLogPanel;

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
