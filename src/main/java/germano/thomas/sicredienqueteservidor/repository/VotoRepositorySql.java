package germano.thomas.sicredienqueteservidor.repository;

/**
 * Agrega os SQLs do {@link VotoRepository} para facilitar a leitura do código.
 */
public class VotoRepositorySql {
    private VotoRepositorySql() {
        // classe utilitária
    }

    static final String COUNT_VOTOS_PAUTA = """
WITH VotosSim AS (
    SELECT v.id_item id_item, count(v.valor) quantidade
    FROM Voto v
        INNER JOIN Item i ON i.id = v.id_item
    WHERE i.id_pauta = :idPauta
        AND v.valor = TRUE
    GROUP BY v.id_item
),
VotosNao AS (
    SELECT v.id_item id_item, count(v.valor) quantidade
    FROM Voto v
        INNER JOIN Item i ON i.id = v.id_item
    WHERE i.id_pauta = :idPauta
        AND v.valor = FALSE
    GROUP BY v.id_item
)
SELECT i.id idItem, COALESCE(vs.quantidade, 0) votosSim, COALESCE(vn.quantidade, 0) votosNao
FROM Item i
LEFT JOIN VotosSim vs ON vs.id_item = i.id
LEFT JOIN VotosNao vn ON vn.id_item = i.id
WHERE i.id_pauta = :idPauta
""";
}
