import com.sun.xml.internal.bind.v2.TODO;
import sun.reflect.generics.tree.Tree;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.ws.Action;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminControlPanel {

    private JFrame adminCtrFrame;
    private JPanel primary, left, right, rightUpper, rightLower;
    private JTextField userID, groupID;
    private JScrollPane treeView_scrollPane;
    private JTree tree;
    private TreeNodeAdapter root;
    private DefaultTreeModel model;
    private AbstractUser ugRoot = new UserGroup("Root");
    private final AbstractUser PERMANENT_ROOT = (UserGroup)ugRoot;
    private JButton addUser, addGroup, showUserTotal, showGroupTotal, showMessageTotal, showPositivePercent, openUserView;

    public AdminControlPanel() {

        root = new TreeNodeAdapter(ugRoot);
        tree = new JTree(root);
        tree.addTreeSelectionListener(new SelectionListener());
        model = (DefaultTreeModel)tree.getModel();

        adminCtrFrame = new JFrame("Admin Control Panel");
        adminCtrFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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
            String newName = userID.getText();

            // Do nothing if the name is taken
            if (UserFinder.getInstance().userNameTaken(newName)) {
                JOptionPane.showMessageDialog(adminCtrFrame, "Name taken, please choose another.");
                return;
            }

            try {
                ((UserGroup) ugRoot).spawnUser(newName);
            } catch(ClassCastException ccE) {
                JOptionPane.showMessageDialog(adminCtrFrame, "Cannot create a new User Group within a User.");
            }

            //ugRoot.acceptVisitor(new TestVisitor()); // TODO: remove this later

            try { model.reload(); } catch(NullPointerException npE) { System.out.println(""); }

            //try { model.reload(root); } catch(NullPointerException npE) { System.out.println(""); }
        }
    }

    public class addGroupAL implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            ((UserGroup)ugRoot).spawnUserGroup(groupID.getText());
            model.reload();

            try { model.reload(); } catch(NullPointerException npE) { System.out.println(""); }
        }
    }

    public class showUserTotalAL implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            JOptionPane.showMessageDialog(adminCtrFrame, getUserTotal(PERMANENT_ROOT));
        }

        private int getUserTotal(AbstractUser currentNode) {
            int userTotal = 0;
            for (int i = 0; i < currentNode.getChildCount(); i++) {
                if (((UserGroup) currentNode).getChildAt(i) instanceof User) {
                    userTotal++;
                }
                else if (((UserGroup) currentNode).getChildAt(i) instanceof UserGroup) {
                    userTotal += getUserTotal(((UserGroup) currentNode).getChildAt(i));
                }
            }

            return userTotal;
        }
    }

    public class showGroupTotalAL implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            JOptionPane.showMessageDialog(adminCtrFrame, getGroupTotal(PERMANENT_ROOT));
        }

        private int getGroupTotal(AbstractUser currentNode) {
            int groupTotal = 0;
            for (int i = 0; i < currentNode.getChildCount(); i++) {
                if (((UserGroup) currentNode).getChildAt(i) instanceof UserGroup) {
                    groupTotal++;
                    if(currentNode.getChildCount() > 0)
                        groupTotal += getGroupTotal(((UserGroup) currentNode).getChildAt(i));
                }
            }

            return groupTotal;
        }
    }

    public class showMessageTotalAL implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            /*
            int messageTotal = 0;
            for(int i = 0 ; i < PERMANENT_ROOT.getChildCount() ; i++) {
                if (((UserGroup)PERMANENT_ROOT).getChildAt(i) instanceof User) {
                    messageTotal += ((User) ((UserGroup)PERMANENT_ROOT).getChildAt(i)).getMessages().size();
                }
            }
            */

            JOptionPane.showMessageDialog(adminCtrFrame, getMessageTotal(PERMANENT_ROOT));
        }

        private int getMessageTotal(AbstractUser currentNode) {
            int messageTotal = 0;
            for (int i = 0; i < currentNode.getChildCount(); i++) {
                if (((UserGroup) currentNode).getChildAt(i) instanceof User) {
                    messageTotal += ((User) ((UserGroup)currentNode).getChildAt(i)).getMessages().size();
                }
                else if (((UserGroup) currentNode).getChildAt(i) instanceof UserGroup) {
                    messageTotal += getMessageTotal(((UserGroup) currentNode).getChildAt(i));
                }
            }

            return messageTotal;
        }
    }

    public class showPositivePercentAL implements ActionListener {
        private double positivity = 0;
        private SentimentEngine engine = SentimentEngine.getInstance();

        public void actionPerformed(ActionEvent event) {
            //SentimentEngine engine = SentimentEngine.getInstance();
            // basically the same as getting the messages except getting positivity from those messages
            JOptionPane.showMessageDialog(adminCtrFrame, getPositivity(PERMANENT_ROOT));
        }

        private double getPositivity(AbstractUser currentNode) {
            return positivity;
        }
    }

    public class openUserViewAL implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            //UserView uv = new UserView((User) ((TreeNodeAdapter) tree.getLastSelectedPathComponent()).getUserObject());
            //if((((TreeNodeAdapter) tree.getLastSelectedPathComponent()).getUserObject()) instanceof User)
            try {
                UserView uv = new UserView((User) ((TreeNodeAdapter) tree.getLastSelectedPathComponent()).getUserObject());
            } catch (ClassCastException ccE) {
                System.out.println("");
            }
        }
    }

    public class SelectionListener implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent se) {
            tree = (JTree) se.getSource();

            TreeNodeAdapter currentNode = (TreeNodeAdapter) tree.getLastSelectedPathComponent();

            // TODO: NullPointerException when adding a UserGroup to a UserGroup having clicked on the Destination UserGroup first
            ugRoot = (AbstractUser) currentNode.getUserObject();

            //System.out.println(tree.getLastSelectedPathComponent());
            //AbstractUser u = (AbstractUser) ((TreeNodeAdapter) tree.getLastSelectedPathComponent()).getUserObject(); // *******

            // TODO: fix having to close then open the 'subfolder' you're currently adding to to get the new nodes to show up.
        }
    }
}