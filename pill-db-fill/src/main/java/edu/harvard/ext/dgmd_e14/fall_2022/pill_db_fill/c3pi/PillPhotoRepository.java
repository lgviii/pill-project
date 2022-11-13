package edu.harvard.ext.dgmd_e14.fall_2022.pill_db_fill.c3pi;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PillPhotoRepository extends JpaRepository<PillPhoto, Long> {

    Optional<PillPhoto> findByC3piImageDirectoryAndC3piImageFile(String directory, String imageFile);

}
