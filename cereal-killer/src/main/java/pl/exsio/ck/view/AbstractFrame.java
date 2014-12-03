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
package pl.exsio.ck.view;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import javax.swing.JFrame;

/**
 *
 * @author exsio
 */
public abstract class AbstractFrame extends JFrame {

    public AbstractFrame() {

    }

    public final void showOnScreen(int screen) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ge.getScreenDevices();
        Rectangle r = null;
        if (screen > -1 && screen < gd.length) {
            r = gd[screen].getDefaultConfiguration().getBounds();
        } else if (gd.length > 0) {
            r = gd[0].getDefaultConfiguration().getBounds();
        } else {
            throw new RuntimeException("No Screens Found");
        }
        this.setLocation(this.getDisplayX(r), this.getDisplayY(r));
    }

    private int getDisplayX(Rectangle r) {
        return r.x + (r.width / 2) - (this.getWidth() / 2);
    }

    private int getDisplayY(Rectangle r) {
        return r.y + (r.height / 2) - (this.getHeight() / 2);
    }
}
