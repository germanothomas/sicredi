package germano.thomas.sicredienqueteservidor.service.externo;

import germano.thomas.sicredienqueteservidor.service.AssociadoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    AssociadoService associadoService;
    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    UserService service;

    @BeforeEach
    void setup() {
        service.urlBase = "http://teste";
        service.permitirTodos = false;
    }

    @Test
    void podeVotar() {
        // given
        Long idAssociado = 8237645L;
        String cpf = "76234263429";
        UserService.CpfValidoBean cpfValidoBean = new UserService.CpfValidoBean(UserService.STATUS_PODE_VOTAR);
        when(associadoService.carregaCpf(idAssociado)).thenReturn(cpf);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        when(restTemplate.getForObject(urlCaptor.capture(), eq(UserService.CpfValidoBean.class))).thenReturn(cpfValidoBean);

        // when
        boolean result = service.podeVotar(idAssociado);

        // then
        assertTrue(result);
        assertTrue(urlCaptor.getValue().contains(cpf), "Serviço externo deve ser chamado com o CPF correto.");
    }

    @Test
    void podeVotarFalso() {
        // given
        Long idAssociado = 8237645L;
        String cpf = "76234263429";
        UserService.CpfValidoBean cpfValidoBean = new UserService.CpfValidoBean("UNABLE_TO_VOTE");
        when(associadoService.carregaCpf(idAssociado)).thenReturn(cpf);
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        when(restTemplate.getForObject(urlCaptor.capture(), eq(UserService.CpfValidoBean.class))).thenReturn(cpfValidoBean);

        // when
        boolean result = service.podeVotar(idAssociado);

        // then
        assertFalse(result);
        assertTrue(urlCaptor.getValue().contains(cpf), "Serviço externo deve ser chamado com o CPF correto.");
    }

    @Test
    void podeVotarPermitirTodos() {
        // given
        Long idAssociado = 8237645L;
        service.permitirTodos = true;

        // when
        boolean result = service.podeVotar(idAssociado);

        // then
        assertTrue(result);
        verify(restTemplate, never()).getForObject(anyString(), any());
    }

    @Test
    void podeVotarExcecaoFalso() {
        // given
        Long idAssociado = 8237645L;
        when(associadoService.carregaCpf(idAssociado)).thenReturn("23422");
        when(restTemplate.getForObject(anyString(), any())).thenThrow(new RuntimeException("Erro de teste ao consultar o serviço externo"));

        // when
        boolean result = service.podeVotar(idAssociado);

        // then
        assertFalse(result);
    }

    @Test
    void podeVotarRetornoNulo() {
        // given
        Long idAssociado = 8237645L;
        String cpf = "76234263429";
        when(associadoService.carregaCpf(idAssociado)).thenReturn(cpf);
        when(restTemplate.getForObject(anyString(), eq(UserService.CpfValidoBean.class))).thenReturn(null);

        // when
        boolean result = service.podeVotar(idAssociado);

        // then
        assertFalse(result);
    }
}
