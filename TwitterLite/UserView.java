import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class UserView {
    private JFrame userViewFrame;
    private JPanel primary, top, middleTop, middleBottom, bottom;
    private JTextField userID;
    private JScrollPane tweetMessage_scroll, currentFollowing_scroll, newsFeed_scroll;
    private JTextArea tweetMessage, currentFollowing, newsFeed;
    private JButton followUser, postTweet;

    private AbstractUser currentUser;
    
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
        currentFollowing = new JTextArea(10, 20);
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
        newsFeed = new JTextArea(10, 20);
        newsFeed_scroll = new JScrollPane(newsFeed);
        
        bottom.add(newsFeed);
        // ====================================================================
        
        userViewFrame.setResizable(false); // prevents the frame from being resized by the users
        userViewFrame.getContentPane().add(primary);
        userViewFrame.pack();
        userViewFrame.setVisible(true);
    }

    public class followUserAL implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            // currentUser.follow(); /* TODO: currentUser should be able to enter another User's ID and follow them */
        }
    }

    public class postTweetAL implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            //currentUser.spawnMessage(tweetMessage.getText());
        }
    }
}