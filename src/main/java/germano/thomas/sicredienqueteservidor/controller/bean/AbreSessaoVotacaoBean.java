package germano.thomas.sicredienqueteservidor.controller.bean;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Cont�m os dados necess�rios para a abertura de uma nova sess�o de vota��o.
 */
public record AbreSessaoVotacaoBean(
        @NotNull(message = "pautaId � obrigat�rio")
        Long pautaId,

        @Positive(message = "duracaoMinutos deve ter um valor positivo.")
        Integer duracaoMinutos
) {
}
