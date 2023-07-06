package germano.thomas.sicredienqueteservidor.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
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

        private String titulo;

        @OneToMany(mappedBy="pauta", cascade=CascadeType.ALL)
        private List<Item> itens = new ArrayList<>();

        @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
        LocalDateTime sessaoVotacaoInicio;
        @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
        LocalDateTime sessaoVotacaoFim;
}
