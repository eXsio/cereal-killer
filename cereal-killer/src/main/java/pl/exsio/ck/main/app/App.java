
package pl.exsio.ck.main.app;

import pl.exsio.ck.comparator.EntryComparatorImpl;
import pl.exsio.ck.importer.EntryImporterImpl;
import pl.exsio.ck.logging.presenter.LogPresenterImpl;
import pl.exsio.ck.logging.view.LogPanel;
import pl.exsio.ck.main.presenter.MainPresenter;
import pl.exsio.ck.main.presenter.MainPresenterImpl;
import pl.exsio.ck.main.view.MainFrame;
import pl.exsio.ck.model.dao.EntryDao;
import pl.exsio.ck.model.dao.EntryDaoImpl;
import pl.exsio.ck.progress.presenter.ProgressPresenterImpl;
import pl.exsio.ck.progress.view.ProgressFrame;

/**
 *
 * @author exsio
 */
public class App {

    private static EntryDao dao;

    private static LogPanel log;

    private static ProgressFrame progress;

    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                App.init();
                App.createMainFrame();
                App.createProgressFrame();
                App.log("uruchomiono aplikację");
            }
        });
    }

    private static void init() {
        App.log = App.createLogPanel();
        App.dao = new EntryDaoImpl();
    }

    private static void createMainFrame() {
        MainFrame main = new MainFrame();
        main.setPresenter(App.createMainPresenter(main));
        main.setLog(App.log);
        main.setVisible(true);
    }

    private static void createProgressFrame() {
        App.progress = new ProgressFrame();
        App.progress.setPresenter(new ProgressPresenterImpl(App.progress));
        App.progress.setVisible(false);
    }

    private static MainPresenter createMainPresenter(MainFrame view) {
        MainPresenter mp = new MainPresenterImpl(view);
        mp.setEntryComparator(new EntryComparatorImpl());
        mp.setEntryImporter(new EntryImporterImpl());
        return mp;
    }

    private static LogPanel createLogPanel() {
        LogPanel log = new LogPanel();
        log.setPresenter(new LogPresenterImpl(log));
        return log;
    }

    public static EntryDao getEntryDao() {
        return dao;
    }

    public static void log(String msg) {
        if (App.log != null) {
            App.log.log(msg);
        } else {
            System.out.println(msg);
        }
    }

    public static ProgressFrame getProgress() {
        return progress;
    }

}
