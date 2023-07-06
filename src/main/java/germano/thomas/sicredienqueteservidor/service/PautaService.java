package germano.thomas.sicredienqueteservidor.service;

import germano.thomas.sicredienqueteservidor.domain.Pauta;
import germano.thomas.sicredienqueteservidor.repository.PautaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PautaService {
    @Autowired
    PautaRepository pautaRepository;

    /**
     * Cadastrar uma nova pauta.
     * @param pauta a ser cadastrada.
     * @return id da nova pauta.
     */
    public Long cadastraPauta(Pauta pauta) {
        pauta.getItens().forEach(item -> item.setPauta(pauta));

        return pautaRepository.save(pauta).getId();
    }

    /**
     * Carregar uma pauta cadastrada.
     * @param id da pauta a ser carregada.
     * @return pauta carregada.
     */
    public Pauta carregaPauta(Long id) {
        return pautaRepository.findById(id).orElse(null);
    }
}
