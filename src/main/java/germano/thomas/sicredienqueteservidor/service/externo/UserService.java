package germano.thomas.sicredienqueteservidor.service.externo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import germano.thomas.sicredienqueteservidor.service.AssociadoService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Integra com um sistema que verifique, a partir do CPF do associado, se ele pode votar.
 */
@Service
@Slf4j
public class UserService {
    static final String STATUS_PODE_VOTAR = "ABLE_TO_VOTE";

    @Value("${externo.userService.urlBase}")
    String urlBase;
    @Value("${externo.userService.permitirTodos}")
    Boolean permitirTodos;

    @Autowired
    AssociadoService associadoService;
    @Autowired
    RestTemplate restTemplate;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class CpfValidoBean {
        private String status;
    }

    /**
     * Verifiqua, a partir do CPF do associado, se ele pode votar.
     * @param idAssociado a ser validado.
     */
    public boolean podeVotar(Long idAssociado) {
        if (Boolean.TRUE.equals(permitirTodos)) {
            log.warn("Consulta se o associado de id " + idAssociado + " pode votar não foi realizada pois o serviço está configurado " +
                    "para permitir todos. Retornando o valor TRUE.");
            return true;
        }

        String cpfAssociado = associadoService.carregaCpf(idAssociado);

        try {
            CpfValidoBean cpfValidoBean = restTemplate.getForObject(urlBase + "/users/" + cpfAssociado, CpfValidoBean.class);

            return cpfValidoBean != null && STATUS_PODE_VOTAR.equals(cpfValidoBean.status);
        } catch (Exception e) {
            log.error("Erro ao consultar se o associado de id " + idAssociado + " pode votar. Retornando o valor FALSE.", e);

            return false;
        }
    }
}
