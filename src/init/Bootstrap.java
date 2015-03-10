package init;

import UI.swingGUI.MainFrame;
import controller.FrontController;
import domain.ProjectManager;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Mathias
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

        int option = JOptionPane.showConfirmDialog(null, "Would you like to initialize the system with an input file?");
        ProjectManager manager = new ProjectManager();
        if (option == 0) {
            initManagerFromFile(manager);
        } else if (option == 2) {
            return;
        }

        FrontController controller = new FrontController(manager);
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new MainFrame(controller).setVisible(true);
        });
    }
    
    /**
     * Show an input file dialog and initialize the given manager from the chosen
     * file.
     * 
     * @param manager The manager to initialize
     */
    private static void initManagerFromFile(ProjectManager manager) {
        
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Task Man inputfile", "tman");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            try (FileReader fileReader = new FileReader(chooser.getSelectedFile())) {

                ProjectManagerFileInitializor fileInitializor = new ProjectManagerFileInitializor(fileReader, manager);
                fileInitializor.processFile();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "An error occured while reading/processing the file, please try again.", null, JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
}