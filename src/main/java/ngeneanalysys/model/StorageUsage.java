package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-03-21
 */
public class StorageUsage {
    private Long totalSpace;
    private Long freeSpace;
    private Long availableSampleCount;

    /**
     * @return totalSpace
     */
    public Long getTotalSpace() {
        return totalSpace;
    }

    /**
     * @return freeSpace
     */
    public Long getFreeSpace() {
        return freeSpace;
    }

    /**
     * @return availableSampleCount
     */
    public Long getAvailableSampleCount() {
        return availableSampleCount;
    }
}
