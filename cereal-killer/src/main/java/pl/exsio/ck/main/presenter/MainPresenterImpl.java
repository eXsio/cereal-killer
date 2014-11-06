
package pl.exsio.ck.main.presenter;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import pl.exsio.ck.browser.view.BrowserFrame;
import pl.exsio.ck.comparator.EntryComparator;
import pl.exsio.ck.entrytable.presenter.EntryTablePresenter;
import pl.exsio.ck.entrytable.presenter.EntryTablePresenterImpl;
import pl.exsio.ck.entrytable.view.EntryTablePanel;
import pl.exsio.ck.importer.EntryImporter;
import pl.exsio.ck.main.app.App;
import pl.exsio.ck.main.view.MainFrame;

/**
 *
 * @author exsio
 */
public class MainPresenterImpl implements MainPresenter {

    private final MainFrame view;

    private EntryImporter importer;

    private EntryComparator comparator;

    public MainPresenterImpl(MainFrame view) {
        this.view = view;
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
                App.log("Podczas porównania wystąpił błąd");
                App.log(ExceptionUtils.getMessage(ex));
            }
            this.view.setEnabled(true);
        }
    }

    @Override
    public void showBrowseWindow() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                BrowserFrame browser = new BrowserFrame();
                EntryTablePanel panel = new EntryTablePanel();
                EntryTablePresenter presenter = new EntryTablePresenterImpl(panel);
                panel.setPresenter(presenter);
                panel.showEntries(App.getEntryDao().findAll());
                browser.setLayout(new BorderLayout());
                browser.add(panel);
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

}
