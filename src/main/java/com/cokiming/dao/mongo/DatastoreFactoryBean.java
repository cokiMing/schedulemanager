package com.cokiming.dao.mongo;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * @author wuyiming
 * Created by admin on 16/3/15.
 */
public class DatastoreFactoryBean  extends AbstractFactoryBean<Datastore> {

    /** morphia实例，最好是单例 */
    private Morphia morphia;

    /** mongo实例，最好是单例 */
    private Mongo mongo;

    /**数据库名*/
    private String dbName;

    /**是否确认索引存在，默认false*/
    private boolean toEnsureIndexes=false;

    /**是否确认caps存在，默认false*/
    private boolean toEnsureCaps=false;


    @Override
    protected Datastore createInstance() throws Exception {
        //这里的username和password可以为null，morphia对象会去处理
        Datastore ds = morphia.createDatastore((MongoClient) mongo, dbName);
        if(toEnsureIndexes){
            ds.ensureIndexes();
        }
        if(toEnsureCaps){
            ds.ensureCaps();
        }
        return ds;
    }

    @Override
    public Class<?> getObjectType() {
        return Datastore.class;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        if (mongo == null) {
            throw new IllegalStateException("mongo is not set");
        }
        if (morphia == null) {
            throw new IllegalStateException("morphia is not set");
        }
    }

    public void setMorphia(Morphia morphia) {
        this.morphia = morphia;
    }

    public void setMongo(Mongo mongo) {
        this.mongo = mongo;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setToEnsureIndexes(boolean toEnsureIndexes) {
        this.toEnsureIndexes = toEnsureIndexes;
    }

    public void setToEnsureCaps(boolean toEnsureCaps) {
        this.toEnsureCaps = toEnsureCaps;
    }
}
