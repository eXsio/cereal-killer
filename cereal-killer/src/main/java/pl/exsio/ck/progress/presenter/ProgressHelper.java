package pl.exsio.ck.progress.presenter;

import pl.exsio.ck.main.app.App;

/**
 *
 * @author exsio
 */
public class ProgressHelper {
    
    public static ProgressPresenter showProgressBar(final String progressName, final boolean indeterminate) {
        
        final ProgressPresenter progress = App.getContext().getBean(ProgressPresenter.class);
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                
                progress.setProgressName(progressName);
                if (!indeterminate) {
                    progress.setProgress(0);
                }
                progress.show(indeterminate);
                
            }
        }).start();
        return progress;
    }
    
    public static void updateProgressBar(final ProgressPresenter progress, final int percent) {
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                
                progress.setProgress(percent);
                
            }
        }).start();
    }
    
    public static void hideProgressBar(final ProgressPresenter progress) {
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                progress.hide();
            }
        }).start();
    }
}
