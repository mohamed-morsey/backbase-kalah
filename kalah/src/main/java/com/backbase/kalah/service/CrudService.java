package com.backbase.kalah.service;

import java.util.List;
import java.util.Optional;

/**
 * Basic create, read, update, and delete (CRUD) operations a service should support
 *
 * @author Mohamed Morsey
 * Date: 2018-11-25
 **/
public interface CrudService<T> {
    /**
     * Get a specific item with its ID
     *
     * @param id The ID of the item
     * @return The item if exists, {@link Optional#empty()} otherwise
     */
    Optional<T> get(long id);

    /**
     * Get all items
     *
     * @return A list of all items if any exists, otherwise and empty list
     */
    List<T> getAll();

    /**
     * Create a new item and adds it to the system
     *
     * @param item The item to be created
     * @return The newly created item
     */
    T create(T item);

    /**
     * Update an existing item
     *
     * @param item The item to be updated
     * @return True if update was successful, false otherwise
     */
    boolean update(T item);

    /**
     * Delete an existing item
     *
     * @param id The ID of the item
     * @return True if deletion was successful, false otherwise
     */
    boolean delete(long id);
}
