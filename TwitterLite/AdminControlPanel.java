import java.awt.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminControlPanel {

    private JFrame adminCtrFrame;
    private JPanel primary, left, right, rightUpper, rightLower;
    private JTextField userID, groupID;
    private       JScrollPane           treeView_scrollPane;
    private final JTree                 TREE;
    private final TreeNodeAdapter       ROOT;
    private final DefaultTreeModel      MODEL;
    private final AbstractCompositeUser ROOT_UG;
    private       AbstractUser          currentSelection;
    private       JButton               addUser, addGroup, showUserTotal, showGroupTotal,
            showMessageTotal, showPositivePercent, openUserView;

    public AdminControlPanel() {

        ROOT_UG = new UserGroup("Root");
        currentSelection = ROOT_UG;
        ROOT = new TreeNodeAdapter(ROOT_UG);
        TREE = new JTree(ROOT);
        TREE.addTreeSelectionListener(new SelectionListener());
        MODEL = (DefaultTreeModel) TREE.getModel();

        adminCtrFrame = new JFrame("Admin Control Panel");
        adminCtrFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // ======================= JPanel Instantiation =======================
        primary = new JPanel();
        left = new JPanel(); // contains ONLY the TreeView
        left.setPreferredSize(new Dimension(200, 350));
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
        rightUpper.setLayout(new GridLayout(3, 2, 5, 5));
        rightLower.setLayout(new GridLayout(2, 2, 5, 5));
        // ====================================================================

        // ========================== Create Borders ==========================
        primary.setBorder(BorderFactory.createEtchedBorder());
        left.setBorder(BorderFactory.createEtchedBorder());
        right.setBorder(BorderFactory.createEtchedBorder());
        // ====================================================================

        // ====================== Left Panel Components =======================
        treeView_scrollPane = new JScrollPane(TREE);
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
            if (LookupEngine.getInstance().userNameTaken(newName)) {
                JOptionPane.showMessageDialog(adminCtrFrame, "Name taken, please choose another.");
                return;
            }

            try {
                ((UserGroup) currentSelection).spawnUser(newName);
            } catch (ClassCastException ccE) {
                JOptionPane.showMessageDialog(adminCtrFrame,
                                              "Cannot create a new User Group within a User.");
            }
            MODEL.reload();
        }
    }

    public class addGroupAL implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String newName = groupID.getText();

            // Do nothing if the name is taken
            if (LookupEngine.getInstance().userNameTaken(newName)) {
                JOptionPane.showMessageDialog(adminCtrFrame, "Name taken, please choose another.");
                return;
            }

            ((UserGroup) currentSelection).spawnUserGroup(newName);
            MODEL.reload();
        }
    }

    public class showUserTotalAL implements ActionListener {
        private final StatisticsTracker tracker = StatisticsTracker.getInstance();
        public void actionPerformed(ActionEvent event) {
            StringBuilder message = new StringBuilder();

            message.append("The current user count is: ")
                   .append(tracker.getTotalUsers()).append('.');

            JOptionPane.showMessageDialog(adminCtrFrame, message.toString());
        }
    }

    public class showGroupTotalAL implements ActionListener {
        private final StatisticsTracker tracker = StatisticsTracker.getInstance();
        public void actionPerformed(ActionEvent event) {
            StringBuilder message = new StringBuilder();

            message.append("The current user group count is: ")
                   .append(tracker.getTotalGroups()).append('.');

            JOptionPane.showMessageDialog(adminCtrFrame, message.toString());
        }
    }

    public class showMessageTotalAL implements ActionListener {
        private final StatisticsTracker tracker = StatisticsTracker.getInstance();
        public void actionPerformed(ActionEvent event) {
            StringBuilder message = new StringBuilder();

            message.append("The current message count is: ")
                   .append(tracker.getTotalMessages()).append('.');

            JOptionPane.showMessageDialog(adminCtrFrame, message.toString());
        }
    }

    public class showPositivePercentAL implements ActionListener {
        private final StatisticsTracker tracker = StatisticsTracker.getInstance();

        public void actionPerformed(ActionEvent event) {
            double[][]    split   = tracker.getRoughSentimentSplit();
            StringBuilder message = new StringBuilder();

            message.append("The current average sentiment score is: ")
                   .append(Math.round(tracker.sentimentTScore() * 100)).append('\n')
                   .append("Roughly ").append(Math.round(split[0][0] * 100))
                   .append("% of messages are positive.").append('\n')
                   .append("Roughly ").append(Math.round(split[0][1] * 100))
                   .append("% of messages are negative.");

            JOptionPane.showMessageDialog(adminCtrFrame, message.toString());
        }
    }

    public class openUserViewAL implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            try {
                UserView uv = new UserView(
                        (User) ((TreeNodeAdapter) TREE.getLastSelectedPathComponent())
                                       .getUserObject());
            } catch (ClassCastException ccE) {}
        }
    }

    public class SelectionListener implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent se) {
            TreeNodeAdapter currentNode = (TreeNodeAdapter) TREE.getLastSelectedPathComponent();

            if (currentNode != null) currentSelection = (AbstractUser) currentNode.getUserObject();
        }
    }

    void loadDefaults() {
        UserGroup root = (UserGroup) ROOT_UG;

        UserGroup admin  = root.spawnUserGroup("Admin");
        UserGroup cs     = root.spawnUserGroup("CS");
        UserGroup cs3620 = cs.spawnUserGroup("CS-3620");
        User      alex   = admin.spawnUser("Alex");
        User      allan  = admin.spawnUser("Allan");
        User      scott  = admin.spawnUser("Scott");
        User      adam  = cs.spawnUser("Adam");
        User      chris  = cs.spawnUser("Chris");
        User      drew   = cs.spawnUser("Drew");
        User      remah  = cs.spawnUser("Remah");
        User      will   = cs.spawnUser("Will");
        User      aaron   = cs3620.spawnUser("Aaron");
        User      bruce  = cs3620.spawnUser("Bruce");
        User      eric   = cs3620.spawnUser("Eric");
        User      sarah  = cs3620.spawnUser("Sarah");
        User      zeru   = cs3620.spawnUser("Zeru");

        alex.spawnMessage("I love COBOL!");
        allan.spawnMessage("You are wrong, and here's why...");
        scott.spawnMessage("*dances*");

        adam.spawnMessage("I hope my wife has triplets!");
        chris.spawnMessage("I love video games, I just wish I was any good :(");
        drew.spawnMessage("I think I'm going to delete my twitter account now.");
        remah.spawnMessage("Yeah this is so much better!");
        will.spawnMessage("I'm working on Theory right now...");
        bruce.spawnMessage("I hate twitter so much!");
        eric.spawnMessage("This is terrible...");
        aaron.spawnMessage("I am playing PokemonGo.");
        sarah.spawnMessage("This is a test message, I'm otherwise not participating.");
        zeru.spawnMessage("This class is awesome.");

        alex.follow(allan);
        alex.follow(scott);
        alex.follow(chris);
        alex.follow(will);
        alex.follow(aaron);

        allan.follow(alex);
        allan.follow(scott);
        allan.follow(eric);
        allan.follow(bruce);
        allan.follow(adam);
        allan.follow(chris);
        allan.follow(remah);
        allan.follow(drew);
        allan.follow(zeru);

        scott.follow(alex);
        scott.follow(allan);
        scott.follow(chris);
        scott.follow(will);
        scott.follow(aaron);

        chris.follow(alex);
        chris.follow(allan);
        chris.follow(scott);
        chris.follow(will);

        will.follow(alex);
        will.follow(allan);
        will.follow(scott);
        will.follow(chris);
        will.follow(adam);

        adam.follow(allan);
        adam.follow(will);

        sarah.follow(eric);

        eric.follow(sarah);

        remah.follow(drew);

        drew.follow(remah);

        bruce.follow(allan);
        bruce.follow(zeru);

        zeru.follow(allan);
        zeru.follow(bruce);

        alex.spawnMessage("I have come to my senses and now hate COBOL...");
        allan.spawnMessage("Clearly Fortran is better.");
        scott.spawnMessage("I only program in OhCrap.");
    }
}