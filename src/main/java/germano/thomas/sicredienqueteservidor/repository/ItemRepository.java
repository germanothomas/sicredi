package germano.thomas.sicredienqueteservidor.repository;

import germano.thomas.sicredienqueteservidor.controller.bean.ResultadoVotacaoItemBean;
import germano.thomas.sicredienqueteservidor.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repositório de {@link Item}.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT new germano.thomas.sicredienqueteservidor.controller.bean.ResultadoVotacaoItemBean( " +
            "i.totalVotos, i.porcentagemAprovacao, i.dataHoraContabilizacao) " +
            "FROM Item i WHERE i.id = :idItem ")
    ResultadoVotacaoItemBean findResultado(Long idItem);
}
