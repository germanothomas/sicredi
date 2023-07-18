package germano.thomas.sicredienqueteservidor.service;

import germano.thomas.sicredienqueteservidor.domain.Item;
import germano.thomas.sicredienqueteservidor.domain.Pauta;
import germano.thomas.sicredienqueteservidor.repository.PautaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PautaServiceTest {
    @Mock
    PautaRepository pautaRepository;

    @InjectMocks
    PautaService service;

    @Test
    void cadastraPauta() {
        // given
        Pauta pautaEntrada = new Pauta();
        Item itemEntrada = new Item();
        pautaEntrada.getItens().add(itemEntrada);

        Pauta pautaPersistida = mock(Pauta.class);
        Long idEsperado = 23452352L;

        when(pautaRepository.save(pautaEntrada)).thenReturn(pautaPersistida);
        when(pautaPersistida.getId()).thenReturn(idEsperado);

        // when
        Long result = service.cadastraPauta(pautaEntrada);

        // then
        assertEquals(idEsperado, result);
        assertEquals(pautaEntrada, itemEntrada.getPauta());
    }

    @Test
    void carregaPauta() {
        // given
        Long pautaId = 23452352L;
        Pauta pautaEsperada = new Pauta();
        Item item = new Item();
        item.setTotalVotos(10L);
        item.setPorcentagemAprovacao(70L);
        item.setDataHoraContabilizacao(LocalDateTime.now());
        pautaEsperada.getItens().add(item);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pautaEsperada));

        // when
        Pauta result = service.carregaPauta(pautaId, false);

        // then
        assertEquals(pautaEsperada, result);
        assertNull(item.getTotalVotos());
        assertNull(item.getPorcentagemAprovacao());
        assertNull(item.getDataHoraContabilizacao());
    }

    @Test
    void carregaPautaMostraResultado() {
        // given
        Long pautaId = 23452352L;
        Pauta pautaEsperada = new Pauta();
        Item item = new Item();
        long totalVotosEsperado = 10L;
        long porcentagemAprovacaoEsperada = 70L;
        LocalDateTime dataHoraContabilizacaoEsperada = LocalDateTime.now();
        item.setTotalVotos(totalVotosEsperado);
        item.setPorcentagemAprovacao(porcentagemAprovacaoEsperada);
        item.setDataHoraContabilizacao(dataHoraContabilizacaoEsperada);
        pautaEsperada.getItens().add(item);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pautaEsperada));

        // when
        Pauta result = service.carregaPauta(pautaId, true);

        // then
        assertEquals(pautaEsperada, result);
        assertEquals(totalVotosEsperado, item.getTotalVotos());
        assertEquals(porcentagemAprovacaoEsperada, item.getPorcentagemAprovacao());
        assertEquals(dataHoraContabilizacaoEsperada, item.getDataHoraContabilizacao());
    }

    @Test
    void carregaPautaNaoEncontrada() {
        // given
        Long pautaId = 23452352L;

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.empty());

        // when
        Pauta result = service.carregaPauta(pautaId, true);

        // then
        assertNull(result);
    }

    @Test
    void abreSessaoVotacao() {
        // given
        Long pautaId = 23452352L;
        Integer duracaoMinutosEsperada = 15;
        Pauta pauta = new Pauta();

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));

        // when
        service.abreSessaoVotacao(pautaId, duracaoMinutosEsperada);

        // then
        ArgumentCaptor<Pauta> pautaArgumentCaptor = ArgumentCaptor.forClass(Pauta.class);
        verify(pautaRepository).save(pautaArgumentCaptor.capture());

        Pauta pautaPersistida = pautaArgumentCaptor.getValue();
        assertEquals(pauta, pautaPersistida);

        long duracaoMinutosPersistida = MINUTES.between(pautaPersistida.getSessaoVotacaoInicio(), pautaPersistida.getSessaoVotacaoFim());
        assertEquals(duracaoMinutosEsperada, (int) duracaoMinutosPersistida);
    }

    @Test
    void abreSessaoVotacaoDuracaoDefault() {
        // given
        Integer duracaoMinutosEsperada = 6;
        service.duracaoMinutosDefault = duracaoMinutosEsperada;
        Long pautaId = 23452352L;
        Pauta pauta = new Pauta();

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));

        // when
        service.abreSessaoVotacao(pautaId, null);

        // then
        ArgumentCaptor<Pauta> pautaArgumentCaptor = ArgumentCaptor.forClass(Pauta.class);
        verify(pautaRepository).save(pautaArgumentCaptor.capture());

        Pauta pautaPersistida = pautaArgumentCaptor.getValue();
        assertEquals(pauta, pautaPersistida);

        long duracaoMinutosPersistida = MINUTES.between(pautaPersistida.getSessaoVotacaoInicio(), pautaPersistida.getSessaoVotacaoFim());
        assertEquals(duracaoMinutosEsperada, (int) duracaoMinutosPersistida);
    }

    @Test
    void abreSessaoVotacaoPautaNaoEncontrada() {
        // given
        Integer duracaoMinutos = 15;
        Long pautaId = 23452352L;

        // when
        IllegalArgumentException excecaoEsperada = assertThrows(IllegalArgumentException.class, () ->
                service.abreSessaoVotacao(pautaId, duracaoMinutos));

        // then
        assertTrue(excecaoEsperada.getMessage().contains("encontr"));
    }

    @Test
    void isSessaoVotacaoAtivaSim() {
        // given
        Pauta pauta = new Pauta();
        pauta.setSessaoVotacaoInicio(LocalDateTime.MIN);
        pauta.setSessaoVotacaoFim(LocalDateTime.MAX);

        // when
        boolean result = service.isSessaoVotacaoAtiva(pauta);

        // then
        assertTrue(result);
    }

    @Test
    void isSessaoVotacaoAtivaInicioFimNulo() {
        // given
        Pauta pauta = new Pauta();

        // when
        boolean result = service.isSessaoVotacaoAtiva(pauta);

        // then
        assertFalse(result);
    }

    @Test
    void isSessaoVotacaoAtivaInicioNulo() {
        // given
        Pauta pauta = new Pauta();
        pauta.setSessaoVotacaoFim(LocalDateTime.MAX);

        // when
        boolean result = service.isSessaoVotacaoAtiva(pauta);

        // then
        assertFalse(result);
    }

    @Test
    void isSessaoVotacaoAtivaFimNulo() {
        // given
        Pauta pauta = new Pauta();
        pauta.setSessaoVotacaoInicio(LocalDateTime.MIN);

        // when
        boolean result = service.isSessaoVotacaoAtiva(pauta);

        // then
        assertFalse(result);
    }

    @Test
    void isSessaoVotacaoAtivaAntesInicio() {
        // given
        Pauta pauta = new Pauta();
        LocalDateTime agora = LocalDateTime.now();
        pauta.setSessaoVotacaoInicio(agora.minusHours(2L));
        pauta.setSessaoVotacaoFim(agora.minusHours(1L));

        // when
        boolean result = service.isSessaoVotacaoAtiva(pauta);

        // then
        assertFalse(result);
    }

    @Test
    void isSessaoVotacaoAtivaDepiosFim() {
        // given
        Pauta pauta = new Pauta();
        LocalDateTime agora = LocalDateTime.now();
        pauta.setSessaoVotacaoInicio(agora.plusHours(1L));
        pauta.setSessaoVotacaoFim(agora.plusHours(2L));

        // when
        boolean result = service.isSessaoVotacaoAtiva(pauta);

        // then
        assertFalse(result);
    }

    @Test
    void isExistePauta() {
        // given
        Long idPauta = 8723645L;
        when(pautaRepository.existsById(idPauta)).thenReturn(true);

        // when
        boolean result = service.isExistePauta(idPauta);

        // then
        assertTrue(result);
    }
}
