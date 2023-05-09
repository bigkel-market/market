-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: localhost    Database: market_server_coupon
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
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'20220723143000','sale management','SQL','V20220723143000__sale_management.sql',-1003200845,'root','2022-11-01 11:15:09',825,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_coupon`
--

DROP TABLE IF EXISTS `sms_coupon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_coupon` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `coupon_type` tinyint(1) DEFAULT NULL COMMENT '优惠卷类型[0->全场赠券；1->会员赠券；2->购物赠券；3->注册赠券]',
  `coupon_img` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '优惠券图片',
  `coupon_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '优惠卷名字',
  `num` int DEFAULT NULL COMMENT '数量',
  `amount` decimal(18,4) DEFAULT NULL COMMENT '金额',
  `per_limit` int DEFAULT NULL COMMENT '每人限领张数',
  `min_point` decimal(18,4) DEFAULT NULL COMMENT '使用门槛',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `use_type` tinyint(1) DEFAULT NULL COMMENT '使用类型[0->全场通用；1->指定分类；2->指定商品]',
  `note` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `publish_count` int DEFAULT NULL COMMENT '发行数量',
  `use_count` int DEFAULT NULL COMMENT '已使用数量',
  `receive_count` int DEFAULT NULL COMMENT '领取数量',
  `enable_start_time` datetime DEFAULT NULL COMMENT '可以领取的开始日期',
  `enable_end_time` datetime DEFAULT NULL COMMENT '可以领取的结束日期',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '优惠码',
  `member_level` tinyint(1) DEFAULT NULL COMMENT '可以领取的会员等级[0->不限等级，其他-对应等级]',
  `publish` tinyint(1) DEFAULT NULL COMMENT '发布状态[0-未发布，1-已发布]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='优惠券信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_coupon`
--

LOCK TABLES `sms_coupon` WRITE;
/*!40000 ALTER TABLE `sms_coupon` DISABLE KEYS */;
/*!40000 ALTER TABLE `sms_coupon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_coupon_history`
--

DROP TABLE IF EXISTS `sms_coupon_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_coupon_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `coupon_id` bigint DEFAULT NULL COMMENT '优惠券id',
  `member_id` bigint DEFAULT NULL COMMENT '会员id',
  `member_nick_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会员名字',
  `get_type` tinyint(1) DEFAULT NULL COMMENT '获取方式[0->后台赠送；1->主动领取]',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `use_type` tinyint(1) DEFAULT NULL COMMENT '使用状态[0->未使用；1->已使用；2->已过期]',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  `order_id` bigint DEFAULT NULL COMMENT '订单id',
  `order_sn` bigint DEFAULT NULL COMMENT '订单号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='优惠券领取历史记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_coupon_history`
--

LOCK TABLES `sms_coupon_history` WRITE;
/*!40000 ALTER TABLE `sms_coupon_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `sms_coupon_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_coupon_spu_category_relation`
--

DROP TABLE IF EXISTS `sms_coupon_spu_category_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_coupon_spu_category_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `coupon_id` bigint DEFAULT NULL COMMENT '优惠券id',
  `category_id` bigint DEFAULT NULL COMMENT '产品分类id',
  `category_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产品分类名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='优惠券分类关联';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_coupon_spu_category_relation`
--

LOCK TABLES `sms_coupon_spu_category_relation` WRITE;
/*!40000 ALTER TABLE `sms_coupon_spu_category_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `sms_coupon_spu_category_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_coupon_spu_relation`
--

DROP TABLE IF EXISTS `sms_coupon_spu_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_coupon_spu_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `coupon_id` bigint DEFAULT NULL COMMENT '优惠券id',
  `spu_id` bigint DEFAULT NULL COMMENT 'spu_id',
  `spu_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'spu_name',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='优惠券与产品关联';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_coupon_spu_relation`
--

LOCK TABLES `sms_coupon_spu_relation` WRITE;
/*!40000 ALTER TABLE `sms_coupon_spu_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `sms_coupon_spu_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_home_adv`
--

DROP TABLE IF EXISTS `sms_home_adv`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_home_adv` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '名字',
  `pic` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片地址',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态',
  `click_count` int DEFAULT NULL COMMENT '点击数',
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '广告详情连接地址',
  `note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `sort` int DEFAULT NULL COMMENT '排序',
  `publisher_id` bigint DEFAULT NULL COMMENT '发布者',
  `auth_id` bigint DEFAULT NULL COMMENT '审核者',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='首页轮播广告';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_home_adv`
--

LOCK TABLES `sms_home_adv` WRITE;
/*!40000 ALTER TABLE `sms_home_adv` DISABLE KEYS */;
/*!40000 ALTER TABLE `sms_home_adv` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_home_subject`
--

DROP TABLE IF EXISTS `sms_home_subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_home_subject` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '专题名字',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '专题标题',
  `sub_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '专题副标题',
  `status` tinyint(1) DEFAULT NULL COMMENT '显示状态',
  `url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '详情连接',
  `sort` int DEFAULT NULL COMMENT '排序',
  `img` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '专题图片地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='首页专题表【jd首页下面很多专题，每个专题链接新的页面，展示专题商品信息】';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_home_subject`
--

LOCK TABLES `sms_home_subject` WRITE;
/*!40000 ALTER TABLE `sms_home_subject` DISABLE KEYS */;
/*!40000 ALTER TABLE `sms_home_subject` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_home_subject_spu`
--

DROP TABLE IF EXISTS `sms_home_subject_spu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_home_subject_spu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '专题名字',
  `subject_id` bigint DEFAULT NULL COMMENT '专题id',
  `spu_id` bigint DEFAULT NULL COMMENT 'spu_id',
  `sort` int DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='专题商品';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_home_subject_spu`
--

LOCK TABLES `sms_home_subject_spu` WRITE;
/*!40000 ALTER TABLE `sms_home_subject_spu` DISABLE KEYS */;
/*!40000 ALTER TABLE `sms_home_subject_spu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_member_price`
--

DROP TABLE IF EXISTS `sms_member_price`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_member_price` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint DEFAULT NULL COMMENT 'sku_id',
  `member_level_id` bigint DEFAULT NULL COMMENT '会员等级id',
  `member_level_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '会员等级名',
  `member_price` decimal(18,4) DEFAULT NULL COMMENT '会员对应价格',
  `add_other` tinyint(1) DEFAULT NULL COMMENT '可否叠加其他优惠[0-不可叠加优惠，1-可叠加]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='商品会员价格';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_member_price`
--

LOCK TABLES `sms_member_price` WRITE;
/*!40000 ALTER TABLE `sms_member_price` DISABLE KEYS */;
INSERT INTO `sms_member_price` VALUES (1,1,2,'铜牌会员',8788.0000,1),(2,1,3,'银牌会员',8688.0000,1),(3,1,4,'金牌会员',8588.0000,1),(4,1,5,'钻石会员',8288.0000,1),(5,2,2,'铜牌会员',8766.0000,1),(6,2,3,'银牌会员',8666.0000,1),(7,2,4,'金牌会员',8566.0000,1),(8,2,5,'钻石会员',8266.0000,1),(9,3,2,'铜牌会员',5888.0000,1),(10,3,3,'银牌会员',5788.0000,1),(11,3,4,'金牌会员',5688.0000,1),(12,3,5,'钻石会员',5588.0000,1),(13,4,2,'铜牌会员',5888.0000,1),(14,4,3,'银牌会员',5788.0000,1),(15,4,4,'金牌会员',5688.0000,1),(16,4,5,'钻石会员',5588.0000,1),(17,5,2,'铜牌会员',5888.0000,1),(18,5,3,'银牌会员',5788.0000,1),(19,5,4,'金牌会员',5688.0000,1),(20,5,5,'钻石会员',5588.0000,1),(21,6,2,'铜牌会员',5888.0000,1),(22,6,3,'银牌会员',5788.0000,1),(23,6,4,'金牌会员',5688.0000,1),(24,6,5,'钻石会员',5588.0000,1),(25,7,2,'铜牌会员',5888.0000,1),(26,7,3,'银牌会员',5788.0000,1),(27,7,4,'金牌会员',5688.0000,1),(28,7,5,'钻石会员',5588.0000,1),(29,8,2,'铜牌会员',5888.0000,1),(30,8,3,'银牌会员',5788.0000,1),(31,8,4,'金牌会员',5688.0000,1),(32,8,5,'钻石会员',5588.0000,1),(33,9,2,'铜牌会员',4088.0000,1),(34,9,3,'银牌会员',3988.0000,1),(35,9,4,'金牌会员',3888.0000,1),(36,9,5,'钻石会员',3288.0000,1);
/*!40000 ALTER TABLE `sms_member_price` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_seckill_promotion`
--

DROP TABLE IF EXISTS `sms_seckill_promotion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_seckill_promotion` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '活动标题',
  `start_time` datetime DEFAULT NULL COMMENT '开始日期',
  `end_time` datetime DEFAULT NULL COMMENT '结束日期',
  `status` tinyint DEFAULT NULL COMMENT '上下线状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `user_id` bigint DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='秒杀活动';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_seckill_promotion`
--

LOCK TABLES `sms_seckill_promotion` WRITE;
/*!40000 ALTER TABLE `sms_seckill_promotion` DISABLE KEYS */;
/*!40000 ALTER TABLE `sms_seckill_promotion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_seckill_session`
--

DROP TABLE IF EXISTS `sms_seckill_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_seckill_session` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '场次名称',
  `start_time` datetime DEFAULT NULL COMMENT '每日开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '每日结束时间',
  `status` tinyint(1) DEFAULT NULL COMMENT '启用状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='秒杀活动场次';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_seckill_session`
--

LOCK TABLES `sms_seckill_session` WRITE;
/*!40000 ALTER TABLE `sms_seckill_session` DISABLE KEYS */;
INSERT INTO `sms_seckill_session` VALUES (3,'第一场','2023-02-19 00:00:00','2023-02-19 05:00:00',1,'2023-02-18 17:18:08'),(4,'第二场','2023-02-20 00:00:00','2023-02-20 02:00:00',1,'2023-02-18 17:19:03'),(5,'4','2023-02-27 00:00:00','2023-03-31 00:00:00',1,'2023-02-27 20:32:23'),(6,'第三场','2023-03-01 00:00:00','2023-03-03 02:00:00',1,'2023-02-28 20:13:35'),(8,'第四场','2023-02-28 00:00:00','2023-03-01 00:00:00',1,'2023-02-28 20:28:10');
/*!40000 ALTER TABLE `sms_seckill_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_seckill_sku_notice`
--

DROP TABLE IF EXISTS `sms_seckill_sku_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_seckill_sku_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `member_id` bigint DEFAULT NULL COMMENT 'member_id',
  `sku_id` bigint DEFAULT NULL COMMENT 'sku_id',
  `session_id` bigint DEFAULT NULL COMMENT '活动场次id',
  `subcribe_time` datetime DEFAULT NULL COMMENT '订阅时间',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `notice_type` tinyint(1) DEFAULT NULL COMMENT '通知方式[0-短信，1-邮件]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='秒杀商品通知订阅';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_seckill_sku_notice`
--

LOCK TABLES `sms_seckill_sku_notice` WRITE;
/*!40000 ALTER TABLE `sms_seckill_sku_notice` DISABLE KEYS */;
/*!40000 ALTER TABLE `sms_seckill_sku_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_seckill_sku_relation`
--

DROP TABLE IF EXISTS `sms_seckill_sku_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_seckill_sku_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `promotion_id` bigint DEFAULT NULL COMMENT '活动id',
  `promotion_session_id` bigint DEFAULT NULL COMMENT '活动场次id',
  `sku_id` bigint DEFAULT NULL COMMENT '商品id',
  `seckill_price` decimal(10,0) DEFAULT NULL COMMENT '秒杀价格',
  `seckill_count` decimal(10,0) DEFAULT NULL COMMENT '秒杀总量',
  `seckill_limit` decimal(10,0) DEFAULT NULL COMMENT '每人限购数量',
  `seckill_sort` int DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='秒杀活动商品关联';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_seckill_sku_relation`
--

LOCK TABLES `sms_seckill_sku_relation` WRITE;
/*!40000 ALTER TABLE `sms_seckill_sku_relation` DISABLE KEYS */;
INSERT INTO `sms_seckill_sku_relation` VALUES (4,NULL,3,1,100,10,1,0),(5,NULL,4,1,200,100,1,0),(6,NULL,5,1,0,5,1,0),(7,NULL,6,1,100,5,1,0),(8,NULL,7,2,500,100,1,0),(9,NULL,8,2,100,100,1,0);
/*!40000 ALTER TABLE `sms_seckill_sku_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_sku_full_reduction`
--

DROP TABLE IF EXISTS `sms_sku_full_reduction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_sku_full_reduction` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint DEFAULT NULL COMMENT 'spu_id',
  `full_price` decimal(18,4) DEFAULT NULL COMMENT '满多少',
  `reduce_price` decimal(18,4) DEFAULT NULL COMMENT '减多少',
  `add_other` tinyint(1) DEFAULT NULL COMMENT '是否参与其他优惠',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='商品满减信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_sku_full_reduction`
--

LOCK TABLES `sms_sku_full_reduction` WRITE;
/*!40000 ALTER TABLE `sms_sku_full_reduction` DISABLE KEYS */;
INSERT INTO `sms_sku_full_reduction` VALUES (1,1,10000.0000,80.0000,NULL),(2,2,10000.0000,80.0000,NULL),(3,3,10000.0000,80.0000,NULL),(4,4,10000.0000,80.0000,NULL),(5,5,10000.0000,80.0000,NULL),(6,6,10000.0000,80.0000,NULL),(7,7,10000.0000,80.0000,NULL),(8,8,10000.0000,80.0000,NULL),(9,9,6666.0000,200.0000,NULL);
/*!40000 ALTER TABLE `sms_sku_full_reduction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_sku_ladder`
--

DROP TABLE IF EXISTS `sms_sku_ladder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_sku_ladder` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `sku_id` bigint DEFAULT NULL COMMENT 'spu_id',
  `full_count` int DEFAULT NULL COMMENT '满几件',
  `discount` decimal(4,2) DEFAULT NULL COMMENT '打几折',
  `price` decimal(18,4) DEFAULT NULL COMMENT '折后价',
  `add_other` tinyint(1) DEFAULT NULL COMMENT '是否叠加其他优惠[0-不可叠加，1-可叠加]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='商品阶梯价格';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_sku_ladder`
--

LOCK TABLES `sms_sku_ladder` WRITE;
/*!40000 ALTER TABLE `sms_sku_ladder` DISABLE KEYS */;
INSERT INTO `sms_sku_ladder` VALUES (1,1,3,0.92,NULL,1),(2,1,3,0.92,NULL,1),(3,2,5,0.92,NULL,1),(4,2,5,0.92,NULL,1),(5,3,3,0.92,NULL,0),(6,3,3,0.92,NULL,0),(7,4,3,0.92,NULL,0),(8,4,3,0.92,NULL,0),(9,5,3,0.92,NULL,0),(10,5,3,0.92,NULL,0),(11,6,3,0.92,NULL,0),(12,6,3,0.92,NULL,0),(13,7,3,0.92,NULL,1),(14,7,3,0.92,NULL,1),(15,8,3,0.92,NULL,1),(16,8,3,0.92,NULL,1),(17,9,4,0.80,NULL,0),(18,9,4,0.80,NULL,0);
/*!40000 ALTER TABLE `sms_sku_ladder` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_spu_bounds`
--

DROP TABLE IF EXISTS `sms_spu_bounds`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_spu_bounds` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `spu_id` bigint DEFAULT NULL,
  `grow_bounds` decimal(18,4) DEFAULT NULL COMMENT '成长积分',
  `buy_bounds` decimal(18,4) DEFAULT NULL COMMENT '购物积分',
  `work` tinyint(1) DEFAULT NULL COMMENT '优惠生效情况[1111（四个状态位，从右到左）;0 - 无优惠，成长积分是否赠送;1 - 无优惠，购物积分是否赠送;2 - 有优惠，成长积分是否赠送;3 - 有优惠，购物积分是否赠送【状态位0：不赠送，1：赠送】]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='商品spu积分设置';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_spu_bounds`
--

LOCK TABLES `sms_spu_bounds` WRITE;
/*!40000 ALTER TABLE `sms_spu_bounds` DISABLE KEYS */;
INSERT INTO `sms_spu_bounds` VALUES (1,1,500.0000,2000.0000,NULL),(2,2,500.0000,2000.0000,NULL),(3,3,500.0000,2000.0000,NULL),(4,4,0.0000,0.0000,NULL),(5,5,0.0000,0.0000,NULL);
/*!40000 ALTER TABLE `sms_spu_bounds` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `undo_log`
--

DROP TABLE IF EXISTS `undo_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `undo_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `branch_id` bigint NOT NULL,
  `xid` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `context` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `undo_log`
--

LOCK TABLES `undo_log` WRITE;
/*!40000 ALTER TABLE `undo_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `undo_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'market_server_coupon'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-05-08 22:51:42
