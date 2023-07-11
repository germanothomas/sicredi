package germano.thomas.sicredienqueteservidor.controller.bean;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Contém os dados necessários para a abertura de uma nova sessão de votação.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbreSessaoVotacaoBean {
        @NotNull(message = "pautaId é obrigatório")
        private Long pautaId;

        @Positive(message = "duracaoMinutos deve ter um valor positivo.")
        private Integer duracaoMinutos;
}
