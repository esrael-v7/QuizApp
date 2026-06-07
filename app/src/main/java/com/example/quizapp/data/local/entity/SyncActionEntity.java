package com.example.quizapp.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sync_actions")
public class SyncActionEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String actionType; // e.g., "SUBMIT_SCORE", "ADD_FAVOURITE", "REMOVE_FAVOURITE"
    public String dataJson;   // JSON representation of the request body
    public long timestamp;
    public boolean isPending;

    public SyncActionEntity(String actionType, String dataJson) {
        this.actionType = actionType;
        this.dataJson = dataJson;
        this.timestamp = System.currentTimeMillis();
        this.isPending = true;
    }
}
