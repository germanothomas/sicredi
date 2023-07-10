package germano.thomas.sicredienqueteservidor.service;

import germano.thomas.sicredienqueteservidor.controller.bean.ResultadoVotacaoItemBean;
import germano.thomas.sicredienqueteservidor.domain.Item;
import germano.thomas.sicredienqueteservidor.domain.Voto;
import germano.thomas.sicredienqueteservidor.repository.ItemRepository;
import germano.thomas.sicredienqueteservidor.repository.VotoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Recebe votos e contabiliza os resultados.
 */
@Service
@Slf4j
public class VotoService {
    @Autowired
    VotoRepository votoRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    PautaService pautaService;

    /**
     * Receber votos dos associados em pautas.
     * @param idAssociado Cada associado é identificado por um id único e pode votar apenas uma vez por pauta.
     * @param idItem identificador do item a ser votado.
     * @param valor Os votos são apenas 'Sim'/'Não'.
     * @return id do novo voto.
     */
    public Long vota(Long idAssociado, Long idItem, Boolean valor) {
        Optional<Item> optionalItem = itemRepository.findById(idItem);
        if (optionalItem.isEmpty()) {
            String mensagemErro = "Item não encontrado para votação.";
            log.warn(constroiLogVota(idAssociado, idItem, mensagemErro));

            throw new IllegalArgumentException(mensagemErro);
        }

        Item item = optionalItem.get();
        if (!pautaService.isSessaoVotacaoAtiva(item.getPauta())) {
            String mensagemErro = "Pauta não está com sessão de votação ativa.";
            log.warn(constroiLogVota(idAssociado, idItem, mensagemErro));

            throw new IllegalArgumentException(mensagemErro);
        }


        Optional<Voto> votoOptional = votoRepository.findOneByItemIdAndIdAssociado(idItem, idAssociado);
        if (votoOptional.isPresent()) {
            String mensagemErro = "Cada associado pode votar apenas uma vez por item.";
            log.warn(constroiLogVota(idAssociado, idItem, mensagemErro));

            throw new IllegalArgumentException(mensagemErro);
        }

        Voto voto = new Voto(null, idAssociado, item, valor);

        return votoRepository.save(voto).getId();
    }

    private String constroiLogVota(Long idAssociado, Long idItem, String mensagem) {
        return "vota (idAssociado=" + idAssociado + ", idItem=" + idItem + "): " + mensagem;
    }

    /**
     * Contabilizar os votos de um item em uma pauta.
     * @param idItem id do item a ter seus votos contabilizados.
     * @return Total de votos contabilizados.
     */
    public Long contabilizaVotos(Long idItem) {
        Optional<Item> optionalItem = itemRepository.findById(idItem);
        if (optionalItem.isEmpty()) {
            String mensagemErro = "Item não encontrado para contabilização.";
            log.warn("contabilizaVotos (idItem=" + idItem + "): ", mensagemErro);

            throw new IllegalArgumentException(mensagemErro);
        }

        long votosPositivos = votoRepository.countVotos(idItem, Boolean.TRUE);
        long votosNegativos = votoRepository.countVotos(idItem, Boolean.FALSE);
        LocalDateTime dataHoraContabilizacao = LocalDateTime.now();

        Long totalVotos = votosPositivos + votosNegativos;
        Long porcentagemAprovacao = totalVotos == 0 ? 0 : 100 * votosPositivos / totalVotos;

        Item item = optionalItem.get();
        item.setTotalVotos(totalVotos);
        item.setPorcentagemAprovacao(porcentagemAprovacao);
        item.setDataHoraContabilizacao(dataHoraContabilizacao);

        itemRepository.save(item);
        log.info("Contabilizados " + totalVotos + " votos do item de id " + idItem);

        return totalVotos;
    }

    /**
     * Dar o resultado da votacao de um item em uma pauta. O item deve ter sido previamente contabilizado.
     * @param idItem id do item a ter o resultado carregado.
     * @return Resultado da votacao na pauta.
     */
    public ResultadoVotacaoItemBean carregaResultado(Long idItem) {
        ResultadoVotacaoItemBean resultado = itemRepository.findResultado(idItem);
        if (resultado == null) {
            String mensagemErro = "Item não encontrado para carregar resultado.";
            log.warn("carregaResultado (idItem=" + idItem + "): ", mensagemErro);

            throw new IllegalArgumentException(mensagemErro);
        }

        if (resultado.dataHoraContabilizacao() == null) {
            String mensagemErro = "Votos do item ainda não foram contabilizados.";
            log.warn("carregaResultado (idItem=" + idItem + "): ", mensagemErro);

            throw new IllegalArgumentException(mensagemErro);
        }

        return resultado;
    }
}
