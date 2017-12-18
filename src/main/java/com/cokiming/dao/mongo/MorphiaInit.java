package com.cokiming.dao.mongo;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.util.List;

/**
 * Created by admin on 16/3/15.
 */
public class MorphiaInit {

    public MorphiaInit(Morphia morphia, Datastore datastore, List<Class> classes) {
        try {
            for (Class clazz : classes) {
                morphia.map(clazz);
            }
            datastore.ensureCaps();
            datastore.ensureIndexes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
