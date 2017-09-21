package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2017-09-20
 */
public class VariantStatistics {
    private Integer totalSampleCountInRun;
    private Integer sameVariantSampleCountInRun;
    private Integer totalSampleCountInMemberGroup;
    private Integer sameVariantSampleCountInMemberGroup;
    private Integer totalSamePanelSampleCountInMemberGroup;
    private Integer samePanelSameVariantSampleCountInMemberGroup;

    /**
     * @return totalSampleCountInRun
     */
    public Integer getTotalSampleCountInRun() {
        return totalSampleCountInRun;
    }

    /**
     * @param totalSampleCountInRun
     */
    public void setTotalSampleCountInRun(Integer totalSampleCountInRun) {
        this.totalSampleCountInRun = totalSampleCountInRun;
    }

    /**
     * @return sameVariantSampleCountInRun
     */
    public Integer getSameVariantSampleCountInRun() {
        return sameVariantSampleCountInRun;
    }

    /**
     * @param sameVariantSampleCountInRun
     */
    public void setSameVariantSampleCountInRun(Integer sameVariantSampleCountInRun) {
        this.sameVariantSampleCountInRun = sameVariantSampleCountInRun;
    }

    /**
     * @return totalSampleCountInMemberGroup
     */
    public Integer getTotalSampleCountInMemberGroup() {
        return totalSampleCountInMemberGroup;
    }

    /**
     * @param totalSampleCountInMemberGroup
     */
    public void setTotalSampleCountInMemberGroup(Integer totalSampleCountInMemberGroup) {
        this.totalSampleCountInMemberGroup = totalSampleCountInMemberGroup;
    }

    /**
     * @return sameVariantSampleCountInMemberGroup
     */
    public Integer getSameVariantSampleCountInMemberGroup() {
        return sameVariantSampleCountInMemberGroup;
    }

    /**
     * @param sameVariantSampleCountInMemberGroup
     */
    public void setSameVariantSampleCountInMemberGroup(Integer sameVariantSampleCountInMemberGroup) {
        this.sameVariantSampleCountInMemberGroup = sameVariantSampleCountInMemberGroup;
    }

    /**
     * @return totalSamePanelSampleCountInMemberGroup
     */
    public Integer getTotalSamePanelSampleCountInMemberGroup() {
        return totalSamePanelSampleCountInMemberGroup;
    }

    /**
     * @param totalSamePanelSampleCountInMemberGroup
     */
    public void setTotalSamePanelSampleCountInMemberGroup(Integer totalSamePanelSampleCountInMemberGroup) {
        this.totalSamePanelSampleCountInMemberGroup = totalSamePanelSampleCountInMemberGroup;
    }

    /**
     * @return samePanelSameVariantSampleCountInMemberGroup
     */
    public Integer getSamePanelSameVariantSampleCountInMemberGroup() {
        return samePanelSameVariantSampleCountInMemberGroup;
    }

    /**
     * @param samePanelSameVariantSampleCountInMemberGroup
     */
    public void setSamePanelSameVariantSampleCountInMemberGroup(Integer samePanelSameVariantSampleCountInMemberGroup) {
        this.samePanelSameVariantSampleCountInMemberGroup = samePanelSameVariantSampleCountInMemberGroup;
    }
}
