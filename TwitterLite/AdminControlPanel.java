import sun.reflect.generics.tree.Tree;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminControlPanel {

    private JFrame adminCtrFrame;
    private JPanel primary, left, right, rightUpper, rightLower;
    private JTextField userID, groupID;
    private JScrollPane treeView_scrollPane;
    private JTree tree;
    private TreeNodeAdapter root;
    private UserGroup ugRoot = new UserGroup("Root");
    private JButton addUser, addGroup, showUserTotal, showGroupTotal, showMessageTotal, showPositivePercent, openUserView;
    
    public AdminControlPanel() {

        root = new TreeNodeAdapter(ugRoot);
        tree = new JTree(root);
        tree.addTreeSelectionListener(new SelectionListener());

        adminCtrFrame = new JFrame("Admin Control Panel");
        adminCtrFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // ======================= JPanel Instantiation =======================
        primary = new JPanel();
        left = new JPanel(); // contains ONLY the TreeView
        left.setPreferredSize(new Dimension(200,350));
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
        treeView_scrollPane = new JScrollPane(tree);
        treeView_scrollPane.setHorizontalScrollBar(treeView_scrollPane.createHorizontalScrollBar());
        treeView_scrollPane.setVerticalScrollBar(treeView_scrollPane.createVerticalScrollBar());

        left.add(treeView_scrollPane);
        // ====================================================================
        
        // =================== Right Upper Panel Components ===================
        userID = new JTextField();
        addUser = new JButton("Add User");
        groupID = new JTextField();
        addGroup = new JButton("Add Group");
        openUserView = new JButton("Open User View");

        addUser.addActionListener(new addUserAL());
        addGroup.addActionListener(new addGroupAL());
        openUserView.addActionListener(new openUserViewAL());

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

        showUserTotal.addActionListener(new showUserTotalAL());
        showGroupTotal.addActionListener((new showGroupTotalAL()));
        showMessageTotal.addActionListener(new showMessageTotalAL());
        showPositivePercent.addActionListener(new showPositivePercentAL());

        rightLower.add(showUserTotal);
        rightLower.add(showGroupTotal);
        rightLower.add(showMessageTotal);
        rightLower.add(showPositivePercent);
        // ====================================================================
        
        adminCtrFrame.setResizable(false); // prevents the frame from being resized by the users
        adminCtrFrame.getContentPane().add(primary);
        adminCtrFrame.pack();
        adminCtrFrame.setVisible(true);
    }

    public class addUserAL implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            //root.add(new TreeNodeAdapter(ugRoot.spawnUser(userID.getText())));
            ugRoot.spawnUser(userID.getText());
            //ugRoot.acceptVisitor(new TestVisitor());
        }
    }

    public class addGroupAL implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            //root.add(new TreeNodeAdapter(ugRoot.spawnUserGroup(groupID.getText())));
            ugRoot.spawnUserGroup(groupID.getText());
            //ugRoot.acceptVisitor(new TestVisitor());
        }
    }

    public class showUserTotalAL implements ActionListener {
        public void actionPerformed(ActionEvent event) {
        }
    }

    public class showGroupTotalAL implements ActionListener {
        public void actionPerformed(ActionEvent event) {
        }
    }

    public class showMessageTotalAL implements ActionListener {
        public void actionPerformed(ActionEvent event) {
        }
    }

    public class showPositivePercentAL implements ActionListener {
        public void actionPerformed(ActionEvent event) {
        }
    }

    public class openUserViewAL implements ActionListener {
        public void actionPerformed(ActionEvent event) {
        }
    }

    public class SelectionListener implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent se) {
            JTree tree = (JTree) se.getSource();
            TreeNodeAdapter selectedNode = (TreeNodeAdapter) tree.getLastSelectedPathComponent();
            String selectedNodeName = selectedNode.toString();
            if(selectedNode.isLeaf())
                System.out.println(selectedNodeName); // does nothing useful but shows listener is working
        }
    }
}