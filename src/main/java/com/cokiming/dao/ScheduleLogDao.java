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
import java.util.List;

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

    public ScheduleLog selectLatestByJobId(String jobId) {
        Query<ScheduleLog> query = getNewQuery();
        query.field("jobId").equal(jobId);
        query.order("-executeTime");

        return super.findOne(query);
    }

    public List<ScheduleLog> selectByPage(String jobId,int offset,int limit) {
        Query<ScheduleLog> query = getNewQuery();
        query.field("jobId").equal(jobId);
        query.order("-executeTime");
        query.offset(offset);
        query.limit(limit);

        return super.find(query).asList();
    }

    public long countByJobId(String jobId) {
        Query<ScheduleLog> query = getNewQuery();
        query.field("jobId").equal(jobId);

        return super.count(query);
    }

    private Query<ScheduleLog> getNewQuery() {
        return super.getDs().createQuery(ScheduleLog.class);
    }
}
