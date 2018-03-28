public class TestUserStructure {
    public static void main(String[] args) {
        UserGroup root = new UserGroup("Root");

        UserGroup level1 = root.spawnUserGroup("One");

        User userRoot = root.spawnUser("Chris Vessey");

        User user1 = level1.spawnUser("Chris MacDonald");

        root.acceptVisitor(new TestVisitor(out));
    }
}
