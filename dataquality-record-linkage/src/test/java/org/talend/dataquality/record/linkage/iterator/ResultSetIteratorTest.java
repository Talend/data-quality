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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.matchmerge.Record;

import junit.framework.TestCase;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class ResultSetIteratorTest extends TestCase {

    List<String> elementNames = new ArrayList<String>();

    @Override
    @Before
    public void setUp() throws Exception {
        elementNames.add("Id"); //$NON-NLS-1$
        elementNames.add("Name"); //$NON-NLS-1$
        elementNames.add("birthday"); //$NON-NLS-1$
    }

    @Test
    // Test zeroDate like as "0000-00-00 00:00:". it will get a SQLException and set current date to null and continue.
    public void testNext() throws SQLException {

        List<Object> dataObjects = new ArrayList<Object>();
        dataObjects.add(1);
        dataObjects.add("Lily"); //$NON-NLS-1$
        dataObjects.add("2015-6-25"); //$NON-NLS-1$

        Connection conn = new MyConnectionImpl(dataObjects);
        ResultSetIterator resIterator = new ResultSetIterator(conn, null, elementNames);
        Record next = resIterator.next();
        Assert.assertNotNull(next);
        List<Attribute> attributes = next.getAttributes();
        Assert.assertTrue(attributes.size() == 3);
        for (Attribute attribute : attributes) {
            Assert.assertNotNull(attribute);
        }
    }

    /**
     * Test method for {@link org.talend.dataquality.record.linkage.iterator.ResultSetIterator#next()}. when it get
     * SQLException like as the date is "0000-00-00 00:00:00", replace the attribute to null and continue to do next.
     * 
     * @throws SQLException
     */
    @Test
    // Test zeroDate like as "0000-00-00 00:00:". it will get a SQLException and set current date to null and continue.
    public void testNext_zerodDate() throws SQLException {
        List<Object> dataObjects = new ArrayList<Object>();
        dataObjects.add(2);
        dataObjects.add("Lily"); //$NON-NLS-1$
        dataObjects.add("0000-00-00 00:00:00"); //$NON-NLS-1$

        // ResultSet resultset = new ResultSetImpl(dataObjects);
        Connection conn = new MyConnectionImpl(dataObjects);
        ResultSetIterator resIterator = new ResultSetIterator(conn, null, elementNames);
        Record next = resIterator.next();
        Assert.assertNotNull(next);
        Assert.assertTrue(next.getAttributes().size() == 3);
        Assert.assertNull(next.getAttributes().get(2).getValue());
    }

    private class MyResultSetImpl implements ResultSet {

        List<Object> dataObjects = null;

        public MyResultSetImpl(List<Object> dataObjects) {
            this.dataObjects = dataObjects;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.mysql.jdbc.ResultSetImpl#getMetaData()
         */
        @Override
        public ResultSetMetaData getMetaData() throws SQLException {
            MyResultSetMetaData myResMetadata = new MyResultSetMetaData();
            return myResMetadata;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.mysql.jdbc.ResultSetImpl#getObject(int)
         */
        @Override
        public Object getObject(int columnIndex) throws SQLException {
            if (dataObjects != null && !dataObjects.isEmpty()) {
                Object object = dataObjects.get(columnIndex - 1);
                // Simulate the 3rd column data is a zero date like as "0000-00-00 00:00:00" and throw SQLException
                if (object != null && "0000-00-00 00:00:00".equals(object.toString())) { //$NON-NLS-1$
                    throw new SQLException();

                } else {
                    return object;
                }
            }
            return "Error!"; //$NON-NLS-1$

        }

        private class MyResultSetMetaData implements ResultSetMetaData {

            /*
             * (non-Javadoc)
             * 
             * @see java.sql.Wrapper#unwrap(java.lang.Class)
             */
            @Override
            public <T> T unwrap(Class<T> iface) throws SQLException {
                // TODO Auto-generated method stub
                return null;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
             */
            @Override
            public boolean isWrapperFor(Class<?> iface) throws SQLException {
                // TODO Auto-generated method stub
                return false;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.sql.ResultSetMetaData#getColumnCount()
             */
            @Override
            public int getColumnCount() throws SQLException {
                // TODO Auto-generated method stub
                return 3;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.sql.ResultSetMetaData#isAutoIncrement(int)
             */
            @Override
            public boolean isAutoIncrement(int column) throws SQLException {
                // TODO Auto-generated method stub
                return false;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.sql.ResultSetMetaData#isCaseSensitive(int)
             */
            @Override
            public boolean isCaseSensitive(int column) throws SQLException {
                // TODO Auto-generated method stub
                return false;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.sql.ResultSetMetaData#isSearchable(int)
             */
            @Override
            public boolean isSearchable(int column) throws SQLException {
                // TODO Auto-generated method stub
                return false;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.sql.ResultSetMetaData#isCurrency(int)
             */
            @Override
            public boolean isCurrency(int column) throws SQLException {
                // TODO Auto-generated method stub
                return false;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.sql.ResultSetMetaData#isNullable(int)
             */
            @Override
            public int isNullable(int column) throws SQLException {
                // TODO Auto-generated method stub
                return 0;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.sql.ResultSetMetaData#isSigned(int)
             */
            @Override
            public boolean isSigned(int column) throws SQLException {
                // TODO Auto-generated method stub
                return false;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.sql.ResultSetMetaData#getColumnDisplaySize(int)
             */
            @Override
            public int getColumnDisplaySize(int column) throws SQLException {
                // TODO Auto-generated method stub
                return 0;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.sql.ResultSetMetaData#getColumnLabel(int)
             */
            @Override
            public String getColumnLabel(int column) throws SQLException {
                // TODO Auto-generated method stub
                return null;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.sql.ResultSetMetaData#getColumnName(int)
             */
            @Override
            public String getColumnName(int column) throws SQLException {
                // TODO Auto-generated method stub
                return null;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.sql.ResultSetMetaData#getSchemaName(int)
             */
            @Override
            public String getSchemaName(int column) throws SQLException {
                // TODO Auto-generated method stub
                return null;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.sql.ResultSetMetaData#getPrecision(int)
             */
            @Override
            public int getPrecision(int column) throws SQLException {
                // TODO Auto-generated method stub
                return 0;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.sql.ResultSetMetaData#getScale(int)
             */
            @Override
            public int getScale(int column) throws SQLException {
                // TODO Auto-generated method stub
                return 0;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.sql.ResultSetMetaData#getTableName(int)
             */
            @Override
            public String getTableName(int column) throws SQLException {
                // TODO Auto-generated method stub
                return null;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.sql.ResultSetMetaData#getCatalogName(int)
             */
            @Override
            public String getCatalogName(int column) throws SQLException {
                // TODO Auto-generated method stub
                return null;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.sql.ResultSetMetaData#getColumnType(int)
             */
            @Override
            public int getColumnType(int column) throws SQLException {
                // TODO Auto-generated method stub
                return 0;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.sql.ResultSetMetaData#getColumnTypeName(int)
             */
            @Override
            public String getColumnTypeName(int column) throws SQLException {
                // TODO Auto-generated method stub
                return null;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.sql.ResultSetMetaData#isReadOnly(int)
             */
            @Override
            public boolean isReadOnly(int column) throws SQLException {
                // TODO Auto-generated method stub
                return false;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.sql.ResultSetMetaData#isWritable(int)
             */
            @Override
            public boolean isWritable(int column) throws SQLException {
                // TODO Auto-generated method stub
                return false;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.sql.ResultSetMetaData#isDefinitelyWritable(int)
             */
            @Override
            public boolean isDefinitelyWritable(int column) throws SQLException {
                // TODO Auto-generated method stub
                return false;
            }

            /*
             * (non-Javadoc)
             * 
             * @see java.sql.ResultSetMetaData#getColumnClassName(int)
             */
            @Override
            public String getColumnClassName(int column) throws SQLException {
                // TODO Auto-generated method stub
                return null;
            }

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Wrapper#unwrap(java.lang.Class)
         */
        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
         */
        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#next()
         */
        @Override
        public boolean next() throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#close()
         */
        @Override
        public void close() throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#wasNull()
         */
        @Override
        public boolean wasNull() throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getString(int)
         */
        @Override
        public String getString(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getBoolean(int)
         */
        @Override
        public boolean getBoolean(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getByte(int)
         */
        @Override
        public byte getByte(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getShort(int)
         */
        @Override
        public short getShort(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getInt(int)
         */
        @Override
        public int getInt(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getLong(int)
         */
        @Override
        public long getLong(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getFloat(int)
         */
        @Override
        public float getFloat(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getDouble(int)
         */
        @Override
        public double getDouble(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getBigDecimal(int, int)
         */
        @Override
        public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getBytes(int)
         */
        @Override
        public byte[] getBytes(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getDate(int)
         */
        @Override
        public Date getDate(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getTime(int)
         */
        @Override
        public Time getTime(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getTimestamp(int)
         */
        @Override
        public Timestamp getTimestamp(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getAsciiStream(int)
         */
        @Override
        public InputStream getAsciiStream(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getUnicodeStream(int)
         */
        @Override
        public InputStream getUnicodeStream(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getBinaryStream(int)
         */
        @Override
        public InputStream getBinaryStream(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getString(java.lang.String)
         */
        @Override
        public String getString(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getBoolean(java.lang.String)
         */
        @Override
        public boolean getBoolean(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getByte(java.lang.String)
         */
        @Override
        public byte getByte(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getShort(java.lang.String)
         */
        @Override
        public short getShort(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getInt(java.lang.String)
         */
        @Override
        public int getInt(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getLong(java.lang.String)
         */
        @Override
        public long getLong(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getFloat(java.lang.String)
         */
        @Override
        public float getFloat(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getDouble(java.lang.String)
         */
        @Override
        public double getDouble(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getBigDecimal(java.lang.String, int)
         */
        @Override
        public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getBytes(java.lang.String)
         */
        @Override
        public byte[] getBytes(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getDate(java.lang.String)
         */
        @Override
        public Date getDate(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getTime(java.lang.String)
         */
        @Override
        public Time getTime(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getTimestamp(java.lang.String)
         */
        @Override
        public Timestamp getTimestamp(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getAsciiStream(java.lang.String)
         */
        @Override
        public InputStream getAsciiStream(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getUnicodeStream(java.lang.String)
         */
        @Override
        public InputStream getUnicodeStream(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getBinaryStream(java.lang.String)
         */
        @Override
        public InputStream getBinaryStream(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getWarnings()
         */
        @Override
        public SQLWarning getWarnings() throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#clearWarnings()
         */
        @Override
        public void clearWarnings() throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getCursorName()
         */
        @Override
        public String getCursorName() throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getObject(java.lang.String)
         */
        @Override
        public Object getObject(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#findColumn(java.lang.String)
         */
        @Override
        public int findColumn(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getCharacterStream(int)
         */
        @Override
        public Reader getCharacterStream(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getCharacterStream(java.lang.String)
         */
        @Override
        public Reader getCharacterStream(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getBigDecimal(int)
         */
        @Override
        public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getBigDecimal(java.lang.String)
         */
        @Override
        public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#isBeforeFirst()
         */
        @Override
        public boolean isBeforeFirst() throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#isAfterLast()
         */
        @Override
        public boolean isAfterLast() throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#isFirst()
         */
        @Override
        public boolean isFirst() throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#isLast()
         */
        @Override
        public boolean isLast() throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#beforeFirst()
         */
        @Override
        public void beforeFirst() throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#afterLast()
         */
        @Override
        public void afterLast() throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#first()
         */
        @Override
        public boolean first() throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#last()
         */
        @Override
        public boolean last() throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getRow()
         */
        @Override
        public int getRow() throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#absolute(int)
         */
        @Override
        public boolean absolute(int row) throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#relative(int)
         */
        @Override
        public boolean relative(int rows) throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#previous()
         */
        @Override
        public boolean previous() throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#setFetchDirection(int)
         */
        @Override
        public void setFetchDirection(int direction) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getFetchDirection()
         */
        @Override
        public int getFetchDirection() throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#setFetchSize(int)
         */
        @Override
        public void setFetchSize(int rows) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getFetchSize()
         */
        @Override
        public int getFetchSize() throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getType()
         */
        @Override
        public int getType() throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getConcurrency()
         */
        @Override
        public int getConcurrency() throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#rowUpdated()
         */
        @Override
        public boolean rowUpdated() throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#rowInserted()
         */
        @Override
        public boolean rowInserted() throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#rowDeleted()
         */
        @Override
        public boolean rowDeleted() throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateNull(int)
         */
        @Override
        public void updateNull(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateBoolean(int, boolean)
         */
        @Override
        public void updateBoolean(int columnIndex, boolean x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateByte(int, byte)
         */
        @Override
        public void updateByte(int columnIndex, byte x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateShort(int, short)
         */
        @Override
        public void updateShort(int columnIndex, short x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateInt(int, int)
         */
        @Override
        public void updateInt(int columnIndex, int x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateLong(int, long)
         */
        @Override
        public void updateLong(int columnIndex, long x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateFloat(int, float)
         */
        @Override
        public void updateFloat(int columnIndex, float x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateDouble(int, double)
         */
        @Override
        public void updateDouble(int columnIndex, double x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateBigDecimal(int, java.math.BigDecimal)
         */
        @Override
        public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateString(int, java.lang.String)
         */
        @Override
        public void updateString(int columnIndex, String x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateBytes(int, byte[])
         */
        @Override
        public void updateBytes(int columnIndex, byte[] x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateDate(int, java.sql.Date)
         */
        @Override
        public void updateDate(int columnIndex, Date x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateTime(int, java.sql.Time)
         */
        @Override
        public void updateTime(int columnIndex, Time x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateTimestamp(int, java.sql.Timestamp)
         */
        @Override
        public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, int)
         */
        @Override
        public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, int)
         */
        @Override
        public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, int)
         */
        @Override
        public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateObject(int, java.lang.Object, int)
         */
        @Override
        public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateObject(int, java.lang.Object)
         */
        @Override
        public void updateObject(int columnIndex, Object x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateNull(java.lang.String)
         */
        @Override
        public void updateNull(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateBoolean(java.lang.String, boolean)
         */
        @Override
        public void updateBoolean(String columnLabel, boolean x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateByte(java.lang.String, byte)
         */
        @Override
        public void updateByte(String columnLabel, byte x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateShort(java.lang.String, short)
         */
        @Override
        public void updateShort(String columnLabel, short x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateInt(java.lang.String, int)
         */
        @Override
        public void updateInt(String columnLabel, int x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateLong(java.lang.String, long)
         */
        @Override
        public void updateLong(String columnLabel, long x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateFloat(java.lang.String, float)
         */
        @Override
        public void updateFloat(String columnLabel, float x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateDouble(java.lang.String, double)
         */
        @Override
        public void updateDouble(String columnLabel, double x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateBigDecimal(java.lang.String, java.math.BigDecimal)
         */
        @Override
        public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateString(java.lang.String, java.lang.String)
         */
        @Override
        public void updateString(String columnLabel, String x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateBytes(java.lang.String, byte[])
         */
        @Override
        public void updateBytes(String columnLabel, byte[] x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateDate(java.lang.String, java.sql.Date)
         */
        @Override
        public void updateDate(String columnLabel, Date x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateTime(java.lang.String, java.sql.Time)
         */
        @Override
        public void updateTime(String columnLabel, Time x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateTimestamp(java.lang.String, java.sql.Timestamp)
         */
        @Override
        public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream, int)
         */
        @Override
        public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream, int)
         */
        @Override
        public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader, int)
         */
        @Override
        public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object, int)
         */
        @Override
        public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object)
         */
        @Override
        public void updateObject(String columnLabel, Object x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#insertRow()
         */
        @Override
        public void insertRow() throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateRow()
         */
        @Override
        public void updateRow() throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#deleteRow()
         */
        @Override
        public void deleteRow() throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#refreshRow()
         */
        @Override
        public void refreshRow() throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#cancelRowUpdates()
         */
        @Override
        public void cancelRowUpdates() throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#moveToInsertRow()
         */
        @Override
        public void moveToInsertRow() throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#moveToCurrentRow()
         */
        @Override
        public void moveToCurrentRow() throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getStatement()
         */
        @Override
        public Statement getStatement() throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getObject(int, java.util.Map)
         */
        @Override
        public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getRef(int)
         */
        @Override
        public Ref getRef(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getBlob(int)
         */
        @Override
        public Blob getBlob(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getClob(int)
         */
        @Override
        public Clob getClob(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getArray(int)
         */
        @Override
        public Array getArray(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getObject(java.lang.String, java.util.Map)
         */
        @Override
        public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getRef(java.lang.String)
         */
        @Override
        public Ref getRef(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getBlob(java.lang.String)
         */
        @Override
        public Blob getBlob(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getClob(java.lang.String)
         */
        @Override
        public Clob getClob(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getArray(java.lang.String)
         */
        @Override
        public Array getArray(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getDate(int, java.util.Calendar)
         */
        @Override
        public Date getDate(int columnIndex, Calendar cal) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getDate(java.lang.String, java.util.Calendar)
         */
        @Override
        public Date getDate(String columnLabel, Calendar cal) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getTime(int, java.util.Calendar)
         */
        @Override
        public Time getTime(int columnIndex, Calendar cal) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getTime(java.lang.String, java.util.Calendar)
         */
        @Override
        public Time getTime(String columnLabel, Calendar cal) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getTimestamp(int, java.util.Calendar)
         */
        @Override
        public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getTimestamp(java.lang.String, java.util.Calendar)
         */
        @Override
        public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getURL(int)
         */
        @Override
        public URL getURL(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getURL(java.lang.String)
         */
        @Override
        public URL getURL(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateRef(int, java.sql.Ref)
         */
        @Override
        public void updateRef(int columnIndex, Ref x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateRef(java.lang.String, java.sql.Ref)
         */
        @Override
        public void updateRef(String columnLabel, Ref x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateBlob(int, java.sql.Blob)
         */
        @Override
        public void updateBlob(int columnIndex, Blob x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateBlob(java.lang.String, java.sql.Blob)
         */
        @Override
        public void updateBlob(String columnLabel, Blob x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateClob(int, java.sql.Clob)
         */
        @Override
        public void updateClob(int columnIndex, Clob x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateClob(java.lang.String, java.sql.Clob)
         */
        @Override
        public void updateClob(String columnLabel, Clob x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateArray(int, java.sql.Array)
         */
        @Override
        public void updateArray(int columnIndex, Array x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateArray(java.lang.String, java.sql.Array)
         */
        @Override
        public void updateArray(String columnLabel, Array x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getRowId(int)
         */
        @Override
        public RowId getRowId(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getRowId(java.lang.String)
         */
        @Override
        public RowId getRowId(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateRowId(int, java.sql.RowId)
         */
        @Override
        public void updateRowId(int columnIndex, RowId x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateRowId(java.lang.String, java.sql.RowId)
         */
        @Override
        public void updateRowId(String columnLabel, RowId x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getHoldability()
         */
        @Override
        public int getHoldability() throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#isClosed()
         */
        @Override
        public boolean isClosed() throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateNString(int, java.lang.String)
         */
        @Override
        public void updateNString(int columnIndex, String nString) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateNString(java.lang.String, java.lang.String)
         */
        @Override
        public void updateNString(String columnLabel, String nString) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateNClob(int, java.sql.NClob)
         */
        @Override
        public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateNClob(java.lang.String, java.sql.NClob)
         */
        @Override
        public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getNClob(int)
         */
        @Override
        public NClob getNClob(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getNClob(java.lang.String)
         */
        @Override
        public NClob getNClob(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getSQLXML(int)
         */
        @Override
        public SQLXML getSQLXML(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getSQLXML(java.lang.String)
         */
        @Override
        public SQLXML getSQLXML(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateSQLXML(int, java.sql.SQLXML)
         */
        @Override
        public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateSQLXML(java.lang.String, java.sql.SQLXML)
         */
        @Override
        public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getNString(int)
         */
        @Override
        public String getNString(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getNString(java.lang.String)
         */
        @Override
        public String getNString(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getNCharacterStream(int)
         */
        @Override
        public Reader getNCharacterStream(int columnIndex) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getNCharacterStream(java.lang.String)
         */
        @Override
        public Reader getNCharacterStream(String columnLabel) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateNCharacterStream(int, java.io.Reader, long)
         */
        @Override
        public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateNCharacterStream(java.lang.String, java.io.Reader, long)
         */
        @Override
        public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, long)
         */
        @Override
        public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, long)
         */
        @Override
        public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, long)
         */
        @Override
        public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream, long)
         */
        @Override
        public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream, long)
         */
        @Override
        public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader, long)
         */
        @Override
        public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateBlob(int, java.io.InputStream, long)
         */
        @Override
        public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateBlob(java.lang.String, java.io.InputStream, long)
         */
        @Override
        public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateClob(int, java.io.Reader, long)
         */
        @Override
        public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateClob(java.lang.String, java.io.Reader, long)
         */
        @Override
        public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateNClob(int, java.io.Reader, long)
         */
        @Override
        public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateNClob(java.lang.String, java.io.Reader, long)
         */
        @Override
        public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateNCharacterStream(int, java.io.Reader)
         */
        @Override
        public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateNCharacterStream(java.lang.String, java.io.Reader)
         */
        @Override
        public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream)
         */
        @Override
        public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream)
         */
        @Override
        public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader)
         */
        @Override
        public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream)
         */
        @Override
        public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream)
         */
        @Override
        public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader)
         */
        @Override
        public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateBlob(int, java.io.InputStream)
         */
        @Override
        public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateBlob(java.lang.String, java.io.InputStream)
         */
        @Override
        public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateClob(int, java.io.Reader)
         */
        @Override
        public void updateClob(int columnIndex, Reader reader) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateClob(java.lang.String, java.io.Reader)
         */
        @Override
        public void updateClob(String columnLabel, Reader reader) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateNClob(int, java.io.Reader)
         */
        @Override
        public void updateNClob(int columnIndex, Reader reader) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#updateNClob(java.lang.String, java.io.Reader)
         */
        @Override
        public void updateNClob(String columnLabel, Reader reader) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getObject(int, java.lang.Class)
         */
        @Override
        public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.ResultSet#getObject(java.lang.String, java.lang.Class)
         */
        @Override
        public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

    }

    private class MyConnectionImpl implements Connection {

        List<Object> dataObjects = null;

        public MyConnectionImpl(List<Object> dataObjects) {
            this.dataObjects = dataObjects;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Wrapper#unwrap(java.lang.Class)
         */
        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
         */
        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#createStatement()
         */
        @Override
        public Statement createStatement() throws SQLException {
            MyStatementImpl stm = new MyStatementImpl(dataObjects);
            return stm;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#prepareStatement(java.lang.String)
         */
        @Override
        public PreparedStatement prepareStatement(String sql) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#prepareCall(java.lang.String)
         */
        @Override
        public CallableStatement prepareCall(String sql) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#nativeSQL(java.lang.String)
         */
        @Override
        public String nativeSQL(String sql) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#setAutoCommit(boolean)
         */
        @Override
        public void setAutoCommit(boolean autoCommit) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#getAutoCommit()
         */
        @Override
        public boolean getAutoCommit() throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#commit()
         */
        @Override
        public void commit() throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#rollback()
         */
        @Override
        public void rollback() throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#close()
         */
        @Override
        public void close() throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#isClosed()
         */
        @Override
        public boolean isClosed() throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#getMetaData()
         */
        @Override
        public DatabaseMetaData getMetaData() throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#setReadOnly(boolean)
         */
        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#isReadOnly()
         */
        @Override
        public boolean isReadOnly() throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#setCatalog(java.lang.String)
         */
        @Override
        public void setCatalog(String catalog) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#getCatalog()
         */
        @Override
        public String getCatalog() throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#setTransactionIsolation(int)
         */
        @Override
        public void setTransactionIsolation(int level) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#getTransactionIsolation()
         */
        @Override
        public int getTransactionIsolation() throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#getWarnings()
         */
        @Override
        public SQLWarning getWarnings() throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#clearWarnings()
         */
        @Override
        public void clearWarnings() throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#createStatement(int, int)
         */
        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
         */
        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
         */
        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#getTypeMap()
         */
        @Override
        public Map<String, Class<?>> getTypeMap() throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#setTypeMap(java.util.Map)
         */
        @Override
        public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#setHoldability(int)
         */
        @Override
        public void setHoldability(int holdability) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#getHoldability()
         */
        @Override
        public int getHoldability() throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#setSavepoint()
         */
        @Override
        public Savepoint setSavepoint() throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#setSavepoint(java.lang.String)
         */
        @Override
        public Savepoint setSavepoint(String name) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#rollback(java.sql.Savepoint)
         */
        @Override
        public void rollback(Savepoint savepoint) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
         */
        @Override
        public void releaseSavepoint(Savepoint savepoint) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#createStatement(int, int, int)
         */
        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
                throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#prepareStatement(java.lang.String, int, int, int)
         */
        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                int resultSetHoldability) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)
         */
        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
                throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#prepareStatement(java.lang.String, int)
         */
        @Override
        public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#prepareStatement(java.lang.String, int[])
         */
        @Override
        public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#prepareStatement(java.lang.String, java.lang.String[])
         */
        @Override
        public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#createClob()
         */
        @Override
        public Clob createClob() throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#createBlob()
         */
        @Override
        public Blob createBlob() throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#createNClob()
         */
        @Override
        public NClob createNClob() throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#createSQLXML()
         */
        @Override
        public SQLXML createSQLXML() throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#isValid(int)
         */
        @Override
        public boolean isValid(int timeout) throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#setClientInfo(java.lang.String, java.lang.String)
         */
        @Override
        public void setClientInfo(String name, String value) throws SQLClientInfoException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#setClientInfo(java.util.Properties)
         */
        @Override
        public void setClientInfo(Properties properties) throws SQLClientInfoException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#getClientInfo(java.lang.String)
         */
        @Override
        public String getClientInfo(String name) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#getClientInfo()
         */
        @Override
        public Properties getClientInfo() throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#createArrayOf(java.lang.String, java.lang.Object[])
         */
        @Override
        public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#createStruct(java.lang.String, java.lang.Object[])
         */
        @Override
        public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#setSchema(java.lang.String)
         */
        @Override
        public void setSchema(String schema) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#getSchema()
         */
        @Override
        public String getSchema() throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#abort(java.util.concurrent.Executor)
         */
        @Override
        public void abort(Executor executor) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#setNetworkTimeout(java.util.concurrent.Executor, int)
         */
        @Override
        public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Connection#getNetworkTimeout()
         */
        @Override
        public int getNetworkTimeout() throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

    }

    private class MyStatementImpl implements Statement {

        List<Object> dataObjects = null;

        public MyStatementImpl(List<Object> dataObjects) {
            this.dataObjects = dataObjects;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Wrapper#unwrap(java.lang.Class)
         */
        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
         */
        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#executeQuery(java.lang.String)
         */
        @Override
        public ResultSet executeQuery(String sql) throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#executeUpdate(java.lang.String)
         */
        @Override
        public int executeUpdate(String sql) throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#close()
         */
        @Override
        public void close() throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#getMaxFieldSize()
         */
        @Override
        public int getMaxFieldSize() throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#setMaxFieldSize(int)
         */
        @Override
        public void setMaxFieldSize(int max) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#getMaxRows()
         */
        @Override
        public int getMaxRows() throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#setMaxRows(int)
         */
        @Override
        public void setMaxRows(int max) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#setEscapeProcessing(boolean)
         */
        @Override
        public void setEscapeProcessing(boolean enable) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#getQueryTimeout()
         */
        @Override
        public int getQueryTimeout() throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#setQueryTimeout(int)
         */
        @Override
        public void setQueryTimeout(int seconds) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#cancel()
         */
        @Override
        public void cancel() throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#getWarnings()
         */
        @Override
        public SQLWarning getWarnings() throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#clearWarnings()
         */
        @Override
        public void clearWarnings() throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#setCursorName(java.lang.String)
         */
        @Override
        public void setCursorName(String name) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#execute(java.lang.String)
         */
        @Override
        public boolean execute(String sql) throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#getResultSet()
         */
        @Override
        public ResultSet getResultSet() throws SQLException {

            return new MyResultSetImpl(dataObjects);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#getUpdateCount()
         */
        @Override
        public int getUpdateCount() throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#getMoreResults()
         */
        @Override
        public boolean getMoreResults() throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#setFetchDirection(int)
         */
        @Override
        public void setFetchDirection(int direction) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#getFetchDirection()
         */
        @Override
        public int getFetchDirection() throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#setFetchSize(int)
         */
        @Override
        public void setFetchSize(int rows) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#getFetchSize()
         */
        @Override
        public int getFetchSize() throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#getResultSetConcurrency()
         */
        @Override
        public int getResultSetConcurrency() throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#getResultSetType()
         */
        @Override
        public int getResultSetType() throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#addBatch(java.lang.String)
         */
        @Override
        public void addBatch(String sql) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#clearBatch()
         */
        @Override
        public void clearBatch() throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#executeBatch()
         */
        @Override
        public int[] executeBatch() throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#getConnection()
         */
        @Override
        public Connection getConnection() throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#getMoreResults(int)
         */
        @Override
        public boolean getMoreResults(int current) throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#getGeneratedKeys()
         */
        @Override
        public ResultSet getGeneratedKeys() throws SQLException {
            // TODO Auto-generated method stub
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#executeUpdate(java.lang.String, int)
         */
        @Override
        public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#executeUpdate(java.lang.String, int[])
         */
        @Override
        public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#executeUpdate(java.lang.String, java.lang.String[])
         */
        @Override
        public int executeUpdate(String sql, String[] columnNames) throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#execute(java.lang.String, int)
         */
        @Override
        public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#execute(java.lang.String, int[])
         */
        @Override
        public boolean execute(String sql, int[] columnIndexes) throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#execute(java.lang.String, java.lang.String[])
         */
        @Override
        public boolean execute(String sql, String[] columnNames) throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#getResultSetHoldability()
         */
        @Override
        public int getResultSetHoldability() throws SQLException {
            // TODO Auto-generated method stub
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#isClosed()
         */
        @Override
        public boolean isClosed() throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#setPoolable(boolean)
         */
        @Override
        public void setPoolable(boolean poolable) throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#isPoolable()
         */
        @Override
        public boolean isPoolable() throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#closeOnCompletion()
         */
        @Override
        public void closeOnCompletion() throws SQLException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.sql.Statement#isCloseOnCompletion()
         */
        @Override
        public boolean isCloseOnCompletion() throws SQLException {
            // TODO Auto-generated method stub
            return false;
        }

    }

}
