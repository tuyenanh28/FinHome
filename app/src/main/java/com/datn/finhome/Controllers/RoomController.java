package com.datn.finhome.Controllers;

import com.datn.finhome.Interfaces.IAfterInsertObject;
import com.datn.finhome.Models.RoomModel;
import com.google.firebase.database.FirebaseDatabase;

public class RoomController {
    private static RoomController instance;

    private RoomController() {
    }

    public static RoomController getInstance() {
        if (instance == null) {
            instance = new RoomController();
        }
        return instance;
    }

    public void insertProduct(RoomModel room, IAfterInsertObject iAfterInsertObject) {
        FirebaseDatabase.getInstance().getReference().child("Room").child(room.getId())
                .setValue(room, (error, ref) -> {
                    if (error == null) {
                        iAfterInsertObject.onSuccess(room);
                    } else {
                        iAfterInsertObject.onError(error);
                    }

                });
    }
}
