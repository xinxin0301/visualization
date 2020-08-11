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

/*Data for the table `component_manage` */

insert  into `component_manage`(`id`,`component_type_id`,`component_name`,`component_code`,`chart_code`,`options`,`sort_index`,`is_visible`,`panel_json`,`icon`,`is_inherit_basics`,`is_use_data`,`is_open_drilldown`,`is_open_linkage`) values
('0e9ed4e1a657471b9546e5b8cf746957','727e1e82226c4892a9b0d98819878027','单饼图','chart','pie','{\"color\":[\"#5178F7\",\"#51A0F7\",\"#44CB97\",\"#F7CB39\",\"#F79C51\"],\"tooltip\":{},\"series\":[{\"name\":\"访问来源\",\"type\":\"pie\",\"data\":[{\"value\":335,\"name\":\"直接访问\"},{\"value\":310,\"name\":\"邮件营销\"},{\"value\":234,\"name\":\"联盟广告\"},{\"value\":135,\"name\":\"视频广告\"},{\"value\":1548,\"name\":\"搜索引擎\"}]}]}',4,1,'{\"series[].label.show\":{\"title\":\"是否显示标签\",\"type\":\"boolean\",\"ui:widget\":\"switch\",\"default\":true},\"series[].label.color\":{\"title\":\"标签颜色\",\"type\":\"string\",\"format\":\"color\",\"default\":\"#FF0000\"},\"series[].label.fontSize\":{\"title\":\"标签大小\",\"type\":\"string\",\"default\":12}}','chart-pie',1,1,2,1),
('5bb538027181410fb4dacb94203d8996','f1a3f2092a824795999ee9a4c465a5eb','单折线图','chart','line','{\"color\":[\"#5178F7\",\"#51A0F7\",\"#44CB97\",\"#F7CB39\",\"#F79C51\"],\"tooltip\":{},\"xAxis\":{\"type\":\"category\",\"data\":[\"Mon\",\"Tue\",\"Wed\",\"Thu\",\"Fri\",\"Sat\",\"Sun\"]},\"yAxis\":[{\"type\":\"value\"}],\"series\":[{\"data\":[820,932,901,934,1290,1330,1320],\"type\":\"line\"}]}',1,1,'{\"series[].step\":{\"title\":\"是否为阶梯线\",\"type\":\"boolean\",\"ui:widget\":\"switch\"},\"series[].smooth\":{\"title\":\"是否光滑\",\"type\":\"boolean\",\"ui:widget\":\"switch\"},\"series[].label.show\":{\"title\":\"是否显示标签\",\"type\":\"boolean\",\"ui:widget\":\"switch\"},\"series[].label.color\":{\"title\":\"标签颜色\",\"type\":\"string\",\"format\":\"color\",\"default\":\"#000000\"},\"series[].lineStyle.fontSize\":{\"title\":\"标签大小\",\"type\":\"string\",\"default\":12},\"series[].lineStyle.width\":{\"title\":\"线宽\",\"type\":\"string\",\"default\":2},\"series[].lineStyle.type\":{\"title\":\"线类型\",\"type\":\"string\",\"enum\":[\"solid\",\"dashed\",\"dotted\"]},\"xAxis.axisLabel.show\":{\"title\":\"x轴标签是否显示\",\"type\":\"boolean\",\"ui:widget\":\"switch\",\"default\":true},\"xAxis.axisLabel.fontSize\":{\"title\":\"x轴标签大小\",\"type\":\"string\",\"default\":12},\"xAxis.axisLabel.color\":{\"title\":\"x轴标签颜色\",\"type\":\"string\",\"format\":\"color\",\"default\":\"#FFFFFF\"},\"yAxis.axisLabel.show\":{\"title\":\"y轴标签是否显示\",\"type\":\"boolean\",\"ui:widget\":\"switch\",\"default\":true},\"yAxis.axisLabel.fontSize\":{\"title\":\"y轴标签大小\",\"type\":\"string\",\"default\":12},\"yAxis.axisLabel.color\":{\"title\":\"y轴标签颜色\",\"type\":\"string\",\"format\":\"color\",\"default\":\"#FFFFFF\"}}','chart-line',1,1,2,1),
('62369af6b68940709a31043bb3a032f7','0ed9a77a3f2740348e208e3186600ba0','普通表格','table',NULL,NULL,10,1,NULL,'table',1,1,2,1),
('7accc903b1934e4c9469e197387fcc87','24cb6923396e44618c67e247cd8262cd','雷达图','chart','radar','{\"color\":[\"#5178F7\",\"#51A0F7\",\"#44CB97\",\"#F7CB39\",\"#F79C51\"],\"radar\":{\"indicator\":[{\"name\":\"销售\",\"max\":6500},{\"name\":\"管理\",\"max\":16000},{\"name\":\"信息技术\",\"max\":30000},{\"name\":\"客服\",\"max\":38000},{\"name\":\"研发\",\"max\":52000},{\"name\":\"市场\",\"max\":25000}]},\"series\":[{\"name\":\"预算\",\"type\":\"radar\",\"data\":[{\"value\":[4300,10000,28000,35000,50000,19000],\"name\":\"预算\"}]}]}',6,1,'{\"series[].symbol\":{\"title\":\"标记图形\",\"type\":\"string\",\"enum\":[\"circle\",\"rect\",\"roundRect\",\"triangle\",\"diamond\",\"pin\",\"arrow\",\"nonoe\"],\"enumNames\":[\"圆形\",\"矩形\",\"圆角矩形\",\"三角形\",\"钻石形\",\"钉形\",\"箭头\",\"无\"]},\"series[].symbolSize\":{\"title\":\"标记图形大小\",\"type\":\"string\",\"default\":4},\"series[].label.show\":{\"title\":\"是否显示标签\",\"type\":\"boolean\",\"ui:widget\":\"switch\"}}','chart-radar',1,1,2,1),
('7d4402fef5344fe6bbf3f8d6a026262e','d99744ce41454c86aaa26f626d34b902','图片','image',NULL,NULL,5,1,NULL,'image',1,1,2,2),
('8db3e53c8d3e4ca5b44c4364ccf20924','f7e6047d2ac14b5fa52ac52d48496544','全国色彩地图','chart','map','{\"visualMap\":{\"text\":[\"销售\"],\"realtime\":false,\"calculable\":true,\"inRange\":{\"color\":[\"lightskyblue\",\"yellow\",\"orangered\"]}},\"series\":[{\"name\":\"中国颜色地图\",\"type\":\"map\",\"mapType\":\"china\",\"label\":{\"show\":false},\"data\":[{\"name\":\"北京\",\"value\":100},{\"name\":\"上海\",\"value\":150}]}]}',7,1,'{\"series[].itemStyle.borderColor\":{\"title\":\"边界颜色\",\"type\":\"string\",\"format\":\"color\",\"default\":\"#000000\"},\"backgroundColor\":{\"title\":\"背景颜色\",\"type\":\"string\",\"format\":\"color\",\"default\":\"#ffffff\"}}','map',1,1,2,1),
('ca426c5d8a7b4a72b7a64997ded190ba','157c3a07d7d345dcbef01c6520dadf58','文字','textbox',NULL,NULL,2,1,NULL,'textbox',1,1,2,2),
('dd72fc83a110475ba67dac84a752fa66','b85819a67a3949dca3c8c0d55724e31f','3D柱图','chart','3d-bar','{\"color\":[\"#5178F7\",\"#51A0F7\",\"#44CB97\",\"#F7CB39\",\"#F79C51\"],\"tooltip\":{},\"xAxis3D\":{\"type\":\"category\",\"data\":[\"12a\",\"1a\",\"2a\",\"3a\",\"4a\",\"5a\",\"6a\",\"7a\",\"8a\",\"9a\",\"10a\",\"11a\",\"12p\",\"1p\",\"2p\",\"3p\",\"4p\",\"5p\",\"6p\",\"7p\",\"8p\",\"9p\",\"10p\",\"11p\"]},\"yAxis3D\":{\"type\":\"category\",\"data\":[\"Saturday\",\"Friday\",\"Thursday\",\"Wednesday\",\"Tuesday\",\"Monday\",\"Sunday\"]},\"zAxis3D\":{\"type\":\"value\"},\"grid3D\":{\"boxWidth\":200,\"boxDepth\":80,\"light\":{\"main\":{\"intensity\":1.2,\"shadow\":true},\"ambient\":{\"intensity\":0.3}}},\"series\":[{\"type\":\"bar3D\",\"data\":[[0,0,\"5\"],[1,0,\"1\"],[16,3,\"2\"],[19,6,\"0\"],[21,6,\"2\"],[22,6,\"2\"],[23,6,\"6\"]],\"shading\":\"lambert\",\"label\":{\"textStyle\":{\"fontSize\":16,\"borderWidth\":1}}}]}',11,1,NULL,'three-bar',1,1,2,1),
('deff559bb9494cd7af1a4003e2125047','f7e6047d2ac14b5fa52ac52d48496544','全国气泡地图','chart','scatterMap','{\"geo\":{\"map\":\"china\",\"label\":{\"emphasis\":{\"show\":true,\"fontSize\":20}},\"left\":0,\"top\":0,\"right\":0,\"bottom\":0,\"boundingCoords\":[[-20,80],[340,-90]],\"zoom\":4,\"scaleLimit\":{\"min\":1,\"max\":15},\"center\":[104.3,35.7],\"roam\":true,\"itemStyle\":{\"areaColor\":\"transparent\",\"borderColor\":\"#000000\",\"borderWidth\":0.5,\"emphasis\":{\"areaColor\":\"rgba(32,219,253,.1)\"}}},\"series\":[{\"type\":\"effectScatter\",\"coordinateSystem\":\"geo\",\"showEffectOn\":\"render\",\"rippleEffect\":{\"period\":15,\"scale\":2,\"brushType\":\"fill\"},\"hoverAnimation\":true,\"itemStyle\":{\"normal\":{\"color\":\"red\",\"shadowBlur\":3,\"shadowColor\":\"yellow\"}},\"data\":[{\"symbolSize\":30,\"name\":\"北京\",\"value\":[116.395645,39.929985]},{\"symbolSize\":20,\"name\":\"上海\",\"value\":[121.4788,31.2302]}]}]}',9,1,'{\"geo.itemStyle.areaColor\":{\"title\":\"区域颜色\",\"type\":\"string\",\"format\":\"color\",\"default\":\"#ffffff\"},\"geo.itemStyle.borderColor\":{\"title\":\"边界颜色\",\"type\":\"string\",\"format\":\"color\",\"default\":\"#000000\"},\"backgroundColor\":{\"title\":\"背景颜色\",\"type\":\"string\",\"format\":\"color\",\"default\":\"#ffffff\"}}','scatter-map',1,1,2,1),
('e8842c2fe5f34a198395537e207885f9','bb5518820b3a4ee49af88239f16fa022','单柱状图','chart','bar','{\"color\":[\"#5178F7\",\"#51A0F7\",\"#44CB97\",\"#F7CB39\",\"#F79C51\"],\"xAxis\":{\"type\":\"category\",\"data\":[\"Mon\",\"Tue\",\"Wed\",\"Thu\",\"Fri\",\"Sat\",\"Sun\"]},\"yAxis\":{\"type\":\"value\"},\"series\":[{\"data\":[820,932,901,934,1290,1330,1320],\"type\":\"bar\"}]}',3,1,'{\"series[].label.show\":{\"title\":\"是否显示标签\",\"type\":\"boolean\",\"ui:widget\":\"switch\"},\"series[].label.color\":{\"title\":\"标签颜色\",\"type\":\"string\",\"format\":\"color\",\"default\":\"#FF0000\"},\"xAxis.axisLabel.show\":{\"title\":\"x轴标签是否显示\",\"type\":\"boolean\",\"ui:widget\":\"switch\",\"default\":true},\"xAxis.axisLabel.fontSize\":{\"title\":\"x轴标签大小\",\"type\":\"string\",\"default\":12},\"xAxis.axisLabel.color\":{\"title\":\"x轴标签颜色\",\"type\":\"string\",\"format\":\"color\",\"default\":\"#000000\"},\"yAxis.axisLabel.show\":{\"title\":\"y轴标签是否显示\",\"type\":\"boolean\",\"ui:widget\":\"switch\",\"default\":true},\"yAxis.axisLabel.fontSize\":{\"title\":\"y轴标签大小\",\"type\":\"string\",\"default\":12},\"yAxis.axisLabel.color\":{\"title\":\"y轴标签颜色\",\"type\":\"string\",\"format\":\"color\",\"default\":\"#000000\"}}','chart-bar',1,1,2,1),
('eda7e5d6977e48818cde5610056363d9','24cb6923396e44618c67e247cd8262cd','桑基图','chart','sankey','{\"color\":[\"#5178F7\",\"#51A0F7\",\"#44CB97\",\"#F7CB39\",\"#F79C51\"],\"series\":[{\"type\":\"sankey\",\"layout\":\"none\",\"focusNodeAdjacency\":\"allEdges\",\"data\":[{\"name\":\"a\"},{\"name\":\"b\"},{\"name\":\"a1\"},{\"name\":\"a2\"},{\"name\":\"b1\"},{\"name\":\"c\"}],\"links\":[{\"source\":\"a\",\"target\":\"a1\",\"value\":5},{\"source\":\"a\",\"target\":\"a2\",\"value\":3},{\"source\":\"b\",\"target\":\"b1\",\"value\":8},{\"source\":\"a\",\"target\":\"b1\",\"value\":3},{\"source\":\"b1\",\"target\":\"a1\",\"value\":1},{\"source\":\"b1\",\"target\":\"c\",\"value\":2}]}]}',8,1,'{\"series[].nodeWidth\":{\"title\":\"矩形节点宽度\",\"type\":\"string\",\"default\":20},\"series[].nodeGap\":{\"title\":\"矩形节点间隔\",\"type\":\"string\",\"default\":8},\"series[].nodeAlign\":{\"title\":\"节点对齐方式\",\"type\":\"string\",\"enum\":[\"justify\",\"left\",\"right\"],\"enumNames\":[\"两端对齐\",\"左对齐\",\"右对齐\"]}}','chart-sankey',1,1,2,1);'桑基图','chart','sankey','{\"color\":[\"#5178F7\",\"#51A0F7\",\"#44CB97\",\"#F7CB39\",\"#F79C51\"],\"series\":[{\"type\":\"sankey\",\"layout\":\"none\",\"focusNodeAdjacency\":\"allEdges\",\"data\":[{\"name\":\"a\"},{\"name\":\"b\"},{\"name\":\"a1\"},{\"name\":\"a2\"},{\"name\":\"b1\"},{\"name\":\"c\"}],\"links\":[{\"source\":\"a\",\"target\":\"a1\",\"value\":5},{\"source\":\"a\",\"target\":\"a2\",\"value\":3},{\"source\":\"b\",\"target\":\"b1\",\"value\":8},{\"source\":\"a\",\"target\":\"b1\",\"value\":3},{\"source\":\"b1\",\"target\":\"a1\",\"value\":1},{\"source\":\"b1\",\"target\":\"c\",\"value\":2}]}]}',8,1,'{\"series[].nodeWidth\":{\"title\":\"矩形节点宽度\",\"type\":\"string\",\"default\":20},\"series[].nodeGap\":{\"title\":\"矩形节点间隔\",\"type\":\"string\",\"default\":8},\"series[].nodeAlign\":{\"title\":\"节点对齐方式\",\"type\":\"string\",\"enum\":[\"justify\",\"left\",\"right\"],\"enumNames\":[\"两端对齐\",\"左对齐\",\"右对齐\"]}}','chart-sankey',1,1,2,1);
/*Data for the table `component_type_manage` */

insert  into `component_type_manage`(`id`,`component_type_name`,`parent_id`,`sort_index`,`is_visible`,`icon`) values
('0ed9a77a3f2740348e208e3186600ba0','表格','6956fef9ef76480faf95d8db455b7ccc',1,1,'table'),
('157c3a07d7d345dcbef01c6520dadf58','普通文字','67e6ad25cccf4ed9800df829b9de8ec3',1,1,'text'),
('24cb6923396e44618c67e247cd8262cd','其他','66e34c4e6e14481eb732f30de4bfbf5e',4,1,'chart-line'),
('66e34c4e6e14481eb732f30de4bfbf5e','图表',NULL,1,1,'chart-bar'),
('67e6ad25cccf4ed9800df829b9de8ec3','文字',NULL,2,1,'font'),
('6956fef9ef76480faf95d8db455b7ccc','表格',NULL,5,1,'table'),
('727e1e82226c4892a9b0d98819878027','饼图','66e34c4e6e14481eb732f30de4bfbf5e',3,1,'chart-pie'),
('871f31c116d14d649f40aea870562858','高级图表',NULL,4,1,'chart-line'),
('b04ef90f455e49faaf44f80b54a011ae','媒体',NULL,3,1,'image'),
('b85819a67a3949dca3c8c0d55724e31f','3D图表','871f31c116d14d649f40aea870562858',5,1,'chart-line'),
('bb5518820b3a4ee49af88239f16fa022','柱状图','66e34c4e6e14481eb732f30de4bfbf5e',2,1,'chart-bar'),
('d99744ce41454c86aaa26f626d34b902','图片','b04ef90f455e49faaf44f80b54a011ae',1,1,'image'),
('f1a3f2092a824795999ee9a4c465a5eb','折线图','66e34c4e6e14481eb732f30de4bfbf5e',1,1,'chart-line'),
('f7e6047d2ac14b5fa52ac52d48496544','平面地图','871f31c116d14d649f40aea870562858',4,1,'chart-line');

/*Data for the table `control_panel_classification` */

insert  into `control_panel_classification`(`id`,`classification_name`,`sort_index`) values
('0f8746b0a4c848bfbc39de19f2a38553','空指面板分类名称',1),
('1c6a6cfa53214b6cbf5ff9f6332270a5','空指面板分类名称',1),
('5a0f7cf91f524faca327f906f778adab','空指面板分类名称',1),
('8a883b50337e4c4ea1293764896c45ba','空指面板分类名称',1),
('8ca31546bc5a42d7aa15718c6c7a8be5','空指面板分类名称',1),
('9bfed3c3d24640438184c6145657e48a','空指面板分类名称',1),
('a2599f430e2842d0b0ba86c0cd7a0176','空指面板分类名称',1),
('a40832ee40524378901fba0b097e7bdb','空指面板分类名称',1),
('bb2c93fc3ad74f35a22659cc82e6a16d','空指面板分类名称',1),
('edb22f01c83040789c7a63268a2c0615','空指面板分类名称',1),
('f91717d9aff548249c29512132d27000','空指面板分类名称',1);

/*Data for the table `control_panel_component_relation` */

/*Data for the table `data_model` */
INSERT INTO `data_model`(`id`, `datasource_manage_id`, `model_name`, `model_script`, `association`, `sql_str`, `sql_condition`, `sql_param`, `sql_show`) VALUES ('756861800f914584b62c14abd25ef814', '07012038e48c425588311c1af52c7d98', '示例数据模型', NULL, '{\"key\":\"0\",\"table\":\"test\",\"name\":\"test\",\"title\":\"test\",\"value\":\"0\",\"children\":[]}', 'SELECT `test`. 省份 AS \'SG6JEVUXLP8LXLAXQI\',`test`. id AS \'SGBXFLWECFS4RT9OVC\',`test`. 配置 AS \'SGBOVXDDEBHDTTH7LI\',`test`. 销售额 AS \'SGYEFJG2NTYAL16YMK\',`test`. 经销商 AS \'SGZTIHGU7VOMLDNLTN\',`test`. 折扣 AS \'SGMZ0LAETU802ODSNM\',`test`. 车型 AS \'SGCK9007IP9UA6EXKU\',`test`. 订单数 AS \'SG2I4JJBNOHKLMVQ8Y\' FROM test LIMIT 0,500', '', '[]', 'SELECT `test`. 省份 AS \'SG6JEVUXLP8LXLAXQI\',`test`. id AS \'SGBXFLWECFS4RT9OVC\',`test`. 配置 AS \'SGBOVXDDEBHDTTH7LI\',`test`. 销售额 AS \'SGYEFJG2NTYAL16YMK\',`test`. 经销商 AS \'SGZTIHGU7VOMLDNLTN\',`test`. 折扣 AS \'SGMZ0LAETU802ODSNM\',`test`. 车型 AS \'SGCK9007IP9UA6EXKU\',`test`. 订单数 AS \'SG2I4JJBNOHKLMVQ8Y\' FROM test LIMIT 0,500');

/*Data for the table `data_model_attribute` */

/*Data for the table `database_type_manage` */

insert  into `database_type_manage`(`id`,`database_type_name`,`database_type`) values
('1','Elasticsearch',2),
('1c5ee78093604f20810583e5635df9bd','MySQL',1),

/*Data for the table `datasource_manage` */
INSERT INTO `datasource_manage`(`id`, `database_type_manage_id`, `datasource_name`, `datasource_describe`, `database_address`, `port`, `database_name`, `username`, `pwd`) VALUES ('07012038e48c425588311c1af52c7d98', '1c5ee78093604f20810583e5635df9bd', 'Test-示例', NULL, '192.168.6.135', 3306, 'test', 'root', 'admin');

/*Data for the table `design_model` */

/*Data for the table `filter` */

/*Data for the table `mapping_data` */

/*Data for the table `mapping_manage` */

/*Data for the table `report_form_manage` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
