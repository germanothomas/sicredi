package germano.thomas.sicredienqueteservidor.repository;

import germano.thomas.sicredienqueteservidor.domain.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PautaRepository extends JpaRepository<Pauta, Long> {
}
