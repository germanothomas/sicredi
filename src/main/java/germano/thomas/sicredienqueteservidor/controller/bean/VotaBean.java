package germano.thomas.sicredienqueteservidor.controller.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record VotaBean(
        @NotNull(message = "idAssociado � obrigat�rio")
        Long idAssociado,

        @NotNull(message = "idItem � obrigat�rio")
        Long idItem,

        @NotNull(message = "valor � obrigat�rio")
        @JsonProperty("valor")
        @Schema(allowableValues = {"true", "false"})
        Boolean valor
) {
}
