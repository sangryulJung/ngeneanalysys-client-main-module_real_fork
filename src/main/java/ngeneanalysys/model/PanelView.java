package ngeneanalysys.model;

import java.util.List;

/**
 * @author Jang
 * @since 2018-01-08
 */
public class PanelView extends Panel {

    private List<Integer> memberGroupIds;

    private List<Integer> diseaseIds;

    private List<VirtualPanelSummary> virtualPanelSummaries;

    /**
     * @return memberGroupIds
     */
    public List<Integer> getMemberGroupIds() {
        return memberGroupIds;
    }

    /**
     * @return diseaseIds
     */
    public List<Integer> getDiseaseIds() {
        return diseaseIds;
    }

    /**
     * @return virtualPanelSummaries
     */
    public List<VirtualPanelSummary> getVirtualPanelSummaries() {
        return virtualPanelSummaries;
    }

    @Override
    public String toString() {
        return "PanelView{" +
                "memberGroupIds=" + memberGroupIds +
                ", diseaseIds=" + diseaseIds +
                ", virtualPanelSummaries=" + virtualPanelSummaries +
                '}';
    }
}
