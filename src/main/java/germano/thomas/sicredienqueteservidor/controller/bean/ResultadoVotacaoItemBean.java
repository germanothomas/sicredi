package germano.thomas.sicredienqueteservidor.controller.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Contém o resultado de um item de uma pauta.
 */
public record ResultadoVotacaoItemBean(
        Long totalVotos,
        @Schema(description = "Valor entre 0 e 100.")
        Long porcentagemAprovacao,

        @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
        LocalDateTime dataHoraContabilizacao
) {
}
