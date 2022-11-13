package edu.harvard.ext.dgmd_e14.fall_2022.pill_db_fill.c3pi;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.GenericDrug;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Pill;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services.GenericDrugService;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services.PillService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Optional;

@Service
public class PillPhotoServiceImpl implements PillPhotoService {

    private final GenericDrugService drugService;
    private final PillService pillService;
    private final PillPhotoRepository photoRepository;

    @Inject
    public PillPhotoServiceImpl(GenericDrugService drugService, PillService pillService,
                                PillPhotoRepository photoRepository) {
        this.drugService = drugService;
        this.pillService = pillService;
        this.photoRepository = photoRepository;
    }

    public Optional<PillPhoto> findByC3piImageDirectoryAndC3piImageFile(String directory, String imageFile) {
        return photoRepository.findByC3piImageDirectoryAndC3piImageFile(directory, imageFile);
    }

    @Override
    public PillPhoto savePillPhoto(PillPhoto pillPhoto) {
        Pill pill = pillPhoto.getPill();

        // If there's already an image saved for this pill, just add the new image
        Optional<Pill> savedPillOpt = pillService.findByNdc11(pill.getNdc11());
        if (savedPillOpt.isPresent()) {
            pillPhoto.setPill(savedPillOpt.get());
            return photoRepository.save(pillPhoto);
        }

        // Otherwise, either get or save the generic drug
        GenericDrug drug = drugService.getOrSaveDrug(pill.getGenericName());
        pill.setGenericDrug(drug);
        // At this point, we know the pill has no other saved images, so just save the image with the new parent
        return photoRepository.save(pillPhoto);
    }
}
