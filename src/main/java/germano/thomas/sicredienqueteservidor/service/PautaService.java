package germano.thomas.sicredienqueteservidor.service;

import germano.thomas.sicredienqueteservidor.domain.Item;
import germano.thomas.sicredienqueteservidor.domain.Pauta;
import germano.thomas.sicredienqueteservidor.repository.PautaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Faz o cadastro de pautas e sess�es de vota��o.
 */
@Service
@Slf4j
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
    public Pauta carregaPauta(Long id, Boolean mostrarResultado) {
        Pauta pauta = pautaRepository.findById(id).orElse(null);

        if (pauta != null && !Boolean.TRUE.equals(mostrarResultado)) {
            pauta.getItens().forEach(this::escondeResultadoVotacao);
        }

        return pauta;
    }

    /**
     * Abrir uma sess�o de vota��o em uma pauta.
     * @param idPauta da pauta que ter� sua sess�o de vota��o aberta.
     * @param duracaoMinutos A sess�o de vota��o deve ficar aberta por um tempo determinado na chamada de abertura ou 1 minuto por default.
     */
    public void abreSessaoVotacao(Long idPauta, Integer duracaoMinutos) {
        Pauta pauta = carregaPauta(idPauta, true);
        if (pauta == null) {
            String mensagemErro = "Pauta n�o encontrada.";
            log.warn("abreSessaoVotacao (idPauta=" + idPauta + "): " + mensagemErro);

            throw new IllegalArgumentException(mensagemErro);
        }

        duracaoMinutos = duracaoMinutos != null ? duracaoMinutos : duracaoMinutosDefault;

        LocalDateTime sessaoInicio = LocalDateTime.now();
        pauta.setSessaoVotacaoInicio(sessaoInicio);
        pauta.setSessaoVotacaoFim(sessaoInicio.plusMinutes(duracaoMinutos));

        pautaRepository.save(pauta);
        log.info("Aberta sess�o de vota��o da pauta de idPauta " + idPauta + " com dura��o de " + duracaoMinutos + " minutos.");
    }

    /**
     * Retorna se determinada pauta est� com uma sess�o de vota��o ativa.
     * @param pauta j� carregada do banco.
     */
    public boolean isSessaoVotacaoAtiva(Pauta pauta) {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime sessaoVotacaoInicio = pauta.getSessaoVotacaoInicio();
        LocalDateTime sessaoVotacaoFim = pauta.getSessaoVotacaoFim();
        if (sessaoVotacaoInicio == null || sessaoVotacaoFim == null) {
            return false;
        }

        return sessaoVotacaoInicio.isBefore(agora) && agora.isBefore(sessaoVotacaoFim);
    }

    private void escondeResultadoVotacao(Item item) {
        item.setTotalVotos(null);
        item.setPorcentagemAprovacao(null);
        item.setDataHoraContabilizacao(null);
    }
}
