package edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.GenericDrug;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Pill;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.repositories.PillRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PillServiceImpl implements PillService {

    private final PillRepository pillRepository;
    private final GenericDrugService drugService;

    @Inject
    public PillServiceImpl(PillRepository pillRepository, GenericDrugService drugService) {
        this.pillRepository = pillRepository;
        this.drugService = drugService;
    }

    @Override
    public Optional<Pill> findBySer(Long pillSer) {
        return pillRepository.findById(pillSer);
    }

    @Override
    public Optional<Pill> findByNdc11(String ndc11) {
        return pillRepository.findByNdc11(ndc11);
    }

    @Override
    public List<Pill> findAllByShape(String shape) {
        return pillRepository.findAllByShape(shape.toUpperCase());
    }

    @Override
    public List<Pill> findAllBySingleColor(String color) {
        return pillRepository.findAllBySingleColor(color.toUpperCase());
    }

    @Override
    public List<Pill> findAllByTwoColors(String colorOne, String colorTwo) {
        return pillRepository.findAllByTwoColors(colorOne.toUpperCase(), colorTwo.toUpperCase());
    }

    @Override
    public List<Pill> findAllByShapeAndSingleColor(String shape, String color) {
        return pillRepository.findAllByShapeAndSingleColor(shape.toUpperCase(), color.toUpperCase());
    }

    @Override
    public List<Pill> findAllByShapeAndTwoColors(String shape, String colorOne, String colorTwo) {
        return pillRepository.findAllByShapeAndTwoColors(shape.toUpperCase(), colorOne.toUpperCase(),
                                                         colorTwo.toUpperCase());
    }

    @Override
    public Pill savePill(Pill pill) {
        GenericDrug savedDrug = drugService.getOrSaveDrug(pill.getGenericName());
        pill.setGenericDrug(savedDrug);

        return pillRepository.save(pill);
    }

}
