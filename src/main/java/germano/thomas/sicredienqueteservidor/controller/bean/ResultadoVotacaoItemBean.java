package germano.thomas.sicredienqueteservidor.controller.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Contém o resultado de um item de uma pauta.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoVotacaoItemBean {
        private Long totalVotos;
        @Schema(description = "Valor entre 0 e 100.")
        private Long porcentagemAprovacao;

        @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
        private LocalDateTime dataHoraContabilizacao;
}
