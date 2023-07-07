package germano.thomas.sicredienqueteservidor.service;

import germano.thomas.sicredienqueteservidor.controller.bean.ContabilizaResultadoBean;
import germano.thomas.sicredienqueteservidor.domain.Item;
import germano.thomas.sicredienqueteservidor.domain.Pauta;
import germano.thomas.sicredienqueteservidor.domain.Voto;
import germano.thomas.sicredienqueteservidor.repository.ItemRepository;
import germano.thomas.sicredienqueteservidor.repository.VotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {
    @Mock
    VotoRepository votoRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    PautaService pautaService;

    @InjectMocks
    VotoService service;

    @Test
    void vota() {
        // given
        Long idAssociado = 76234L;
        Long idItem = 2342L;
        Boolean valor = Boolean.TRUE;
        Item item = mock(Item.class);
        Pauta pauta = mock(Pauta.class);
        Voto votoEsperado = new Voto();
        Long idEsperado = 827364L;
        votoEsperado.setId(idEsperado);

        when(itemRepository.findById(idItem)).thenReturn(Optional.of(item));
        when(item.getPauta()).thenReturn(pauta);
        when(pautaService.isSessaoVotacaoAtiva(pauta)).thenReturn(true);
        ArgumentCaptor<Voto> votoArgumentCaptor = ArgumentCaptor.forClass(Voto.class);
        when(votoRepository.save(votoArgumentCaptor.capture())).thenReturn(votoEsperado);

        // when
        Long result = service.vota(idAssociado, idItem, valor);

        // then
        assertEquals(idEsperado, result);
        Voto votoPersistido = votoArgumentCaptor.getValue();
        assertEquals(idAssociado, votoPersistido.getIdAssociado());
        assertEquals(item, votoPersistido.getItem());
        assertEquals(valor, votoPersistido.getValor());
    }

    @Test
    void votaItemNaoEncontrado() {
        // given
        Long idAssociado = 76234L;
        Long idItem = 2342L;
        Boolean valor = Boolean.TRUE;

        // when
        IllegalArgumentException excecaoEsperada = assertThrows(IllegalArgumentException.class, () ->
                service.vota(idAssociado, idItem, valor));

        // then
        assertTrue(excecaoEsperada.getMessage().contains("encontr"));
    }

    @Test
    void votaPautaSessaoInativa() {
        // given
        Long idAssociado = 76234L;
        Long idItem = 2342L;
        Boolean valor = Boolean.TRUE;
        Item item = mock(Item.class);
        Pauta pauta = mock(Pauta.class);

        when(itemRepository.findById(idItem)).thenReturn(Optional.of(item));
        when(item.getPauta()).thenReturn(pauta);
        when(pautaService.isSessaoVotacaoAtiva(pauta)).thenReturn(false);

        // when
        IllegalArgumentException excecaoEsperada = assertThrows(IllegalArgumentException.class, () ->
                service.vota(idAssociado, idItem, valor));

        // then
        assertTrue(excecaoEsperada.getMessage().contains("com sess"));
    }

    @Test
    void votaUsuarioEItemRepetido() {
        // given
        Long idAssociado = 76234L;
        Long idItem = 2342L;
        Boolean valor = Boolean.TRUE;
        Item item = mock(Item.class);
        Pauta pauta = mock(Pauta.class);

        when(itemRepository.findById(idItem)).thenReturn(Optional.of(item));
        when(item.getPauta()).thenReturn(pauta);
        when(pautaService.isSessaoVotacaoAtiva(pauta)).thenReturn(true);
        when(votoRepository.findOneByItemIdAndIdAssociado(idItem, idAssociado)).thenReturn(Optional.of(mock(Voto.class)));

        // when
        IllegalArgumentException excecaoEsperada = assertThrows(IllegalArgumentException.class, () ->
                service.vota(idAssociado, idItem, valor));

        // then
        assertTrue(excecaoEsperada.getMessage().contains("apenas uma vez"));
    }

    @Test
    void contabilizaResultado() {
        // given
        Long idItem = 9876345L;
        when(votoRepository.countVotos(idItem, Boolean.TRUE)).thenReturn(3L);
        when(votoRepository.countVotos(idItem, Boolean.FALSE)).thenReturn(1L);

        // when
        ContabilizaResultadoBean result = service.contabilizaResultado(idItem);

        // then
        assertEquals(4, result.totalVotos());
        assertEquals(75, result.porcentagemAprovacao());
    }

    @Test
    void contabilizaResultadoSemVotos() {
        // given
        Long idItem = 9876345L;
        when(votoRepository.countVotos(idItem, Boolean.TRUE)).thenReturn(0L);
        when(votoRepository.countVotos(idItem, Boolean.FALSE)).thenReturn(0L);

        // when
        ContabilizaResultadoBean result = service.contabilizaResultado(idItem);

        // then
        assertEquals(0, result.totalVotos());
        assertEquals(0, result.porcentagemAprovacao());
    }

    @Test
    void contabilizaResultadoUnanimidadePositiva() {
        // given
        Long idItem = 9876345L;
        when(votoRepository.countVotos(idItem, Boolean.TRUE)).thenReturn(3L);
        when(votoRepository.countVotos(idItem, Boolean.FALSE)).thenReturn(0L);

        // when
        ContabilizaResultadoBean result = service.contabilizaResultado(idItem);

        // then
        assertEquals(3, result.totalVotos());
        assertEquals(100, result.porcentagemAprovacao());
    }

    @Test
    void contabilizaResultadoUnanimidadeNegativa() {
        // given
        Long idItem = 9876345L;
        when(votoRepository.countVotos(idItem, Boolean.TRUE)).thenReturn(0L);
        when(votoRepository.countVotos(idItem, Boolean.FALSE)).thenReturn(5L);

        // when
        ContabilizaResultadoBean result = service.contabilizaResultado(idItem);

        // then
        assertEquals(5, result.totalVotos());
        assertEquals(0, result.porcentagemAprovacao());
    }
}
