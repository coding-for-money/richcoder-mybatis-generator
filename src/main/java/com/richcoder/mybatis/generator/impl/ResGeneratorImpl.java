package com.richcoder.mybatis.generator.impl;

import com.richcoder.mybatis.connect.Connector;
import com.richcoder.mybatis.generator.base.BaseGeneratorImpl;
import com.richcoder.mybatis.generator.context.GeneratorContext;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.velocity.VelocityContext;

/**
 * 功能描述：Model代码生成
 */
public class ResGeneratorImpl extends BaseGeneratorImpl {

    @Override
    public void initVelocityContext(VelocityContext velocityContext,
                                    GeneratorContext generatorContext) {
        super.initVelocityContext(velocityContext, generatorContext);
        velocityContext
                .put("SerialVersionUID", String.valueOf(UUID.randomUUID().getLeastSignificantBits()));

        String tableName = generatorContext.getTableName();
        Connector connector = (Connector) generatorContext.getAttribute("connector");

        Map<String, String> colMap = connector.mapColumnNameType(tableName);
        Map<String, String> columnRemarkMap = connector.mapColumnRemark(tableName);
        Set<String> keySet = colMap.keySet();
        Set<String> importSets = new HashSet<>();
        for (String key : keySet) {
            String value = colMap.get(key);
            if ("BigDecimal".equals(value) && !importSets.contains("BigDecimal")) {
                importSets.add("import java.math.BigDecimal;\n");
            } else if ("Date".equals(value) && !importSets.contains("Date")) {
                importSets.add("import java.util.Date;\n");
            } else if ("Timestamp".equals(value) && !importSets.contains("Timestamp")) {
                importSets.add("import java.sql.Timestamp;\n");
            }
        }
        velocityContext
                .put("methods", generateGetAndSetMethods(colMap, generatorContext.getProperties()));
        velocityContext
                .put("fields", generateFields(colMap, columnRemarkMap, generatorContext.getProperties(), "res"));
        velocityContext.put("importSets", importSets);
    }
}
