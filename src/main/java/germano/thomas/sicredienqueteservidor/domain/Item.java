package germano.thomas.sicredienqueteservidor.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Item de uma {@link Pauta}.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_NULL)
public class Item {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        @ManyToOne
        @JsonIgnore
        @JoinColumn(name="idPauta", nullable=false)
        private Pauta pauta;

        private TipoItem tipo;
        @NotNull(message = "titulo do item é obrigatório")
        private String titulo;
        private String valor;

        @OneToMany(mappedBy="item", cascade=CascadeType.ALL)
        @JsonIgnore
        private List<Voto> votos = new ArrayList<>();

		@JsonIgnore
        private Long totalVotos;
		@JsonIgnore
        private Long porcentagemAprovacao;
		@JsonIgnore
        private LocalDateTime dataHoraContabilizacao;
}
