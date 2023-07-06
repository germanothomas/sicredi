package germano.thomas.sicredienqueteservidor.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        @ManyToOne
        @JsonIgnore
        @JoinColumn(name="idPauta", nullable=false)
        private Pauta pauta;

        private TipoItem tipo;
        private String titulo;
        private String valor;
}
