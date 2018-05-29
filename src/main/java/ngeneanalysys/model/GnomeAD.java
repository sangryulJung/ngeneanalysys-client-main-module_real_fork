package ngeneanalysys.model;

import java.math.BigDecimal;

/**
 * @author Jang
 * @since 2018-01-17
 */
public class GnomeAD {
    private BigDecimal all;
    private BigDecimal admixedAmerican;
    private BigDecimal africanAfricanAmerican;
    private BigDecimal eastAsian;
    private BigDecimal finnish;
    private BigDecimal nonFinnishEuropean;
    private BigDecimal others;
    private BigDecimal southAsian;

    public BigDecimal getAll() {
        return all;
    }

    public BigDecimal getAdmixedAmerican() {
        return admixedAmerican;
    }

    public BigDecimal getAfricanAfricanAmerican() {
        return africanAfricanAmerican;
    }

    public BigDecimal getEastAsian() {
        return eastAsian;
    }

    public BigDecimal getFinnish() {
        return finnish;
    }

    public BigDecimal getNonFinnishEuropean() {
        return nonFinnishEuropean;
    }

    public BigDecimal getOthers() {
        return others;
    }

    public BigDecimal getSouthAsian() {
        return southAsian;
    }
}
