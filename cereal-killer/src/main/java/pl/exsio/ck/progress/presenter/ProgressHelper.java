/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.exsio.ck.progress.presenter;

import pl.exsio.ck.main.app.App;

/**
 *
 * @author exsio
 */
public class ProgressHelper {
    
    public static ProgressPresenter showProgressBar(final String progressName, final boolean indeterminate) {
        
        final ProgressPresenter progress = (ProgressPresenter) App.getContext().getBean("progressPresenter");
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
