import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class UserView {

    // ================================== GUI =================================
    private JFrame userViewFrame;
    private JPanel primary, top, middleTop, middleBottom, bottom;
    private JTextField userID;
    private JScrollPane tweetMessage_scroll, currentFollowing_scroll, newsFeed_scroll;
    private JList currentFollowing, newsFeed;
    private JTextArea tweetMessage;
    private JButton followUser, postTweet;
    // ========================================================================

    private AbstractUser currentUser;
    private DefaultListModel<User> userListModel = new DefaultListModel<>();
    private DefaultListModel<Message> messageListModel = new DefaultListModel<>();
    
    public UserView(AbstractUser p_currentUser) {
        currentUser = p_currentUser;

        userViewFrame = new JFrame(currentUser.name);
        userViewFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        // ======================= JPanel Instantiation =======================
        primary = new JPanel();
        top = new JPanel();
        middleTop = new JPanel();
        middleBottom = new JPanel();
        bottom = new JPanel();
        // ====================================================================
        
        // =================== Add Subpanels and RigidArea ====================
        primary.add(top);
        primary.add(middleTop);
        primary.add(middleBottom);
        primary.add(bottom);
        // ====================================================================
        
        // ======================= Set Layout Managers ========================
        primary.setLayout(new BoxLayout(primary, BoxLayout.Y_AXIS));
        top.setLayout(new GridLayout(1, 2, 5 ,5));
        middleBottom.setLayout(new GridLayout(1, 2, 5 ,5));
        // ====================================================================
        
        // ========================== Create Borders ==========================
        primary.setBorder(BorderFactory.createEtchedBorder());
        top.setBorder(BorderFactory.createEtchedBorder());
        middleTop.setBorder(BorderFactory.createEtchedBorder());
        middleBottom.setBorder(BorderFactory.createEtchedBorder());
        bottom.setBorder(BorderFactory.createEtchedBorder());
        // ====================================================================
        
        // ========================== top Components ==========================
        userID = new JTextField();
        followUser = new JButton("Follow User");

        followUser.addActionListener(new followUserAL());
        
        top.add(userID);
        top.add(followUser);
        // ====================================================================
        
        // ======================= middleTop Components =======================
        currentFollowing = new JList<>(userListModel);
        currentFollowing_scroll = new JScrollPane(currentFollowing);
        
        middleTop.add(currentFollowing_scroll);
        // ====================================================================
        
        // ===================== middleBottom Components ======================
        tweetMessage = new JTextArea(5,10);
        postTweet = new JButton("Post Tweet");
        tweetMessage_scroll = new JScrollPane(tweetMessage);

        postTweet.addActionListener(new postTweetAL());

        middleBottom.add(tweetMessage_scroll);
        middleBottom.add(postTweet);
        // ====================================================================
        
        // ======================== bottom Components =========================
        newsFeed = new JList<>(messageListModel);
        newsFeed_scroll = new JScrollPane(newsFeed);
        
        bottom.add(newsFeed_scroll);
        // ====================================================================
        
        userViewFrame.setResizable(false); // prevents the frame from being resized by the users
        userViewFrame.getContentPane().add(primary);
        userViewFrame.pack();
        userViewFrame.setVisible(true);

        // ===================== CREATE LIST OF FOLLOWERS =====================
        userListModel.clear();
        for(int i = 0 ; i < ((User) currentUser).getFollowing().size() ; i++)
            userListModel.addElement(((User) currentUser).getFollowing().get(i));
        // ====================================================================

        // ======================= CREATE LIST OF FEEDS =======================
        messageListModel.clear();
        //userListModel.addElement(((User) currentUser));
        // ====================================================================
    }

    public class followUserAL implements ActionListener {

        private User match;

        public void actionPerformed(ActionEvent event) {
            if (!(currentUser instanceof User)) return;

            String findText = userID.getText();
            //AbstractCompositeUser root = currentUser.getRoot();
            AbstractUser match;

            try {
                int findID = Integer.parseInt(findText);
                match = LookupEngine.getInstance().getUser(findID);//root.getUserByID(findID);
            } catch (NumberFormatException e) {
                match = LookupEngine.getInstance().getUser(findText);
            }

            if (match != null && match instanceof User) {
                ((User) currentUser).follow((User) match);

                //for(int i = 0 ; i < ((User) currentUser).getFollowing().size() ; i++)
                    //currentFollowing.add(((User) currentUser).getFollowing().getUser(i).toString());
                // here
            }

            //((User) currentUser).getFollowing().forEach(System.out::println);
        }

        private User searchForName(String name, UserGroup root)
        {
            User result = null;

            for(int i = 0; i < root.getChildCount(); i++)
            {
                if (root.getChildAt(i) instanceof User)
                {
                    if (root.getChildAt(i).name.equals(name))
                        return (User)(root.getChildAt(i));
                }
                else if (result == null) {
                    result = searchForName(name, (UserGroup) root.getChildAt(i));
                }
            }

            return result;
        }
    }

    public class postTweetAL implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            ((User) currentUser).spawnMessage(tweetMessage.getText());
        }
    }
}