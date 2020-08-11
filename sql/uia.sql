USE `prometheus-uia`;
set names utf8;

SET FOREIGN_KEY_CHECKS=0;
-- 组织机构
INSERT INTO `sys_menu`(`id`, `app_id`, `menu_code`, `menu_name`, `menu_alias`, `router_url`, `menu_component`, `icon`, `sort_index`, `parent_id`, `show_type`, `restrict_flag`, `dynamic_flag`, `dynamic_url`, `sub_router_type`, `sub_router_url`, `position`, `extra_param`, `tier_level`, `tier_path`, `create_user`, `leaf_flag`, `create_time`, `update_user`, `update_time`) VALUES ('001c637afe104affb2533f6d6b0ec073', NULL, 'visualization-data-model', '数据模型管理', NULL, '/index', NULL, 'desktop', 1, 'f9943742773449cdb6297fbb2579bcc1', '0', NULL, 0, '', 1, '/visualization/data-model', 1, NULL, 2, '_f9943742773449cdb6297fbb2579bcc1_001c637afe104affb2533f6d6b0ec073', 'sysadmin', 1, '2020-07-24 09:53:25', 'sysadmin', '2020-07-24 09:53:25');
INSERT INTO `sys_menu`(`id`, `app_id`, `menu_code`, `menu_name`, `menu_alias`, `router_url`, `menu_component`, `icon`, `sort_index`, `parent_id`, `show_type`, `restrict_flag`, `dynamic_flag`, `dynamic_url`, `sub_router_type`, `sub_router_url`, `position`, `extra_param`, `tier_level`, `tier_path`, `create_user`, `leaf_flag`, `create_time`, `update_user`, `update_time`) VALUES ('327b218902744fa1a94c09c28ee61096', NULL, 'visualization-component-manage', '组件管理', NULL, '/index', NULL, 'desktop', 6, 'f9943742773449cdb6297fbb2579bcc1', '0', NULL, 0, NULL, 1, '/visualization/component-manage', 1, NULL, 2, '_f9943742773449cdb6297fbb2579bcc1_327b218902744fa1a94c09c28ee61096', 'sysadmin', 1, '2020-07-24 09:55:19', NULL, NULL);
INSERT INTO `sys_menu`(`id`, `app_id`, `menu_code`, `menu_name`, `menu_alias`, `router_url`, `menu_component`, `icon`, `sort_index`, `parent_id`, `show_type`, `restrict_flag`, `dynamic_flag`, `dynamic_url`, `sub_router_type`, `sub_router_url`, `position`, `extra_param`, `tier_level`, `tier_path`, `create_user`, `leaf_flag`, `create_time`, `update_user`, `update_time`) VALUES ('6500ce4dd18c4f308e5f2a5c750bfcd0', NULL, 'visualization-data-mapping', '数据值映射管理', '', '/index', NULL, 'desktop', 5, 'f9943742773449cdb6297fbb2579bcc1', '0', NULL, 0, NULL, 1, '/visualization/data-mapping', 1, NULL, 2, '_f9943742773449cdb6297fbb2579bcc1_6500ce4dd18c4f308e5f2a5c750bfcd0', 'sysadmin', 1, '2020-07-24 09:54:25', NULL, NULL);
INSERT INTO `sys_menu`(`id`, `app_id`, `menu_code`, `menu_name`, `menu_alias`, `router_url`, `menu_component`, `icon`, `sort_index`, `parent_id`, `show_type`, `restrict_flag`, `dynamic_flag`, `dynamic_url`, `sub_router_type`, `sub_router_url`, `position`, `extra_param`, `tier_level`, `tier_path`, `create_user`, `leaf_flag`, `create_time`, `update_user`, `update_time`) VALUES ('70f455925ac54860b01dcdc01bf6263c', NULL, 'visualization-datasource', '数据源管理', NULL, '/index', NULL, 'desktop', 3, 'f9943742773449cdb6297fbb2579bcc1', '0', NULL, 0, '', 1, '/visualization/datasource', 1, NULL, 2, '_f9943742773449cdb6297fbb2579bcc1_70f455925ac54860b01dcdc01bf6263c', 'sysadmin', 1, '2020-07-24 09:53:36', 'sysadmin', '2020-07-24 09:53:36');
INSERT INTO `sys_menu`(`id`, `app_id`, `menu_code`, `menu_name`, `menu_alias`, `router_url`, `menu_component`, `icon`, `sort_index`, `parent_id`, `show_type`, `restrict_flag`, `dynamic_flag`, `dynamic_url`, `sub_router_type`, `sub_router_url`, `position`, `extra_param`, `tier_level`, `tier_path`, `create_user`, `leaf_flag`, `create_time`, `update_user`, `update_time`) VALUES ('73af20425376469ba7eb9e9e709c32af', NULL, 'visualization-component-type', '组件分组管理', NULL, '/index', NULL, 'desktop', 7, 'f9943742773449cdb6297fbb2579bcc1', '0', NULL, 0, NULL, 1, '/visualization/component-type', 1, NULL, 2, '_f9943742773449cdb6297fbb2579bcc1_73af20425376469ba7eb9e9e709c32af', 'sysadmin', 1, '2020-07-24 09:56:12', NULL, NULL);
INSERT INTO `sys_menu`(`id`, `app_id`, `menu_code`, `menu_name`, `menu_alias`, `router_url`, `menu_component`, `icon`, `sort_index`, `parent_id`, `show_type`, `restrict_flag`, `dynamic_flag`, `dynamic_url`, `sub_router_type`, `sub_router_url`, `position`, `extra_param`, `tier_level`, `tier_path`, `create_user`, `leaf_flag`, `create_time`, `update_user`, `update_time`) VALUES ('85f29f7b59094d00895537ffece92b1a', NULL, 'visualization-dashboard', '大屏管理', NULL, '/index', NULL, 'desktop', 2, 'f9943742773449cdb6297fbb2579bcc1', '0', NULL, 0, '', 1, '/visualization/dashboard', 1, NULL, 2, '_f9943742773449cdb6297fbb2579bcc1_85f29f7b59094d00895537ffece92b1a', 'sysadmin', 1, '2020-07-24 09:53:31', 'sysadmin', '2020-07-24 09:53:31');
INSERT INTO `sys_menu`(`id`, `app_id`, `menu_code`, `menu_name`, `menu_alias`, `router_url`, `menu_component`, `icon`, `sort_index`, `parent_id`, `show_type`, `restrict_flag`, `dynamic_flag`, `dynamic_url`, `sub_router_type`, `sub_router_url`, `position`, `extra_param`, `tier_level`, `tier_path`, `create_user`, `leaf_flag`, `create_time`, `update_user`, `update_time`) VALUES ('b8d6e66f9dcf4bb98576676e7e2c6453', NULL, 'visualization-report-form', '报表管理', NULL, '/index', NULL, 'desktop', 4, 'f9943742773449cdb6297fbb2579bcc1', '0', NULL, 0, '', 1, '/visualization/report-form', 1, NULL, 2, '_f9943742773449cdb6297fbb2579bcc1_b8d6e66f9dcf4bb98576676e7e2c6453', 'sysadmin', 1, '2020-07-24 09:53:40', 'sysadmin', '2020-07-24 09:53:40');
INSERT INTO `sys_menu`(`id`, `app_id`, `menu_code`, `menu_name`, `menu_alias`, `router_url`, `menu_component`, `icon`, `sort_index`, `parent_id`, `show_type`, `restrict_flag`, `dynamic_flag`, `dynamic_url`, `sub_router_type`, `sub_router_url`, `position`, `extra_param`, `tier_level`, `tier_path`, `create_user`, `leaf_flag`, `create_time`, `update_user`, `update_time`) VALUES ('f9943742773449cdb6297fbb2579bcc1', NULL, 'visualization', '可视化分析', NULL, '/index', NULL, 'desktop', 1, NULL, '1', NULL, 0, NULL, 1, NULL, 1, NULL, 1, '_f9943742773449cdb6297fbb2579bcc1', 'sysadmin', 0, '2020-07-23 16:10:00', NULL, NULL);















