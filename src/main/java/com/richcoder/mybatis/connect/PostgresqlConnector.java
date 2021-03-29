package com.richcoder.mybatis.connect;

import com.google.common.collect.Lists;
import com.mysql.cj.jdbc.JdbcConnection;
import com.richcoder.mybatis.utils.PropertiesUtils;

import java.sql.*;
import java.util.*;

/**
 * oracle connect
 */
public class PostgresqlConnector implements Connector {

    private Properties properties;

    public PostgresqlConnector(Properties properties) {
        this.properties = properties;
    }

    @Override
    public Map<String, String> mapColumnNameType(String tableName) {
        Map<String, String> colMap = new LinkedHashMap<>();
        Connection connection = null;
        try {
            connection = getConnection();
            DatabaseMetaData meta = getDatabaseMetaData(connection);
            ResultSet colRet = meta.getColumns(null, "%", tableName, "%");
            while (colRet.next()) {
                String columnName = colRet.getString("COLUMN_NAME");
                int digits = colRet.getInt("DECIMAL_DIGITS");
                int dataType = colRet.getInt("DATA_TYPE");
                String columnType = getDataType(dataType, digits);
                colMap.put(columnName, columnType);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        }
        return colMap;
    }

    @Override
    public Map<String, String> mapColumnRemark(String tableName) {
        Map<String, String> colMap = new LinkedHashMap<>();
        Connection connection = null;
        try {
            connection = getConnection();
            DatabaseMetaData meta = getDatabaseMetaData(connection);
            ResultSet colRet = meta.getColumns(null, "%", tableName, "%");
            while (colRet.next()) {
                String columnName = colRet.getString("COLUMN_NAME");
                String columnRemark = colRet.getString("REMARKS");
                colMap.put(columnName, columnRemark);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        }
        return colMap;
    }

    @Override
    public List<String> listAllIndex(String tableName) {
        List<String> indexes = Lists.newArrayList();
        Connection connection = null;
        try {
            connection = getConnection();
            ResultSet resultSet = getDatabaseMetaData(connection)
                    .getIndexInfo(null, null, tableName, false, true);
            while (resultSet.next()) {
                String indexName = resultSet.getString("INDEX_NAME");
                indexes.add(indexName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        }
        return indexes;
    }

    @Override
    public Map<String, String> getPrimaryKey(String tableName) {
        Map<String, String> map = new HashMap<>();
        Connection connection = null;
        try {
            connection = getConnection();
            ResultSet pkRSet = getDatabaseMetaData(connection).getPrimaryKeys(null, null, tableName);
            while (pkRSet.next()) {
                String primaryKey = pkRSet.getString("COLUMN_NAME");
                String primaryKeyType = mapColumnNameType(pkRSet.getString("TABLE_NAME")).get(primaryKey);
                map.put("primaryKey", primaryKey);
                map.put("primaryKeyType", primaryKeyType);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        }
        return map;
    }

    @Override
    public String getTableRemark(String tableName) {
        String remark = "";
        Connection connection = null;
        try {
            connection = getConnection();
            if (connection instanceof JdbcConnection) {
                JdbcConnection jdbcConnection = (JdbcConnection) connection;
                ResultSet resultSet = getDatabaseMetaData(connection)
                        .getTables(null, jdbcConnection.getCatalog(), tableName, null);
                while (resultSet.next()) {
                    remark = resultSet.getString("REMARKS");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        }
        return remark;
    }

    @Override
    public List<String> listTablesByTablePrefix(String tablePrefix) {
        List<String> tableList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = getConnection();
            DatabaseMetaData databaseMetaData = getDatabaseMetaData(connection);
            String[] tableType = {"TABLE"};
            ResultSet rs = databaseMetaData.getTables(null, null, "", tableType);
            while (rs.next()) {
                String tableName = rs.getString(3);
                if (tableName.toLowerCase().startsWith(tablePrefix.toLowerCase())) {
                    tableList.add(tableName);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                }
            }
        }
        return tableList;
    }

    /**
     * translate database type into java type
     *
     * @param type
     * @return
     */
    private String getDataType(int type, int digits) {
        String dataType;
        switch (type) {
            case Types.INTEGER:
            case Types.BIT:
            case Types.SMALLINT:
            case Types.TINYINT:
                dataType = "Integer";
                break;
            case Types.BIGINT:
                dataType = "Long";
                break;
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.REAL:
                dataType = getPrecisionType();
                break;
            case Types.DECIMAL:
                dataType = "BigDecimal";
                break;
            case Types.NUMERIC:
                switch (digits) {
                    case 0:
                        dataType = "Integer";
                        break;
                    default:
                        dataType = getPrecisionType();
                }
                break;
            case Types.TIME:
            case Types.TIMESTAMP:
            case Types.DATE:
                dataType = "Date";
                break;
            default:
                dataType = "String";
        }
        return dataType;
    }

    private String getPrecisionType() {
        String dataType;
        if ("high".equals(PropertiesUtils.getPrecision(properties))) {
            dataType = "BigDecimal";
        } else {
            dataType = "Double";
        }
        return dataType;
    }

    private Connection getConnection() {
        Connection connection;
        try {
            String driverClassName = properties.getProperty("jdbc.driverClassName");
            String url = properties.getProperty("jdbc.url");
            String userName = properties.getProperty("jdbc.username");
            String password = properties.getProperty("jdbc.password");
            Class.forName(driverClassName);
            connection = DriverManager.getConnection(url, userName, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return connection;
    }

    private DatabaseMetaData getDatabaseMetaData(Connection connection) {
        DatabaseMetaData databaseMetaData;
        try {
            databaseMetaData = connection.getMetaData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return databaseMetaData;
    }
}
