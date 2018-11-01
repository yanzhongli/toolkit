/*
 * This file is generated by jOOQ.
*/
package com.common.toolkit.database.jooq.db.information_schema.tables;


import com.common.toolkit.database.jooq.db.information_schema.InformationSchema;
import com.common.toolkit.database.jooq.db.information_schema.tables.records.QueryStatisticsRecord;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.8"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class QueryStatistics extends TableImpl<QueryStatisticsRecord> {

    private static final long serialVersionUID = -1107054744;

    /**
     * The reference instance of <code>INFORMATION_SCHEMA.QUERY_STATISTICS</code>
     */
    public static final QueryStatistics QUERY_STATISTICS = new QueryStatistics();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<QueryStatisticsRecord> getRecordType() {
        return QueryStatisticsRecord.class;
    }

    /**
     * The column <code>INFORMATION_SCHEMA.QUERY_STATISTICS.SQL_STATEMENT</code>.
     */
    public final TableField<QueryStatisticsRecord, String> SQL_STATEMENT = createField("SQL_STATEMENT", org.jooq.impl.SQLDataType.VARCHAR(2147483647), this, "");

    /**
     * The column <code>INFORMATION_SCHEMA.QUERY_STATISTICS.EXECUTION_COUNT</code>.
     */
    public final TableField<QueryStatisticsRecord, Integer> EXECUTION_COUNT = createField("EXECUTION_COUNT", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>INFORMATION_SCHEMA.QUERY_STATISTICS.MIN_EXECUTION_TIME</code>.
     */
    public final TableField<QueryStatisticsRecord, Double> MIN_EXECUTION_TIME = createField("MIN_EXECUTION_TIME", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>INFORMATION_SCHEMA.QUERY_STATISTICS.MAX_EXECUTION_TIME</code>.
     */
    public final TableField<QueryStatisticsRecord, Double> MAX_EXECUTION_TIME = createField("MAX_EXECUTION_TIME", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>INFORMATION_SCHEMA.QUERY_STATISTICS.CUMULATIVE_EXECUTION_TIME</code>.
     */
    public final TableField<QueryStatisticsRecord, Double> CUMULATIVE_EXECUTION_TIME = createField("CUMULATIVE_EXECUTION_TIME", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>INFORMATION_SCHEMA.QUERY_STATISTICS.AVERAGE_EXECUTION_TIME</code>.
     */
    public final TableField<QueryStatisticsRecord, Double> AVERAGE_EXECUTION_TIME = createField("AVERAGE_EXECUTION_TIME", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>INFORMATION_SCHEMA.QUERY_STATISTICS.STD_DEV_EXECUTION_TIME</code>.
     */
    public final TableField<QueryStatisticsRecord, Double> STD_DEV_EXECUTION_TIME = createField("STD_DEV_EXECUTION_TIME", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>INFORMATION_SCHEMA.QUERY_STATISTICS.MIN_ROW_COUNT</code>.
     */
    public final TableField<QueryStatisticsRecord, Integer> MIN_ROW_COUNT = createField("MIN_ROW_COUNT", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>INFORMATION_SCHEMA.QUERY_STATISTICS.MAX_ROW_COUNT</code>.
     */
    public final TableField<QueryStatisticsRecord, Integer> MAX_ROW_COUNT = createField("MAX_ROW_COUNT", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>INFORMATION_SCHEMA.QUERY_STATISTICS.CUMULATIVE_ROW_COUNT</code>.
     */
    public final TableField<QueryStatisticsRecord, Long> CUMULATIVE_ROW_COUNT = createField("CUMULATIVE_ROW_COUNT", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>INFORMATION_SCHEMA.QUERY_STATISTICS.AVERAGE_ROW_COUNT</code>.
     */
    public final TableField<QueryStatisticsRecord, Double> AVERAGE_ROW_COUNT = createField("AVERAGE_ROW_COUNT", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * The column <code>INFORMATION_SCHEMA.QUERY_STATISTICS.STD_DEV_ROW_COUNT</code>.
     */
    public final TableField<QueryStatisticsRecord, Double> STD_DEV_ROW_COUNT = createField("STD_DEV_ROW_COUNT", org.jooq.impl.SQLDataType.DOUBLE, this, "");

    /**
     * Create a <code>INFORMATION_SCHEMA.QUERY_STATISTICS</code> table reference
     */
    public QueryStatistics() {
        this(DSL.name("QUERY_STATISTICS"), null);
    }

    /**
     * Create an aliased <code>INFORMATION_SCHEMA.QUERY_STATISTICS</code> table reference
     */
    public QueryStatistics(String alias) {
        this(DSL.name(alias), QUERY_STATISTICS);
    }

    /**
     * Create an aliased <code>INFORMATION_SCHEMA.QUERY_STATISTICS</code> table reference
     */
    public QueryStatistics(Name alias) {
        this(alias, QUERY_STATISTICS);
    }

    private QueryStatistics(Name alias, Table<QueryStatisticsRecord> aliased) {
        this(alias, aliased, null);
    }

    private QueryStatistics(Name alias, Table<QueryStatisticsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return InformationSchema.INFORMATION_SCHEMA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryStatistics as(String alias) {
        return new QueryStatistics(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryStatistics as(Name alias) {
        return new QueryStatistics(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public QueryStatistics rename(String name) {
        return new QueryStatistics(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public QueryStatistics rename(Name name) {
        return new QueryStatistics(name, null);
    }
}
