// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.iterator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.record.linkage.exception.DQRecordLinkageRuntimeException;
import org.talend.dataquality.record.linkage.grouping.swoosh.RichRecord;

/**
 * created by yyin on 2014-9-4 Detailled comment
 * 
 */
public class ResultSetIterator implements Iterator<Record> {

    private static Logger LOG = Logger.getLogger(ResultSetIterator.class);

    private final java.sql.Connection connection;

    private final Statement statement;

    private final ResultSet resultSet;

    private List<String> columnNames;

    private long index = 0;

    public ResultSetIterator(Connection sqlConnection, String sqlQuery, List<String> elementNames) throws SQLException {
        this.connection = sqlConnection;
        this.statement = sqlConnection.createStatement();
        statement.execute(sqlQuery);
        this.resultSet = statement.getResultSet();

        this.columnNames = elementNames;
    }

    /*
     * check if the resultset has the next record, if not, close the connection
     * 
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        try {
            if (resultSet.next()) {
                return true;
            } else {
                close();
                return false;
            }
        } catch (SQLException e) {
            try {
                close();
            } catch (SQLException e1) {
                LOG.debug(e1);
                throw new DQRecordLinkageRuntimeException("Could not close the connection", e); //$NON-NLS-1$
            }
            throw new DQRecordLinkageRuntimeException("Could not move to next result", e); //$NON-NLS-1$
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#next()
     */
    @Override
    public Record next() {
        List<Attribute> attributes = new ArrayList<Attribute>();
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            if (metaData.getColumnCount() == 0) {
                return null;
            }
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                Attribute attribute = new Attribute(columnNames.get(i), i);
                String value = null;
                try {
                    Object object = resultSet.getObject(i + 1);
                    // when the value is null, do not turn to "null"
                    value = object == null ? null : String.valueOf(object);
                } catch (SQLException exp) {
                    LOG.debug(exp);
                    // TDQ-11425 if SQLException, keep the current value is null and continue.
                }
                attribute.setValue(value);
                attributes.add(attribute);
            }
            return new RichRecord(attributes, String.valueOf(index++), 0, StringUtils.EMPTY);
        } catch (Exception e) {
            LOG.error(e);
            throw new DQRecordLinkageRuntimeException("Could not build next result", e); //$NON-NLS-1$
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Read only iterator"); //$NON-NLS-1$
    }

    private void close() throws SQLException {
        if (resultSet != null) {
            resultSet.close();
        }
        if (statement != null) {
            statement.close();
        }
        if (connection != null) {
            connection.close();
        }
    }
}
