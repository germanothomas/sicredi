package germano.thomas.sicredienqueteservidor.controller.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VotaBean {
        @NotNull(message = "idAssociado � obrigat�rio")
        private Long idAssociado;

        @NotNull(message = "idItem � obrigat�rio")
        private Long idItem;

        @NotNull(message = "valor � obrigat�rio")
        @JsonProperty("valor")
        @Schema(allowableValues = {"true", "false"})
        private Boolean valor;
}
