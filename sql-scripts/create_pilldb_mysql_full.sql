/*
 * Schema creation script for full database built by pill-db-fill from C3PI XML data.
 * Includes PillPhoto table tracking C3PI images.
 */

USE pilldb;

create table GenericDrug (
	GenericDrugSer bigint not null auto_increment,
	GenericName varchar(500) not null,
	CONSTRAINT PK_GenericDrug_GenericDrugSer PRIMARY KEY (GenericDrugSer),
	CONSTRAINT UQ_GenericDrug_GenericName UNIQUE (GenericName)
);

create table Pill (
	PillSer bigint not null auto_increment,
	GenericDrugSer bigint not null,
	Ndc9 varchar(9),
	Ndc11 varchar(11),
	LabeledBy varchar(500),
	ProprietaryName varchar(500) not null,
	Imprint varchar(50),
	Shape varchar(50) not null,
	Score varchar(25) not null,
	PillSize integer not null,
	CONSTRAINT PK_Pill_PillSer PRIMARY KEY (PillSer),
	CONSTRAINT UQ_Pill_Ndc11 UNIQUE (Ndc11),
	CONSTRAINT FK_Pill_GenericDrugSer_GenericDrug FOREIGN KEY (GenericDrugSer)
					  REFERENCES GenericDrug (GenericDrugSer)
					  ON DELETE NO ACTION
					  ON UPDATE NO ACTION
);

create table PillColor (
	PillSer bigint not null,
	Color varchar(25) not null,
	CONSTRAINT PK_PillColor_PillSer_Color PRIMARY KEY (PillSer, Color),
	CONSTRAINT FK_PillColor_PillSer_Pill FOREIGN KEY (PillSer)
					   REFERENCES Pill (PillSer)
					   ON DELETE NO ACTION
					   ON UPDATE NO ACTION
);

create table PillPhoto (
	PillPhotoSer bigint not null auto_increment,
	PillSer bigint not null,
	C3piClass varchar(255),
	C3piImageFile varchar(255),
	C3piImageFileType varchar(255),
	C3piImageDirectory varchar(255),
	ImprintRating varchar(255),
	ShapeRating varchar(255),
	ColorRating varchar(255),
	ShadowRating varchar(255),
	BackgroundRating varchar(255),
	ImprintType varchar(255),
	ImprintColor varchar(255),
	ImprintSymbol bit,
	CONSTRAINT PK_PillPhoto_PillPhotoSer PRIMARY KEY (PillPhotoSer),
	CONSTRAINT FK_PillPhoto_PillSer_Pill FOREIGN KEY (PillSer)
					  REFERENCES Pill (PillSer)
					  ON DELETE NO ACTION
					  ON UPDATE NO ACTION
);

CREATE INDEX IX_Pill_Imprint ON Pill (Imprint);

CREATE INDEX IX_Pill_Shape ON Pill (Shape);

CREATE INDEX IX_Pill_ProprietaryName ON Pill (ProprietaryName);

CREATE INDEX IX_PillPhoto_C3PiClass ON PillPhoto (C3PiClass);

CREATE VIEW vv_PillColor
AS
	SELECT PillSer, group_concat(Color ORDER BY Color SEPARATOR ', ') AS Colors
    FROM PillColor
    GROUP BY PillSer;


CREATE VIEW vv_Pill
AS
	SELECT GenericDrug.GenericName, Pill.*, vv_PillColor.Colors
	FROM Pill
	JOIN GenericDrug ON Pill.GenericDrugSer = GenericDrug.GenericDrugSer
    JOIN vv_PillColor ON Pill.PillSer = vv_PillColor.PillSer;


CREATE VIEW vv_PillPhoto
AS
	SELECT GenericDrug.GenericName, Pill.*, 
	PillPhoto.PillPhotoSer,
	PillPhoto.C3piClass,
	PillPhoto.C3piImageDirectory,
	PillPhoto.C3piImageFile,
	PillPhoto.C3piImageFileType,
	PillPhoto.ImprintRating,
	PillPhoto.ShapeRating,
	PillPhoto.ColorRating,
	PillPhoto.ShadowRating,
	PillPhoto.BackgroundRating,
	PillPhoto.ImprintType,
	PillPhoto.ImprintColor,
	PillPhoto.ImprintSymbol,
	vv_PillColor.Colors
	FROM Pill
	JOIN GenericDrug ON Pill.GenericDrugSer = GenericDrug.GenericDrugSer
	JOIN PillPhoto ON Pill.PillSer = PillPhoto.PillSer
    JOIN vv_PillColor ON Pill.PillSer = vv_PillColor.PillSer;
