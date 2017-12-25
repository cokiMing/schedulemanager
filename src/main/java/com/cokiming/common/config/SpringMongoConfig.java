package com.cokiming.common.config;

import com.cokiming.dao.mongo.DatastoreFactoryBean;
import com.cokiming.dao.mongo.MongoFactoryBean;
import com.cokiming.dao.mongo.MorphiaFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wuyiming
 * Created by wuyiming on 2017/12/15.
 */
@Configuration
public class SpringMongoConfig {

    @Value("${cokiming.mongo.host}")
    private String[] serverStrings;

    @Value("${cokiming.mongo.user}")
    private String username;

    @Value("${cokiming.mongo.pwd}")
    private String password;

    @Value("${cokiming.mongo.authDb}")
    private String authDb;

    @Value("${cokiming.mongo.dbname}")
    private String dbName;

    @Bean("mongoClient")
    public MongoFactoryBean mongoClient() {
        MongoFactoryBean factoryBean = new MongoFactoryBean();
        factoryBean.setServerStrings(serverStrings);
        factoryBean.setAuthDb(authDb);
//        factoryBean.setUsername(username);
//        factoryBean.setPassword(password);

        return factoryBean;
    }

    @Bean
    public MorphiaFactoryBean morphiaFactoryBean() {
        MorphiaFactoryBean morphiaFactoryBean = new MorphiaFactoryBean();
        String[] arrays = new String[1];
        arrays[0] = "com.cokiming.dao.entity";
        morphiaFactoryBean.setMapPackages(arrays);

        return morphiaFactoryBean;
    }

    @Bean("schedulemanager")
    public DatastoreFactoryBean schedulemanager(@Autowired MongoFactoryBean mongoFactoryBean,
                                                @Autowired MorphiaFactoryBean morphiaFactoryBean) {
        DatastoreFactoryBean factoryBean = new DatastoreFactoryBean();
        try {
            factoryBean.setMorphia(morphiaFactoryBean.getObject());
            factoryBean.setMongo(mongoFactoryBean.getObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        factoryBean.setDbName(dbName);
        factoryBean.setToEnsureCaps(false);
        factoryBean.setToEnsureCaps(false);

        return factoryBean;
    }
}
