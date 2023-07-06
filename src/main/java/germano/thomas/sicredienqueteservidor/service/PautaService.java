package germano.thomas.sicredienqueteservidor.service;

import germano.thomas.sicredienqueteservidor.domain.Pauta;
import germano.thomas.sicredienqueteservidor.repository.PautaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Faz o cadastro de pautas e sessões de votação.
 */
@Service
public class PautaService {
    @Value("${sessaoVotacao.duracaoMinutos.default}")
    Integer duracaoMinutosDefault;

    @Autowired
    PautaRepository pautaRepository;

    /**
     * Cadastrar uma nova pauta.
     * @param pauta a ser cadastrada.
     * @return id da nova pauta.
     */
    public Long cadastraPauta(Pauta pauta) {
        pauta.getItens().forEach(item -> item.setPauta(pauta));

        return pautaRepository.save(pauta).getId();
    }

    /**
     * Carregar uma pauta cadastrada.
     * @param id da pauta a ser carregada.
     * @return pauta carregada.
     */
    public Pauta carregaPauta(Long id) {
        return pautaRepository.findById(id).orElse(null);
    }

    /**
     * Abrir uma sessão de votação em uma pauta.<br>
     *
     * @param pautaId da pauta que terá sua sessão de votação aberta.
     * @param duracaoMinutos A sessão de votação deve ficar aberta por um tempo determinado na chamada de abertura ou 1 minuto por default.
     */
    public void abreSessaoVotacao(Long pautaId, Integer duracaoMinutos) {
        Pauta pauta = carregaPauta(pautaId);
        if (pauta == null) {
            throw new IllegalArgumentException("Nenhuma pauta encontrada para o id " + pautaId);
        }

        duracaoMinutos = duracaoMinutos != null ? duracaoMinutos : duracaoMinutosDefault;

        LocalDateTime sessaoInicio = LocalDateTime.now();
        pauta.setSessaoVotacaoInicio(sessaoInicio);
        pauta.setSessaoVotacaoFim(sessaoInicio.plusMinutes(duracaoMinutos));

        pautaRepository.save(pauta);
    }
}
