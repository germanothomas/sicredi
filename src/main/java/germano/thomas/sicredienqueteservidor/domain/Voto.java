package germano.thomas.sicredienqueteservidor.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Voto de um associado para determinado {@link Item}.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(NON_NULL)
public class Voto {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        private Long idAssociado;

        @ManyToOne
        @JsonIgnore
        @JoinColumn(name="idItem", nullable=false)
        private Item item;

        private Boolean valor;
}
