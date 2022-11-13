package edu.harvard.ext.dgmd_e14.fall_2022.pill_db_fill.c3pi;

import java.util.Optional;

public interface PillPhotoService {

    Optional<PillPhoto> findByC3piImageDirectoryAndC3piImageFile(String directory, String imageFile);

    PillPhoto savePillPhoto(PillPhoto pillPhoto);

}
