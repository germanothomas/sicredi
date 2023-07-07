package germano.thomas.sicredienqueteservidor.repository;

import germano.thomas.sicredienqueteservidor.domain.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório de {@link Voto}.
 */
@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {
    Optional<Voto> findOneByItemIdAndIdAssociado(Long itemId, Long idAssociado);

    @Query("SELECT count(1) FROM Voto v WHERE v.item.id = :idItem and v.valor = :valor")
    long countVotos(@Param("idItem") Long idItem, @Param("valor") Boolean valor);
}
