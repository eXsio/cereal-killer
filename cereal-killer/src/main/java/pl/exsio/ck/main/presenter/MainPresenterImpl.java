package pl.exsio.ck.main.presenter;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;
import pl.exsio.ck.comparator.ComparisonResult;
import pl.exsio.ck.comparator.EntryComparator;
import pl.exsio.ck.entrytable.presenter.EntryTablePresenter;
import pl.exsio.ck.importer.EntryImporter;
import pl.exsio.ck.main.view.AbstractMainFrame;
import pl.exsio.ck.model.dao.EntryDao;
import pl.exsio.ck.model.reader.EntryReader;
import pl.exsio.ck.serialtable.presenter.SerialTablePresenter;
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

    private EntryReader reader;

    private EntryDao dao;

    @Override
    public void setView(AbstractMainFrame view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void showImportWindow(final boolean updateEnabled) {
        JFileChooser jfc = this.createFileChooser("Wczytaj plik do " + (updateEnabled ? "aktualizacji" : "importu"));
        int returnVal = jfc.showOpenDialog(view);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File file = jfc.getSelectedFile();
            this.view.setEnabled(false);
            Thread importThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    view.setEnabled(false);
                    importer.importFile(file, updateEnabled);
                    view.setEnabled(true);
                }
            });
            importThread.start();
            this.view.setEnabled(true);
        }
    }

    @Override
    public void showCompareWindow() {
        JFileChooser jfc = this.createFileChooser("Wczytaj plik aby porównać");
        int returnVal = jfc.showOpenDialog(view);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File file = jfc.getSelectedFile();
            this.view.setEnabled(false);
            Thread compare = new Thread(new Runnable() {

                @Override
                public void run() {
                    view.setEnabled(false);
                    ComparisonResult result = comparator.compareFile(file);
                    showCompareWindow(result);
                    view.setEnabled(true);
                }
            });
            compare.start();
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
                presenter.showEntries();
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

    private void showCompareWindow(final ComparisonResult result) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                AbstractFrame browser = getBrowserFrame();
                JTabbedPane tabs = new JTabbedPane();

                EntryTablePresenter entryPresenter = getEntryTablePresenter();
                entryPresenter.showEntries(result.getFound());

                SerialTablePresenter serialPresenter = getSerialTablePresenter();
                serialPresenter.showSerials(result.getNotFound());

                browser.setLayout(new BorderLayout());
                tabs.add("Znalezione", entryPresenter.getView());
                tabs.add("Nieznalezione", serialPresenter.getView());
                browser.add(tabs);
                browser.setTitle("Porównaj wpisy");
                browser.pack();
                browser.showOnScreen(0);
                browser.setVisible(true);
            }
        });
    }

    private JFileChooser createFileChooser(String title) {
        JFileChooser jfc = new JFileChooser();
        jfc.setDialogTitle(title);
        jfc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return this.isExtensionValid(f) || f.isDirectory();
            }

            private boolean isExtensionValid(File file) {
                for (String extension : reader.getAcceptedFormats().keySet()) {
                    if (file.getName().toLowerCase().endsWith("." + extension)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public String getDescription() {
                StringBuilder sb = new StringBuilder();
                for (String desc : reader.getAcceptedFormats().values()) {
                    sb.append(desc).append(", ");
                }
                return sb.substring(0, sb.length() - 2);
            }
        });
        jfc.setCurrentDirectory(new File("."));
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

    public void setReader(EntryReader reader) {
        this.reader = reader;
    }

}
