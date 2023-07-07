package germano.thomas.sicredienqueteservidor.repository;

import germano.thomas.sicredienqueteservidor.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório de {@link Item}.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}
