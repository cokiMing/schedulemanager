package com.cokiming.dao.mongo;

import com.mongodb.*;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wuyiming
 * Created by admin on 16/3/15.
 */
public class MongoFactoryBean extends AbstractFactoryBean<MongoClient> {

    /** 表示服务器列表(主从复制或者分片)的字符串数组*/
    private String[] serverStrings;

    /** mongoDB配置对象 */
    private MongoClientOptions mongoOptions;

    /** 是否主从分离(读取从库)，默认读写都在主库*/
    private boolean readSecondary = false;

    /** 设定写策略(出错时是否抛异常)，默认采用SAFE模式(需要抛异常)*/
    private WriteConcern writeConcern = WriteConcern.SAFE;

    /** 用户名，可为空*/
    private String username;

    /** 密码，可为空*/
    private String password;

    private String authDb;

    @Override
    public Class<?> getObjectType() {
        return Mongo.class;
    }

    @Override
    protected MongoClient createInstance() throws Exception {
        Mongo mongo = initMongo();

        // 设定主从分离
        if (readSecondary) {
            mongo.setReadPreference(ReadPreference.secondaryPreferred());
        }

        // 设定写策略
        mongo.setWriteConcern(writeConcern);
        return (MongoClient)mongo;
    }

    /**
     * 初始化mongo实例
     * @return
     * @throws Exception
     */
    private MongoClient initMongo() throws Exception {
        // 根据条件创建Mongo实例
        MongoClient mongo = null;
        List<ServerAddress>  serverList = getServerList();
        MongoCredential mongoCredential = null;
        if(!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password) && !StringUtils.isEmpty(authDb)){
        	mongoCredential = MongoCredential.createCredential(username, authDb, password.toCharArray());
        }
        if (serverList.size() == 0) {
        	mongo = new MongoClient();	
        } else if (serverList.size() == 1) {
            if (mongoOptions != null) {
            	if(mongoCredential != null){
            		mongo = new MongoClient(serverList.get(0), Arrays.asList(mongoCredential), mongoOptions);
            	}else{
            		mongo = new MongoClient(serverList.get(0), mongoOptions);	
            	}
            } else {
            	if(mongoCredential != null){
            		mongo = new MongoClient(serverList.get(0), Arrays.asList(mongoCredential));            		
            	}else{
            		mongo = new MongoClient(serverList.get(0));	
            	}
            }
        } else {
            if (mongoOptions != null) {
            	if(mongoCredential != null){
            		mongo = new MongoClient(serverList, Arrays.asList(mongoCredential), mongoOptions);
            	}else{
            		mongo = new MongoClient(serverList, mongoOptions);
            	}
            } else {
            	if(mongoCredential != null){
            		mongo = new MongoClient(serverList, Arrays.asList(mongoCredential));
            	}else{
            		mongo = new MongoClient(serverList);
            	}
            }
        }
        return mongo;
    }


    private List<ServerAddress> getServerList() throws Exception {
        List<ServerAddress> serverList = new ArrayList<ServerAddress>();
        try {
            for (String serverString : serverStrings) {
                String[] temp = serverString.split(":");
                String host = temp[0];
                if (temp.length > 2) {
                    throw new IllegalArgumentException(
                            "Invalid server address string: " + serverString);
                }
                if (temp.length == 2) {
                    serverList.add(new ServerAddress(host, Integer
                            .parseInt(temp[1])));
                } else {
                    serverList.add(new ServerAddress(host));
                }
            }
            return serverList;
        } catch (Exception e) {
            throw new Exception(
                    "Error while converting serverString to ServerAddressList",
                    e);
        }
    }

    public String[] getServerStrings() {
        return serverStrings;
    }

    public void setServerStrings(String[] serverStrings) {
        this.serverStrings = serverStrings;
    }

    public MongoClientOptions getMongoOptions() {
        return mongoOptions;
    }

    public void setMongoOptions(MongoClientOptions mongoOptions) {
        this.mongoOptions = mongoOptions;
    }

    public boolean isReadSecondary() {
        return readSecondary;
    }

    public void setReadSecondary(boolean readSecondary) {
        this.readSecondary = readSecondary;
    }

    public WriteConcern getWriteConcern() {
        return writeConcern;
    }

    public void setWriteConcern(WriteConcern writeConcern) {
        this.writeConcern = writeConcern;
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAuthDb() {
		return authDb;
	}

	public void setAuthDb(String authDb) {
		this.authDb = authDb;
	}
    
}
