package com.antonio.samir.meteoritelandingsspots.service.repository.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;

import java.util.List;

/**
 * Created by samirantonio on 08/07/17.
 */
@Dao
public interface MeteoriteDao {

    @Insert
    void insertAll(List<Meteorite> items);

    @Update
    void update(Meteorite meteorite);

    @Query("SELECT * from meteorites ORDER BY :orderBy")
    List<Meteorite> retrive(String orderBy);

    @Query("SELECT * from meteorites where id = :meteoriteId LIMIT 1")
    Meteorite getMeteorite(String meteoriteId);
}
