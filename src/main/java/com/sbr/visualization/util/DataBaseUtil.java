package com.sbr.visualization.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbr.common.exception.SBRException;
import com.sbr.common.finder.Finder;
import com.sbr.common.util.StringUtil;
import com.sbr.ms.feign.system.dictionary.api.DictionaryFeignClient;
import com.sbr.ms.feign.system.dictionary.model.DataDictionary;
import com.sbr.ms.feign.system.organization.api.OrganizationFeignClient;
import com.sbr.ms.feign.system.organization.model.Organization;
import com.sbr.platform.auth.util.SecurityContextUtil;
import com.sbr.springboot.context.SpringContextUtils;
import com.sbr.springboot.json.InfoJson;
import com.sbr.springboot.rest.exception.RestResouceNotFoundException;
import com.sbr.visualization.bigscreendata.model.BigAttributeData;
import com.sbr.visualization.bigscreendata.model.BigScreenData;
import com.sbr.visualization.bigscreendata.model.BiglinkageData;
import com.sbr.visualization.constant.CommonConstant;
import com.sbr.visualization.datamodel.model.DataModel;
import com.sbr.visualization.datamodelattribute.dao.DataModelAttributeDAO;
import com.sbr.visualization.datamodelattribute.model.DataModelAttribute;
import com.sbr.visualization.datasourcemanage.model.DatasourceManage;
import com.sbr.visualization.filter.dao.FilterDAO;
import com.sbr.visualization.filter.model.Filter;
import com.sbr.visualization.mappingdata.dao.MappingDataDAO;
import com.sbr.visualization.mappingdata.model.MappingData;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName DataBaseUtil
 * @Description TODO Mysql数据源工具类
 * @Author zxx
 * @Version 1.0
 */
public class DataBaseUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseUtil.class);

    private static final String SQL = "SELECT * FROM ";// 数据库操作

    private static DataModelAttributeDAO dataModelAttributeDAO = SpringContextUtils.getBean(DataModelAttributeDAO.class);

    private static MappingDataDAO mappingDataDAO = SpringContextUtils.getBean(MappingDataDAO.class);

    private static FilterDAO filterDAO = SpringContextUtils.getBean(FilterDAO.class);

    private static OrganizationFeignClient organizationFeignClient = SpringContextUtils.getBean(OrganizationFeignClient.class);

    private static DictionaryFeignClient dictionaryFeignClient = SpringContextUtils.getBean(DictionaryFeignClient.class);

    /**
     * @return java.sql.Connection
     * @Author zxx
     * @Description //TODO 数据源连接
     * @Date 16:15 2020/6/11
     * @Param datasourseManage 数据源对象
     **/
    public static Connection databaseConnect(DatasourceManage datasourceManage) throws Exception {
        Connection connection = null;
        switch (datasourceManage.getDatabaseTypeManage().getDatabaseTypeName()) {
            case CommonConstant.MYSQL:
                connection = mysqlConnect(datasourceManage);
                break;
            /*case CommonConstant.ORACLE:
                connection = mysqlConnect(datasourceManage);
                break;目前不支持*/
        }
        return connection;
    }

    /**
     * @param datasourceManage 数据源对象
     * @return java.sql.Connection
     * @Author zxx
     * @Description //TODO 连接Mysql数据库
     * @Date 16:10 2020/6/11
     **/
    public static Connection mysqlConnect(DatasourceManage datasourceManage) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        try {
            //声明数据源（jdbc:mysql://localhost:端口号/数据库名）
            String url = "jdbc:mysql://" + datasourceManage.getDatabaseAddress() + ":" + datasourceManage.getPort() + "/" + datasourceManage.getDatabaseName() +
                    "?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false&serverTimezone=Asia/Shanghai";
            //数据库账号
            String user = datasourceManage.getUsername();
            //数据库密码
            String password = datasourceManage.getPwd();
            //加载数据库驱动
            Class.forName("com.mysql.jdbc.Driver");
            //创建数据库连接Connection conn=DriverManager.getConnection(数据源, 数据库账号, 数据库密码);
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.error("数据库连接失败", e);
            throw e;
        }
        return connection;
    }


    /**
     * 获取数据库下的所有表名
     */
    public static List<Map<String, Object>> getMySqlTableNames(DatasourceManage datasourceManage) throws Exception {
        List<Map<String, Object>> tableNames = new ArrayList<>();
        //获取Mysql连接
        Connection conn = mysqlConnect(datasourceManage);
        Statement st = null;
        ResultSet rs = null;
        try {
            //获取数据库的元数据
            //DatabaseMetaData db = conn.getMetaData();
            //从元数据中获取到所有的表名
//            rs = db.getTables(null, null, null, new String[]{"TABLE"});
//            while (rs.next()) {
//                tableNames.add(rs.getString(3));
//            }
            //获得语句执行者
            st = conn.createStatement();
            //执行SQL语句
            rs = st.executeQuery(" SELECT table_name,table_comment FROM information_schema.TABLES WHERE table_schema = '" + datasourceManage.getDatabaseName() + "' ORDER BY table_name ");
            //获取表名和注释
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("tableName", rs.getString("table_name"));
                map.put("tableAlias", rs.getString("table_name") + "(" + rs.getString("table_comment") + ")");
                tableNames.add(map);
            }
        } catch (SQLException e) {
            LOGGER.error("获取数据库表名列表失败", e);
        } finally {
            try {
                rs.close();
                closeConnection(conn);
            } catch (SQLException e) {
                LOGGER.error("关闭数据库获取列表失败", e);
            }
        }
        return tableNames;
    }

    /**
     * 关闭数据库连接
     *
     * @param conn
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("关闭数据库连接失败", e);
            }
        }
    }


    /**
     * @param tableName 数据表名称
     * @param conn      数据库连接
     * @param closeConn 关闭连接，如果不传，请求完成自动关闭，如果不为空需手动关闭
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @Author zxx
     * @Description //TODO 根据数据表名，获取字段名，字段类型
     * @Date 10:39 2020/6/15
     * @Param
     **/
    public static LinkedHashMap<String, Object> getColumnNames(String tableName, Connection conn, String closeConn) throws SQLException {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        try {
            pStemt = conn.prepareStatement(tableSql);
            //结果集元数据
            ResultSetMetaData rsmd = pStemt.getMetaData();
            //表列数
            int size = rsmd.getColumnCount();
            for (int i = 0; i < size; i++) {
                //key 字段名称，value 字段类型
                map.put(rsmd.getColumnName(i + 1), rsmd.getColumnTypeName(i + 1));
            }
        } catch (SQLException e) {
            LOGGER.error("获取字段数据类型失败", e);
            throw e;
        } finally {
            if (pStemt != null) {
                try {
                    pStemt.close();
                } catch (SQLException e) {
                    LOGGER.error("数据库连接关闭失败", e);
                    throw e;
                }
            }
            if (closeConn == null) {
                //关闭数据库连接
                closeConnection(conn);
            }
        }
        return map;
    }


    /**
     * @param tableName 数据表名
     * @param conn      连接
     * @return java.util.List<java.lang.String>
     * @Author zxx
     * @Description //TODO 获取数据库表，的字段注释
     * @Date 9:14 2020/7/6
     **/
    public static List<String> getColumnComments(String tableName, Connection conn) throws SQLException {
        //与数据库的连接
        PreparedStatement pStemt = null;
        String tableSql = SQL + tableName;
        List<String> columnComments = new ArrayList<>();//列名注释集合
        ResultSet rs = null;
        try {
            pStemt = conn.prepareStatement(tableSql);
            rs = pStemt.executeQuery("show full columns from " + tableName);
            while (rs.next()) {
                columnComments.add(rs.getString("Comment"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pStemt != null) {
                try {
                    pStemt.close();
                } catch (SQLException e) {
                    LOGGER.error("流异常关闭", e);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    LOGGER.error("流异常关闭", e);
                }
            }
            closeConnection(conn);
        }
        return columnComments;
    }


    /**
     * @param datasourceManage 数据源信息
     * @param sql              执行SQL语句
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.String>>
     * @Author zxx
     * @Description //TODO 执行SQL语句返回数据结果
     * @Date 10:23 2020/6/18
     **/
    public static List<Map<String, String>> getDatas(DatasourceManage datasourceManage, String sql, List<String> params) throws Exception {
        Statement st = null;
        ResultSet rs = null;
        Connection connection = null;
        List<Map<String, String>> mapList = null;
        PreparedStatement stmt = null;
        try {
            mapList = new ArrayList<>();
            //获取MySql连接
            connection = mysqlConnect(datasourceManage);
            //赋值条件参数
            if (params != null && params.size() > 0) {
                stmt = connection.prepareStatement(sql);
                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }
                rs = stmt.executeQuery();
            } else {
                st = connection.createStatement();
                rs = st.executeQuery(sql);
            }

            ResultSetMetaData data = rs.getMetaData();
            int columnCount = data.getColumnCount();
            while (rs.next()) {
                Map<String, String> map = new LinkedHashMap<String, String>();
                for (int i = 0; i < columnCount; i++) {
                    map.put(data.getColumnLabel(i + 1), rs.getString(i + 1));
                }
                mapList.add(map);
            }

        } catch (Exception e) {
            throw e;
        } finally {
            //关闭连接
            closeConnection(connection);
        }
        return mapList;
    }


    /**
     * @param dataModelAttributes 維度集合
     * @return java.lang.StringBuffer
     * @Author zxx
     * @Description //TODO 拼接分组条件 `tableName`.fieldsName
     * @Date 10:23 2020/6/18
     **/
    public static StringBuffer buildGroupBy(List<DataModelAttribute> dataModelAttributes) {
        StringBuffer groupSQL = new StringBuffer();
        if (dataModelAttributes != null && dataModelAttributes.size() > 0) {
            for (DataModelAttribute dataModelAttribute : dataModelAttributes) {
                //处理时间类型
                if (dataModelAttribute.getDateType() != null && StringUtils.isNotBlank(dataModelAttribute.getDateType())) {
                    buidDateTypeSQL(groupSQL, dataModelAttribute, dataModelAttribute.getDateType(), false);
                } else {
                    //获取当前数据模型属性
                    groupSQL.append("`" + dataModelAttribute.getTableName() + "`. " + dataModelAttribute.getFieldsName() + ",");
                }
            }
        }
        return groupSQL;
    }


    /**
     * @param dataModelAttributes 维度集合
     * @return java.lang.StringBuffer
     * @Author zxx
     * @Description //TODO 添加拼接sql条件判断，映射值匹配，包含新建计算维度
     * @Date 10:22 2020/6/18
     **/
    public static StringBuffer buildVeidooSQL(List<DataModelAttribute> dataModelAttributes) throws SBRException {
        StringBuffer sqlBuffer = new StringBuffer();
        if (dataModelAttributes != null && dataModelAttributes.size() > 0) {
            dataModelAttributes.forEach(dataModelAttribute -> {
                if (dataModelAttribute == null) {
                    throw new SBRException("维度不存在");
                }
                if (dataModelAttribute.getIsHide() == 2) {//不隐藏
                    //当前数据模型属性绑定映射值，需要翻译映射值处理
                    if (dataModelAttribute.getMappingManage() != null) {
                        if (dataModelAttribute.getIsNewCalculation() == null || dataModelAttribute.getIsNewCalculation() == 0) {//否
                            //拼接条件判断
                            sqlBuffer.append(" CASE `" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "");
                            //获取所有映射值
                            List<MappingData> mappingData = mappingDataDAO.findByMappingManageId(dataModelAttribute.getMappingManage().getId());
                            mappingData.stream().forEach(mappingData1 -> {
                                sqlBuffer.append(" WHEN '" + mappingData1.getOriginalData() + "'THEN '" + mappingData1.getMappingData() + "'");
                            });
                            sqlBuffer.append(" ELSE `" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + " END '" + dataModelAttribute.getRandomAlias() + "',");

                        } else if (dataModelAttribute.getIsNewCalculation() == 1) {//是新建计算维度
                            //拼接条件判断
                            sqlBuffer.append(" CASE " + dataModelAttribute.getExpression() + "");
                            //获取所有映射值
                            List<MappingData> mappingData = mappingDataDAO.findByMappingManageId(dataModelAttribute.getMappingManage().getId());
                            mappingData.stream().forEach(mappingData1 -> {
                                sqlBuffer.append(" WHEN '" + mappingData1.getOriginalData() + "'THEN '" + mappingData1.getMappingData() + "'");
                            });
                            sqlBuffer.append(" ELSE " + dataModelAttribute.getExpression() + " END '" + dataModelAttribute.getRandomAlias() + "',");
                        }

                    } else {
                        //处理时间
                        if (dataModelAttribute.getDateType() != null && StringUtils.isNotBlank(dataModelAttribute.getDateType())) {
                            buidDateTypeSQL(sqlBuffer, dataModelAttribute, dataModelAttribute.getDateType(), true);
                        } else {
                            if (dataModelAttribute.getIsNewCalculation() == null || dataModelAttribute.getIsNewCalculation() == 0) {//否
                                sqlBuffer.append("`" + dataModelAttribute.getTableName() + "`. " + dataModelAttribute.getFieldsName() + " AS '" + dataModelAttribute.getRandomAlias() + "',");

                            } else if (dataModelAttribute.getIsNewCalculation() == 1) {//是新建计算维度
                                sqlBuffer.append(" " + dataModelAttribute.getExpression() + " AS " + dataModelAttribute.getRandomAlias() + "");
                            }
                        }
                    }
                }
            });
        }
        return sqlBuffer;
    }

    /**
     * @param yAll 聚合參數
     * @return java.lang.StringBuffer
     * @Author zxx
     * @Description //TODO 拼接聚合條件
     * @Date 10:21 2020/6/18
     **/
    public static StringBuffer buildMeasureSQL(List<BigAttributeData> yAll) throws SBRException {
        StringBuffer sqlBuffer = new StringBuffer();
        if (yAll != null && yAll.size() > 0) {
            yAll.forEach(bigAttributeData -> {
                //TODO 度量
                if (bigAttributeData.getType().equals("m")) {
                    DataModelAttribute dataModelAttribute = dataModelAttributeDAO.findOne(bigAttributeData.getId());
                    if (dataModelAttribute == null) {
                        throw new SBRException("度量不存在");
                    }
                    if (bigAttributeData.getAggregator() == null || bigAttributeData.getAggregator().equals("")) {
                        //新建计算度量
                        if (dataModelAttribute.getIsNewCalculation() != null && dataModelAttribute.getIsNewCalculation() == 2 && dataModelAttribute.getModelType() == 2) {
                            sqlBuffer.append(" " + dataModelAttribute.getExpression() + " AS '" + dataModelAttribute.getRandomAlias() + "',");
                        } else {
                            //默认求和
                            sqlBuffer.append(" SUM(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + ")AS '" + dataModelAttribute.getRandomAlias() + "',");
                        }
                    } else {
                        if (bigAttributeData.getAggregator().equalsIgnoreCase("DISTINCT")) {//去重Count计数
                            //去重计数，特殊
                            sqlBuffer.append("" + "COUNT( DISTINCT `" + dataModelAttribute.getTableName() + "`. " + dataModelAttribute.getFieldsName() + ")AS '" + dataModelAttribute.getRandomAlias() + "',");
                        } else {
                            //根据传递的聚合参数操作
                            sqlBuffer.append(" " + bigAttributeData.getAggregator() + "(`" + dataModelAttribute.getTableName() + "`. " + dataModelAttribute.getFieldsName() + ")AS '" + dataModelAttribute.getRandomAlias() + "',");
                        }
                    }
                }
            });
        }
        return sqlBuffer;
    }


    /**
     * @param ids ID集合
     * @return java.lang.String
     * @Author zxx
     * @Description //TODO 获取SQL IN" 'id','id' "
     * @Date 15:56 2020/6/19
     * @Param
     **/
    public static String getSqlIn(List<String> ids) {
        String join = String.join(",", ids);
        String[] split = (join.replaceAll("，", ",")).split(",");
        String sqlIn = "'" + StringUtils.join(split, "','") + "'";
        return sqlIn;
    }


    /**
     * @param association 关联关系
     * @return java.lang.String
     * @Author zxx
     * @Description //TODO 根据关联关系，获取关系SQL
     * @Date 14:35 2020/6/19
     **/
    public static StringBuffer getJOINSqlByAssociation(String association) throws IOException {
        StringBuffer sqlBuffer = new StringBuffer();
        ObjectMapper objectMapper = new ObjectMapper();
        Map map = objectMapper.readValue(association, Map.class);
        //主表名称
        String table = (String) map.get("table");
        //关联条件
        List<Map<String, Object>> joinList = (List<Map<String, Object>>) map.get("children");
        //添加开头主表
        sqlBuffer.append(table);
        //子表集合
        List<Map<String, Object>> sonTable = new ArrayList<>();

        //拼接主表关联
        joinList.forEach(map1 -> {
            buidJOINSql(sqlBuffer, sonTable, map1);
        });

        //拼接子表关联,sonTable传递null代表不在查找字节点
        sonTable.forEach(map1 -> {
            buidJOINSql(sqlBuffer, null, map1);
        });
        return sqlBuffer;
    }


    /**
     * @param sqlBuffer
     * @param sonTable  子表集合
     * @param map1      当前表
     * @return void
     * @Author zxx
     * @Description //TODO 拼接SQL，并找当所有子节点
     * @Date 14:32 2020/6/19
     * @Param
     **/
    private static void buidJOINSql(StringBuffer sqlBuffer, List<Map<String, Object>> sonTable, Map<String, Object> map1) {
        //连接类型 LEFT  INNER
        String joinType = (String) map1.get("joinType");
        //获取关联条件
        List<Map<String, Object>> condition = (List<Map<String, Object>>) map1.get("condition");
        //当前表关联的子表
        List<Map<String, Object>> joins = (List<Map<String, Object>>) map1.get("children");
        if (sonTable != null && joins != null) {
            //递归取当前表的所有子表，存入子表集合获
            childTable(sonTable, map1);
        }
        // TODO 拼接LEFT连接和INNER 连接
        if ("left".equalsIgnoreCase(joinType)) {
            sqlBuffer.append(" LEFT JOIN " + map1.get("table") + " ON ");
        } else {
            if ("inner".equalsIgnoreCase(joinType)) {
                sqlBuffer.append(" INNER JOIN " + map1.get("table") + " ON ");
            }
        }
        //TODO 循环条件，拼接连接条件，拼接ON 条件，如果有多个ON就添加AND
        final int[] i = {0};
        condition.forEach(map2 -> {
            i[0]++;
            if (i[0] > 1) {//如果有多个关联条件就拼接AND
                sqlBuffer.append(" AND " + map2.get("left") + " = " + map2.get("right") + "");
            } else {
                sqlBuffer.append("" + map2.get("left") + " = " + map2.get("right") + "");
            }
        });
    }


    /**
     * @param sonTable 子节点集合
     * @param map      当前表对象
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @Author zxx
     * @Description //TODO 递归获取所有子节点表关联
     * @Date 14:19 2020/6/19
     **/
    public static List<Map<String, Object>> childTable(List<Map<String, Object>> sonTable, Map<String, Object> map) {
        List<Map<String, Object>> join = (List<Map<String, Object>>) map.get("children");
        if (join != null && join.size() > 0) {
            for (Map<String, Object> stringObjectMap : join) {
                sonTable.add(stringObjectMap);
                childTable(sonTable, stringObjectMap);
            }
        }
        return sonTable;
    }


    /**
     * @param sonTableName 子表名集合
     * @param map          表信息
     * @return java.util.List<java.lang.String>
     * @Author zxx
     * @Description //TODO 递归获取子表明
     * @Date 15:12 2020/6/24
     **/
    public static Set<String> childTableName(Set<String> sonTableName, Map<String, Object> map) {
        sonTableName.add((String) map.get("name"));
        List<Map<String, Object>> children = (List<Map<String, Object>>) map.get("children");
        if (children != null && children.size() > 0) {
            for (Map<String, Object> stringObjectMap : children) {
                sonTableName.add((String) stringObjectMap.get("name"));
                childTableName(sonTableName, stringObjectMap);
            }
        }
        return sonTableName;
    }


    /**
     * @param association      表关系
     * @param fieldList        字段属性
     * @param datasourceManage 数据源
     * @return java.lang.StringBuffer
     * @Author zxx
     * @Description //TODO 获取字段SQL `sys_api`.`id` AS SG4044904F58249DE4, `sys_api`.`app_id` AS SGF04C2F6CA8489BDD,
     * @Date 15:03 2020/6/24
     * @Param
     **/
    public static StringBuffer getSQLfield(String association, DatasourceManage datasourceManage, List<String> fieldList) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map map = objectMapper.readValue(association, Map.class);
        //主表名称
        String table = (String) map.get("table");
        //关联条件
        List<Map<String, Object>> joinList = (List<Map<String, Object>>) map.get("join");
        //所有子表
        Set<String> sonTableName = new HashSet<>();
        sonTableName.add(table);
        //递归获取所有子表名
        if (joinList != null && joinList.size() > 0) {
            joinList.forEach(map1 -> {
                childTableName(sonTableName, map1);
            });
        }
        //获取数据库连接
        Connection connection = databaseConnect(datasourceManage);
        //循环表，获取所有字段属性，拼接SQL
        StringBuffer fieldBuffer = new StringBuffer();
        for (String tableName : sonTableName) {
            Map<String, Object> columnNames = getColumnNames(tableName, connection, "no");
            columnNames.forEach((k, v) -> {
                if (fieldList != null) {
                    //如果属性已经存在追加表名 field(tableName)
                    if (fieldList.contains(k)) {
                        fieldList.add(k + "(" + tableName + ")");
                    } else {
                        //获取字段属性
                        fieldList.add(k);
                    }
                }

                if (fieldBuffer.toString().contains(k)) {
                    //拼接属性SQL，如果当前属性存在追加表名 AS 'xx(tableName)'
                    fieldBuffer.append(" `" + tableName + "`.`" + k + "`AS '" + k + "(" + tableName + ")" + "',");
                } else {
                    //拼接属性SQL
                    fieldBuffer.append(" `" + tableName + "`.`" + k + "`,");
                }
            });
        }
        //关闭连接
        closeConnection(connection);
        return fieldBuffer;
    }


    /**
     * @param filterList 过滤器集合
     * @param sqlParam   SQL条件参数集合
     * @return java.lang.StringBuffer
     * @Author zxx
     * @Description //TODO 构建SQL-WHERE条件
     * @Date 16:13 2020/7/1
     **/
    public static List<StringBuffer> buidWhereSQL(List<Filter> filterList, List<String> sqlParam) throws IOException {
        List<StringBuffer> bufferList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        //TODO 条件SQL
        StringBuffer sqlbuffer = new StringBuffer();
        //TODO 展示SQL
        StringBuffer sqlShow = new StringBuffer();
        if (filterList != null) {
            for (Filter filter : filterList) {
                //TODO 条件SQL
                StringBuffer textMatchParamBuffer = new StringBuffer();

                //TODO 展示SQL
                StringBuffer textMatchShowBuffer = new StringBuffer();

                //拼接Text条件方法
                if (filter.getTextMatch() != null && StringUtils.isNotEmpty(filter.getTextMatch())) {
                    Map textMatchMap = objectMapper.readValue(filter.getTextMatch(), Map.class);
                    if (textMatchMap != null && textMatchMap.size() > 0) {
                        //连接方式
                        String operator = (String) textMatchMap.get("operator");
                        //获取值
                        List<Map<String, Object>> valueList = (List<Map<String, Object>>) textMatchMap.get("value");
                        if (valueList.size() > 1) {//如果大于1需要添加括号包裹（）
                            textMatchParamBuffer.append("(");//条件
                            textMatchShowBuffer.append("(");//展示

                            //TODO 条件SQL
                            //遍历集合，构建查询条件
                            buildTextSql(sqlParam, textMatchParamBuffer, textMatchShowBuffer, operator, valueList);
                            //截取最后面的operator连接方式条件
                            String sqlStr = textMatchParamBuffer.substring(0, textMatchParamBuffer.length() - operator.length());
                            sqlbuffer.append(sqlStr += " )");

                            //TODO 展示SQL
                            String sqlShowStr = textMatchShowBuffer.substring(0, textMatchShowBuffer.length() - operator.length());
                            sqlShow.append(sqlShowStr += " )");
                        } else {
                            //TODO 条件SQL
                            //遍历集合，构建查询条件
                            buildTextSql(sqlParam, textMatchParamBuffer, textMatchShowBuffer, operator, valueList);
                            //截取最后面的operator连接方式
                            String sqlStr = textMatchParamBuffer.substring(0, textMatchParamBuffer.length() - operator.length());
                            sqlbuffer.append(sqlStr);

                            //TODO 展示SQL
                            String sqlShowStr = textMatchShowBuffer.substring(0, textMatchShowBuffer.length() - operator.length());
                            sqlShow.append(sqlShowStr);
                        }
                        //TODO 条件SQL
                        sqlbuffer.append("" + " AND");

                        //TODO 展示SQL
                        sqlShow.append("" + " AND");
                    }
                }

                //拼接list_match条件
                if (filter.getListMatch() != null && StringUtils.isNotEmpty(filter.getListMatch())) {
                    buildListMatch(sqlParam, objectMapper, sqlbuffer, sqlShow, filter);
                }

                //拼接date条件
                if (filter.getDate() != null && StringUtils.isNotEmpty(filter.getDate())) {
                    Map dateMap = objectMapper.readValue(filter.getDate(), Map.class);
                    if (dateMap != null && dateMap.size() > 0) {
                        //时间范围参数
                        String value = (String) dateMap.get("value");
                        //`tableName`.`字段名`
                        String name = (String) dateMap.get("name");
                        //获取自定义时间
                        Map<String, String> datailMap = (Map) dateMap.get("detail");
                        //时间参数结果
                        Map<String, String> dateScope = null;
                        if ("date".equals(filter.getType())) {//年月日
                            //获取时间范围,年、月、日
                            dateScope = DateUtil.getDateScope(value, datailMap);
                        } else {
                            //获取时间范围，年、月、日、时、分、秒 datetime
                            dateScope = DateUtil.getDateHHMMSSScope(value, datailMap);
                        }
                        String minDate = dateScope.get("minDate");
                        String maxDate = dateScope.get("maxDate");
                        sqlParam.add(minDate);
                        sqlParam.add(maxDate);
                        //TODO 时间条件SQL
                        sqlbuffer.append("" + name + " >= '? ' AND " + name + " <= '? ' AND");

                        //TODO 展示SQL
                        sqlShow.append("" + name + " >= '" + dateScope.get("minDate") + "' AND " + name + " <= '" + dateScope.get("maxDate") + "' AND");
                    }
                }
            }
        }
        bufferList.add(sqlbuffer);//0条件
        bufferList.add(sqlShow);//1展示
        return bufferList;
    }

    private static void buildListMatch(List<String> sqlParam, ObjectMapper objectMapper, StringBuffer sqlbuffer, StringBuffer sqlShow, Filter filter) throws IOException {
        Map listMatchMap = objectMapper.readValue(filter.getListMatch(), Map.class);
        if (listMatchMap != null && listMatchMap.size() > 0) {
            //类型
            String mode = (String) listMatchMap.get("mode");
            //表名字段名`tableName`.`name`
            String name = (String) listMatchMap.get("name");
            List<String> strList = null;
            if ("list".equals(mode)) {//选择
                strList = (List<String>) listMatchMap.get("list");
            } else {//手动
                strList = (List<String>) listMatchMap.get("manual");
            }
            StringBuffer inBuffer = new StringBuffer("(");
            for (int i = 0; i < strList.size(); i++) {
                inBuffer.append(" ? ,");
                sqlParam.add(strList.get(i));
            }
            String str = inBuffer.substring(0, inBuffer.length() - 1);
            str += (")");
            //TODO 条件SQL
            if (sqlbuffer != null) {
                sqlbuffer.append("" + name + " IN " + str + " AND");
            }

            //TODO 展示SQL
            if (sqlShow != null) {
                String sqlIn = DataBaseUtil.getSqlIn(strList);
                sqlShow.append("" + name + " IN (" + sqlIn + ") AND");
            }
        }
    }

    private static void buildTextSql(List<String> sqlParam, StringBuffer textMatchParamBuffer, StringBuffer textMatchShowBuffer, String operator, List<Map<String, Object>> valueList) {
        valueList.forEach(map1 -> {
            StringBuffer sqlParambuffer = new StringBuffer();
            StringBuffer sqlShowBuffer = new StringBuffer();
            buidTextMatch(operator, (String) map1.get("name"), (String) map1.get("type"), map1.get("value"), sqlParam, sqlParambuffer, sqlShowBuffer);
            textMatchParamBuffer.append(sqlParambuffer);
            textMatchShowBuffer.append(sqlShowBuffer);
        });
    }

    /**
     * like %123% OR AND
     *
     * @param operator      连接方式
     * @param name          参数
     * @param type          type      条件类型
     *                      value     条件值
     * @param sqlParam      条件参数
     * @param sqlbuffer     条件SQL
     * @param sqlShowBuffer 展示SQL
     * @return java.lang.StringBuffer
     * @Author zxx
     * @Description //TODO 根据查询条件拼接条件SQL
     * @Date 9:39 2020/7/2
     **/
    private static void buidTextMatch(String operator, String name, String type, Object value, List<String> sqlParam, StringBuffer sqlbuffer, StringBuffer sqlShowBuffer) {
        //设置条件字段`tableName`.`字段`
        //条件SQL
        sqlbuffer.append("" + name);
        if (sqlShowBuffer != null) {
            //展示SQL
            sqlShowBuffer.append("" + name);
        }
        switch (type) {
                case "include"://包含
                sqlbuffer.append(" LIKE ? " + operator);
                sqlParam.add("%" + value + "%");
                if (sqlShowBuffer != null) {
                    //展示SQL
                    sqlShowBuffer.append(" LIKE '%" + value + "%' " + operator);
                }
                break;
            case "start"://开始于
                sqlbuffer.append(" LIKE ? " + operator);
                sqlParam.add("" + value + "%");
                if (sqlShowBuffer != null) {
                    //展示SQL
                    sqlShowBuffer.append(" LIKE '" + value + "%' " + operator);
                }
                break;
            case "end"://结束于
                sqlbuffer.append(" LIKE ? " + operator);
                sqlParam.add("%" + value + "");
                if (sqlShowBuffer != null) {
                    //展示SQL
                    sqlShowBuffer.append(" LIKE '%" + value + "' " + operator);
                }
                break;
            case "notIncluded"://不包含
                sqlbuffer.append(" NOT LIKE ? " + operator);
                sqlParam.add("%" + value + "%");
                if (sqlShowBuffer != null) {
                    //展示SQL
                    sqlShowBuffer.append(" NOT LIKE '%" + value + "%' " + operator);
                }
                break;
            case "equal"://等于
                sqlbuffer.append(" = ? " + operator);
                sqlParam.add("" + value + "");
                if (sqlShowBuffer != null) {
                    //展示SQL
                    sqlShowBuffer.append(" = '" + value + "' " + operator);
                }
                break;
            case "ineq"://不等于
                sqlbuffer.append(" <> ? " + operator);
                sqlParam.add("" + value + "");
                if (sqlShowBuffer != null) {
                    //展示SQL
                    sqlShowBuffer.append(" <> '" + value + "' " + operator);
                }
                break;
            case "null"://等于NULL
                sqlbuffer.append(" IS NULL " + operator);
                if (sqlShowBuffer != null) {
                    //展示SQL
                    sqlShowBuffer.append(" IS NULL " + operator);
                }
                break;
            case "notNull"://不等于NULL
                sqlbuffer.append(" IS NOT NULL " + operator);
                if (sqlShowBuffer != null) {
                    //展示SQL
                    sqlShowBuffer.append(" IS NOT NULL " + operator);
                }
                break;
            case "gt"://大于
                sqlbuffer.append(" > ? " + operator);
                sqlParam.add("" + value + "");
                if (sqlShowBuffer != null) {
                    //展示SQL
                    sqlShowBuffer.append(" > '" + value + "' " + operator);
                }
                break;
            case "gte"://大于等于
                sqlbuffer.append(" >= ? " + operator);
                sqlParam.add("" + value + "");
                if (sqlShowBuffer != null) {
                    //展示SQL
                    sqlShowBuffer.append(" >= '" + value + "' " + operator);
                }
                break;
            case "lt"://小于
                sqlbuffer.append(" < ? " + operator);
                sqlParam.add("" + value + "");
                if (sqlShowBuffer != null) {
                    //展示SQL
                    sqlShowBuffer.append(" < '" + value + "' " + operator);
                }
                break;
            case "lte"://小于等于
                sqlbuffer.append(" <= ? " + operator);
                sqlParam.add("" + value + "");
                if (sqlShowBuffer != null) {
                    //展示SQL
                    sqlShowBuffer.append(" <= '" + value + "' " + operator);
                }
                break;
        }
    }


    /**
     * @param sortAll 维度、度量参数
     * @return java.lang.StringBuffer
     * @Author zxx
     * @Description //TODO 构建排序条件 （大屏）
     * @Date 15:59 2020/7/15
     **/
    public static StringBuffer buildSortSQL(List<BigAttributeData> sortAll) {
        StringBuffer sqlBuffer = new StringBuffer();
        if (sortAll != null && sortAll.size() > 0) {
            sortAll.forEach(bigAttributeData -> {
                if (bigAttributeData != null && StringUtils.isNotEmpty(bigAttributeData.getSort())) {
                    DataModelAttribute dataModelAttribute = dataModelAttributeDAO.findOne(bigAttributeData.getId());
                    if (bigAttributeData.getSort() != null && StringUtils.isNotEmpty(bigAttributeData.getSort())) {
                        //拼接排序条件
                        String sort = bigAttributeData.getSort();
                        //`tableName`.`name` decs
//                        sqlBuffer.append(" `" + dataModelAttribute.getTableName() + "`.`" + dataModelAttribute.getFieldsName() + "` " + sort + ",");
                        //修改排序问题，排序使用随机别名排序
                        sqlBuffer.append(" `" + dataModelAttribute.getRandomAlias() + "` " + sort + ",");
                    }
                }
            });
        }
        return sqlBuffer;
    }

    /**
     * @param bigScreenData 当前大屏对象
     * @param modelDAOOne   当前数据模型对象
     * @param param         SQL条件参数
     * @return java.lang.String
     * @Author zxx
     * @Description //TODO 构建WHERE条件SQL语句 （大屏）
     * @Date 16:27 2020/7/14
     **/
    public static String buildWhereSQL(BigScreenData bigScreenData, DataModel modelDAOOne, List<String> param) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String whereSQL = "";
        if (bigScreenData.getFilterList() != null && bigScreenData.getFilterList().size() > 0) {
            //如果数据模型存在SQL条件，统计图需要拼接上此条件
            if (modelDAOOne.getSqlCondition() != null && StringUtils.isNotEmpty(modelDAOOne.getSqlCondition())) {
                List<String> strList = new ArrayList<>();
                //获取大屏过滤器条件，0条件SQL 1展示SQL
                List<StringBuffer> stringBufferList = DataBaseUtil.buidWhereSQL(bigScreenData.getFilterList(), strList);
                //拼接模型SQL，和条件SQL 截取AND
                whereSQL = modelDAOOne.getSqlCondition() + " AND " + stringBufferList.get(0).substring(0, stringBufferList.get(0).length() - 3);
                //获取SQL条件参数
                String sqlParam = modelDAOOne.getSqlParam();
                List<String> list = objectMapper.readValue(sqlParam, List.class);
                list.addAll(strList);
                param.addAll(list);
            } else {
                //0条件SQL 1展示SQL
                List<StringBuffer> stringBufferList = DataBaseUtil.buidWhereSQL(bigScreenData.getFilterList(), param);
                whereSQL = " WHERE" + stringBufferList.get(0).substring(0, stringBufferList.get(0).length() - 3);
            }
        } else {
            if (modelDAOOne.getSqlCondition() != null && StringUtils.isNotEmpty(modelDAOOne.getSqlCondition())) {
                String sqlParam = modelDAOOne.getSqlParam();
                List<String> list = objectMapper.readValue(sqlParam, List.class);
                param.addAll(list);
                //拼接模型SQL
                whereSQL = modelDAOOne.getSqlCondition();
            }
        }
        return whereSQL;
    }


    /**
     * @param bigScreenData 数据大屏实体
     * @param modelDAOOne   当前数据模型
     * @param measureSQL    度量条件SQL 聚合
     * @param dimensionsSQL 维度条件SQL
     * @param groupBy       分组条件SQL
     * @param whereSQL      WHERE条件SQL
     * @param sortSQL       排序SQL
     * @param param         SQL条件参数
     * @return java.lang.StringBuffer
     * @Author zxx
     * @Description //TODO 处理查询结果SQL语句 （大屏）
     * @Date 16:23 2020/7/14
     * @Param
     **/
    public static StringBuffer buidSQL(BigScreenData bigScreenData, DataModel modelDAOOne, StringBuffer measureSQL, StringBuffer dimensionsSQL, StringBuffer groupBy, String whereSQL, StringBuffer sortSQL, List<String> param) throws IOException {
        //TODO  如果关联关系不为空的话，证明是关联查询,处理大SQL
        StringBuffer sqlBuffer = null;
        ObjectMapper objectMapper = new ObjectMapper();

        Map map = objectMapper.readValue(modelDAOOne.getAssociation(), Map.class);
        //获取关联关系，如果没有证明为单表
        List<Map<String, Object>> joinList = (List<Map<String, Object>>) map.get("children");


        //没有关联关系，单表处理
        //度量条件处理SQL
        String measure = StringUtils.isEmpty(measureSQL.toString()) ? "" : measureSQL.substring(0, measureSQL.length() - 1);
        //处理分组SQL
        String group = "";
        if (groupBy != null) {
            group = StringUtils.isEmpty(groupBy.toString()) ? "" : groupBy.substring(0, groupBy.length() - 1);
            if (group != null && StringUtils.isNotEmpty(group)) {
                group = " GROUP BY " + group;
            }
        }

        //处理排序
        String sort = StringUtils.isEmpty(sortSQL.toString()) ? "" : sortSQL.substring(0, sortSQL.length() - 1);
        if (sort != null && StringUtils.isNotEmpty(sort)) {
            sort = " ORDER BY " + sort;
        }

        //文字类型，截取，
        String dimensions = "";
        if (dimensionsSQL != null && StringUtils.isNotEmpty(dimensionsSQL.toString())) {
            if (StringUtils.isEmpty(measure)) {
                dimensions = dimensionsSQL.toString().substring(0, dimensionsSQL.toString().length() - 1);
            } else {
                dimensions = dimensionsSQL.toString();
            }
        }

        //联动条件
        if (bigScreenData.getBiglinkageData() != null) {
            //获取模型属性
            DataModelAttribute dataModelAttribute = dataModelAttributeDAO.findOne(bigScreenData.getBiglinkageData().getDataModelAttributeId());

            //如果被联动的模型属性，绑定了数据映射，那么就应该获取数据映射的值，然后判断传过来的值是否等于映射的值，如果存在则取映射的原始值作为联动条件，否则的话直接用传过来的联动值
            String value = "";
            if (dataModelAttribute.getMappingManage() != null) {
                //找到对应的数据映射值
                List<MappingData> mappingDataList = mappingDataDAO.findByMappingManageId(dataModelAttribute.getMappingManage().getId());
                for (MappingData mappingData : mappingDataList) {
                    if (mappingData.getMappingData().equals(bigScreenData.getBiglinkageData().getValue())) {
                        //获取原始值
                        value = mappingData.getOriginalData();
                        break;
                    }
                }
            } else {
                //直接使用传递值
                value = bigScreenData.getBiglinkageData().getValue();
            }

            StringBuffer sqlbuffer = new StringBuffer();
            //拼接SQL条件
            buidTextMatch("", "`" + dataModelAttribute.getTableName() + "`.`" + dataModelAttribute.getFieldsName() + "`", bigScreenData.getBiglinkageData().getLinkType(), value, param, sqlbuffer, null);
            //如果之前有条件
            if (whereSQL != null && StringUtils.isNotEmpty(whereSQL)) {
                whereSQL += " AND " + sqlbuffer.toString();
            } else {//之前没有条件
                whereSQL += " WHERE " + sqlbuffer.toString();
            }
        }

        //关联Url参数
        if (bigScreenData.getQueryData() != null) {
            //获取模型属性
            DataModelAttribute dataModelAttribute = dataModelAttributeDAO.findOne(bigScreenData.getQueryData().getDataModelAttributeId());

            //如果被联动的模型属性，绑定了数据映射，那么就应该获取数据映射的值，然后判断传过来的值是否等于映射的值，如果存在则取映射的原始值作为联动条件，否则的话直接用传过来的联动值
            String value = "";
            if (dataModelAttribute.getMappingManage() != null) {
                //找到对应的数据映射值
                List<MappingData> mappingDataList = mappingDataDAO.findByMappingManageId(dataModelAttribute.getMappingManage().getId());
                for (MappingData mappingData : mappingDataList) {
                    if (mappingData.getMappingData().equals(bigScreenData.getQueryData().getValue())) {
                        //获取原始值
                        value = mappingData.getOriginalData();
                        break;
                    }
                }
            } else {
                //直接使用传递值
                value = bigScreenData.getQueryData().getValue();
            }

            StringBuffer sqlbuffer = new StringBuffer();
            //拼接SQL条件
            buidTextMatch("", "`" + dataModelAttribute.getTableName() + "`.`" + dataModelAttribute.getFieldsName() + "`", bigScreenData.getQueryData().getLinkType(), value, param, sqlbuffer, null);
            //如果之前有条件
            if (whereSQL != null && StringUtils.isNotEmpty(whereSQL)) {
                whereSQL += " AND " + sqlbuffer.toString();
            } else {//之前没有条件
                whereSQL += " WHERE " + sqlbuffer.toString();
            }
        }

        //全局过滤条件
        if (bigScreenData.getConditions() != null) {
            List<BiglinkageData> conditions = bigScreenData.getConditions();
            for (BiglinkageData condition : conditions) {
                //获取模型属性
                DataModelAttribute dataModelAttribute = dataModelAttributeDAO.findOne(condition.getDataModelAttributeId());
                //获取条件类型 select multiSelect
                String k = condition.getK();
                //获取全局条件值
                String value = condition.getValue();
                StringBuffer sqlbuffer = new StringBuffer();
                if (k.equals("select")) {
                    //拼接SQL条件
                    buidTextMatch("", "`" + dataModelAttribute.getTableName() + "`.`" + dataModelAttribute.getFieldsName() + "`", condition.getLinkType(), value, param, sqlbuffer, null);
                } else if (k.equals("multiSelect")) {
                    String[] split = value.split(",");
                    String sqlIn = getSqlIn(Arrays.asList(split));
                    sqlbuffer.append(" `" + dataModelAttribute.getTableName() + "`.`" + dataModelAttribute.getFieldsName() + "` IN (" + sqlIn + ")");
                }
                //如果之前有条件
                if (whereSQL != null && StringUtils.isNotEmpty(whereSQL)) {
                    whereSQL += " AND " + sqlbuffer.toString();
                } else {//之前没有条件
                    whereSQL += " WHERE " + sqlbuffer.toString();
                }
            }
        }

        //单位权限数据
        whereSQL = buildOrgDataAuthority(modelDAOOne, whereSQL, param, objectMapper);


        if (joinList != null && joinList.size() > 0) {
            //获取JOINSQL，多表连接
            StringBuffer sqlJoinBuffer = DataBaseUtil.getJOINSqlByAssociation(modelDAOOne.getAssociation());
            //拼接SQL
            sqlBuffer = new StringBuffer(" SELECT " + dimensions + measure + " FROM " + sqlJoinBuffer + whereSQL + group + sort + " LIMIT 0 ," + bigScreenData.getLimit() + " ");
        } else {
            //获取表名
            String tableName = (String) map.get("name");
            //结果SQL
            sqlBuffer = new StringBuffer(" SELECT " + dimensions + measure + " FROM " + tableName + whereSQL + group + sort + " LIMIT 0 ," + bigScreenData.getLimit() + " ");
        }
        return sqlBuffer;
    }

    /**
     * 构建单位数据权限
     *
     * @param modelDAOOne  数据模型
     * @param whereSQL     字符串
     * @param param        参数集合
     * @param objectMapper 字符串转换
     * @return
     * @throws IOException
     */
    public static String buildOrgDataAuthority(DataModel modelDAOOne, String whereSQL, List<String> param, ObjectMapper objectMapper) throws IOException {
        List<Filter> filterList = filterDAO.findByDataModelId(modelDAOOne.getId());
        List<Filter> list = filterList.stream().filter(filter -> (filter.getOrgCategory() != null) && (filter.getOrgType() != null)).collect(Collectors.toList());
        if (list != null && list.size() > 0) {
            Set<Organization> organizationSet = new HashSet<>();
            for (Filter filter : list) {
                //1、包含下级 2、包含上级 3、包含自己
                String orgCategory = filter.getOrgCategory();
                String[] split = orgCategory.split(",");
                //当前单位ID
                String orgId = SecurityContextUtil.getUserSessionInfo().getOrgId();
                for (String s : split) {
                    switch (s) {
                        case "1":
                            organizationSet.addAll(organizationFeignClient.findChildOrg(orgId));
                            break;
                        case "2":
                            Organization parentOrg = organizationFeignClient.findParentOrg(orgId);
                            organizationSet.add(parentOrg);
                            organizationSet.add(parentOrg.getParent());
                            break;
                        case "3":
                            organizationSet.add(organizationFeignClient.findParentOrg(orgId));
                    }
                }
                //过滤出去单位权限的ID
                List<String> idList = null;
                if (filter.getOrgType() != null && !StringUtil.isEmpty(filter.getOrgType())) {
                    //获取机构类型字典Key
                    List<Integer> orgTypeList = new ArrayList<>();
                    String orgType = filter.getOrgType();
                    String[] split1 = orgType.split(",");
                    Map<String, Object> queryMap = new HashMap<>();
                    queryMap.put("dictionary_group", "org_type");
                    List<DataDictionary> dataDictionaryPage = dictionaryFeignClient.findDataDictionaryPage(queryMap);
                    for (DataDictionary dataDictionary : dataDictionaryPage) {
                        for (String s : split1) {
                            if (s.equals(dataDictionary.getDictionaryValue())) {
                                orgTypeList.add(Integer.valueOf(dataDictionary.getDictionaryKey()));
                            }
                        }
                    }
                    //过滤字典数据
                    List<Organization> collect = organizationSet.stream().filter((Organization o) -> orgTypeList.contains(o.getOrgType())).collect(Collectors.toList());
                    idList = collect.stream().map(organization -> organization.getId()).collect(Collectors.toList());
                }

                String listStr = "";

                if (idList == null || idList.size() == 0) {
                    idList.add("默认单位");
                }

                if (idList != null && idList.size() > 0) {
                    listStr = objectMapper.writeValueAsString(idList);
                    DataModelAttribute dataModelAttribute = dataModelAttributeDAO.findOne(filter.getFieldId());
                    filter.setListMatch("{\"mode\":\"list\",\"name\":\"`" + dataModelAttribute.getTableName() + "`.`" + dataModelAttribute.getFieldsName() + "`\",\"list\":" + listStr + "}");
                    StringBuffer sqlbuffer = new StringBuffer();
                    buildListMatch(param, objectMapper, sqlbuffer, null, filter);
                    //如果之前有条件
                    if (whereSQL != null && StringUtils.isNotEmpty(whereSQL)) {
                        whereSQL += " AND " + sqlbuffer.toString().substring(0, sqlbuffer.length() - 3);
                    } else {//之前没有条件
                        whereSQL += " WHERE " + sqlbuffer.toString().substring(0, sqlbuffer.length() - 3);
                    }
                }
            }
        }
        return whereSQL;
    }


    /**
     * @param bigScreenData 大屏对象
     * @param infoJson      infojson返回
     * @param modelDAOOne   数据模型
     * @return boolean
     * @Author zxx
     * @Description //TODO 校验数据模型
     * @Date 13:17 2020/7/16
     * @Param
     **/
    public static boolean buidCheckModel(BigScreenData bigScreenData, InfoJson infoJson, DataModel modelDAOOne) {
        if (bigScreenData.getDataModelId() == null || StringUtils.isEmpty(bigScreenData.getDataModelId())) {
            infoJson.setSuccess(false);
            infoJson.setDescription("数据模型ID不能为空！");
            return true;
        }

        if (modelDAOOne == null) {
            infoJson.setSuccess(false);
            infoJson.setDescription("数据模型ID不合法！");
            return true;
        }
        return false;
    }


    /**
     * @param value
     * @return java.util.List<com.sbr.visualization.datamodelattribute.model.DataModelAttribute>
     * @Author zxx
     * @Description //TODO 根据大屏属性数据集合，获取数据模型ID集合，查询数据模型
     * @Date 14:00 2020/7/21
     * @Param
     **/
    public static List<DataModelAttribute> findBigAttributeDataByListId(List<BigAttributeData> value) {
        List<DataModelAttribute> valueDataList = new ArrayList<>();
        for (BigAttributeData bigAttributeData : value) {
            DataModelAttribute attribute = dataModelAttributeDAO.findOne(bigAttributeData.getId());
            if (attribute != null) {
                //处理时间类型特殊条件
                if (bigAttributeData.getDateType() != null && StringUtils.isNotBlank(bigAttributeData.getDateType())) {
                    attribute.setDateType(bigAttributeData.getDateType());
                }
                valueDataList.add(attribute);
            }
        }
        return valueDataList;
    }


    /**
     * @param y
     * @param dataModelAttribute
     * @return void
     * @Author zxx
     * @Description //TODO 修改展示名称
     * @Date 11:13 2020/8/25
     * @Param
     **/
    public static String buildShowName(List<BigAttributeData> y, DataModelAttribute dataModelAttribute) {
        String name = "";
        if (y != null && y.size() > 0) {
            for (BigAttributeData bigAttributeData : y) {
                if (bigAttributeData.getAlias() != null && bigAttributeData.getId().equals(dataModelAttribute.getId())) {
                    name = bigAttributeData.getAlias();//传递过来的展示名称
                    break;
                }
            }
            //如果为空，取属性别名
            if (org.apache.commons.lang3.StringUtils.isBlank(name)) {
                name = dataModelAttribute.getFieldsAlias();
            }
        }
        return name;
    }

    /**
     * @return java.lang.StringBuffer
     * @Author 张鑫鑫
     * @Description //TODO 维度时间类型条件
     * @Date 16:27 2020/9/10
     * @Param [sqlBuffer, dataModelAttribute, dateType,flag]
     **/
    private static void buidDateTypeSQL(StringBuffer sqlBuffer, DataModelAttribute dataModelAttribute, String dateType, boolean flag) {
        //维度
        if (flag) {
            switch (dateType) {
                case "QUARTER"://季度
                    sqlBuffer.append("CONCAT('第',QUARTER(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'季度')AS '" + dataModelAttribute.getRandomAlias() + "',");
                    break;
                case "MONTH"://月
                    sqlBuffer.append("CONCAT(MONTH(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'月')AS '" + dataModelAttribute.getRandomAlias() + "',");
                    break;
                case "WEEK"://第几周
                    sqlBuffer.append("CONCAT('第',WEEK(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'周')AS '" + dataModelAttribute.getRandomAlias() + "',");
                    break;
                case "DAYOFWEEK"://星期几
                    sqlBuffer.append("CONCAT('星期',WEEKDAY(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + ")+1)AS '" + dataModelAttribute.getRandomAlias() + "',");
                    break;
                case "DAY"://查询日
                    sqlBuffer.append("CONCAT(DATE_FORMAT(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + ",'%d'),'日')AS '" + dataModelAttribute.getRandomAlias() + "',");
                    break;
                case "YEAR"://查询年
                    sqlBuffer.append("CONCAT(YEAR(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'年')AS '" + dataModelAttribute.getRandomAlias() + "',");
                    break;
                case "YEAR-QUARTER"://查询年-季度
                    sqlBuffer.append("CONCAT(YEAR(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'年',QUARTER(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'季度')AS '" + dataModelAttribute.getRandomAlias() + "',");
                    break;
                case "YEAR-MONTH"://查询年-月
                    sqlBuffer.append("CONCAT(YEAR(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'年',MONTH(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'月')AS '" + dataModelAttribute.getRandomAlias() + "',");
                    break;
                case "YEAR-WEEK"://查询年-第几周
                    sqlBuffer.append("CONCAT(YEAR(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'年','第',WEEK(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'周')AS '" + dataModelAttribute.getRandomAlias() + "',");
                    break;
                case "YEAR-MONTH-DAY"://查询年-月-日
                    sqlBuffer.append("CONCAT(YEAR(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'年',MONTH(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'月',DATE_FORMAT(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + ", '%d'),'日')AS '" + dataModelAttribute.getRandomAlias() + "',");
                    break;
            }
        } else {
            //分组
            switch (dateType) {
                case "QUARTER"://季度
                    sqlBuffer.append("CONCAT('第',QUARTER(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'季度'),");
                    break;
                case "MONTH"://月
                    sqlBuffer.append("CONCAT(MONTH(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'月'),");
                    break;
                case "WEEK"://第几周
                    sqlBuffer.append("CONCAT('第',WEEK(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'周'),");
                    break;
                case "DAYOFWEEK"://星期几
                    sqlBuffer.append("CONCAT('星期',WEEKDAY(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + ")+1),");
                    break;
                case "DAY"://查询日
                    sqlBuffer.append("CONCAT(DATE_FORMAT(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + ",'%d'),'日'),");
                    break;
                case "YEAR"://查询年
                    sqlBuffer.append("CONCAT(YEAR(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'年'),");
                    break;
                case "YEAR-QUARTER"://查询年-季度
                    sqlBuffer.append("CONCAT(YEAR(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'年',QUARTER(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'季度'),");
                    break;
                case "YEAR-MONTH"://查询年-月
                    sqlBuffer.append("CONCAT(YEAR(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'年',MONTH(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'月'),");
                    break;
                case "YEAR-WEEK"://查询年-第几周
                    sqlBuffer.append("CONCAT(YEAR(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'年','第',WEEK(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'周'),");
                    break;
                case "YEAR-MONTH-DAY"://查询年-月-日
                    sqlBuffer.append("CONCAT(YEAR(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'年',MONTH(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + "),'月',DATE_FORMAT(`" + dataModelAttribute.getTableName() + "`." + dataModelAttribute.getFieldsName() + ", '%d'),'日'),");
                    break;
            }
        }
    }


    /**
     * @param bigScreenData 当前大屏对象
     * @param modelDAOOne   当前数据模型对象
     * @param param         SQL条件参数
     * @return java.lang.String
     * @Author zxx
     * @Description //TODO 构建WHERE条件SQL语句 （大屏）给max使用
     * @Date 16:27 2020/7/14
     **/
    public static String buildWhereSQLAndValue(BigScreenData bigScreenData, DataModel modelDAOOne, List<String> param) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String whereSQL = "";
        if (bigScreenData.getValueFilterList() != null && bigScreenData.getValueFilterList().size() > 0) {
            //如果数据模型存在SQL条件，统计图需要拼接上此条件
            if (modelDAOOne.getSqlCondition() != null && StringUtils.isNotEmpty(modelDAOOne.getSqlCondition())) {
                List<String> strList = new ArrayList<>();
                //获取大屏过滤器条件，0条件SQL 1展示SQL
                List<StringBuffer> stringBufferList = DataBaseUtil.buidWhereSQL(bigScreenData.getValueFilterList(), strList);
                //拼接模型SQL，和条件SQL 截取AND
                whereSQL = modelDAOOne.getSqlCondition() + " AND " + stringBufferList.get(0).substring(0, stringBufferList.get(0).length() - 3);
                //获取SQL条件参数
                String sqlParam = modelDAOOne.getSqlParam();
                List<String> list = objectMapper.readValue(sqlParam, List.class);
                list.addAll(strList);
                param.addAll(list);
            } else {
                //0条件SQL 1展示SQL
                List<StringBuffer> stringBufferList = DataBaseUtil.buidWhereSQL(bigScreenData.getValueFilterList(), param);
                whereSQL = " WHERE" + stringBufferList.get(0).substring(0, stringBufferList.get(0).length() - 3);
            }
        } else {
            if (modelDAOOne.getSqlCondition() != null && StringUtils.isNotEmpty(modelDAOOne.getSqlCondition())) {
                String sqlParam = modelDAOOne.getSqlParam();
                List<String> list = objectMapper.readValue(sqlParam, List.class);
                param.addAll(list);
                //拼接模型SQL
                whereSQL = modelDAOOne.getSqlCondition();
            }
        }
        return whereSQL;
    }

}
