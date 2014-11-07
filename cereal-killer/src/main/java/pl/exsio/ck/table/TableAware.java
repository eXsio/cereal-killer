/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.exsio.ck.table;

import pl.exsio.ck.browser.view.BrowserFrame;
import pl.exsio.ck.entrytable.presenter.EntryTablePresenter;
import pl.exsio.ck.main.app.App;
import pl.exsio.ck.serialtable.presenter.SerialTablePresenter;

/**
 *
 * @author exsio
 */
public abstract class TableAware {

    private static final String BROWSER_FRAME_BEAN = "browserFrame";

    private static final String ENTRY_TABLE_PRESENTER_BEAN = "entryTablePresenter";

    private static final String SERIAL_TABLE_PRESENTER_BEAN = "serialTablePresenter";

    protected BrowserFrame getBrowserFrame() {
        return (BrowserFrame) App.getContext().getBean(BROWSER_FRAME_BEAN);
    }

    protected EntryTablePresenter getEntryTablePresenter() {
        return (EntryTablePresenter) App.getContext().getBean(ENTRY_TABLE_PRESENTER_BEAN);
    }

    protected SerialTablePresenter getSerialTablePresenter() {
        return (SerialTablePresenter) App.getContext().getBean(SERIAL_TABLE_PRESENTER_BEAN);
    }
}
