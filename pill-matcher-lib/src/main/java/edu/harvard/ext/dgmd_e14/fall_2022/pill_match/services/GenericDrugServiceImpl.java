package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.GenericDrug;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.repositories.GenericDrugRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GenericDrugServiceImpl implements GenericDrugService {

    private final GenericDrugRepository drugRepository;

    @Inject
    public GenericDrugServiceImpl(GenericDrugRepository drugRepository) {
        this.drugRepository = drugRepository;
    }

    @Override
    public Optional<GenericDrug> findByName(String name) {
        name = name.trim().toUpperCase();
        return drugRepository.findByGenericName(name);
    }

    @Override
    public List<GenericDrug> findDrugsContainingName(String name) {
        return drugRepository.findByGenericNameContainingIgnoreCase(name.trim());
    }

    @Override
    public List<GenericDrug> findAll() {
        return drugRepository.findAll();
    }

    @Override
    public GenericDrug saveDrug(GenericDrug drug) {
        return drugRepository.save(drug);
    }

    @Override
    public GenericDrug getOrSaveDrug(String genericName) {
        String name = genericName.trim().toUpperCase();
        Optional<GenericDrug> drug = drugRepository.findByGenericName(name);
        if (drug.isPresent()) {
            return drug.get();
        }
        else {
            return drugRepository.save(new GenericDrug(name));
        }
    }
}
