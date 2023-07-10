package germano.thomas.sicredienqueteservidor.service;

import germano.thomas.sicredienqueteservidor.domain.Item;
import germano.thomas.sicredienqueteservidor.domain.Pauta;
import germano.thomas.sicredienqueteservidor.domain.Voto;
import germano.thomas.sicredienqueteservidor.repository.ItemRepository;
import germano.thomas.sicredienqueteservidor.repository.VotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        Long idAssociado = 87698L;
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
        Long idAssociado = 6454L;
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
        Long idAssociado = 234L;
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

    @ParameterizedTest
    @CsvSource({
            "3, 1, 75",
            "8, 0, 100",
            "0, 9, 0",
            "0, 0, 0",
    })
    void contabilizaVotos(Long votosSim, Long votosNao, Long porcentagemAprovacaoEsperada) {
        // given
        Long idItem = 252352L;
        Long totalVotosEsperados = votosSim + votosNao;
        Item item = new Item();
        when(votoRepository.countVotos(idItem, Boolean.TRUE)).thenReturn(votosSim);
        when(votoRepository.countVotos(idItem, Boolean.FALSE)).thenReturn(votosNao);
        when(itemRepository.findById(idItem)).thenReturn(Optional.of(item));

        // when
        Long result = service.contabilizaVotos(idItem);

        // then
        assertEquals(totalVotosEsperados, result);

        ArgumentCaptor<Item> itemArgumentCaptor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item itemPersistido = itemArgumentCaptor.getValue();

        assertEquals(totalVotosEsperados, itemPersistido.getTotalVotos());
        assertEquals(porcentagemAprovacaoEsperada, itemPersistido.getPorcentagemAprovacao());
        assertNotNull(itemPersistido.getDataHoraContabilizacao());
    }
}
