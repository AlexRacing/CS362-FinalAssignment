import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Queue;
import javax.swing.*;

public class UserView implements IObserver {

    // ================================== GUI =================================
    private JFrame userViewFrame;
    private JPanel primary, top, middleTop, middleBottom, bottom;
    private JTextField userID;
    private JScrollPane tweetMessage_scroll, currentFollowing_scroll, newsFeed_scroll;
    private JList currentFollowing, newsFeed;
    private JTextArea tweetMessage;
    private JButton followUser, postTweet;
    // ========================================================================

    private MessageAggregationVisitor feedVisitor;
    private User currentUser;
    private DefaultListModel<User> userListModel = new DefaultListModel<>();
    private DefaultListModel<Message> messageListModel = new DefaultListModel<>();
    
    public UserView(AbstractUser p_currentUser) {
        currentUser = (User) p_currentUser;

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
        top.setLayout(new GridLayout(1, 2, 10 ,10));
        middleBottom.setLayout(new GridLayout(1, 2, 10 ,10));
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
        currentFollowing_scroll.setPreferredSize(new Dimension(200,100));
        currentFollowing_scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        currentFollowing_scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        
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
        newsFeed_scroll.setPreferredSize(new Dimension(250,200));
        newsFeed_scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        newsFeed_scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        bottom.add(newsFeed_scroll);
        // ====================================================================
        
        userViewFrame.setResizable(false); // prevents the frame from being resized by the users
        userViewFrame.getContentPane().add(primary);
        userViewFrame.pack();
        userViewFrame.setVisible(true);

        // ===================== CREATE LIST OF FOLLOWERS =====================
        updateFollows();
        currentUser.attachObserver(this);
        // ====================================================================

        // ======================= CREATE LIST OF FEEDS =======================

        feedVisitor = new FollowingAggregationFilter(currentUser, new SimpleAggregationVisitor());

        currentUser.acceptVisitor(feedVisitor);
        currentUser.getFollowing().forEach(feedVisitor::visit);

        updateFeed();

        feedVisitor.attachObserver(this);

        //feedVisitor.stopObserving(); // If done
        // ====================================================================
    }

    public class followUserAL implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            if (!(currentUser instanceof User)) return;

            String findText = userID.getText();
            AbstractUser match;

            try {
                int findID = Integer.parseInt(findText);
                match = LookupEngine.getInstance().getUser(findID);//root.getUserByID(findID);
            } catch (NumberFormatException e) {
                match = LookupEngine.getInstance().getUser(findText);
            }

            if (match != null && match instanceof User) {
                ((User) currentUser).follow((User) match);
            }
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
            currentUser.spawnMessage(tweetMessage.getText());
        }
    }

    @Override
    public void update() {
        updateFollows();
        updateFeed();
    }

    @Override
    public void update(IObservable source) {
        if (source == currentUser) {
            updateFollows();
            currentUser.getFollowing().forEach(feedVisitor::visit);
            updateFeed();
        }
        if (source == feedVisitor) {
            updateFeed();
        }
    }

    @Override
    public void update(IObservable source, Object content) {
        if (source == currentUser && content instanceof User) {
            User newUser = ((User) content);
            userListModel.addElement(newUser);
            newUser.acceptVisitor(feedVisitor);
            updateFeed();
        }
        if (source == currentUser && content instanceof Message) {
            updateFeed();
        }
        if (source == feedVisitor) {
            updateFeed();
        }
    }

    private void updateFeed() {
        messageListModel.clear();
        Queue<Message> feed = feedVisitor.getFeed();
        while (!feed.isEmpty()) messageListModel.addElement(feed.remove());
    }

    private void updateFollows() {
        userListModel.clear();
        currentUser.getFollowing().forEach(userListModel::addElement);
    }

    protected void finalize() {
        feedVisitor.detachObserver(this);
        feedVisitor.stopObserving();
    }
}