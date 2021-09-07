package com.richcoder.mybatis.generator.impl;

import com.google.common.collect.Lists;
import com.richcoder.mybatis.connect.Connector;
import com.richcoder.mybatis.generator.base.BaseGeneratorImpl;
import com.richcoder.mybatis.generator.context.GeneratorContext;
import com.richcoder.mybatis.utils.GeneratorStringUtils;
import com.richcoder.mybatis.utils.MysqlDatabaseMetaTypeUtil;
import com.richcoder.mybatis.utils.MysqlKeywordUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;

import java.util.List;
import java.util.Map;

/**
 * 功能描述：Mapper.xml代码生成
 */
public class MapperGeneratorImpl extends BaseGeneratorImpl {

    private static String LINE = System.getProperty("line.separator");

    @Override
    public void initVelocityContext(VelocityContext velocityContext,
                                    GeneratorContext generatorContext) {
        super.initVelocityContext(velocityContext, generatorContext);

        String tableName = generatorContext.getTableName();
        Connector connector = (Connector) generatorContext.getAttribute("connector");
        Map<String, String> columnNameTypeMap = connector.mapColumnNameType(tableName);
        /* List<String> allIndexes = connector.listAllIndex(tableName); */

        List<String> resultMapColumns = Lists.newArrayList();
        List<String> whereConditions = Lists.newArrayList();
        List<String> fields = Lists.newArrayList();
        List<String> insertValueConditions = Lists.newArrayList();
        List<String> batchInsertValueConditions = Lists.newArrayList();
        List<String> insertColsConditions = Lists.newArrayList();
        List<String> batchInsertColsConditions = Lists.newArrayList();
        List<String> updateConditions = Lists.newArrayList();

        String pk = (String) velocityContext.get("normalPrimaryKey");
        for (String col : columnNameTypeMap.keySet()) {
            String field = GeneratorStringUtils.format(col, generatorContext.getProperties());
            fields.add(MysqlKeywordUtil.getFieldName(col));

            String resultMapColumn = String.format("<result column=\"%s\" property=\"%s\"/>", col, field);
            resultMapColumns.add(resultMapColumn);

            if (columnNameTypeMap.get(col).equals("Date")) {
                StringBuilder conditionBfs = new StringBuilder();
                conditionBfs.append(String.format("<if test=\"%s != null and '' != %s\">", field, field))
                        .append(LINE);
                conditionBfs.append(String.format("\t\t\t\tAND <![CDATA[%s >= #{%s}]]>",
                        MysqlKeywordUtil.getFieldName(col),
                        "dynamicFields_startTime"))
                        .append(LINE);
                conditionBfs.append("\t\t\t</if>");
                whereConditions.add(conditionBfs.toString());

                StringBuilder conditionBfe = new StringBuilder();
                conditionBfe.append(String.format("<if test=\"%s != null and '' != %s\">", field, field))
                        .append(LINE);
                conditionBfe.append(String.format("\t\t\t\tAND <![CDATA[%s < #{%s}]]>",
                        MysqlKeywordUtil.getFieldName(col),
                        "dynamicFields_endTime"))
                        .append(LINE);
                conditionBfe.append("\t\t\t</if>");
                whereConditions.add(conditionBfe.toString());

            } else {
                StringBuilder conditionBf = new StringBuilder();
                if (MysqlDatabaseMetaTypeUtil.isNumberType(columnNameTypeMap.get(col))) { // 防止数字类型值为0判断失败

                    conditionBf.append(String.format("<if test=\"%s != null\">", field)).append(LINE);
                    conditionBf.append(String.format("\t\t\t\tAND %s = #{%s}",
                            MysqlKeywordUtil.getFieldName(col),
                            field))
                            .append(LINE);
                    conditionBf.append("\t\t\t</if>");

                } else {

                    conditionBf.append(String.format("<if test=\"%s != null and '' != %s\">", field, field))
                            .append(LINE);
                    conditionBf.append(String.format("\t\t\t\tAND %s = #{%s}",
                            MysqlKeywordUtil.getFieldName(col),
                            field))
                            .append(LINE);
                    conditionBf.append("\t\t\t</if>");
                }
                whereConditions.add(conditionBf.toString());
            }

            if (col.startsWith("gmt")) {
                insertValueConditions.add("now(),");
            } else {
                StringBuilder conditionValueBf = new StringBuilder();

                conditionValueBf.append(String.format("<if test=\"%s != null\">", field)).append(LINE);
                conditionValueBf.append(String.format("\t\t\t\t#{%s},", field)).append(LINE);
                conditionValueBf.append("\t\t\t</if>");

                insertValueConditions.add(conditionValueBf.toString());
            }

            if (col.startsWith("gmt")) {
                batchInsertValueConditions.add("now(),");
            } else {
                StringBuilder conditionValueBf = new StringBuilder();
                conditionValueBf.append(String.format("\t\t\t\t#{%s},", "item." + field)).append(LINE);
                batchInsertValueConditions.add(conditionValueBf.toString());
            }
            StringBuilder conditionColBf = new StringBuilder();
            StringBuilder conditionColBfBatch = new StringBuilder();

            conditionColBf.append(String.format("<if test=\"%s != null\">", field)).append(LINE);
            conditionColBf.append(String.format("\t\t\t\t%s,", MysqlKeywordUtil.getFieldName(col)))
                    .append(LINE);
            conditionColBfBatch.append(String.format("\t\t\t\t%s,", MysqlKeywordUtil.getFieldName(col)))
                    .append(LINE);
            conditionColBf.append("\t\t\t</if>");

            insertColsConditions.add(conditionColBf.toString());

            batchInsertColsConditions.add(conditionColBfBatch.toString());

            if (!StringUtils.equals(pk, field)) {
                StringBuilder upBf = new StringBuilder();

                upBf.append(String.format("<if test=\"%s != null\">", field)).append(LINE);
                upBf.append(String.format("\t\t\t\t%s = #{%s},", MysqlKeywordUtil.getFieldName(col), field))
                        .append(LINE);
                upBf.append("\t\t\t</if>");

                updateConditions.add(upBf.toString());
            }
        }

        velocityContext.put("resultMapColumns", resultMapColumns);
        velocityContext.put("whereConditions", whereConditions);
        velocityContext.put("insertValueConditions", insertValueConditions);
        velocityContext.put("batchInsertValueConditions", batchInsertValueConditions);
        velocityContext.put("insertColsConditions", insertColsConditions);
        velocityContext.put("batchInsertColsConditions", batchInsertColsConditions);
        velocityContext.put("updateConditions", updateConditions);
        velocityContext.put("fields", StringUtils.join(fields, ","));
        velocityContext.put("columnPrimaryKey", generatorContext.getAttribute("columnPrimaryKey"));
    }
}
