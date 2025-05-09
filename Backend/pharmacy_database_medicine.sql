CREATE DATABASE  IF NOT EXISTS `pharmacy_database` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `pharmacy_database`;
-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: pharmacy_database
-- ------------------------------------------------------
-- Server version	8.2.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `medicine`
--

DROP TABLE IF EXISTS `medicine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medicine` (
  `medicine_name` varchar(30) DEFAULT NULL,
  `generic_name` varchar(30) DEFAULT NULL,
  `manufacturer` varchar(30) DEFAULT NULL,
  `production_date` date DEFAULT NULL,
  `expired_date` date DEFAULT NULL,
  `stock` int DEFAULT NULL,
  `price` double DEFAULT NULL,
  `prescription` tinyint(1) DEFAULT NULL,
  `route_usage` varchar(30) DEFAULT NULL,
  `storaging` varchar(30) DEFAULT NULL,
  `strength` double DEFAULT NULL,
  `dosage_form` varchar(30) DEFAULT NULL,
  `barcode` int NOT NULL,
  `manufacturer_id` int DEFAULT NULL,
  PRIMARY KEY (`barcode`),
  KEY `manufacturer_id` (`manufacturer_id`),
  CONSTRAINT `medicine_ibfk_1` FOREIGN KEY (`manufacturer_id`) REFERENCES `medicinecompany` (`manufacturer_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medicine`
--

LOCK TABLES `medicine` WRITE;
/*!40000 ALTER TABLE `medicine` DISABLE KEYS */;
INSERT INTO `medicine` VALUES ('Acamol','Paracetamol','Shefa','2023-05-27','2026-05-27',550,12,0,'Oral','Normal',500,'Tablet',100,2),('Panadol','Paracetamol','AlQuds','2022-05-27','2027-05-27',1400,10,0,'Oral','Normal',500,'Tablet',101,1),('Nexium','Nexium','Birzeit','2020-09-01','2025-09-01',45,42,0,'Oral','Normal',40,'Oral',102,1),('Nexium','Nexium','Birzeit','2020-09-01','2025-09-01',45,42,0,'Oral','Normal',20,'Oral',103,1);
/*!40000 ALTER TABLE `medicine` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-12-18  0:12:33
