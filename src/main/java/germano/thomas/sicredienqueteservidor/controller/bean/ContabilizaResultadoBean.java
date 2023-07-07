package germano.thomas.sicredienqueteservidor.controller.bean;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Contém o resultado de um item de uma pauta.
 */
public record ContabilizaResultadoBean(
        Long totalVotos,
        @Schema(description = "Valor entre 0 e 100.")
        Long porcentagemAprovacao
) {
}
