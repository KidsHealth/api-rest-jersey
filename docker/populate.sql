USE `kidshealth`;

-- USERS 
INSERT INTO `User` VALUES(1, "Miguel", "González", "sosa@uma.es", "sosa");
INSERT INTO `User` VALUES(2, "Samuel", "Delgado", "samdelfer@uma.es", "samdelfer");
INSERT INTO `User` VALUES(3, "Victoria", "Cruz", "cruzpazv@uma.es", "cruzpazv");

-- DOCTOR 
INSERT INTO `Doctor` VALUES(3);

-- PATIENTRESPONSIBLE
INSERT INTO `PatientResponsible` VALUES("1234", "Camino del mundo, 3", 1);
INSERT INTO `PatientResponsible` VALUES("5678", "Camino de la luna, 5", 2); 

-- PATIENTS
INSERT INTO `Patient` VALUES(1, "Manuela", "González", '2015-09-10', 1, 3);
INSERT INTO `Patient` VALUES(2, "Pepe", "González", '2019-01-20', 1, 3);
INSERT INTO `Patient` VALUES(3, "Juan", "González", '2018-05-17', 1, 3);
INSERT INTO `Patient` VALUES(4, "Francisco", "Delgado", '2016-01-02', 2, 3);
INSERT INTO `Patient` VALUES(5, "Josefa", "Delgado", '2017-11-05', 2, 3);

-- MESSAGES
INSERT INTO `Message` VALUES(1, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.", 
							'2016-05-12 12:03:00', 1, 1, 3); 
INSERT INTO `Message` VALUES(2, "njcnjkcanjksancas.", '2017-04-20 21:05:00', 0, 4, 3);

-- EVENT
INSERT INTO `Event` VALUES(1, "First doctor appointment", "nuicsancasbhas", "HOSPITAL", '2023-02-05','2023-02-10',"FIRST", 2);

-- DOCUMENT
INSERT INTO `Document` VALUES("", "ANALYTIC", 1);

-- DISEASE CONTRACTION
INSERT INTO `DiseaseContraction` VALUES(3,1,"ASTHMA INFLAMMATORY DISEASE", "ASTHMA", '2018-11-02');
INSERT INTO `DiseaseContraction` VALUES(5,2,"MEASLES VIRUS", "MEASLES", '2019-01-16');

-- MEDICATION
INSERT INTO `Medication` VALUES(1, "INHALED CORTICOSTEROIDS", "FLUTICASONE",1, 2, '2018-11-02', '2019-01-31', 1);
