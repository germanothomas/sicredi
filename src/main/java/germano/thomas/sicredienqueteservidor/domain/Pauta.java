package germano.thomas.sicredienqueteservidor.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Pauta de votação. Contém uma lista de {@link Item}.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_NULL)
public class Pauta {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        @NotNull(message = "titulo da pauta é obrigatório")
        private String titulo;

        @Valid
        @Size(min = 1, message="Uma pauta deve conter pelo menos 1 item.")
        @OneToMany(mappedBy="pauta", cascade=CascadeType.ALL)
        private List<Item> itens = new ArrayList<>();

        @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
        LocalDateTime sessaoVotacaoInicio;
        @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
        LocalDateTime sessaoVotacaoFim;
}
