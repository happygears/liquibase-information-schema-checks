package liquibase.precondition.ext;

import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.database.core.HsqlDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.*;
import liquibase.logging.LogFactory;
import liquibase.precondition.AbstractPrecondition;
import liquibase.util.JdbcUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import static java.lang.String.format;

/**
 * Copyright (c) 2017 Happy Gears
 * author: vadim2
 * Date: 7/10/17
 */
public class IsNullable extends AbstractPrecondition {

    private liquibase.logging.Logger log;
    private String catalogName;
    private String schemaName;
    private String tableName;
    private String columnName;

    public IsNullable() {
        log = LogFactory.getLogger();
    }

    @Override
    public String getName() {
        return "isNullable";
    }

    @Override
    public Warnings warn(Database database) {
        return new Warnings();
    }

    @Override
    public ValidationErrors validate(Database database) {
        return new ValidationErrors();
    }

    @Override
    public void check(Database database, DatabaseChangeLog databaseChangeLog, ChangeSet changeSet) throws PreconditionFailedException, PreconditionErrorException {

        Statement statement = null;
        try {
            statement = ((JdbcConnection) database.getConnection())
                    .createStatement();

            String schemaName = getSchemaName();
            if (schemaName == null) {
                schemaName = database.getDefaultSchemaName();
            }
            String tableName = getTableName();
            String columnName = getColumnName();

            // hsql requires table and column names to be uppercase, while MySQL is case-sensitive
            if (database instanceof HsqlDatabase) {
                tableName = tableName.toUpperCase();
                columnName = columnName.toUpperCase();
            }

            log.debug("schemaName=" + schemaName + "; tableName=" + tableName + " columnName=" + columnName);

            String sql = "SELECT LOWER(IS_NULLABLE) FROM INFORMATION_SCHEMA.COLUMNS WHERE " +
                    "TABLE_SCHEMA='" + schemaName + "' AND " +
                    "TABLE_NAME = '" + tableName + "' AND COLUMN_NAME='" + columnName + "';";
            try {
                ResultSet rs = statement.executeQuery(sql);
                try {
                    if (rs.next()) {
                        log.debug("result=" + rs.getString(1));
                        if (rs.getBoolean(1)) return;
                        // not nullable
                        throw new PreconditionFailedException(format("Column %s.%s.%s is not nullable",
                                schemaName, tableName, columnName), databaseChangeLog, this);
                    } else {
                        // column does not exist
                        throw new PreconditionFailedException(format("Column %s.%s.%s does not exist",
                                        schemaName, tableName, columnName), databaseChangeLog, this);
                    }
                } finally {
                    rs.close();
                }
            } catch (SQLException e) {
                throw new PreconditionErrorException(e, databaseChangeLog, this);
            }

        } catch (DatabaseException e) {
            throw new PreconditionErrorException(e, databaseChangeLog, this);

        } finally {
            JdbcUtils.closeStatement(statement);
        }

    }

    @Override
    public String getSerializedObjectNamespace() {
        return null;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
