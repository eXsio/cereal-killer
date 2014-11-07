
package pl.exsio.ck.main.view;

import javax.swing.SwingWorker;
import pl.exsio.ck.logging.view.LogPanel;
import pl.exsio.ck.main.presenter.MainPresenter;
import pl.exsio.ck.view.AbstractFrame;

/**
 *
 * @author exsio
 */
public class MainFrame extends AbstractFrame {

    private MainPresenter presenter;

    private LogPanel logPanel;

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        this.showOnScreen(0);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        importBtn = new javax.swing.JButton();
        compareBtn = new javax.swing.JButton();
        closeBtn = new javax.swing.JButton();
        mainTabs = new javax.swing.JTabbedPane();
        updateBtn = new javax.swing.JButton();
        browseBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CerealKiller v1.0");
        setName("mainFrame"); // NOI18N
        setResizable(false);

        importBtn.setText("Importuj");
        importBtn.setName("importBtn"); // NOI18N
        importBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importBtnActionPerformed(evt);
            }
        });

        compareBtn.setText("Porównaj");
        compareBtn.setName("compareBtn"); // NOI18N
        compareBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compareBtnActionPerformed(evt);
            }
        });

        closeBtn.setText("Zamknij");
        closeBtn.setActionCommand("closeBtn");
        closeBtn.setName("closeBtn"); // NOI18N
        closeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeBtnActionPerformed(evt);
            }
        });

        mainTabs.setName("mainTabs"); // NOI18N

        updateBtn.setText("Aktualizuj");
        updateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateBtnActionPerformed(evt);
            }
        });

        browseBtn.setText("Przeglądaj");
        browseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mainTabs)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(importBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(updateBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(compareBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 195, Short.MAX_VALUE)
                        .addComponent(closeBtn)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(importBtn)
                    .addComponent(compareBtn)
                    .addComponent(closeBtn)
                    .addComponent(updateBtn)
                    .addComponent(browseBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(mainTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void importBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importBtnActionPerformed
        this.presenter.showImportWindow(false);
    }//GEN-LAST:event_importBtnActionPerformed

    private void compareBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_compareBtnActionPerformed
        this.presenter.showCompareWindow();
    }//GEN-LAST:event_compareBtnActionPerformed

    private void closeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeBtnActionPerformed
        this.presenter.exit();
    }//GEN-LAST:event_closeBtnActionPerformed

    private void updateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateBtnActionPerformed
        this.presenter.showImportWindow(true);
    }//GEN-LAST:event_updateBtnActionPerformed

    private void browseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseBtnActionPerformed
        this.presenter.showBrowseWindow();
    }//GEN-LAST:event_browseBtnActionPerformed

    public MainFrame setPresenter(MainPresenter presenter) {
        this.presenter = presenter;
        return this;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseBtn;
    private javax.swing.JButton closeBtn;
    private javax.swing.JButton compareBtn;
    private javax.swing.JButton importBtn;
    private javax.swing.JTabbedPane mainTabs;
    private javax.swing.JButton updateBtn;
    // End of variables declaration//GEN-END:variables

    public void setLogPanel(final LogPanel logPanel) {
        this.logPanel = logPanel;
        SwingWorker worker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                mainTabs.add("Log", logPanel);
                return null;
            }

        };
        worker.execute();
    }
}
