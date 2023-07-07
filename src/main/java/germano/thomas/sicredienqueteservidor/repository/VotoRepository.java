package germano.thomas.sicredienqueteservidor.repository;

import germano.thomas.sicredienqueteservidor.domain.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório de {@link Voto}.
 */
@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {
    Optional<Voto> findOneByItemIdAndIdAssociado(Long itemId, Long idAssociado);
}
