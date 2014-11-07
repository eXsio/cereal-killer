package pl.exsio.ck.main.presenter;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import pl.exsio.ck.comparator.EntryComparator;
import pl.exsio.ck.entrytable.presenter.EntryTablePresenter;
import pl.exsio.ck.importer.EntryImporter;
import pl.exsio.ck.logging.presenter.LogPresenter;
import pl.exsio.ck.main.view.AbstractMainFrame;
import pl.exsio.ck.model.dao.EntryDao;
import pl.exsio.ck.table.TableAware;
import pl.exsio.ck.view.AbstractFrame;

/**
 *
 * @author exsio
 */
public class MainPresenterImpl extends TableAware implements MainPresenter {

    private AbstractMainFrame view;

    private EntryImporter importer;

    private EntryComparator comparator;

    private LogPresenter log;

    private EntryDao dao;

    @Override
    public void setView(AbstractMainFrame view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void showImportWindow(final boolean updateEnabled) {
        JFileChooser jfc = this.createFileChooser();
        int returnVal = jfc.showOpenDialog(view);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File file = jfc.getSelectedFile();
            this.view.setEnabled(false);
            Thread importThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    importer.importFile(file, updateEnabled);
                }
            });
            importThread.start();
            this.view.setEnabled(true);
        }
    }

    @Override
    public void showCompareWindow() {
        JFileChooser jfc = this.createFileChooser();
        int returnVal = jfc.showOpenDialog(view);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File file = jfc.getSelectedFile();
            this.view.setEnabled(false);
            Thread importThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    comparator.compareFile(file);
                }
            });
            importThread.start();
            try {
                importThread.join();
            } catch (InterruptedException ex) {
                this.log.log("Podczas porównania wystąpił błąd");
                this.log.log(ExceptionUtils.getMessage(ex));
            }
            this.view.setEnabled(true);
        }
    }

    @Override
    public void showBrowseWindow() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                AbstractFrame browser = getBrowserFrame();
                EntryTablePresenter presenter = getEntryTablePresenter();
                presenter.showEntries(dao.findAll());
                browser.setLayout(new BorderLayout());
                browser.add(presenter.getView());
                browser.setTitle("Przeglądaj zaimportowane wpisy");
                browser.pack();
                browser.showOnScreen(0);
                browser.setVisible(true);
            }
        });
    }

    @Override
    public void exit() {
        System.exit(0);
    }

    @Override
    public Container getView() {
        return this.view;
    }

    private JFileChooser createFileChooser() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".xlsx") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Pliki XLSX";
            }
        });
        return jfc;
    }

    @Override
    public void setEntryImporter(EntryImporter importer) {
        this.importer = importer;
    }

    @Override
    public void setEntryComparator(EntryComparator comparator) {
        this.comparator = comparator;
    }

    public void setDao(EntryDao dao) {
        this.dao = dao;
    }

    public void setLog(LogPresenter log) {
        this.log = log;
    }

}
