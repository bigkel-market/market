-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: localhost    Database: market_backstage
-- ------------------------------------------------------
-- Server version	8.0.28

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
-- Table structure for table `qrtz_blob_triggers`
--

DROP TABLE IF EXISTS `qrtz_blob_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_blob_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `BLOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `SCHED_NAME` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_blob_triggers`
--

LOCK TABLES `qrtz_blob_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_blob_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_blob_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_calendars`
--

DROP TABLE IF EXISTS `qrtz_calendars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_calendars` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `CALENDAR_NAME` varchar(200) NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_calendars`
--

LOCK TABLES `qrtz_calendars` WRITE;
/*!40000 ALTER TABLE `qrtz_calendars` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_calendars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_cron_triggers`
--

DROP TABLE IF EXISTS `qrtz_cron_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_cron_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `CRON_EXPRESSION` varchar(120) NOT NULL,
  `TIME_ZONE_ID` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_cron_triggers`
--

LOCK TABLES `qrtz_cron_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_cron_triggers` DISABLE KEYS */;
INSERT INTO `qrtz_cron_triggers` VALUES ('RenrenScheduler','TASK_1','DEFAULT','0 0/30 * * * ?','Asia/Shanghai');
/*!40000 ALTER TABLE `qrtz_cron_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_fired_triggers`
--

DROP TABLE IF EXISTS `qrtz_fired_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_fired_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `ENTRY_ID` varchar(95) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `FIRED_TIME` bigint NOT NULL,
  `SCHED_TIME` bigint NOT NULL,
  `PRIORITY` int NOT NULL,
  `STATE` varchar(16) NOT NULL,
  `JOB_NAME` varchar(200) DEFAULT NULL,
  `JOB_GROUP` varchar(200) DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`ENTRY_ID`),
  KEY `IDX_QRTZ_FT_TRIG_INST_NAME` (`SCHED_NAME`,`INSTANCE_NAME`),
  KEY `IDX_QRTZ_FT_INST_JOB_REQ_RCVRY` (`SCHED_NAME`,`INSTANCE_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_QRTZ_FT_J_G` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_FT_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_FT_T_G` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_FT_TG` (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_fired_triggers`
--

LOCK TABLES `qrtz_fired_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_fired_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_fired_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_job_details`
--

DROP TABLE IF EXISTS `qrtz_job_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_job_details` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) NOT NULL,
  `IS_DURABLE` varchar(1) NOT NULL,
  `IS_NONCONCURRENT` varchar(1) NOT NULL,
  `IS_UPDATE_DATA` varchar(1) NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) NOT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_J_REQ_RECOVERY` (`SCHED_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_QRTZ_J_GRP` (`SCHED_NAME`,`JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_job_details`
--

LOCK TABLES `qrtz_job_details` WRITE;
/*!40000 ALTER TABLE `qrtz_job_details` DISABLE KEYS */;
INSERT INTO `qrtz_job_details` VALUES ('RenrenScheduler','TASK_1','DEFAULT',NULL,'io.renren.modules.job.utils.ScheduleJob','0','0','0','0',_binary '�\�\0sr\0org.quartz.JobDataMap���迩�\�\0\0xr\0&org.quartz.utils.StringKeyDirtyFlagMap�\�\��\�](\0Z\0allowsTransientDataxr\0org.quartz.utils.DirtyFlagMap\�.�(v\n\�\0Z\0dirtyL\0mapt\0Ljava/util/Map;xpsr\0java.util.HashMap\��\�`\�\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0\rJOB_PARAM_KEYsr\0.io.renren.modules.job.entity.ScheduleJobEntity\0\0\0\0\0\0\0\0L\0beanNamet\0Ljava/lang/String;L\0\ncreateTimet\0Ljava/util/Date;L\0cronExpressionq\0~\0	L\0jobIdt\0Ljava/lang/Long;L\0paramsq\0~\0	L\0remarkq\0~\0	L\0statust\0Ljava/lang/Integer;xpt\0testTasksr\0java.util.Datehj�KYt\0\0xpw\0\0�*3Pxt\00 0/30 * * * ?sr\0java.lang.Long;�\�̏#\�\0J\0valuexr\0java.lang.Number����\��\0\0xp\0\0\0\0\0\0\0t\0renrent\0参数测试sr\0java.lang.Integer⠤\���8\0I\0valuexq\0~\0\0\0\0\0x\0');
/*!40000 ALTER TABLE `qrtz_job_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_locks`
--

DROP TABLE IF EXISTS `qrtz_locks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_locks` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `LOCK_NAME` varchar(40) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_locks`
--

LOCK TABLES `qrtz_locks` WRITE;
/*!40000 ALTER TABLE `qrtz_locks` DISABLE KEYS */;
INSERT INTO `qrtz_locks` VALUES ('RenrenScheduler','STATE_ACCESS'),('RenrenScheduler','TRIGGER_ACCESS');
/*!40000 ALTER TABLE `qrtz_locks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_paused_trigger_grps`
--

DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_paused_trigger_grps` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_paused_trigger_grps`
--

LOCK TABLES `qrtz_paused_trigger_grps` WRITE;
/*!40000 ALTER TABLE `qrtz_paused_trigger_grps` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_paused_trigger_grps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_scheduler_state`
--

DROP TABLE IF EXISTS `qrtz_scheduler_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_scheduler_state` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `LAST_CHECKIN_TIME` bigint NOT NULL,
  `CHECKIN_INTERVAL` bigint NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_scheduler_state`
--

LOCK TABLES `qrtz_scheduler_state` WRITE;
/*!40000 ALTER TABLE `qrtz_scheduler_state` DISABLE KEYS */;
INSERT INTO `qrtz_scheduler_state` VALUES ('RenrenScheduler','BigKel-Yang1677585767042',1677588990603,15000);
/*!40000 ALTER TABLE `qrtz_scheduler_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_simple_triggers`
--

DROP TABLE IF EXISTS `qrtz_simple_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_simple_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `REPEAT_COUNT` bigint NOT NULL,
  `REPEAT_INTERVAL` bigint NOT NULL,
  `TIMES_TRIGGERED` bigint NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_simple_triggers`
--

LOCK TABLES `qrtz_simple_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_simple_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_simple_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_simprop_triggers`
--

DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_simprop_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `STR_PROP_1` varchar(512) DEFAULT NULL,
  `STR_PROP_2` varchar(512) DEFAULT NULL,
  `STR_PROP_3` varchar(512) DEFAULT NULL,
  `INT_PROP_1` int DEFAULT NULL,
  `INT_PROP_2` int DEFAULT NULL,
  `LONG_PROP_1` bigint DEFAULT NULL,
  `LONG_PROP_2` bigint DEFAULT NULL,
  `DEC_PROP_1` decimal(13,4) DEFAULT NULL,
  `DEC_PROP_2` decimal(13,4) DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_simprop_triggers`
--

LOCK TABLES `qrtz_simprop_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_simprop_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_simprop_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_triggers`
--

DROP TABLE IF EXISTS `qrtz_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint DEFAULT NULL,
  `PREV_FIRE_TIME` bigint DEFAULT NULL,
  `PRIORITY` int DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) NOT NULL,
  `TRIGGER_TYPE` varchar(8) NOT NULL,
  `START_TIME` bigint NOT NULL,
  `END_TIME` bigint DEFAULT NULL,
  `CALENDAR_NAME` varchar(200) DEFAULT NULL,
  `MISFIRE_INSTR` smallint DEFAULT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_T_J` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_T_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_T_C` (`SCHED_NAME`,`CALENDAR_NAME`),
  KEY `IDX_QRTZ_T_G` (`SCHED_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_T_STATE` (`SCHED_NAME`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_N_STATE` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_N_G_STATE` (`SCHED_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_NEXT_FIRE_TIME` (`SCHED_NAME`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_ST` (`SCHED_NAME`,`TRIGGER_STATE`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_ST_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_NFT_ST_MISFIRE_GRP` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `qrtz_job_details` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_triggers`
--

LOCK TABLES `qrtz_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_triggers` DISABLE KEYS */;
INSERT INTO `qrtz_triggers` VALUES ('RenrenScheduler','TASK_1','DEFAULT','TASK_1','DEFAULT',NULL,1677589200000,1677587400000,5,'WAITING','CRON',1658562218000,0,NULL,2,_binary '�\�\0sr\0org.quartz.JobDataMap���迩�\�\0\0xr\0&org.quartz.utils.StringKeyDirtyFlagMap�\�\��\�](\0Z\0allowsTransientDataxr\0org.quartz.utils.DirtyFlagMap\�.�(v\n\�\0Z\0dirtyL\0mapt\0Ljava/util/Map;xpsr\0java.util.HashMap\��\�`\�\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0\rJOB_PARAM_KEYsr\0.io.renren.modules.job.entity.ScheduleJobEntity\0\0\0\0\0\0\0\0L\0beanNamet\0Ljava/lang/String;L\0\ncreateTimet\0Ljava/util/Date;L\0cronExpressionq\0~\0	L\0jobIdt\0Ljava/lang/Long;L\0paramsq\0~\0	L\0remarkq\0~\0	L\0statust\0Ljava/lang/Integer;xpt\0testTasksr\0java.util.Datehj�KYt\0\0xpw\0\0�*3Pxt\00 0/30 * * * ?sr\0java.lang.Long;�\�̏#\�\0J\0valuexr\0java.lang.Number����\��\0\0xp\0\0\0\0\0\0\0t\0renrent\0参数测试sr\0java.lang.Integer⠤\���8\0I\0valuexq\0~\0\0\0\0\0x\0');
/*!40000 ALTER TABLE `qrtz_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schedule_job`
--

DROP TABLE IF EXISTS `schedule_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `schedule_job` (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务id',
  `bean_name` varchar(200) DEFAULT NULL COMMENT 'spring bean名称',
  `params` varchar(2000) DEFAULT NULL COMMENT '参数',
  `cron_expression` varchar(100) DEFAULT NULL COMMENT 'cron表达式',
  `status` tinyint DEFAULT NULL COMMENT '任务状态  0：正常  1：暂停',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schedule_job`
--

LOCK TABLES `schedule_job` WRITE;
/*!40000 ALTER TABLE `schedule_job` DISABLE KEYS */;
INSERT INTO `schedule_job` VALUES (1,'testTask','renren','0 0/30 * * * ?',0,'参数测试','2022-07-23 15:41:38');
/*!40000 ALTER TABLE `schedule_job` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schedule_job_log`
--

DROP TABLE IF EXISTS `schedule_job_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `schedule_job_log` (
  `log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务日志id',
  `job_id` bigint NOT NULL COMMENT '任务id',
  `bean_name` varchar(200) DEFAULT NULL COMMENT 'spring bean名称',
  `params` varchar(2000) DEFAULT NULL COMMENT '参数',
  `status` tinyint NOT NULL COMMENT '任务状态    0：成功    1：失败',
  `error` varchar(2000) DEFAULT NULL COMMENT '失败信息',
  `times` int NOT NULL COMMENT '耗时(单位：毫秒)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`log_id`),
  KEY `job_id` (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=135 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务日志';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schedule_job_log`
--

LOCK TABLES `schedule_job_log` WRITE;
/*!40000 ALTER TABLE `schedule_job_log` DISABLE KEYS */;
INSERT INTO `schedule_job_log` VALUES (1,1,'testTask','renren',0,NULL,0,'2022-08-14 17:30:00'),(2,1,'testTask','renren',0,NULL,0,'2022-08-14 22:00:00'),(3,1,'testTask','renren',0,NULL,3,'2022-08-15 21:30:00'),(4,1,'testTask','renren',0,NULL,1,'2022-08-15 22:00:00'),(5,1,'testTask','renren',0,NULL,1,'2022-08-16 22:00:00'),(6,1,'testTask','renren',0,NULL,0,'2022-08-20 10:00:00'),(7,1,'testTask','renren',0,NULL,0,'2022-08-20 10:30:00'),(8,1,'testTask','renren',0,NULL,0,'2022-08-20 11:00:00'),(9,1,'testTask','renren',0,NULL,0,'2022-08-20 11:30:00'),(10,1,'testTask','renren',0,NULL,1,'2022-08-20 13:30:00'),(11,1,'testTask','renren',0,NULL,1,'2022-08-20 14:00:00'),(12,1,'testTask','renren',0,NULL,1,'2022-08-20 14:30:00'),(13,1,'testTask','renren',0,NULL,1,'2022-08-20 15:00:00'),(14,1,'testTask','renren',0,NULL,0,'2022-08-20 15:30:00'),(15,1,'testTask','renren',0,NULL,1,'2022-08-20 16:00:00'),(16,1,'testTask','renren',0,NULL,1,'2022-08-20 16:30:00'),(17,1,'testTask','renren',0,NULL,1,'2022-08-20 17:00:00'),(18,1,'testTask','renren',0,NULL,1,'2022-08-21 20:30:00'),(19,1,'testTask','renren',0,NULL,10,'2022-08-21 21:00:00'),(20,1,'testTask','renren',0,NULL,4,'2022-08-21 21:30:00'),(21,1,'testTask','renren',0,NULL,0,'2022-08-21 22:00:00'),(22,1,'testTask','renren',0,NULL,0,'2022-08-25 21:30:00'),(23,1,'testTask','renren',0,NULL,0,'2022-08-25 22:00:00'),(24,1,'testTask','renren',0,NULL,1,'2022-08-28 21:00:00'),(25,1,'testTask','renren',0,NULL,1,'2022-08-28 21:30:00'),(26,1,'testTask','renren',0,NULL,1,'2022-08-28 22:00:00'),(27,1,'testTask','renren',0,NULL,0,'2022-08-31 21:30:00'),(28,1,'testTask','renren',0,NULL,1,'2022-09-01 21:30:00'),(29,1,'testTask','renren',0,NULL,1,'2022-09-01 22:00:00'),(30,1,'testTask','renren',0,NULL,0,'2022-09-02 21:30:00'),(31,1,'testTask','renren',0,NULL,1,'2022-09-02 22:00:00'),(32,1,'testTask','renren',0,NULL,1,'2022-09-03 21:30:00'),(33,1,'testTask','renren',0,NULL,0,'2022-09-03 22:00:00'),(34,1,'testTask','renren',0,NULL,1,'2022-09-04 14:00:00'),(35,1,'testTask','renren',0,NULL,1,'2022-09-04 14:30:00'),(36,1,'testTask','renren',0,NULL,1,'2022-09-04 15:00:00'),(37,1,'testTask','renren',0,NULL,1,'2022-09-04 15:30:00'),(38,1,'testTask','renren',0,NULL,1,'2022-09-04 16:00:00'),(39,1,'testTask','renren',0,NULL,0,'2022-09-04 16:30:00'),(40,1,'testTask','renren',0,NULL,0,'2022-09-04 17:00:00'),(41,1,'testTask','renren',0,NULL,0,'2022-09-04 17:30:00'),(42,1,'testTask','renren',0,NULL,0,'2022-09-04 18:00:00'),(43,1,'testTask','renren',0,NULL,0,'2022-09-04 18:30:00'),(44,1,'testTask','renren',0,NULL,0,'2022-09-04 19:00:00'),(45,1,'testTask','renren',0,NULL,0,'2022-09-04 19:30:00'),(46,1,'testTask','renren',0,NULL,0,'2022-09-04 20:00:00'),(47,1,'testTask','renren',0,NULL,2,'2022-09-04 20:30:00'),(48,1,'testTask','renren',0,NULL,0,'2022-09-04 21:00:00'),(49,1,'testTask','renren',0,NULL,1,'2022-09-04 21:30:00'),(50,1,'testTask','renren',0,NULL,3,'2022-09-05 21:30:00'),(51,1,'testTask','renren',0,NULL,2,'2022-09-06 21:30:00'),(52,1,'testTask','renren',0,NULL,1,'2022-09-07 21:30:00'),(53,1,'testTask','renren',0,NULL,0,'2022-09-07 22:00:00'),(54,1,'testTask','renren',0,NULL,1,'2022-09-08 21:30:00'),(55,1,'testTask','renren',0,NULL,1,'2022-09-08 22:00:00'),(56,1,'testTask','renren',0,NULL,1,'2022-09-11 14:30:00'),(57,1,'testTask','renren',0,NULL,0,'2022-09-11 15:00:00'),(58,1,'testTask','renren',0,NULL,1,'2022-09-11 15:30:00'),(59,1,'testTask','renren',0,NULL,1,'2022-09-11 16:00:00'),(60,1,'testTask','renren',0,NULL,1,'2022-09-11 16:30:00'),(61,1,'testTask','renren',0,NULL,1,'2022-09-11 17:00:00'),(62,1,'testTask','renren',0,NULL,1,'2022-09-11 17:30:00'),(63,1,'testTask','renren',0,NULL,0,'2022-09-11 18:00:00'),(64,1,'testTask','renren',0,NULL,1,'2022-09-11 18:30:00'),(65,1,'testTask','renren',0,NULL,1,'2022-09-11 19:00:00'),(66,1,'testTask','renren',0,NULL,1,'2022-09-11 19:30:00'),(67,1,'testTask','renren',0,NULL,0,'2022-09-11 20:00:00'),(68,1,'testTask','renren',0,NULL,1,'2022-09-11 20:30:00'),(69,1,'testTask','renren',0,NULL,1,'2022-09-11 21:00:00'),(70,1,'testTask','renren',0,NULL,1,'2022-09-11 21:30:00'),(71,1,'testTask','renren',0,NULL,0,'2022-09-12 14:00:00'),(72,1,'testTask','renren',0,NULL,1,'2022-09-12 14:30:00'),(73,1,'testTask','renren',0,NULL,0,'2022-09-12 15:00:00'),(74,1,'testTask','renren',0,NULL,0,'2022-09-12 15:30:00'),(75,1,'testTask','renren',0,NULL,1,'2022-09-12 16:00:00'),(76,1,'testTask','renren',0,NULL,0,'2022-09-12 16:30:00'),(77,1,'testTask','renren',0,NULL,2,'2022-09-12 17:00:00'),(78,1,'testTask','renren',0,NULL,1,'2022-09-13 20:30:00'),(79,1,'testTask','renren',0,NULL,0,'2022-09-15 17:00:00'),(80,1,'testTask','renren',0,NULL,0,'2022-09-15 17:30:00'),(81,1,'testTask','renren',0,NULL,0,'2022-09-15 18:00:00'),(82,1,'testTask','renren',0,NULL,2,'2022-09-15 18:30:00'),(83,1,'testTask','renren',0,NULL,2,'2022-09-15 19:00:00'),(84,1,'testTask','renren',0,NULL,1,'2022-09-15 19:30:00'),(85,1,'testTask','renren',0,NULL,1,'2022-09-15 20:00:00'),(86,1,'testTask','renren',0,NULL,2,'2022-09-15 20:30:00'),(87,1,'testTask','renren',0,NULL,2,'2022-09-17 15:00:00'),(88,1,'testTask','renren',0,NULL,2,'2022-09-17 15:30:00'),(89,1,'testTask','renren',0,NULL,0,'2022-09-17 16:00:00'),(90,1,'testTask','renren',0,NULL,3,'2022-09-17 16:30:00'),(91,1,'testTask','renren',0,NULL,2,'2022-09-17 17:00:00'),(92,1,'testTask','renren',0,NULL,0,'2022-09-19 19:00:00'),(93,1,'testTask','renren',0,NULL,2,'2022-09-19 19:30:00'),(94,1,'testTask','renren',0,NULL,1,'2022-09-19 20:30:00'),(95,1,'testTask','renren',0,NULL,0,'2022-09-21 19:30:00'),(96,1,'testTask','renren',0,NULL,0,'2022-09-21 20:00:00'),(97,1,'testTask','renren',0,NULL,0,'2022-09-21 20:30:00'),(98,1,'testTask','renren',0,NULL,3,'2022-09-21 21:00:00'),(99,1,'testTask','renren',0,NULL,0,'2022-09-22 19:00:00'),(100,1,'testTask','renren',0,NULL,3,'2022-09-22 19:30:00'),(101,1,'testTask','renren',0,NULL,4,'2022-09-22 20:00:00'),(102,1,'testTask','renren',0,NULL,0,'2022-10-11 20:00:00'),(103,1,'testTask','renren',0,NULL,1,'2022-10-11 20:30:00'),(104,1,'testTask','renren',0,NULL,2,'2022-10-12 17:00:00'),(105,1,'testTask','renren',0,NULL,2,'2022-10-12 17:30:00'),(106,1,'testTask','renren',0,NULL,1,'2022-10-12 18:00:00'),(107,1,'testTask','renren',0,NULL,2,'2022-10-12 18:30:00'),(108,1,'testTask','renren',0,NULL,0,'2022-10-14 19:00:00'),(109,1,'testTask','renren',0,NULL,0,'2022-10-14 19:30:00'),(110,1,'testTask','renren',0,NULL,0,'2022-10-14 20:00:00'),(111,1,'testTask','renren',0,NULL,0,'2022-10-14 20:30:00'),(112,1,'testTask','renren',0,NULL,0,'2022-10-19 20:00:00'),(113,1,'testTask','renren',0,NULL,1,'2022-10-19 20:30:00'),(114,1,'testTask','renren',0,NULL,0,'2022-10-21 15:30:00'),(115,1,'testTask','renren',0,NULL,1,'2022-11-01 19:30:00'),(116,1,'testTask','renren',0,NULL,1,'2022-11-01 20:00:00'),(117,1,'testTask','renren',0,NULL,2,'2022-11-01 20:30:00'),(118,1,'testTask','renren',0,NULL,0,'2023-02-11 15:00:00'),(119,1,'testTask','renren',0,NULL,1,'2023-02-11 15:30:00'),(120,1,'testTask','renren',0,NULL,2,'2023-02-11 16:00:00'),(121,1,'testTask','renren',0,NULL,1,'2023-02-11 16:30:00'),(122,1,'testTask','renren',0,NULL,3,'2023-02-11 17:00:00'),(123,1,'testTask','renren',0,NULL,0,'2023-02-11 17:30:00'),(124,1,'testTask','renren',0,NULL,2,'2023-02-11 18:00:00'),(125,1,'testTask','renren',0,NULL,3,'2023-02-13 16:00:00'),(126,1,'testTask','renren',0,NULL,5,'2023-02-13 16:30:00'),(127,1,'testTask','renren',0,NULL,0,'2023-02-13 17:00:00'),(128,1,'testTask','renren',0,NULL,1,'2023-02-13 17:30:00'),(129,1,'testTask','renren',0,NULL,0,'2023-02-13 18:00:00'),(130,1,'testTask','renren',0,NULL,1,'2023-02-13 18:30:00'),(131,1,'testTask','renren',0,NULL,0,'2023-02-18 17:30:00'),(132,1,'testTask','renren',0,NULL,0,'2023-02-18 18:00:00'),(133,1,'testTask','renren',0,NULL,0,'2023-02-27 20:30:00'),(134,1,'testTask','renren',0,NULL,0,'2023-02-28 20:30:00');
/*!40000 ALTER TABLE `schedule_job_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_captcha`
--

DROP TABLE IF EXISTS `sys_captcha`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_captcha` (
  `uuid` char(36) NOT NULL COMMENT 'uuid',
  `code` varchar(6) NOT NULL COMMENT '验证码',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统验证码';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_captcha`
--

LOCK TABLES `sys_captcha` WRITE;
/*!40000 ALTER TABLE `sys_captcha` DISABLE KEYS */;
INSERT INTO `sys_captcha` VALUES ('065a0c4d-44ec-4c13-898c-c807290ddc76','feb77','2022-09-15 16:41:49'),('074e5baa-388a-4474-8430-31e4f0b28a7f','67273','2022-08-14 21:36:15'),('09975b8b-9736-4010-8492-5cd8905692a2','me6a6','2022-09-07 21:33:46'),('09b28ffa-29e2-4d26-824b-49dd1db444f8','cg4ep','2022-08-20 14:56:12'),('15f95c4f-6201-4209-8edd-7d621077968e','cmxw8','2023-02-28 20:10:39'),('1a954b6b-d2f5-4a21-8d35-22aa084f041d','g8dw2','2022-09-19 18:41:48'),('22b7659a-3fe7-4749-8bcc-cdf4d96e01c4','yybxg','2022-11-01 19:07:59'),('22b977af-5189-47f6-8def-62adb3a960a0','8b6e3','2022-08-16 21:56:05'),('27d85ddf-db88-4b07-86b2-02814bcef56c','acp4g','2022-08-21 20:33:04'),('2d0bd070-d602-4843-8687-b592dc87b5ec','2cfyb','2022-09-13 20:30:04'),('4137e65d-9bc0-43db-897b-02ba815cc81f','wnp2x','2022-10-14 20:42:56'),('54ddc544-8f29-46a9-8192-6be59e32d786','n3cf8','2022-09-07 21:33:41'),('61595e70-684c-41cd-8a69-e9f655731173','5gawf','2022-08-14 17:50:07'),('62c38aa6-72c8-4130-8f88-582a4c3b0b80','4ye35','2022-09-12 13:52:39'),('77058bc5-7a73-4206-844e-52d33be904b1','mnyg7','2022-08-14 21:58:28'),('77fa8e8f-a642-4d07-85a4-03011b7cc325','nne3b','2022-10-21 15:07:25'),('82346499-4dee-4857-86a9-a0bb3209ef4e','xpgnw','2022-09-05 21:07:23'),('858d9ac9-0383-44a0-8547-18dd93b614c3','5ye48','2023-02-18 17:21:43'),('8d9d9f65-a588-4799-8460-cec58c449f42','3gnen','2022-09-08 21:15:37'),('973f2ba7-7e54-41ca-8a42-ced85a3d7d9a','e8eyx','2022-08-15 21:19:12'),('a24c1ba2-e4a0-4138-8058-401de22adc07','cye26','2022-09-02 21:37:20'),('b3d29cf2-bbbd-40fb-819c-1e61f078a6c6','27npy','2022-08-14 17:39:00'),('b7a3439b-fc27-452b-8a92-c8f015d44a99','73bg4','2022-09-03 21:13:04'),('bc36e92a-a887-48ae-8d35-d2f7e0ff41ca','7nppn','2022-10-19 19:45:54'),('bfdb1cac-83aa-4a17-8c1e-45f555d42b96','bw5ny','2022-09-01 21:27:40'),('c03a1d5f-7379-4346-8a92-26ba593d9868','apaen','2022-10-14 19:04:20'),('cff89916-0566-4234-80b3-3b4fbe7aa592','bnxap','2022-09-04 13:47:15'),('d01ed0c4-3993-4dc4-8c82-29c579eca97d','6774e','2022-09-06 21:37:03'),('d5c98685-35bc-4f6b-85d2-5819c3f6c202','xg4ag','2022-08-31 21:12:07'),('d904a782-c07a-4193-8ae1-8c31a9b291a9','e2ne5','2022-08-17 21:38:32'),('dc798bb9-3aeb-4ff6-81a2-11b5dc525add','6faa7','2022-08-24 21:54:51'),('de83e0b1-fb2e-41e7-8c93-6493e467515d','eycdx','2022-10-21 15:17:27'),('edba6404-5d1e-40e0-89de-920dabe8f2cb','edpbw','2022-08-28 20:53:53'),('fa54caa5-9700-4115-8400-fe2979a2c5fc','wcy4y','2022-10-11 19:42:39'),('fab61d91-51c7-4d2b-87a8-c9dca94273e5','7xdwf','2022-08-16 21:56:03');
/*!40000 ALTER TABLE `sys_captcha` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `param_key` varchar(50) DEFAULT NULL COMMENT 'key',
  `param_value` varchar(2000) DEFAULT NULL COMMENT 'value',
  `status` tinyint DEFAULT '1' COMMENT '状态   0：隐藏   1：显示',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `param_key` (`param_key`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统配置信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_config`
--

LOCK TABLES `sys_config` WRITE;
/*!40000 ALTER TABLE `sys_config` DISABLE KEYS */;
INSERT INTO `sys_config` VALUES (1,'CLOUD_STORAGE_CONFIG_KEY','{\"aliyunAccessKeyId\":\"\",\"aliyunAccessKeySecret\":\"\",\"aliyunBucketName\":\"\",\"aliyunDomain\":\"\",\"aliyunEndPoint\":\"\",\"aliyunPrefix\":\"\",\"qcloudBucketName\":\"\",\"qcloudDomain\":\"\",\"qcloudPrefix\":\"\",\"qcloudSecretId\":\"\",\"qcloudSecretKey\":\"\",\"qiniuAccessKey\":\"NrgMfABZxWLo5B-YYSjoE8-AZ1EISdi1Z3ubLOeZ\",\"qiniuBucketName\":\"ios-app\",\"qiniuDomain\":\"http://7xqbwh.dl1.z0.glb.clouddn.com\",\"qiniuPrefix\":\"upload\",\"qiniuSecretKey\":\"uIwJHevMRWU0VLxFvgy0tAcOdGqasdtVlJkdy6vV\",\"type\":1}',0,'云存储配置信息');
/*!40000 ALTER TABLE `sys_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_log`
--

DROP TABLE IF EXISTS `sys_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `operation` varchar(50) DEFAULT NULL COMMENT '用户操作',
  `method` varchar(200) DEFAULT NULL COMMENT '请求方法',
  `params` varchar(5000) DEFAULT NULL COMMENT '请求参数',
  `time` bigint NOT NULL COMMENT '执行时长(毫秒)',
  `ip` varchar(64) DEFAULT NULL COMMENT 'IP地址',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统日志';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_log`
--

LOCK TABLES `sys_log` WRITE;
/*!40000 ALTER TABLE `sys_log` DISABLE KEYS */;
INSERT INTO `sys_log` VALUES (1,'admin','保存菜单','io.renren.modules.sys.controller.SysMenuController.save()','[{\"menuId\":31,\"parentId\":0,\"name\":\"商品分类\",\"url\":\"\",\"perms\":\"\",\"type\":0,\"icon\":\"zonghe\",\"orderNum\":0,\"list\":[]}]',20,'0:0:0:0:0:0:0:1','2022-08-14 17:40:40'),(2,'admin','修改菜单','io.renren.modules.sys.controller.SysMenuController.update()','[{\"menuId\":31,\"parentId\":0,\"name\":\"商品系统\",\"url\":\"\",\"perms\":\"\",\"type\":0,\"icon\":\"zonghe\",\"orderNum\":0,\"list\":[]}]',8,'0:0:0:0:0:0:0:1','2022-08-14 17:43:18'),(3,'admin','保存菜单','io.renren.modules.sys.controller.SysMenuController.save()','[{\"menuId\":32,\"parentId\":31,\"name\":\"分类维护\",\"url\":\"product/category\",\"perms\":\"\",\"type\":1,\"icon\":\"menu\",\"orderNum\":0,\"list\":[]}]',7,'0:0:0:0:0:0:0:1','2022-08-14 17:43:47'),(4,'admin','保存菜单','io.renren.modules.sys.controller.SysMenuController.save()','[{\"menuId\":33,\"parentId\":31,\"name\":\"品牌管理\",\"url\":\"product/brand\",\"perms\":\"\",\"type\":1,\"icon\":\"zonghe\",\"orderNum\":0,\"list\":[]}]',18,'0:0:0:0:0:0:0:1','2022-08-20 09:40:44'),(5,'admin','保存用户','io.renren.modules.sys.controller.SysUserController.save()','[{\"userId\":2,\"username\":\"cjk\",\"password\":\"4a2b2a22827d95b094e694fdde689dd19c0f0180b63054de1c4cc02256ec8bd1\",\"salt\":\"VJ9ZkPiK8xyQI8FwbNAz\",\"email\":\"fdsa@qq.com\",\"mobile\":\"11111111111\",\"status\":1,\"roleIdList\":[],\"createUserId\":1,\"createTime\":\"Sep 12, 2022, 3:13:58 PM\"}]',192,'0:0:0:0:0:0:0:1','2022-09-12 15:13:59');
/*!40000 ALTER TABLE `sys_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_menu` (
  `menu_id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint DEFAULT NULL COMMENT '父菜单ID，一级菜单为0',
  `name` varchar(50) DEFAULT NULL COMMENT '菜单名称',
  `url` varchar(200) DEFAULT NULL COMMENT '菜单URL',
  `perms` varchar(500) DEFAULT NULL COMMENT '授权(多个用逗号分隔，如：user:list,user:create)',
  `type` int DEFAULT NULL COMMENT '类型   0：目录   1：菜单   2：按钮',
  `icon` varchar(50) DEFAULT NULL COMMENT '菜单图标',
  `order_num` int DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单管理';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1,0,'系统管理',NULL,NULL,0,'system',0),(2,1,'管理员列表','sys/user',NULL,1,'admin',1),(3,1,'角色管理','sys/role',NULL,1,'role',2),(4,1,'菜单管理','sys/menu',NULL,1,'menu',3),(5,1,'SQL监控','http://localhost:8080/renren-fast/druid/sql.html',NULL,1,'sql',4),(6,1,'定时任务','job/schedule',NULL,1,'job',5),(7,6,'查看',NULL,'sys:schedule:list,sys:schedule:info',2,NULL,0),(8,6,'新增',NULL,'sys:schedule:save',2,NULL,0),(9,6,'修改',NULL,'sys:schedule:update',2,NULL,0),(10,6,'删除',NULL,'sys:schedule:delete',2,NULL,0),(11,6,'暂停',NULL,'sys:schedule:pause',2,NULL,0),(12,6,'恢复',NULL,'sys:schedule:resume',2,NULL,0),(13,6,'立即执行',NULL,'sys:schedule:run',2,NULL,0),(14,6,'日志列表',NULL,'sys:schedule:log',2,NULL,0),(15,2,'查看',NULL,'sys:user:list,sys:user:info',2,NULL,0),(16,2,'新增',NULL,'sys:user:save,sys:role:select',2,NULL,0),(17,2,'修改',NULL,'sys:user:update,sys:role:select',2,NULL,0),(18,2,'删除',NULL,'sys:user:delete',2,NULL,0),(19,3,'查看',NULL,'sys:role:list,sys:role:info',2,NULL,0),(20,3,'新增',NULL,'sys:role:save,sys:menu:list',2,NULL,0),(21,3,'修改',NULL,'sys:role:update,sys:menu:list',2,NULL,0),(22,3,'删除',NULL,'sys:role:delete',2,NULL,0),(23,4,'查看',NULL,'sys:menu:list,sys:menu:info',2,NULL,0),(24,4,'新增',NULL,'sys:menu:save,sys:menu:select',2,NULL,0),(25,4,'修改',NULL,'sys:menu:update,sys:menu:select',2,NULL,0),(26,4,'删除',NULL,'sys:menu:delete',2,NULL,0),(27,1,'参数管理','sys/config','sys:config:list,sys:config:info,sys:config:save,sys:config:update,sys:config:delete',1,'config',6),(29,1,'系统日志','sys/log','sys:log:list',1,'log',7),(30,1,'文件上传','oss/oss','sys:oss:all',1,'oss',6),(31,0,'商品系统','','',0,'editor',0),(32,31,'分类维护','product/category','',1,'menu',0),(34,31,'品牌管理','product/brand','',1,'editor',0),(37,31,'平台属性','','',0,'system',0),(38,37,'属性分组','product/attrgroup','',1,'tubiao',0),(39,37,'规格参数','product/baseattr','',1,'log',0),(40,37,'销售属性','product/saleattr','',1,'zonghe',0),(41,31,'商品维护','product/spu','',0,'zonghe',0),(42,0,'优惠营销','','',0,'mudedi',0),(43,0,'库存系统','','',0,'shouye',0),(44,0,'订单系统','','',0,'config',0),(45,0,'用户系统','','',0,'admin',0),(46,0,'内容管理','','',0,'sousuo',0),(47,42,'优惠券管理','coupon/coupon','',1,'zhedie',0),(48,42,'发放记录','coupon/history','',1,'sql',0),(49,42,'专题活动','coupon/subject','',1,'tixing',0),(50,42,'秒杀活动','coupon/seckill','',1,'daohang',0),(51,42,'积分维护','coupon/bounds','',1,'geren',0),(52,42,'满减折扣','coupon/full','',1,'shoucang',0),(53,43,'仓库维护','ware/wareinfo','',1,'shouye',0),(54,43,'库存工作单','ware/task','',1,'log',0),(55,43,'商品库存','ware/sku','',1,'jiesuo',0),(56,44,'订单查询','order/order','',1,'zhedie',0),(57,44,'退货单处理','order/return','',1,'shanchu',0),(58,44,'等级规则','order/settings','',1,'system',0),(59,44,'支付流水查询','order/payment','',1,'job',0),(60,44,'退款流水查询','order/refund','',1,'mudedi',0),(61,45,'会员列表','member/member','',1,'geren',0),(62,45,'会员等级','member/level','',1,'tubiao',0),(63,45,'积分变化','member/growth','',1,'bianji',0),(64,45,'统计信息','member/statistics','',1,'sql',0),(65,46,'首页推荐','content/index','',1,'shouye',0),(66,46,'分类热门','content/category','',1,'zhedie',0),(67,46,'评论管理','content/comments','',1,'pinglun',0),(68,41,'spu管理','product/spu','',1,'config',0),(69,41,'发布商品','product/spuadd','',1,'bianji',0),(70,43,'采购单维护','','',0,'tubiao',0),(71,70,'采购需求','ware/purchaseitem','',1,'editor',0),(72,70,'采购单','ware/purchase','',1,'menu',0),(73,41,'商品管理','product/manager','',1,'zonghe',0),(74,42,'会员价格','coupon/memberprice','',1,'admin',0),(75,42,'每日秒杀','coupon/seckillsession','',1,'job',0);
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_oss`
--

DROP TABLE IF EXISTS `sys_oss`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_oss` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `url` varchar(200) DEFAULT NULL COMMENT 'URL地址',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文件上传';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_oss`
--

LOCK TABLES `sys_oss` WRITE;
/*!40000 ALTER TABLE `sys_oss` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_oss` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` varchar(100) DEFAULT NULL COMMENT '角色名称',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint DEFAULT NULL COMMENT '角色ID',
  `menu_id` bigint DEFAULT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色与菜单对应关系';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_menu`
--

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) DEFAULT NULL COMMENT '密码',
  `salt` varchar(20) DEFAULT NULL COMMENT '盐',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(100) DEFAULT NULL COMMENT '手机号',
  `status` tinyint DEFAULT NULL COMMENT '状态  0：禁用   1：正常',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','9ec9750e709431dad22365cabc5c625482e574c74adaebba7dd02f1129e4ce1d','YzcmCZNvbXocrsz9dm8e','root@renren.io','13612345678',1,1,'2016-11-11 11:11:11'),(2,'cjk','4a2b2a22827d95b094e694fdde689dd19c0f0180b63054de1c4cc02256ec8bd1','VJ9ZkPiK8xyQI8FwbNAz','fdsa@qq.com','11111111111',1,1,'2022-09-12 15:13:59');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `role_id` bigint DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户与角色对应关系';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_token`
--

DROP TABLE IF EXISTS `sys_user_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_token` (
  `user_id` bigint NOT NULL,
  `token` varchar(100) NOT NULL COMMENT 'token',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户Token';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_token`
--

LOCK TABLES `sys_user_token` WRITE;
/*!40000 ALTER TABLE `sys_user_token` DISABLE KEYS */;
INSERT INTO `sys_user_token` VALUES (1,'9084659dfc9970ef125541c9e2b8a0d4','2023-03-01 08:12:42','2023-02-28 20:12:42');
/*!40000 ALTER TABLE `sys_user_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_user`
--

DROP TABLE IF EXISTS `tb_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `mobile` varchar(20) NOT NULL COMMENT '手机号',
  `password` varchar(64) DEFAULT NULL COMMENT '密码',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_user`
--

LOCK TABLES `tb_user` WRITE;
/*!40000 ALTER TABLE `tb_user` DISABLE KEYS */;
INSERT INTO `tb_user` VALUES (1,'mark','13612345678','8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918','2017-03-23 22:37:41');
/*!40000 ALTER TABLE `tb_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'market_backstage'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-05-08 22:51:41
