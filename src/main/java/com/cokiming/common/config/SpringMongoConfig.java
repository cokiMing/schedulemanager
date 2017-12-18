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

    /**
     * <bean id="mongoClient" class="com.mrwind.mongo.morphia.MongoFactoryBean">
     <!-- 设定服务器列表，默认为localhost:27017 -->
     <property name="serverStrings">
     <array>
     <value>${mvn.mongo.server.host}</value>
     </array>
     </property>
     <property name="username" value="${mvn.mongo.server.user}" />
     <property name="password" value="${mvn.mongo.server.pwd}" />
     <property name="authDb" value="${mvn.mongo.server.authDb}" />
     </bean>

     <!-- 使用工厂创建morphia实例，同时完成类映射操作 -->
     <bean id="morphia" class="com.mrwind.mongo.morphia.MorphiaFactoryBean">
     <!-- 指定要扫描的POJO包路径 -->
     <property name="mapPackages">
     <array>
     <value>com.mrwind.mongo.pojo</value>
     </array>
     </property>
     </bean>

     <!-- 使用工厂创建datastore，同时完成index和caps的确认操作 -->
     <bean id="windlog" class="com.mrwind.mongo.morphia.DatastoreFactoryBean">
     <property name="morphia" ref="morphia" />
     <property name="mongo" ref="mongoClient" />

     <!-- collection的名称 -->
     <property name="dbName" value="${mvn.mongo.server.dbName}" />

     <!-- 是否进行index和caps的确认操作，默认为flase -->
     <property name="toEnsureIndexes" value="false" />
     <property name="toEnsureCaps" value="false" />
     </bean>
     */

    @Bean("mongoClient")
    public MongoFactoryBean mongoClient() {
        MongoFactoryBean factoryBean = new MongoFactoryBean();
        factoryBean.setServerStrings(serverStrings);
        factoryBean.setAuthDb(authDb);

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
