/*
 * Copyright (C) 2011-2013 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.greenrobot.dao.query;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.AbstractDao;

/**
 * A repeatable query for deleting entities.<br/>
 * New API note: this is more likely to change.
 *
 * @param <T> The enitity class the query will delete from.
 * @author Markus
 */
public class ProjectionQuery<T, J> extends AbstractQuery<T> {
    private final static class QueryData<T2, J> extends AbstractQueryData<T2, ProjectionQuery<T2, J>> {

        private final Class<J> valueType;

        private final int firstResult;

        private final int maxResults;

        private QueryData(AbstractDao<T2, ?> dao, String sql, String[] initialValues, Class<J> valueType, int firstResult, int maxResults) {
            super(dao, sql, initialValues);
            this.valueType = valueType;
            this.firstResult = firstResult;
            this.maxResults = maxResults;
        }

        @Override
        protected ProjectionQuery<T2, J> createQuery() {
            return new ProjectionQuery<T2, J>(this, dao, sql, initialValues.clone(), valueType, firstResult, maxResults);
        }
    }

    static <T2, J> ProjectionQuery<T2, J> create(AbstractDao<T2, ?> dao, String sql, Object[] initialValues, Class<J> valueType) {
        return create(dao, sql, initialValues, valueType, 0, Integer.MAX_VALUE);
    }

    static <T2, J> ProjectionQuery<T2, J> create(AbstractDao<T2, ?> dao, String sql, Object[] initialValues, Class<J> valueType, int firstResult, int maxResults) {
        QueryData<T2, J> queryData = new QueryData<T2, J>(dao, sql, toStringArray(initialValues), valueType, firstResult, maxResults);
        return queryData.forCurrentThread();
    }

    private final QueryData<T, J> queryData;

    private final Class<J> valueType;

    private final int firstResult;

    private final int maxResults;

    private ProjectionQuery(QueryData<T, J> queryData, AbstractDao<T, ?> dao, String sql, String[] initialValues, Class<J> valueType, int firstResult, int maxResults) {
        super(dao, sql, initialValues);
        this.queryData = queryData;
        this.valueType = valueType;
        this.firstResult = firstResult;
        this.maxResults = maxResults;
    }

    public ProjectionQuery<T, J> forCurrentThread() {
        return queryData.forCurrentThread(this);
    }

    /**
     * Deletes all matching entities without detaching them from the identity scope (aka session/cache). Note that this
     * method may lead to stale entity objects in the session cache. Stale entities may be returned when loaded by their
     * primary key, but not using queries.
     */
    public List<J> list() {
        checkThread();
        SQLiteDatabase db = dao.getDatabase();
        Cursor cursor;
        List<J> lst;
        if (db.isDbLockedByCurrentThread()) {
            cursor = dao.getDatabase().rawQuery(sql, parameters);
            lst = toList(cursor);
        } else {
            // Do TX to acquire a connection before locking this to avoid deadlocks
            // Locking order as described in AbstractDao
            db.beginTransaction();
            try {
                cursor = dao.getDatabase().rawQuery(sql, parameters);
                lst = toList(cursor);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
        return lst;
    }

    public Cursor rawQuery() {
        SQLiteDatabase db = dao.getDatabase();
        return db.rawQuery(sql, parameters);
    }

    @SuppressWarnings("unchecked")
    protected List<J> toList(final Cursor cursor) {
        final List<Object> vals = new ArrayList<Object>(cursor.getCount());
        if (cursor.moveToPosition(firstResult)) {
            int i = 0;
            do {
                if (Long.TYPE.equals(valueType) || Long.class.equals(valueType)) {
                    vals.add(cursor.getLong(0));
                } else if (Integer.TYPE.equals(valueType) || Integer.class.equals(valueType)) {
                    vals.add(cursor.getInt(0));
                } else if (String.class.equals(valueType)) {
                    vals.add(cursor.getString(0));
                } else if (Double.TYPE.equals(valueType) || Double.class.equals(valueType)) {
                    vals.add(cursor.getDouble(0));
                } else if (Object[].class.equals(valueType)) {
                    vals.add(readObjectArr(cursor));
                } else if (Float.TYPE.equals(valueType) || Float.class.equals(valueType)) {
                    vals.add(cursor.getFloat(0));
                } else if (Short.TYPE.equals(valueType) || Short.class.equals(valueType)) {
                    vals.add(cursor.getShort(0));
                } else if (byte[].class.equals(valueType)) {
                    vals.add(cursor.getBlob(0));
                }
            } while (cursor.moveToNext() && ++i < maxResults);
        }
        return (List<J>)vals;
    }

    private Object[] readObjectArr(Cursor cursor) {
        final Object[] val = new Object[cursor.getColumnCount()];
        for (int i = 0; i < cursor.getColumnCount();i++) {
            switch (cursor.getType(i)) {
                case Cursor.FIELD_TYPE_BLOB:
                    val[i] = cursor.getBlob(i);
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    val[i] = cursor.getDouble(i);
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    val[i] = cursor.getLong(i);
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    val[i] = cursor.getString(i);
                    break;
            }
        }
        return val;
    }
}
