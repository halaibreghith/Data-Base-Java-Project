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
-- Table structure for table `consumes`
--

DROP TABLE IF EXISTS `consumes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consumes` (
  `consumption_date` date DEFAULT NULL,
  `prescription_id` int DEFAULT NULL,
  `dosage` varchar(30) DEFAULT NULL,
  `duration` varchar(30) DEFAULT NULL,
  `customer_id` int NOT NULL,
  `barcode` int NOT NULL,
  PRIMARY KEY (`customer_id`,`barcode`),
  KEY `barcode` (`barcode`),
  CONSTRAINT `consumes_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `consumes_ibfk_2` FOREIGN KEY (`barcode`) REFERENCES `medicine` (`barcode`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consumes`
--

LOCK TABLES `consumes` WRITE;
/*!40000 ALTER TABLE `consumes` DISABLE KEYS */;
INSERT INTO `consumes` VALUES ('2023-09-07',0,'Oral','5 Days',100,100),('2023-10-01',1,'Oral','14 Days',100,101),('2023-10-01',1,'Oral','14 Days',100,102),('2023-05-01',0,'Oral','1 Day',100,103),('2023-05-25',1,'Oral','7 Days',101,100),('2023-02-25',1,'Oral','7 Days',101,101),('2023-05-25',0,'Oral','10 Days',102,100),('2023-10-01',1,'Oral','14 Days',102,101);
/*!40000 ALTER TABLE `consumes` ENABLE KEYS */;
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
