package pl.exsio.ck.main.app;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import pl.exsio.ck.logging.presenter.LogPresenter;
import pl.exsio.ck.main.view.MainFrame;
import pl.exsio.ck.model.dao.EntryDao;
import pl.exsio.ck.progress.view.ProgressFrame;

/**
 *
 * @author exsio
 */
public class App {

    private static ApplicationContext ctx;

    public static final String MAIN_FRAME_BEAN = "mainFrame";

    public static final String PROGRESS_FRAME_BEAN = "progressFrame";

    public static final String LOG_BEAN = "logPresenter";

    public static final String ENTRY_DAO_BEAN = "entryDao";

    public static void main(String[] args) {
        App.ctx = new ClassPathXmlApplicationContext("context.xml");
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                App.getProgressFrame().setVisible(false);
                App.getMainFrame().setVisible(true);
                App.getLog().log("uruchomiono aplikację");
            }
        });
    }

    public static ApplicationContext getContext() {
        return App.ctx;
    }

    public static EntryDao getEntryDao() {
        return (EntryDao) App.ctx.getBean(ENTRY_DAO_BEAN);
    }

    private static MainFrame getMainFrame() {
        return (MainFrame) App.ctx.getBean(MAIN_FRAME_BEAN);
    }

    private static ProgressFrame getProgressFrame() {
        return (ProgressFrame) App.ctx.getBean(PROGRESS_FRAME_BEAN);
    }

    private static LogPresenter getLog() {
        return (LogPresenter) App.ctx.getBean(LOG_BEAN);
    }

}
