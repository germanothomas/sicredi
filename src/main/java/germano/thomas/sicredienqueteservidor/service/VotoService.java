package germano.thomas.sicredienqueteservidor.service;

import germano.thomas.sicredienqueteservidor.controller.bean.ResultadoVotacaoItemBean;
import germano.thomas.sicredienqueteservidor.domain.Item;
import germano.thomas.sicredienqueteservidor.domain.Voto;
import germano.thomas.sicredienqueteservidor.domain.VotoAgrupadoProjection;
import germano.thomas.sicredienqueteservidor.repository.ItemRepository;
import germano.thomas.sicredienqueteservidor.repository.VotoRepository;
import germano.thomas.sicredienqueteservidor.service.externo.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
    @Autowired
    UserService userService;

    /**
     * Receber votos dos associados em pautas.
     * @param idAssociado Cada associado � identificado por um id �nico e pode votar apenas uma vez por pauta.
     * @param idItem identificador do item a ser votado.
     * @param valor Os votos s�o apenas 'Sim'/'N�o'.
     * @return id do novo voto.
     */
    public Long vota(Long idAssociado, Long idItem, Boolean valor) {
        if (!userService.podeVotar(idAssociado)) {
            throw new IllegalArgumentException("Associado de id " + idAssociado + " n�o pode votar devido a um servi�o externo.");
        }

        Optional<Item> optionalItem = itemRepository.findById(idItem);
        if (optionalItem.isEmpty()) {
            String mensagemErro = "Item n�o encontrado para vota��o.";
            log.warn(constroiLogVota(idAssociado, idItem, mensagemErro));

            throw new IllegalArgumentException(mensagemErro);
        }

        Item item = optionalItem.get();
        if (!pautaService.isSessaoVotacaoAtiva(item.getPauta())) {
            String mensagemErro = "Pauta n�o est� com sess�o de vota��o ativa.";
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
     * Contabilizar os votos de uma pauta.
     * @param idPauta id da pauta a ter os votos de todos os seus itens contabilizados.
     * @return Total de votos contabilizados.
     */
    public long contabilizaVotosPauta(Long idPauta) {
        boolean existePauta = pautaService.isExistePauta(idPauta);
        if (!existePauta) {
            String mensagemErro = "Pauta n�o encontrada.";
            log.warn("contabilizaVotosPauta (idPauta=" + idPauta + "): " + mensagemErro);

            throw new IllegalArgumentException(mensagemErro);
        }

        List<VotoAgrupadoProjection> votosAgrupados = votoRepository.countVotosPauta(idPauta);
        final LocalDateTime dataHoraContabilizacao = LocalDateTime.now();
        long totalVotos = votosAgrupados.stream().mapToLong(votoAgrupado -> {
            Item item = itemRepository.findById(votoAgrupado.getIdItem()).get();

            return contabilizaVotosItem(item, votoAgrupado.getVotosSim(), votoAgrupado.getVotosNao(), dataHoraContabilizacao);
        }).sum();
        log.info("Contabilizados " + totalVotos + " votos entre todos os itens da pauta de id " + idPauta);

        return totalVotos;
    }

    /**
     * Contabilizar os votos de um item em uma pauta.
     * @param idItem id do item a ter seus votos contabilizados.
     * @return Total de votos contabilizados.
     */
    public Long contabilizaVotosItem(Long idItem) {
        Optional<Item> optionalItem = itemRepository.findById(idItem);
        if (optionalItem.isEmpty()) {
            String mensagemErro = "Item n�o encontrado para contabiliza��o.";
            log.warn("contabilizaVotos (idItem=" + idItem + "): " + mensagemErro);

            throw new IllegalArgumentException(mensagemErro);
        }

        long votosSim = votoRepository.countVotosItem(idItem, Boolean.TRUE);
        long votosNao = votoRepository.countVotosItem(idItem, Boolean.FALSE);
        LocalDateTime dataHoraContabilizacao = LocalDateTime.now();

        Long totalVotos = contabilizaVotosItem(optionalItem.get(), votosSim, votosNao, dataHoraContabilizacao);
        log.info("Contabilizados " + totalVotos + " votos do item de id " + idItem);

        return totalVotos;
    }

    private Long contabilizaVotosItem(Item item, long votosSim, long votosNao, LocalDateTime dataHoraContabilizacao) {
        long totalVotos = votosSim + votosNao;
        long porcentagemAprovacao = totalVotos == 0 ? 0 : 100 * votosSim / totalVotos;

        item.setTotalVotos(totalVotos);
        item.setPorcentagemAprovacao(porcentagemAprovacao);
        item.setDataHoraContabilizacao(dataHoraContabilizacao);

        itemRepository.save(item);
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
            String mensagemErro = "Item n�o encontrado para carregar resultado.";
            log.warn("carregaResultado (idItem=" + idItem + "): " + mensagemErro);

            throw new IllegalArgumentException(mensagemErro);
        }

        if (resultado.dataHoraContabilizacao() == null) {
            String mensagemErro = "Votos do item ainda n�o foram contabilizados.";
            log.warn("carregaResultado (idItem=" + idItem + "): " + mensagemErro);

            throw new IllegalArgumentException(mensagemErro);
        }

        return resultado;
    }
}
