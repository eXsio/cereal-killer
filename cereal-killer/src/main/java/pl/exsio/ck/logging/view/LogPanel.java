
package pl.exsio.ck.logging.view;

import javax.swing.JTextArea;
import pl.exsio.ck.logging.presenter.LogPresenter;

/**
 *
 * @author exsio
 */
public class LogPanel extends javax.swing.JPanel {

    private LogPresenter presenter;

    /**
     * Creates new form LogPanel
     */
    public LogPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        logArea = new javax.swing.JTextArea();
        cleanBtn = new javax.swing.JButton();

        logArea.setEditable(false);
        logArea.setColumns(20);
        logArea.setRows(5);
        logArea.setName("logArea"); // NOI18N
        jScrollPane1.setViewportView(logArea);

        cleanBtn.setText("Wyczyść");
        cleanBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cleanBtn)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cleanBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cleanBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanBtnActionPerformed
        this.clean();
    }//GEN-LAST:event_cleanBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cleanBtn;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea logArea;
    // End of variables declaration//GEN-END:variables

    public JTextArea getLogArea() {
        return logArea;
    }

    public void setPresenter(LogPresenter presenter) {
        this.presenter = presenter;
    }

    public void log(String msg) {
        this.presenter.log(msg);
    }

    public void clean() {
        this.presenter.clean();
    }

}
