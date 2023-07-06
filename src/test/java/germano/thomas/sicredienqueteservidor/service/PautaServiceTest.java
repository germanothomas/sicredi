package germano.thomas.sicredienqueteservidor.service;

import germano.thomas.sicredienqueteservidor.domain.Item;
import germano.thomas.sicredienqueteservidor.domain.Pauta;
import germano.thomas.sicredienqueteservidor.repository.PautaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        Pauta pautaEsperada = mock(Pauta.class);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pautaEsperada));

        // when
        Pauta result = service.carregaPauta(pautaId);

        // then
        assertEquals(pautaEsperada, result);
    }

    @Test
    void carregaPautaNaoEncontrada() {
        // given
        Long pautaId = 23452352L;

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.empty());

        // when
        Pauta result = service.carregaPauta(pautaId);

        // then
        assertNull(result);
    }
}
