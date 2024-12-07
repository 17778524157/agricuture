-- MySQL dump 10.13  Distrib 8.3.0, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: agriculture
-- ------------------------------------------------------
-- Server version	8.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `produce` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '产品',
  `warehouse` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '仓库',
  `region` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '存储区',
  `number` int DEFAULT NULL COMMENT '数量',
  `keeper` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '仓库管理员',
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory`
--

LOCK TABLES `inventory` WRITE;
/*!40000 ALTER TABLE `inventory` DISABLE KEYS */;
INSERT INTO `inventory` VALUES (1,'小麦','1号仓','产品区',1000,'哈哈哈','无'),(2,'农业无人机','1号仓','设备区',2,'哈哈哈','无'),(3,'尿素','1号仓','农资区',50,'哈哈哈','无'),(4,'多功能拖拉机','2号仓','设备区',1,'哈哈哈','无'),(5,'除草剂','2号仓','农资区',50,'哈哈哈','无'),(6,'11','11','1',1,'哈哈哈','1');
/*!40000 ALTER TABLE `inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notice`
--

DROP TABLE IF EXISTS `notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notice` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci COMMENT '内容',
  `time` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '发布时间',
  `user` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '发布人',
  `img` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '封面',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notice`
--

LOCK TABLES `notice` WRITE;
/*!40000 ALTER TABLE `notice` DISABLE KEYS */;
INSERT INTO `notice` VALUES (10,'开启智慧农业元年','日前，农业农村部印发《全国智慧农业行动计划（2024—2028年）》（以下简称《行动计划》），提出在公共服务能力建设上，加快打造国家农业农村大数据平台、农业农村用地“一张图”和基础模型算法等公共服务产品；在产业布局上，着力推动主要作物大面积单产提升，培育一批智慧农场、智慧牧场、智慧渔场，推进全产业链数字化改造；在示范带动上，支持浙江先行先试，探索推广“伏羲农场”等未来应用场景。　　','2024-11-30 00:00:00','admin','http://localhost:9090/file/1de1a2792fef41919174c1f0388750ca.jpg');
/*!40000 ALTER TABLE `notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchase`
--

DROP TABLE IF EXISTS `purchase`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `product` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '产品',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格（元）',
  `number` int DEFAULT NULL COMMENT '采购数量',
  `provider` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '供应商',
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '地址',
  `phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '电话',
  `purchaser` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '采购人',
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase`
--

LOCK TABLES `purchase` WRITE;
/*!40000 ALTER TABLE `purchase` DISABLE KEYS */;
INSERT INTO `purchase` VALUES (1,'尿素',200.00,50,'河南尿素厂','河南省南阳市','15938877193','王五五','无'),(2,'除草剂',15.00,50,'河北农资厂','河北保定','15830097645','王五五','无');
/*!40000 ALTER TABLE `purchase` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sales`
--

DROP TABLE IF EXISTS `sales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sales` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `product` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '产品',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格（元）',
  `number` int DEFAULT NULL COMMENT '采购数量',
  `buyer` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '采购商',
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '地址',
  `phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '电话',
  `shipper` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '出货人',
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sales`
--

LOCK TABLES `sales` WRITE;
/*!40000 ALTER TABLE `sales` DISABLE KEYS */;
INSERT INTO `sales` VALUES (1,'小麦',2.00,1000,'想念公司','河南省南阳市','18638760772','李四四','无'),(2,'玉米',3.45,3000,'标点食品厂','河南南阳','15938876234','李四四','无'),(3,'大豆',4.45,1500,'向阳豆腐厂','北京','18638769078','李四四','无');
/*!40000 ALTER TABLE `sales` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `statistic`
--

DROP TABLE IF EXISTS `statistic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `statistic` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `farm` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '农田名称',
  `area` int DEFAULT NULL COMMENT '面积',
  `address` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '地址',
  `crop` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '作物名称',
  `number` int DEFAULT NULL COMMENT '数量',
  `state` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '生长状态',
  `temperature` decimal(11,1) DEFAULT NULL COMMENT '温度（℃）',
  `airhumidity` int DEFAULT NULL COMMENT '空气湿度（%）',
  `soilhumidity` int DEFAULT NULL COMMENT '土壤湿度（%）',
  `carbon` int DEFAULT NULL COMMENT 'CO2含量ppm',
  `ph` decimal(11,2) DEFAULT NULL COMMENT '土壤PH值',
  `light` int DEFAULT NULL COMMENT '光照强度(lux)',
  `filllight` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '补光灯状态',
  `monitor` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '摄像头状态',
  `pump` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '水泵状态',
  `keeper` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '农田负责人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `statistic`
--

LOCK TABLES `statistic` WRITE;
/*!40000 ALTER TABLE `statistic` DISABLE KEYS */;
INSERT INTO `statistic` VALUES (1,'1号田',3,'产业园1区','小麦',2000,'正常',26.0,40,70,1000,8.20,2000,'关闭','开启','关闭','张三三'),(2,'2号田',5,'产业园2区','玉米',3500,'正常',27.0,76,56,980,7.40,1000,'开启','开启','关闭','张三三'),(3,'3号田',3,'产业园3区','大豆',4000,'正常',30.0,69,45,789,7.50,2000,'关闭','开启','关闭','张三三'),(4,'4号田',10,'产业园2区','棉花',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'张三三');
/*!40000 ALTER TABLE `statistic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict`
--

DROP TABLE IF EXISTS `sys_dict`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '名称',
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '内容',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=281 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict`
--

LOCK TABLES `sys_dict` WRITE;
/*!40000 ALTER TABLE `sys_dict` DISABLE KEYS */;
INSERT INTO `sys_dict` VALUES (1,'user','el-icon-user','icon'),(2,'house','el-icon-house','icon'),(3,'menu','el-icon-menu','icon'),(4,'s-custom','el-icon-s-custom','icon'),(5,'s-grid','el-icon-s-grid','icon'),(6,'document','el-icon-document','icon'),(7,'coffee','el-icon-coffee\r\n','icon'),(8,'s-marketing','el-icon-s-marketing','icon'),(9,'phone-outline','el-icon-phone-outline','icon'),(10,'platform-eleme','el-icon-platform-eleme','icon'),(11,'eleme','el-icon-eleme','icon'),(12,'delete-solid','el-icon-delete-solid','icon'),(13,'delete','el-icon-delete','icon'),(14,'s-tools','el-icon-s-tools','icon'),(15,'setting','el-icon-setting','icon'),(16,'user-solid','el-icon-user-solid','icon'),(17,'phone','el-icon-phone','icon'),(18,'more','el-icon-more','icon'),(19,'more-outline','el-icon-more-outline','icon'),(20,'star-on','el-icon-star-on','icon'),(21,'star-off','el-icon-star-off','icon'),(22,'s-goods','el-icon-s-goods','icon'),(23,'goods','el-icon-goods','icon'),(24,'warning','el-icon-warning','icon'),(25,'warning-outline','el-icon-warning-outline','icon'),(26,'question','el-icon-question','icon'),(27,'info','el-icon-info','icon'),(28,'remove','el-icon-remove','icon'),(29,'circle-plus','el-icon-circle-plus','icon'),(30,'success','el-icon-success','icon'),(31,'error','el-icon-error','icon'),(32,'zoom-in','el-icon-zoom-in','icon'),(33,'zoom-out','el-icon-zoom-out','icon'),(34,'remove-outline','el-icon-remove-outline','icon'),(35,'circle-plus-outline','el-icon-circle-plus-outline','icon'),(36,'circle-check','el-icon-circle-check','icon'),(37,'circle-close','el-icon-circle-close','icon'),(38,'s-help','el-icon-s-help','icon'),(39,'help','el-icon-help','icon'),(40,'minus','el-icon-minus','icon'),(41,'plus','el-icon-plus','icon'),(42,'check','el-icon-check','icon'),(43,'close','el-icon-close','icon'),(44,'picture','el-icon-picture','icon'),(45,'picture-outline','el-icon-picture-outline','icon'),(46,'picture-outline-round','el-icon-picture-outline-round','icon'),(47,'upload','el-icon-upload','icon'),(48,'upload2','el-icon-upload2','icon'),(49,'download','el-icon-download','icon'),(50,'camera-solid','el-icon-camera-solid','icon'),(51,'camera','el-icon-camera','icon'),(52,'video-camera-solid','el-icon-video-camera-solid','icon'),(53,'video-camera','el-icon-video-camera','icon'),(54,'message-solid','el-icon-message-solid','icon'),(55,'bell','el-icon-bell','icon'),(56,'s-cooperation','el-icon-s-cooperation','icon'),(57,'s-order','el-icon-s-order','icon'),(58,'s-platform','el-icon-s-platform','icon'),(59,'s-fold','el-icon-s-fold','icon'),(60,'s-unfold','el-icon-s-unfold','icon'),(61,'s-operation','el-icon-s-operation','icon'),(62,'s-promotion','el-icon-s-promotion','icon'),(63,'s-home','el-icon-s-home','icon'),(64,'s-release','el-icon-s-release','icon'),(65,'s-ticket','el-icon-s-ticket','icon'),(66,'s-management','el-icon-s-management','icon'),(67,'s-open','el-icon-s-open','icon'),(68,'s-shop','el-icon-s-shop','icon'),(69,'s-flag','el-icon-s-flag','icon'),(70,'s-comment','el-icon-s-comment','icon'),(71,'s-finance','el-icon-s-finance','icon'),(72,'s-claim','el-icon-s-claim','icon'),(73,'s-opportunity','el-icon-s-opportunity','icon'),(74,'s-data','el-icon-s-data','icon'),(75,'s-check','el-icon-s-check','icon'),(76,'share','el-icon-share','icon'),(77,'d-caret','el-icon-d-caret','icon'),(78,'caret-left','el-icon-caret-left','icon'),(79,'caret-right','el-icon-caret-right','icon'),(80,'caret-bottom','el-icon-caret-bottom','icon'),(81,'caret-top','el-icon-caret-top','icon'),(82,'bottom-left','el-icon-bottom-left','icon'),(83,'bottom-right','el-icon-bottom-right','icon'),(84,'back','el-icon-back','icon'),(85,'right','el-icon-right','icon'),(86,'bottom','el-icon-bottom','icon'),(87,'top','el-icon-top','icon'),(88,'top-left','el-icon-top-left','icon'),(89,'top-right','el-icon-top-right','icon'),(90,'arrow-left','el-icon-arrow-left','icon'),(91,'arrow-right','el-icon-arrow-right','icon'),(92,'arrow-down','el-icon-arrow-down','icon'),(93,'arrow-up','el-icon-arrow-up','icon'),(94,'d-arrow-left','el-icon-d-arrow-left','icon'),(95,'d-arrow-right','el-icon-d-arrow-right','icon'),(96,'video-pause','el-icon-video-pause','icon'),(97,'video-play','el-icon-video-play','icon'),(98,'refresh','el-icon-refresh','icon'),(99,'refresh-right','el-icon-refresh-right','icon'),(100,'refresh-left','el-icon-refresh-left','icon'),(101,'finished','el-icon-finished','icon'),(102,'sort','el-icon-sort','icon'),(103,'sort-up','el-icon-sort-up','icon'),(104,'sort-down','el-icon-sort-down','icon'),(105,'rank','el-icon-rank','icon'),(106,'loading','el-icon-loading','icon'),(107,'view','el-icon-view','icon'),(108,'c-scale-to-original','el-icon-c-scale-to-original','icon'),(109,'date','el-icon-date','icon'),(110,'edit','el-icon-edit','icon'),(111,'edit-outline','el-icon-edit-outline','icon'),(112,'folder','el-icon-folder','icon'),(113,'folder-opened','el-icon-folder-opened','icon'),(114,'folder-add','el-icon-folder-add','icon'),(115,'folder-remove','el-icon-folder-remove','icon'),(116,'folder-delete','el-icon-folder-delete','icon'),(117,'folder-checked','el-icon-folder-checked','icon'),(118,'tickets','el-icon-tickets','icon'),(119,'document-remove','el-icon-document-remove','icon'),(120,'document-delete','el-icon-document-delete','icon'),(121,'document-copy','el-icon-document-copy','icon'),(122,'document-checked','el-icon-document-checked','icon'),(123,'document-add','el-icon-document-add','icon'),(124,'printer','el-icon-printer','icon'),(125,'paperclip','el-icon-paperclip','icon'),(126,'takeaway-box','el-icon-takeaway-box','icon'),(127,'search','el-icon-search','icon'),(128,'monitor','el-icon-monitor','icon'),(129,'attract','el-icon-attract','icon'),(130,'mobile','el-icon-mobile','icon'),(131,'scissors','el-icon-scissors','icon'),(132,'umbrella','el-icon-umbrella','icon'),(133,'headset','el-icon-headset','icon'),(134,'brush','el-icon-brush','icon'),(135,'mouse','el-icon-mouse','icon'),(136,'coordinate','el-icon-coordinate','icon'),(137,'magic-stick','el-icon-magic-stick','icon'),(138,'reading','el-icon-reading','icon'),(139,'data-line','el-icon-data-line','icon'),(140,'data-board','el-icon-data-board','icon'),(141,'pie-chart','el-icon-pie-chart','icon'),(142,'data-analysis','el-icon-data-analysis','icon'),(143,'collection-tag','el-icon-collection-tag','icon'),(144,'film','el-icon-film','icon'),(145,'suitcase','el-icon-suitcase','icon'),(146,'suitcase-1','el-icon-suitcase-1','icon'),(147,'receiving','el-icon-receiving','icon'),(148,'collection','el-icon-collection','icon'),(149,'files','el-icon-files','icon'),(150,'notebook-1','el-icon-notebook-1','icon'),(151,'notebook-2','el-icon-notebook-2','icon'),(152,'toilet-paper','el-icon-toilet-paper','icon'),(153,'office-building','el-icon-office-building','icon'),(154,'school','el-icon-school','icon'),(155,'table-lamp','el-icon-table-lamp','icon'),(156,'no-smoking','el-icon-no-smoking','icon'),(157,'smoking','el-icon-smoking','icon'),(158,'shopping-cart-full','el-icon-shopping-cart-full','icon'),(159,'shopping-cart-1','el-icon-shopping-cart-1','icon'),(160,'shopping-cart-2','el-icon-shopping-cart-2','icon'),(161,'shopping-bag-1','el-icon-shopping-bag-1','icon'),(162,'shopping-bag-2','el-icon-shopping-bag-2','icon'),(163,'sold-out','el-icon-sold-out','icon'),(164,'sell','el-icon-sell','icon'),(165,'present','el-icon-present','icon'),(166,'box','el-icon-box','icon'),(167,'bank-card','el-icon-bank-card','icon'),(168,'money','el-icon-money','icon'),(169,'coin','el-icon-coin','icon'),(170,'wallet','el-icon-wallet','icon'),(171,'discount','el-icon-discount','icon'),(172,'price-tag','el-icon-price-tag','icon'),(173,'news','el-icon-news','icon'),(174,'guide','el-icon-guide','icon'),(175,'male','el-icon-male','icon'),(176,'female','el-icon-female','icon'),(177,'thumb','el-icon-thumb','icon'),(178,'cpu','el-icon-cpu','icon'),(179,'link','el-icon-link','icon'),(180,'connection','el-icon-connection','icon'),(181,'open','el-icon-open','icon'),(182,'turn-off','el-icon-turn-off','icon'),(183,'set-up','el-icon-set-up','icon'),(184,'chat-round','el-icon-chat-round','icon'),(185,'chat-line-round','el-icon-chat-line-round','icon'),(186,'chat-square','el-icon-chat-square','icon'),(187,'chat-dot-round','el-icon-chat-dot-round','icon'),(188,'chat-dot-square','el-icon-chat-dot-square','icon'),(189,'chat-line-square','el-icon-chat-line-square','icon'),(190,'message','el-icon-message','icon'),(191,'postcard','el-icon-postcard','icon'),(192,'position','el-icon-position','icon'),(193,'turn-off-microphone','el-icon-turn-off-microphone','icon'),(194,'microphone','el-icon-microphone','icon'),(195,'close-notification','el-icon-close-notification','icon'),(196,'bangzhu','el-icon-bangzhu','icon'),(197,'time','el-icon-time','icon'),(198,'odometer','el-icon-odometer','icon'),(199,'crop','el-icon-crop','icon'),(200,'aim','el-icon-aim','icon'),(201,'switch-button','el-icon-switch-button','icon'),(202,'full-screen','el-icon-full-screen','icon'),(203,'copy-document','el-icon-copy-document','icon'),(204,'mic','el-icon-mic','icon'),(205,'stopwatch','el-icon-stopwatch','icon'),(206,'medal-1','el-icon-medal-1','icon'),(207,'medal','el-icon-medal','icon'),(208,'trophy','el-icon-trophy','icon'),(209,'trophy-1','el-icon-trophy-1','icon'),(210,'first-aid-kit','el-icon-first-aid-kit','icon'),(211,'discover','el-icon-discover','icon'),(212,'place','el-icon-place','icon'),(213,'location','el-icon-location','icon'),(214,'location-outline','el-icon-location-outline','icon'),(215,'location-information','el-icon-location-information','icon'),(216,'add-location','el-icon-add-location','icon'),(217,'delete-location','el-icon-delete-location','icon'),(218,'map-location','el-icon-map-location','icon'),(219,'alarm-clock','el-icon-alarm-clock','icon'),(220,'timer','el-icon-timer','icon'),(221,'watch-1','el-icon-watch-1','icon'),(222,'watch','el-icon-watch','icon'),(223,'lock','el-icon-lock','icon'),(224,'unlock','el-icon-unlock','icon'),(225,'key','el-icon-key','icon'),(226,'service','el-icon-service','icon'),(227,'mobile-phone','el-icon-mobile-phone','icon'),(228,'bicycle','el-icon-bicycle','icon'),(229,'truck','el-icon-truck','icon'),(230,'ship','el-icon-ship','icon'),(231,'basketball','el-icon-basketball','icon'),(232,'football','el-icon-football','icon'),(233,'soccer','el-icon-soccer','icon'),(234,'baseball','el-icon-baseball','icon'),(235,'wind-power','el-icon-wind-power','icon'),(236,'light-rain','el-icon-light-rain','icon'),(237,'lightning','el-icon-lightning','icon'),(238,'heavy-rain','el-icon-heavy-rain','icon'),(239,'sunrise','el-icon-sunrise','icon'),(240,'sunrise-1','el-icon-sunrise-1','icon'),(241,'sunset','el-icon-sunset','icon'),(242,'sunny','el-icon-sunny','icon'),(243,'cloudy','el-icon-cloudy','icon'),(244,'partly-cloudy','el-icon-partly-cloudy','icon'),(245,'cloudy-and-sunny','el-icon-cloudy-and-sunny','icon'),(246,'moon','el-icon-moon','icon'),(247,'moon-night','el-icon-moon-night','icon'),(248,'dish','el-icon-dish','icon'),(249,'dish-1','el-icon-dish-1','icon'),(250,'food','el-icon-food','icon'),(251,'chicken','el-icon-chicken','icon'),(252,'fork-spoon','el-icon-fork-spoon','icon'),(253,'knife-fork','el-icon-knife-fork','icon'),(254,'burger','el-icon-burger','icon'),(255,'tableware','el-icon-tableware','icon'),(256,'sugar','el-icon-sugar','icon'),(257,'dessert','el-icon-dessert','icon'),(258,'ice-cream','el-icon-ice-cream','icon'),(259,'hot-water','el-icon-hot-water','icon'),(260,'water-cup','el-icon-water-cup','icon'),(261,'coffee-cup','el-icon-coffee-cup','icon'),(262,'cold-drink','el-icon-cold-drink','icon'),(263,'goblet','el-icon-goblet','icon'),(264,'goblet-full','el-icon-goblet-full','icon'),(265,'goblet-square','el-icon-goblet-square','icon'),(266,'goblet-square-full','el-icon-goblet-square-full','icon'),(267,'refrigerator','el-icon-refrigerator','icon'),(268,'grape','el-icon-grape','icon'),(269,'watermelon','el-icon-watermelon','icon'),(270,'cherry','el-icon-cherry','icon'),(271,'apple','el-icon-apple','icon'),(272,'pear','el-icon-pear','icon'),(273,'orange','el-icon-orange','icon'),(274,'ice-tea','el-icon-ice-tea','icon'),(275,'ice-drink','el-icon-ice-drink','icon'),(276,'milk-tea','el-icon-milk-tea','icon'),(277,'potato-strips','el-icon-potato-strips','icon'),(278,'lollipop','el-icon-lollipop','icon'),(279,'ice-cream-square','el-icon-ice-cream-square','icon'),(280,'ice-cream-round','el-icon-ice-cream-round','icon');
/*!40000 ALTER TABLE `sys_dict` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_file`
--

DROP TABLE IF EXISTS `sys_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_file` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件名称',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件类型',
  `size` bigint DEFAULT NULL COMMENT '文件大小(kb)',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '下载链接',
  `md5` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件md5',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `enable` tinyint(1) DEFAULT '1' COMMENT '是否禁用链接',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_file`
--

LOCK TABLES `sys_file` WRITE;
/*!40000 ALTER TABLE `sys_file` DISABLE KEYS */;
INSERT INTO `sys_file` VALUES (39,'92effbce5b51049a0fe0171b6b2f4587.jpeg','jpeg',119,'http://localhost:9090/file/056319a354b9496b91eba75a70a7f06e.jpeg','b23aca3520ca81d95db48a45e442fb00',0,1),(40,'1.jpg','jpg',1302,'http://localhost:9090/file/006d5c13b81644a4a2e9336d2f935165.jpg','ef953abb79e483660d123abaa548555f',0,1),(41,'77cc879803f94c57e5a6acbbb6ef482.jpg','jpg',292,'http://localhost:9090/file/1de1a2792fef41919174c1f0388750ca.jpg','60439ed5e0f60f618c3ff1774540ea42',0,1),(42,'77cc879803f94c57e5a6acbbb6ef482.jpg','jpg',292,'http://localhost:9090/file/1de1a2792fef41919174c1f0388750ca.jpg','60439ed5e0f60f618c3ff1774540ea42',0,1),(43,'77cc879803f94c57e5a6acbbb6ef482.jpg','jpg',292,'http://localhost:9090/file/1de1a2792fef41919174c1f0388750ca.jpg','60439ed5e0f60f618c3ff1774540ea42',0,1),(44,'77cc879803f94c57e5a6acbbb6ef482.jpg','jpg',292,'http://localhost:9090/file/1de1a2792fef41919174c1f0388750ca.jpg','60439ed5e0f60f618c3ff1774540ea42',0,1);
/*!40000 ALTER TABLE `sys_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_menu` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '名称',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '路径',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `pid` int DEFAULT NULL COMMENT '父级id',
  `page_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '页面路径',
  `sort_num` int DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (4,'系统管理',NULL,'el-icon-s-grid',NULL,NULL,NULL,700),(5,'职工管理','/user','el-icon-user',NULL,4,'User',301),(6,'角色管理','/role','el-icon-s-custom',NULL,4,'Role',302),(7,'菜单管理','/menu','el-icon-menu',NULL,4,'Menu',303),(10,'主页','/home','el-icon-house',NULL,NULL,'Home',300),(13,'农资管理','','el-icon-s-goods',NULL,NULL,'',600),(15,'产品出售','/sales','el-icon-s-cooperation',NULL,13,'Sales',2),(21,'农田管理','/farmland','el-icon-menu',NULL,NULL,'Farmland',560),(23,'农资采购','/purchase','el-icon-shopping-cart-full',NULL,13,'Purchase',1),(27,'库存','/inventory','el-icon-house',NULL,13,'Inventory',3),(28,'统计分析','','el-icon-s-data',NULL,NULL,'',550),(29,'农田信息','/statistic','el-icon-data-line',NULL,28,'Statistic',1),(30,'专家咨询','/im','el-icon-s-comment',NULL,28,'Im',2),(39,'公告管理','/notice','el-icon-news',NULL,4,'Notice',300),(40,'环境监测','/dashbord','el-icon-s-marketing',NULL,28,'Dashbord',0);
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `flag` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '唯一标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'系统管理员','','ROLE_ADMIN'),(2,'农田负责人','','ROLE_USER1'),(3,'仓库管理员',NULL,'ROLE_USER2'),(4,'销售人员',NULL,'ROLE_USER3'),(5,'采购人员',NULL,'ROLE_USER4'),(6,'农业专家',NULL,'ROLE_USER5');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_menu` (
  `role_id` int NOT NULL COMMENT '角色id',
  `menu_id` int NOT NULL COMMENT '菜单id',
  PRIMARY KEY (`role_id`,`menu_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='角色菜单关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_menu`
--

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` VALUES (1,4),(1,5),(1,6),(1,7),(1,10),(1,13),(1,15),(1,21),(1,23),(1,27),(1,28),(1,29),(1,30),(1,39),(1,40),(2,10),(2,21),(2,28),(2,29),(2,30),(2,40),(3,10),(3,13),(3,27),(4,10),(4,13),(4,15),(5,10),(5,13),(5,15),(5,23),(5,27),(6,10),(6,28),(6,29),(6,30),(6,40);
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户名',
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','123456','admin','admin@qq.com','18240589365','广州应用科技学院','2024-10-19 13:47:05','http://localhost:9090/file/c707313cb33744b181b80fc962ec77ff.jpg','ROLE_ADMIN'),(2,'张三三','123456','张三三','2265334234@qq.com','15837746295','北京','2024-10-22 08:14:35',NULL,'ROLE_USER1'),(3,'哈哈哈','123456','哈哈哈','2327900989@qq.com','15219222126','广州应用科技学院','2024-12-02 11:06:14',NULL,'ROLE_USER2'),(4,'李四四','123456','李四四','4326774837@qq.com','15936657824','广州','2024-10-22 08:22:22',NULL,'ROLE_USER3'),(5,'王五五','123456','王五五','3246557649@qq.com','18639735674','浙江','2024-11-22 08:26:11',NULL,'ROLE_USER4'),(6,'孙一一','123456','孙一一','2266554476@qq.com','18764976964','河北','2024-11-22 08:43:20',NULL,'ROLE_USER5');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-12-07 14:01:10
