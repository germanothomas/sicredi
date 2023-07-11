package germano.thomas.sicredienqueteservidor.controller.bean;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cont�m os dados necess�rios para a abertura de uma nova sess�o de vota��o.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbreSessaoVotacaoBean {
        @NotNull(message = "pautaId � obrigat�rio")
        private Long pautaId;

        @Positive(message = "duracaoMinutos deve ter um valor positivo.")
        private Integer duracaoMinutos;
}
