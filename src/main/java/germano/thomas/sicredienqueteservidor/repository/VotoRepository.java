package germano.thomas.sicredienqueteservidor.repository;

import germano.thomas.sicredienqueteservidor.domain.Voto;
import germano.thomas.sicredienqueteservidor.domain.VotoAgrupadoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório de {@link Voto}.
 */
@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {
    Optional<Voto> findOneByItemIdAndIdAssociado(Long itemId, Long idAssociado);

    @Query("SELECT count(1) FROM Voto v WHERE v.item.id = :idItem and v.valor = :valor")
    long countVotosItem(@Param("idItem") Long idItem, @Param("valor") Boolean valor);

    @Query(value = VotoRepositorySql.COUNT_VOTOS_PAUTA, nativeQuery = true)
    List<VotoAgrupadoProjection> countVotosPauta(@Param("idPauta") Long idPauta);
}
