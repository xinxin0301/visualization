/*
SQLyog Ultimate v13.1.1 (64 bit)
MySQL - 5.6.16-log : Database - visualization
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`visualization` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `visualization`;

/*Table structure for table `component_manage` */

DROP TABLE IF EXISTS `component_manage`;

CREATE TABLE `component_manage` (
  `id` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '主键',
  `component_type_id` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '组件类型表主键',
  `component_name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '组件名称',
  `component_code` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '组件类型，text、image、chart',
  `chart_code` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '图形代码,line、pie',
  `options` longtext COLLATE utf8_bin COMMENT '设置',
  `sort_index` int(11) DEFAULT NULL COMMENT '显示顺序',
  `is_visible` int(11) DEFAULT NULL COMMENT '1:可见 2:不可见',
  `panel_json` longtext COLLATE utf8_bin COMMENT '面板JSON',
  `icon` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '图标',
  `is_inherit_basics` int(11) DEFAULT NULL COMMENT '是否继承基础属性1:是 2:否',
  `is_use_data` int(11) DEFAULT NULL COMMENT '是否使用数据属性1:是 2:否',
  `is_open_drilldown` int(11) DEFAULT NULL COMMENT '是否开启下钻1:开启 2:不开启',
  `is_open_linkage` int(11) DEFAULT NULL COMMENT '是否开启联动1:开启 2:不开启',
  `chart_form_data` text COLLATE utf8_bin COMMENT '组件默认配置表单',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='元组件管理';

/*Table structure for table `component_type_manage` */

DROP TABLE IF EXISTS `component_type_manage`;

CREATE TABLE `component_type_manage` (
  `id` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '主键',
  `component_type_name` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '类型名称',
  `parent_id` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '上级类型id',
  `sort_index` int(11) DEFAULT NULL COMMENT '显示顺序',
  `is_visible` int(11) DEFAULT NULL COMMENT '1:可用 2:不可用',
  `icon` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '图标',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='组件类型管理';

/*Table structure for table `control_panel_classification` */

DROP TABLE IF EXISTS `control_panel_classification`;

CREATE TABLE `control_panel_classification` (
  `id` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '主键',
  `classification_name` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '控制面板分类名称',
  `sort_index` int(11) DEFAULT NULL COMMENT '显示顺序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='控制面板分类表';

/*Table structure for table `control_panel_component_relation` */

DROP TABLE IF EXISTS `control_panel_component_relation`;

CREATE TABLE `control_panel_component_relation` (
  `id` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '主键',
  `control_panel_classification_id` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '控制面板分类表主键',
  `component_manage_id` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '组件管理表主键',
  PRIMARY KEY (`id`),
  KEY `FK_Reference_3` (`control_panel_classification_id`),
  KEY `FK_Reference_4` (`component_manage_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='控制面板-组件类型关联表';

/*Table structure for table `data_model` */

DROP TABLE IF EXISTS `data_model`;

CREATE TABLE `data_model` (
  `id` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '主键',
  `datasource_manage_id` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '数据源管理表主键',
  `model_name` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '数据模型名称',
  `model_script` text COLLATE utf8_bin COMMENT '数据模型命令/脚本',
  `association` text COLLATE utf8_bin COMMENT '关联关系',
  `sql_str` longtext COLLATE utf8_bin COMMENT 'SQL字符串，参数占位符',
  `sql_condition` longtext COLLATE utf8_bin COMMENT 'SQL条件',
  `sql_param` longtext COLLATE utf8_bin COMMENT 'SQL参数',
  `sql_show` longtext COLLATE utf8_bin COMMENT 'SQL展示使用',
  `indexes` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '索引名称',
  PRIMARY KEY (`id`),
  KEY `FK_Reference_6` (`datasource_manage_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='数据模型管理';

/*Table structure for table `data_model_attribute` */

DROP TABLE IF EXISTS `data_model_attribute`;

CREATE TABLE `data_model_attribute` (
  `id` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '主键',
  `data_model_id` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '数据模型表主键',
  `mapping_manage_id` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '数据映射管理主键',
  `fields_name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '维度字段',
  `fields_type` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '字段类型',
  `fields_alias` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '字段别名',
  `table_name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '表名',
  `table_name_alias` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '表名别名',
  `is_hide` int(11) DEFAULT NULL COMMENT '是否隐藏1:隐藏 2:不隐藏',
  `model_type` int(11) DEFAULT NULL COMMENT '类型1:维度 2:度量',
  `sort_index` int(11) DEFAULT NULL COMMENT '排序',
  `random_alias` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT 'AS别名随机数',
  `polymerization_type` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '聚合方式',
  `chart_type` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '图表类型',
  `is_new_calculation` int(11) DEFAULT NULL COMMENT '是否是新建计算1:维度2:度量',
  `expression` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '新建计算维度表达式',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='数据模型属性表';

/*Table structure for table `database_type_manage` */

DROP TABLE IF EXISTS `database_type_manage`;

CREATE TABLE `database_type_manage` (
  `id` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '主键',
  `database_type_name` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '数据库类型名称 如MySQL，SQLserver，Oracle，db2',
  `database_type` int(11) DEFAULT NULL COMMENT '数据库类型 如1：关系型数据，2：非关系型数据库',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='数据库类型管理';

/*Table structure for table `datasource_manage` */

DROP TABLE IF EXISTS `datasource_manage`;

CREATE TABLE `datasource_manage` (
  `id` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '主键',
  `database_type_manage_id` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '数据库类型管理表主键',
  `datasource_name` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '数据源名称',
  `datasource_describe` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '数据源描述',
  `database_address` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '数据库地址',
  `port` int(11) DEFAULT NULL COMMENT '端口',
  `database_name` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '数据库名/path',
  `username` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '用户名',
  `pwd` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '密码',
  PRIMARY KEY (`id`),
  KEY `FK_Reference_9` (`database_type_manage_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='数据源管理';

/*Table structure for table `design_model` */

DROP TABLE IF EXISTS `design_model`;

CREATE TABLE `design_model` (
  `id` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '主键',
  `model_name` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '名称',
  `model_describe` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '描述',
  `model_json` longtext COLLATE utf8_bin COMMENT '模型json',
  `sort_index` int(11) DEFAULT NULL COMMENT '排序',
  `share_flag` int(11) DEFAULT NULL COMMENT '是否共享',
  `model_type` int(11) DEFAULT NULL COMMENT '类型 1：大屏 2：报表',
  `model_create_unit_id` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '模型创建单位id',
  `model_create_unit` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '模型创建单位',
  `model_preview_url` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '模型预览URL',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_user` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='设计模型';

/*Table structure for table `filter` */

DROP TABLE IF EXISTS `filter`;

CREATE TABLE `filter` (
  `id` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '主键',
  `field_name` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '字段名称',
  `table_name` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '表名',
  `field_id` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '字段ID',
  `type` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '字段类型',
  `text_match` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '文本筛选',
  `list_match` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '列表筛选',
  `date` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '时间过滤',
  `description` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '描述',
  `use_type` int(11) DEFAULT NULL COMMENT '使用类型 1:数据模型 2:数据大屏',
  `data_model_id` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '数据模型ID',
  `component_id` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '元组件ID',
  PRIMARY KEY (`id`),
  KEY `FK_Reference_12` (`component_id`),
  KEY `FK_Reference_13` (`data_model_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='过滤器';

/*Table structure for table `mapping_data` */

DROP TABLE IF EXISTS `mapping_data`;

CREATE TABLE `mapping_data` (
  `id` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '主键',
  `original_data` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '原始值',
  `mapping_data` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '映射值',
  `mapping_manage_id` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '映射管理表主键',
  PRIMARY KEY (`id`),
  KEY `FK_Reference_10` (`mapping_manage_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='映射数据表';

/*Table structure for table `mapping_manage` */

DROP TABLE IF EXISTS `mapping_manage`;

CREATE TABLE `mapping_manage` (
  `id` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '主键',
  `mapping_name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '名称',
  `description` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '简介',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='数据映射管理';

/*Table structure for table `report_form_manage` */

DROP TABLE IF EXISTS `report_form_manage`;

CREATE TABLE `report_form_manage` (
  `id` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '主键',
  `report_form_name` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '名称',
  `report_form_describe` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '描述',
  `report_form_json` longtext COLLATE utf8_bin COMMENT '报表json',
  `sort_index` int(11) DEFAULT NULL COMMENT '排序',
  `share_flag` int(11) DEFAULT NULL COMMENT '是否共享',
  `report_form_preview_url` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '报表模型预览URL',
  `report_form_create_unit_id` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '报表模型创建单位id',
  `report_form_create_unit` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '报表模型创建单位',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_user` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `update_user` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='报表管理';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
