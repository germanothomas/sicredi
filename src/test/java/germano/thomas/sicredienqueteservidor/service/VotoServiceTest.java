package germano.thomas.sicredienqueteservidor.service;

import germano.thomas.sicredienqueteservidor.controller.bean.ResultadoVotacaoItemBean;
import germano.thomas.sicredienqueteservidor.domain.Item;
import germano.thomas.sicredienqueteservidor.domain.Pauta;
import germano.thomas.sicredienqueteservidor.domain.Voto;
import germano.thomas.sicredienqueteservidor.domain.VotoAgrupadoProjection;
import germano.thomas.sicredienqueteservidor.repository.ItemRepository;
import germano.thomas.sicredienqueteservidor.repository.VotoRepository;
import germano.thomas.sicredienqueteservidor.service.externo.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    @Mock
    UserService userService;

    @InjectMocks
    VotoService service;

    @BeforeEach
    void setup() {
        lenient().when(userService.podeVotar(anyLong())).thenReturn(true);
    }

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

    @Test
    void votaAssociadoNaoPodeVotar() {
        // given
        Long idAssociado = 87698L;
        Long idItem = 2342L;
        Boolean valor = Boolean.TRUE;
        when(userService.podeVotar(idAssociado)).thenReturn(false);

        // when
        IllegalArgumentException excecaoEsperada = assertThrows(IllegalArgumentException.class, () ->
                service.vota(idAssociado, idItem, valor));

        // then
        assertTrue(excecaoEsperada.getMessage().contains("pode votar"));
    }

    @Data
    @AllArgsConstructor
    private static class VotoAgrupadoProjectionImpl implements VotoAgrupadoProjection {
        private Long idItem;
        private Long votosSim;
        private Long votosNao;
    }

    @Test
    void contabilizaVotosPauta() {
        // given
        Long idPauta = 21342536L;
        when(pautaService.isExistePauta(idPauta)).thenReturn(true);

        Item item1 = criaItem(1L);
        Item item2 = criaItem(2L);
        Item item3 = criaItem(3L);
        Item item4 = criaItem(4L);

        List<VotoAgrupadoProjection> votosAgrupados = new ArrayList<>();
        votosAgrupados.add(new VotoAgrupadoProjectionImpl(item1.getId(), 3L, 1L));
        votosAgrupados.add(new VotoAgrupadoProjectionImpl(item2.getId(), 8L, 0L));
        votosAgrupados.add(new VotoAgrupadoProjectionImpl(item3.getId(), 0L, 9L));
        votosAgrupados.add(new VotoAgrupadoProjectionImpl(item4.getId(), 0L, 0L));
        when(votoRepository.countVotosPauta(idPauta)).thenReturn(votosAgrupados);

        // when
        long result = service.contabilizaVotosPauta(idPauta);

        // then
        assertEquals(21, result);

        ArgumentCaptor<Item> itemArgumentCaptor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository, times(4)).save(itemArgumentCaptor.capture());
        List<Item> itensPersistidos = itemArgumentCaptor.getAllValues();

        verificaItem(4, 75, itensPersistidos.get(0));
        verificaItem(8, 100, itensPersistidos.get(1));
        verificaItem(9, 0, itensPersistidos.get(2));
        verificaItem(0, 0, itensPersistidos.get(3));
    }

    private Item criaItem(long idItem) {
        Item item = new Item();
        item.setId(idItem);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        return item;
    }

    private void verificaItem(long totalVotosEsperado, long porcentagemAprovacaoEsperada, Item itemPersistido) {
        assertEquals(totalVotosEsperado, itemPersistido.getTotalVotos());
        assertEquals(porcentagemAprovacaoEsperada, itemPersistido.getPorcentagemAprovacao());
        assertNotNull(itemPersistido.getDataHoraContabilizacao());
    }

    @Test
    void contabilizaVotosPautaSemVotos() {
        // given
        Long idPauta = 21342536L;
        when(pautaService.isExistePauta(idPauta)).thenReturn(true);
        List<VotoAgrupadoProjection> votosAgrupados = new ArrayList<>();
        when(votoRepository.countVotosPauta(idPauta)).thenReturn(votosAgrupados);

        // when
        long result = service.contabilizaVotosPauta(idPauta);

        // then
        assertEquals(0, result);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void contabilizaVotosPautaNaoEncontrada() {
        // given
        Long idPauta = 21342536L;
        when(pautaService.isExistePauta(idPauta)).thenReturn(false);

        // when
        IllegalArgumentException excecaoEsperada = assertThrows(IllegalArgumentException.class, () ->
                service.contabilizaVotosPauta(idPauta));

        // then
        assertTrue(excecaoEsperada.getMessage().contains("encontrada"));
    }

    @ParameterizedTest
    @CsvSource({
            "3, 1, 75",
            "8, 0, 100",
            "0, 9, 0",
            "0, 0, 0",
    })
    void contabilizaVotosItem(Long votosSim, Long votosNao, Long porcentagemAprovacaoEsperada) {
        // given
        Long idItem = 252352L;
        Long totalVotosEsperados = votosSim + votosNao;
        Item item = new Item();
        when(votoRepository.countVotosItem(idItem, Boolean.TRUE)).thenReturn(votosSim);
        when(votoRepository.countVotosItem(idItem, Boolean.FALSE)).thenReturn(votosNao);
        when(itemRepository.findById(idItem)).thenReturn(Optional.of(item));

        // when
        Long result = service.contabilizaVotosItem(idItem);

        // then
        assertEquals(totalVotosEsperados, result);

        ArgumentCaptor<Item> itemArgumentCaptor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item itemPersistido = itemArgumentCaptor.getValue();

        assertEquals(totalVotosEsperados, itemPersistido.getTotalVotos());
        assertEquals(porcentagemAprovacaoEsperada, itemPersistido.getPorcentagemAprovacao());
        assertNotNull(itemPersistido.getDataHoraContabilizacao());
    }

    @Test
    void carregaResultado() {
        // given
        Long idItem = 238746L;
        ResultadoVotacaoItemBean resultadoEsperado = new ResultadoVotacaoItemBean(4L, 25L, LocalDateTime.now());
        when(itemRepository.findResultado(idItem)).thenReturn(resultadoEsperado);

        // when
        ResultadoVotacaoItemBean result = service.carregaResultado(idItem);

        // then
        assertEquals(resultadoEsperado, result);
    }

    @Test
    void carregaResultadoItemNaoEncontrado() {
        // given
        Long idItem = 238746L;
        when(itemRepository.findResultado(idItem)).thenReturn(null);

        // when
        IllegalArgumentException excecaoEsperada = assertThrows(IllegalArgumentException.class, () ->
                service.carregaResultado(idItem));

        // then
        assertTrue(excecaoEsperada.getMessage().contains("encontrado"));
    }

    @Test
    void carregaResultadoItemNaoContabilizado() {
        // given
        Long idItem = 238746L;
        when(itemRepository.findResultado(idItem)).thenReturn(new ResultadoVotacaoItemBean(null, null, null));

        // when
        IllegalArgumentException excecaoEsperada = assertThrows(IllegalArgumentException.class, () ->
                service.carregaResultado(idItem));

        // then
        assertTrue(excecaoEsperada.getMessage().contains("contabilizados"));
    }
}
