package ngeneanalysys.controller.extend;

import ngeneanalysys.controller.AnalysisDetailLayoutController;

/**
 * @author Jang
 * @since 2017-08-10
 */
public abstract class AnalysisDetailCommonController extends SubPaneController{
    /** 분석 상세 결과 레이아웃 컨트롤러 */
    protected AnalysisDetailLayoutController analysisDetailLayoutController;

    /**
     * 분석 상세 결과 레이아웃 컨트롤러 객체 반환
     * @return the analysisDetailLayoutController
     */
    public AnalysisDetailLayoutController getAnalysisDetailLayoutController() {
        return analysisDetailLayoutController;
    }

    /**
     * 분석 상세 결과 레이아웃 컨트롤러 설정
     * @param analysisDetailLayoutController the analysisDetailLayoutController to set
     */
    public void setAnalysisDetailLayoutController(AnalysisDetailLayoutController analysisDetailLayoutController) {
        setMainController(analysisDetailLayoutController.getMainController());
        this.analysisDetailLayoutController = analysisDetailLayoutController;
    }

}
