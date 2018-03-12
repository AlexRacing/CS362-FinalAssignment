import java.awt.*;
import javax.swing.*;

public class AdminControlPanel {
    private JFrame adminCtrFrame;
    private JPanel primary, left, right, rightUpper, rightLower;
    private JTextArea treeView;
    private JTextField userID, groupID;
    private JScrollPane treeView_scrollPane;
    private JButton addUser, addGroup, showUserTotal, showGroupTotal, showMessageTotal, showPositivePercent, openUserView;
    
    public AdminControlPanel() {
        adminCtrFrame = new JFrame("Admin Control Panel");
        adminCtrFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // ======================= JPanel Instantiation =======================
        primary = new JPanel();
        left = new JPanel(); // contains ONLY the TreeView
        right = new JPanel(); // contains two panels
        rightUpper = new JPanel();
        rightLower = new JPanel();
        // ====================================================================
        
        // =================== Add Subpanels and RigidArea ====================
        primary.add(left, BorderLayout.WEST);
        primary.add(right, BorderLayout.EAST);
        
        right.add(rightUpper);
        right.add(Box.createRigidArea(new Dimension(0, 178)));
        right.add(rightLower);
        // ====================================================================
        
        // ======================= Set Layout Managers ========================
        left.setLayout(new BorderLayout());
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        rightUpper.setLayout(new GridLayout(3, 2, 5 ,5));
        rightLower.setLayout(new GridLayout(2, 2, 5, 5));
        // ====================================================================
        
        // ========================== Create Borders ==========================
        primary.setBorder(BorderFactory.createEtchedBorder());
        left.setBorder(BorderFactory.createEtchedBorder());
        right.setBorder(BorderFactory.createEtchedBorder());
        // ====================================================================
        
        // ====================== Left Panel Components =======================
        treeView = new JTextArea(20,20);
        treeView_scrollPane = new JScrollPane(treeView);
        
        left.add(treeView_scrollPane);
        // ====================================================================
        
        // =================== Right Upper Panel Components ===================
        userID = new JTextField();
        addUser = new JButton("Add User");
        groupID = new JTextField();
        addGroup = new JButton("Add Group");
        openUserView = new JButton("Open User View");
        
        rightUpper.add(userID);
        rightUpper.add(addUser);
        rightUpper.add(groupID);
        rightUpper.add(addGroup);
        rightUpper.add(openUserView);
        //=====================================================================
        
        // =================== Right Lower Panel Components ===================
        showUserTotal = new JButton("Show User Total");
        showGroupTotal = new JButton("Show Group Total");
        showMessageTotal = new JButton("Show Message Total");
        showPositivePercent = new JButton("Show Positive Percentage");
        
        rightLower.add(showUserTotal);
        rightLower.add(showGroupTotal);
        rightLower.add(showMessageTotal);
        rightLower.add(showPositivePercent);
        // ====================================================================
        
        adminCtrFrame.setResizable(false); // prevents the frame from being resized by the user
        adminCtrFrame.getContentPane().add(primary);
        adminCtrFrame.pack();
        adminCtrFrame.setVisible(true);
    }
}