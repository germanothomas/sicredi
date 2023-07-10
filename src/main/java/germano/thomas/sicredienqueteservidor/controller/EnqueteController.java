package germano.thomas.sicredienqueteservidor.controller;

import germano.thomas.sicredienqueteservidor.controller.bean.AbreSessaoVotacaoBean;
import germano.thomas.sicredienqueteservidor.controller.bean.ResultadoVotacaoItemBean;
import germano.thomas.sicredienqueteservidor.controller.bean.VotaBean;
import germano.thomas.sicredienqueteservidor.domain.Pauta;
import germano.thomas.sicredienqueteservidor.service.PautaService;
import germano.thomas.sicredienqueteservidor.service.VotoService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controla a utilização das enquetes: cadastro de pautas, sessões de votação e resultados.
 */
@OpenAPIDefinition(info =
@Info(
        title = "API de enquetes Sicredi",
        description = "Controla a utilizacao das enquetes: cadastro de pautas, sessoes de votacao e resultados.",
        version = "v1.2.0"
)
)
@RequestMapping(produces = "application/json;charset=UTF-8")
@RestController
public class EnqueteController {
    @Autowired
    PautaService pautaService;
    @Autowired
    VotoService votoService;

    @Operation(summary = "Cadastra pauta",
            description = "Cadastrar uma nova pauta.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "pauta a ser cadastrada"),
            responses = @ApiResponse(description = "id da nova pauta")
    )
    @PostMapping("/pauta")
    public Long cadastraPauta(@RequestBody @Valid Pauta pauta) {
        return pautaService.cadastraPauta(pauta);
    }

    @Operation(summary = "Carrega pauta",
            description = "Carregar uma pauta cadastrada.",
            responses = @ApiResponse(description = "pauta carregada")
    )
    @GetMapping("/pauta/{id}")
    public Pauta carregaPauta(@Parameter(description = "id da pauta a ser carregada.", required = true) @PathVariable Long id) {
        return pautaService.carregaPauta(id);
    }

    @Operation(summary = "Abre sessao votacao",
            description = "Abrir uma sessao de votacao em uma pauta.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "A sessao de votacao deve ficar aberta por " +
                    "um tempo determinado na chamada de abertura ou 1 minuto por default.")
    )
    @PostMapping("/sessao-votacao/abre")
    public void abreSessaoVotacao(@RequestBody @Valid AbreSessaoVotacaoBean abreSessaoBean) {
        pautaService.abreSessaoVotacao(abreSessaoBean.pautaId(), abreSessaoBean.duracaoMinutos());
    }

    @Operation(summary = "Vota",
            description = "Receber votos dos associados em pautas.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Os votos sao apenas 'Sim'/'Nao'.<br>" +
                    "Cada associado e identificado por um id unico e pode votar apenas uma vez por pauta.")
    )
    @PostMapping("/sessao-votacao/vota")
    public Long vota(@RequestBody @Valid VotaBean votaBean) {
        return votoService.vota(votaBean.idAssociado(), votaBean.idItem(), votaBean.valor());
    }

    @Operation(summary = "Contabiliza resultado",
            description = "Será removido na próxima versão. Faz a mesma coisa que o endpoint de Carrega resultado.",
            deprecated = true
    )
    @GetMapping("/pauta/resultado/{idItem}")
    public ResultadoVotacaoItemBean contabilizaResultado(
            @Parameter(description = "id do item a ter o resultado carregado.", required = true) @PathVariable Long idItem) {
        return carregaResultado(idItem);
    }

    @Operation(summary = "Contabiliza votos",
            description = "Contabilizar os votos de um item em uma pauta.",
            responses = @ApiResponse(description = "Total de votos contabilizados.")
    )
    @GetMapping("/item/{idItem}/contabiliza-votos")
    public Long contabilizaVotos(
            @Parameter(description = "id do item a ter seus votos contabilizados.", required = true) @PathVariable Long idItem) {
        return votoService.contabilizaVotos(idItem);
    }

    @Operation(summary = "Carrega resultado",
            description = "Dar o resultado da votacao de um item em uma pauta. O item deve ter sido previamente contabilizado",
            responses = @ApiResponse(description = "Resultado da votacao na pauta.")
    )
    @GetMapping("/item/{idItem}/resultado")
    public ResultadoVotacaoItemBean carregaResultado(
            @Parameter(description = "id do item a ter o resultado carregado.", required = true) @PathVariable Long idItem) {
        return votoService.carregaResultado(idItem);
    }
}
