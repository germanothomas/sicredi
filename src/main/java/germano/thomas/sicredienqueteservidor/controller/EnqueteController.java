package germano.thomas.sicredienqueteservidor.controller;

import germano.thomas.sicredienqueteservidor.controller.bean.AbreSessaoVotacaoBean;
import germano.thomas.sicredienqueteservidor.domain.Pauta;
import germano.thomas.sicredienqueteservidor.service.PautaService;
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
        description = "Controla a utilizacao das enquetes: cadastro de pautas, sessoes de votacao e resultados."
)
)
@RequestMapping(produces = "application/json;charset=UTF-8")
@RestController
public class EnqueteController {
    @Autowired
    PautaService pautaService;

    @Operation(summary = "Cadastra pauta",
            description = "Cadastrar uma nova pauta.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "pauta a ser cadastrada"),
            responses = @ApiResponse(description = "id da nova pauta")
    )
    @PostMapping("/pauta")
    public Long cadastraPauta(@RequestBody Pauta pauta) {
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
}
