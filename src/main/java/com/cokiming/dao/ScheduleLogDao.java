package com.cokiming.dao;

import com.cokiming.dao.entity.ScheduleLog;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @author wuyiming
 * Created by wuyiming on 2017/12/7.
 */
@Repository
public class ScheduleLogDao extends BasicDAO<ScheduleLog, ObjectId> {

    @Autowired
    public ScheduleLogDao(@Qualifier("schedulemanager") Datastore ds) {
        super(ds);
    }

    public void saveLog(ScheduleLog scheduleLog) {
        scheduleLog.setExecuteTime(new Date());
        super.save(scheduleLog);
    }

    public ScheduleLog selectLatestByJobName(String jobName) {
        Query<ScheduleLog> query = getNewQuery();
        query.field("jobName").equal(jobName);
        query.order("-executeTime");

        return super.findOne(query);
    }

    private Query<ScheduleLog> getNewQuery() {
        return super.getDs().createQuery(ScheduleLog.class);
    }
}
