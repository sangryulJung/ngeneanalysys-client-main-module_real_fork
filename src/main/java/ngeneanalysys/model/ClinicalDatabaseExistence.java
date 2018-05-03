package ngeneanalysys.model;

public class ClinicalDatabaseExistence {
    private String cosmicExistence;
    private String oncoKBExistence;
    private String clinVarExistence;

    public String getCosmicExistence() {
        return cosmicExistence;
    }

    public void setCosmicExistence(String cosmicExistence) {
        this.cosmicExistence = cosmicExistence;
    }

    public String getOncoKBExistence() {
        return oncoKBExistence;
    }

    public void setOncoKBExistence(String oncoKBExistence) {
        this.oncoKBExistence = oncoKBExistence;
    }

    public String getClinVarExistence() {
        return clinVarExistence;
    }

    public void setClinVarExistence(String clinVarExistence) {
        this.clinVarExistence = clinVarExistence;
    }

    @Override
    public String toString() {
        return "ClinicalDatabaseExistence{" +
                "cosmicExistence='" + cosmicExistence + '\'' +
                ", oncoKBExistence='" + oncoKBExistence + '\'' +
                ", clinVarExistence='" + clinVarExistence + '\'' +
                '}';
    }
}
