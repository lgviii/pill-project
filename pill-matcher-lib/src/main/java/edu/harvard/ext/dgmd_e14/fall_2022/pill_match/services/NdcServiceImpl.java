package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.GenericDrug;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Ndc;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.repositories.NdcRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class NdcServiceImpl implements NdcService {

    private final NdcRepository ndcRepository;

    private final GenericDrugService drugService;

    @Inject
    public NdcServiceImpl(NdcRepository ndcRepository, GenericDrugService drugService) {
        this.ndcRepository = ndcRepository;
        this.drugService = drugService;
    }

    @Override
    public Optional<Ndc> findByNdc11(String ndc11) {
        return ndcRepository.findByNdc11(ndc11);
    }

    @Override
    public Ndc saveNdc(Ndc ndc) {
        Optional<Ndc> result = ndcRepository.findByNdc11(ndc.getNdc11());
        if (result.isPresent()) {
            return result.get();
        }

        GenericDrug savedDrug = drugService.getOrSaveDrug(ndc.getGenericName());
        ndc.setGenericDrug(savedDrug);

        return ndcRepository.save(ndc);
    }
}
