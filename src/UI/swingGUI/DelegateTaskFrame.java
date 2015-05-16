package UI.swingGUI;

import controller.DelegateTaskHandler;
import domain.dto.DetailedResource;
import domain.dto.DetailedResourceType;
import domain.dto.DetailedTask;
import java.awt.CardLayout;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * This jFrame handles the plan task status use case
 *
 * @author Frederic, Mathias, Pieter-Jan
 */
public class DelegateTaskFrame extends javax.swing.JFrame {

    private static final long serialVersionUID = -7570577954812578587L;

    private final DelegateTaskHandler handler;
    private DefaultTableModel taskModel;
    private LocalDateTime start;
    private int selectedTaskId, selectedProjectId;
    private List<JComboBox<DetailedResource>> selectedResources;

    /**
     * Creates new form ListProjectsFrame
     *
     * @param handler The use case handler to use to pass the requests to.
     */
    public DelegateTaskFrame(DelegateTaskHandler handler) {
        this.handler = handler;
        initComponents();
        initAvailableTaskTable();
    }

    /**
     * Fills the available task table with the appropriate data
     */
    private void initAvailableTaskTable() {
        String[] columnNames = {"Id", "Description", "Estimated Duration", "Acceptable Deviation", "Project id", "Project"};
        List<DetailedTask> tasks = handler.getUnplannedTasks();
        Object[][] data = new Object[tasks.size()][];

        int i = 0;
        for (DetailedTask task : tasks) {
            data[i] = new Object[]{
                task.getId(),
                task.getDescription(),
                task.getEstimatedDuration(),
                task.getAcceptableDeviation() + "%",
                task.getProject().getId(),
                task.getProject().getName()
            };
            i++;
        }

        taskModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        availableTaskTable.setModel(taskModel);
        availableTaskTable.getColumnModel().getColumn(0).setMaxWidth(50);
    }
    
      /**
     * Fills the available task table with the appropriate data
     */
    private void initDeveloperTable(Set<DetailedResource> devs) {
        String[] columnNames = {"Id", "Name"};
        Object[][] data = new Object[devs.size()][];

        int i = 0;
        for (DetailedResource dev : devs) {
            data[i] = new Object[]{
                dev.getId(),
                dev.getName(),
            };
            i++;
        }

        DefaultTableModel devModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane3 = new javax.swing.JScrollPane();
        availableTaskTable = new javax.swing.JTable();
        selectTaskButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        delegateButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        officeList = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        mainPanel.setLayout(new java.awt.CardLayout());

        jLabel2.setFont(jLabel2.getFont().deriveFont(jLabel2.getFont().getSize()+15f));
        jLabel2.setText("Select Task");

        availableTaskTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(availableTaskTable);

        selectTaskButton.setText("Select Task");
        selectTaskButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectTaskButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(selectTaskButton, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(282, 282, 282)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addComponent(selectTaskButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37))
        );

        mainPanel.add(jPanel1, "card3");

        delegateButton.setText("Delegate");
        delegateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delegateButtonActionPerformed(evt);
            }
        });

        jLabel3.setFont(jLabel3.getFont().deriveFont(jLabel3.getFont().getSize()+15f));
        jLabel3.setText("Delegate Task");

        jLabel9.setFont(new java.awt.Font("Calibri", 0, 14)); // NOI18N
        jLabel9.setText("Select a branch office below:");

        jScrollPane1.setViewportView(officeList);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 11, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 708, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(200, 200, 200))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(252, 252, 252)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(283, 283, 283)
                        .addComponent(delegateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(delegateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        mainPanel.add(jPanel2, "selectTime");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 753, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * The select task button is pressed in the available tasks list overview
     *
     * @param evt
     */
    private void selectTaskButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectTaskButtonActionPerformed
        try {
            selectedTaskId = (int) taskModel.getValueAt(availableTaskTable.convertRowIndexToModel(availableTaskTable.getSelectedRow()), 0);
            selectedProjectId = (int) taskModel.getValueAt(availableTaskTable.convertRowIndexToModel(availableTaskTable.getSelectedRow()), 4);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, "Please select a task.", null, JOptionPane.ERROR_MESSAGE);
        }

        initTimeList();
        CardLayout card = (CardLayout) mainPanel.getLayout();
        card.show(mainPanel, "selectTime");


    }//GEN-LAST:event_selectTaskButtonActionPerformed

    protected void initTimeList() {
        // init possible start times
        DefaultListModel listModel = new DefaultListModel();
        for (LocalDateTime time : handler.getPossibleStartTimesCurrentTask(selectedProjectId, selectedTaskId)) {
            listModel.addElement(time);
        }
        
        officeList.setModel(listModel);
    }

    /**
     * The update task button is pressed in the task update form
     *
     * @param evt
     */
    private void delegateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delegateButtonActionPerformed

        try {
           
            if (officeList.getSelectedValue() != null) {
                //start = LocalDateTime.parse((String) timeList.getSelectedValue(), formatter);
            } 
            selectedResources = new ArrayList<>();
            // init resource panel
            for (Map.Entry<DetailedResourceType, DetailedResource> entry : handler.getRequiredResources(selectedProjectId, selectedTaskId, start)) {
                if(entry.getKey().getName().equalsIgnoreCase("developer")){
                    initDeveloperTable((Set<DetailedResource>) entry.getKey().getResources());
                    continue;
                }
                JComboBox<DetailedResource> comboBox = new JComboBox();
                comboBox.setModel(new javax.swing.DefaultComboBoxModel(entry.getKey().getResources().toArray()));
                comboBox.setSelectedItem(entry.getValue());
                comboBox.setPreferredSize(new java.awt.Dimension(200, 30));
                
                
                selectedResources.add(comboBox);

            }

           
            CardLayout card = (CardLayout) mainPanel.getLayout();
            card.show(mainPanel, "selectResources");
        } catch (DateTimeException e) {
            JOptionPane.showMessageDialog(rootPane, "The given time is not in the right format.", null, JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_delegateButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable availableTaskTable;
    private javax.swing.JButton delegateButton;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JList officeList;
    private javax.swing.JButton selectTaskButton;
    // End of variables declaration//GEN-END:variables

}
