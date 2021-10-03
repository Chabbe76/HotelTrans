package com.company;

import java.sql.SQLException;

/**
 * Represents a constructor using SQL-queries
 * to create an object from a row in a table.
 *
 * @return the type of the object that calls it.
 *
 */
@FunctionalInterface
public interface QueryObject<T> {

    public T construct(int id) throws SQLException;

}
