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
