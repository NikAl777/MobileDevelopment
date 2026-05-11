package ru.mirea.aleksandrovnd.employeedb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Hero {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public String power;
    public int dangerLevel;
}
