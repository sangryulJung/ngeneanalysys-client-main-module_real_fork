package ngeneanalysys.model;

import java.util.List;

/**
 * @author Jang
 * @since 2018-03-08
 */
public class ServerFileInfo {
    private ServerFile parent;
    private List<ServerFile> child;

    /**
     * @return parent
     */
    public ServerFile getParent() {
        return parent;
    }

    /**
     * @return child
     */
    public List<ServerFile> getChild() {
        return child;
    }
}
