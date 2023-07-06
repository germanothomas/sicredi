package germano.thomas.sicredienqueteservidor.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pauta {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        private String titulo;

        @OneToMany(mappedBy="pauta", cascade=CascadeType.ALL)
        private List<Item> itens = new ArrayList<>();
}
