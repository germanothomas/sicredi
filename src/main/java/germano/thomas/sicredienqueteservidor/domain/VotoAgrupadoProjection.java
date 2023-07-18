package germano.thomas.sicredienqueteservidor.domain;

/**
 * Projeção utilizada para contabilizar votação.
 */
public interface VotoAgrupadoProjection {
    Long getIdItem();
    Long getVotosSim();
    Long getVotosNao();
}
