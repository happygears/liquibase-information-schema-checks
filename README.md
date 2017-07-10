# liquibase-information-schema-checks
liquibase extension that adds isNullable and checkColumnType  &lt;preConditions> checks

# How to use:

Do not forget to add namespace `xmlns:ext="http://www.liquibase.org/xml/ns/dbchangelog-ext"` to the top
&lt;databaseChangeLog> tag.

    <?xml version="1.1" encoding="UTF-8" standalone="no"?>
    <databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
                       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                       xmlns:ext="http://www.liquibase.org/xml/ns/dbchangelog-ext"
                       xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.6.xsd">

    
        <changeSet author="vadim" id="1498949276056-53">
            <preConditions onFail="MARK_RAN">
                <ext:checkColumnType tableName="clusters" columnName="updatedAt" expectedColumnType="timestamp"/>
            </preConditions>
            <modifyDataType columnName="updatedAt" newDataType="timestamp" tableName="clusters"/>
        </changeSet>
    
        <changeSet author="vadim" id="1499559974251-2">
            <preConditions onFail="MARK_RAN">
                <ext:isNullable columnName="enabled" tableName="areas"/>
            </preConditions>
            <addNotNullConstraint columnDataType="bit(1)" columnName="enabled" tableName="areas"/>
            <addDefaultValue columnDataType="bit(1)" columnName="enabled" defaultValueBoolean="false" tableName="areas"/>
        </changeSet>

# Compatibility

this extension has been developed and tested with MySQL and HSQLDB

# Implementation

this extension works by reading column properties from the table INFORMATION_SCHEMA.COLUMNS

# Caveats

it looks like liquibase silently fails if you misspell precondition check tag name and works as if
the condition is always true, so that the changeset always runs. This needs to be investigated further.

# License

Apache License, Version 2.0


