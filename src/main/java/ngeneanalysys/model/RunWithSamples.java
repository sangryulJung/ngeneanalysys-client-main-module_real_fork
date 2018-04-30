package ngeneanalysys.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RunWithSamples {
    private Run run;
    private List<Sample> samples;

    @JsonProperty(required = false)
    public Run getRun() {
        return run;
    }

    public List<Sample> getSamples() {
        return samples;
    }
}
