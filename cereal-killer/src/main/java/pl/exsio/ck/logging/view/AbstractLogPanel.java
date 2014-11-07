/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.exsio.ck.logging.view;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import pl.exsio.ck.logging.presenter.LogPresenter;

/**
 *
 * @author exsio
 */
public abstract class AbstractLogPanel extends JPanel {

    protected LogPresenter presenter;

    public void setPresenter(LogPresenter presenter) {
        this.presenter = presenter;
    }

    public abstract JTextArea getLogArea();
}
