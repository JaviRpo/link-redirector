package me.javirpo.linkredirector.storage;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileModelUtils {
    public static final String USER_HOME_FOLDER = "user.home";
    public static final String APP_FOLDER = ".link-redirector";
    public static final String LINKS_FOLDER = "links";

    private static final File HOME_DIR = new File(System.getProperty(USER_HOME_FOLDER));
    private static final File APP_DIR = new File(HOME_DIR, APP_FOLDER);
    private static final File LINKS_DIR = new File(APP_DIR, LINKS_FOLDER);

    public static File getLinksDir() {
        return LINKS_DIR;
    }

    public static File getLinkFile(String linkId) {
        return new File(getLinksDir(), linkId + ".json");
    }
}
