package germano.thomas.sicredienqueteservidor.controller.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record VotaBean(
        @NotNull(message = "idAssociado é obrigatório")
        Long idAssociado,

        @NotNull(message = "idItem é obrigatório")
        Long idItem,

        @NotNull(message = "valor é obrigatório")
        @JsonProperty("valor")
        @Schema(allowableValues = {"true", "false"})
        Boolean valor
) {
}
