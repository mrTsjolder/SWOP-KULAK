package init;

import UI.swingGUI.MainFrame;
import controller.HandlerFactory;
import domain.Company;
import domain.time.Clock;
import domain.user.Acl;
import domain.user.Auth;
import domain.user.Role;
import java.io.FileReader;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Mathias :)
 */
public class Bootstrap {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        Clock clock = new Clock();
        Company company = new Company();
        int option = JOptionPane.showConfirmDialog(null, "Would you like to initialize the system with an input file?");
        
        if (option == 0) {
            initManagerFromFile(clock, company);
        } else if (option == 2) {
            return;
        }

        Auth auth = new Auth(company);
        Acl acl = initAcl();
        
        HandlerFactory factory = new HandlerFactory(company, auth, acl, clock);
//        //display uncaught exceptions
//        Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e) -> {
//            JOptionPane.showMessageDialog(null, e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
//        });
       
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new MainFrame(factory).setVisible(true);
        });
    }

    private static Acl initAcl() {
        Acl acl = new Acl();
        acl.addEntry(Role.ADMIN, Arrays.asList("UpdateTaskStatus", "CreateProject", "PlanTask", "RunSimulation", "CreateTask", "CreateTaskSimulator", "PlanTaskSimulator", "updateTaskStatus", "DelegateTask"));
        acl.addEntry(Role.DEVELOPER, Arrays.asList("UpdateTaskStatus"));
        acl.addEntry(Role.MANAGER, Arrays.asList("CreateTask", "CreateProject", "PlanTask", "RunSimulation", "CreateTask", "CreateTaskSimulator", "PlanTaskSimulator", "DelegateTask"));
        return acl;
    }

    /**
     * Show an input file dialog and initialize the given manager from the
     * chosen file.
     */
    private static void initManagerFromFile(Clock clock, Company db) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Task Man inputfile", "tman");
        chooser.setFileFilter(filter);
        
        //Solve the chooser deadlock bug
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try (FileReader fileReader = new FileReader(chooser.getSelectedFile())) {
                FileInitializor fileInitializor = new FileInitializor(fileReader, clock, db);
                fileInitializor.processFile();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "An error occured while reading/processing the file, please try again.", null, JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
