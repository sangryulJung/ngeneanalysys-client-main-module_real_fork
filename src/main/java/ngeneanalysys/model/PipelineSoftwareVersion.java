package ngeneanalysys.model;

/**
 * @author Jang
 * @since 2018-08-01
 */
public class PipelineSoftwareVersion {
    private String dockerApp;
    private String brcaAccuTest;
    private String hemeAccuTest;
    private String solidAccuTest;
    private String heredAccuTest;

    /**
     * @return dockerApp
     */
    public String getDockerApp() {
        return dockerApp;
    }

    /**
     * @return brcaAccuTest
     */
    public String getBrcaAccuTest() {
        return brcaAccuTest;
    }

    /**
     * @return hemeAccuTest
     */
    public String getHemeAccuTest() {
        return hemeAccuTest;
    }

    /**
     * @return solidAccuTest
     */
    public String getSolidAccuTest() {
        return solidAccuTest;
    }

    /**
     * @return heredAccuTest
     */
    public String getHeredAccuTest() {
        return heredAccuTest;
    }
}
