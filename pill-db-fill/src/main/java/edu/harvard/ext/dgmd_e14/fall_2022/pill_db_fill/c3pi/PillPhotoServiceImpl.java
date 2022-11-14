package edu.harvard.ext.dgmd_e14.fall_2022.pill_db_fill.c3pi;

import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.entities.Pill;
import edu.harvard.ext.dgmd_e14.fall_2022.pill_match.services.PillService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Optional;

@Service
public class PillPhotoServiceImpl implements PillPhotoService {

    private final PillService pillService;
    private final PillPhotoRepository photoRepository;

    @Inject
    public PillPhotoServiceImpl(PillService pillService,
                                PillPhotoRepository photoRepository) {
        this.pillService = pillService;
        this.photoRepository = photoRepository;
    }

    public Optional<PillPhoto> findByC3piImageDirectoryAndC3piImageFile(String directory, String imageFile) {
        return photoRepository.findByC3piImageDirectoryAndC3piImageFile(directory, imageFile);
    }

    @Override
    public PillPhoto savePillPhoto(PillPhoto pillPhoto) {
        // First save the pill, in case it's not already saved - this will also create all parent entities
        Pill pill = pillService.savePill(pillPhoto.getPill());
        pillPhoto.setPill(pill);

        // Now save the photo, in case the pill was already saved and the method just returned the previously saved pill
        return photoRepository.save(pillPhoto);
    }
}
