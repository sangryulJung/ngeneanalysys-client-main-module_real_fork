package ngeneanalysys.model;

public class ClinicalDatabaseExistence {
    private String cosmicExistence;
    private String clinVarExistence;

    public String getCosmicExistence() {
        return cosmicExistence;
    }

    public void setCosmicExistence(String cosmicExistence) {
        this.cosmicExistence = cosmicExistence;
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
                ", clinVarExistence='" + clinVarExistence + '\'' +
                '}';
    }
}
