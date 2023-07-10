package germano.thomas.sicredienqueteservidor.service;

import org.springframework.stereotype.Service;

/**
 * Retorna informações relativas a associados.
 */
@Service
public class AssociadoService {
    // Cadastro de funcionário vai além do escopo da avaliação técnica, por isso um cadastro de mentira está sendo utilizado.
    private static final String[] CPF_FAKE_DATABASE = {"59962341060",
            "96319318031",
            "44384044011",
            "80829275002",
            "88027338050"};

    /**
     * Carrega o CPF de um associado de determinado id.
     */
    public String carregaCpf(Long idAssociado) {
        // Cadastro de funcionário vai além do escopo da avaliação técnica, por isso um cadastro de mentira está sendo utilizado.
        int posicaoFake = (int) (idAssociado % 5);

        return CPF_FAKE_DATABASE[posicaoFake];
    }
}
