package germano.thomas.sicredienqueteservidor.controller.bean;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Contém os dados necessários para a abertura de uma nova sessão de votação.
 */
public record AbreSessaoVotacaoBean(
        @NotNull(message = "pautaId é obrigatório")
        Long pautaId,

        @Positive(message = "duracaoMinutos deve ter um valor positivo.")
        Integer duracaoMinutos
) {
}
