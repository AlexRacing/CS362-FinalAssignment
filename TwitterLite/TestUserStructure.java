public class TestUserStructure {
    public static void main(String[] args) {
        UserGroup root = new UserGroup("Root");

        UserGroup level1 = root.spawnUserGroup("One");

        User uRoot = root.spawnUser("Chris Vessey");

        User uL1 = level1.spawnUser("Chris MacDonald");

        uL1.follow(uRoot);

        Message m1Root = uRoot.spawnMessage("The cake is a lie!");

        Message m1L1 = uL1.spawnMessage("I'm a different person than Vessey");

        root.acceptVisitor(new TestVisitor(System.out));
    }
}
