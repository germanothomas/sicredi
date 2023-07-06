package germano.thomas.sicredienqueteservidor.controller.bean;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AbreSessaoVotacaoBean(
        @NotNull(message = "pautaId é obrigatório")
        Long pautaId,

        @Positive(message = "duracaoMinutos deve ter um valor positivo.")
        Integer duracaoMinutos
) {
}
