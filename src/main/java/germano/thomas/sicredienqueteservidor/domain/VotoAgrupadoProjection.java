package germano.thomas.sicredienqueteservidor.domain;

/**
 * Proje��o utilizada para contabilizar vota��o.
 */
public interface VotoAgrupadoProjection {
    Long getIdItem();
    Long getVotosSim();
    Long getVotosNao();
}
