package com.cokiming.dao;

import com.cokiming.dao.entity.ScheduleJob;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author wuyiming
 * Created by wuyiming on 2017/12/18.
 */
@Repository
public class ScheduleJobDao extends BasicDAO<ScheduleJob, ObjectId> {

    @Autowired
    public ScheduleJobDao(@Qualifier("schedulemanager") Datastore ds) {
        super(ds);
    }

    public String saveJob(ScheduleJob scheduleJob) {
        Date current = new Date();
        scheduleJob.setUpdateTime(current);
        scheduleJob.setCreateTime(current);
        return (String)super.save(scheduleJob).getId();
    }

    public ScheduleJob selectById(String id) {
        Query<ScheduleJob> query = getNewQuery();
        query.field("id").equal(new ObjectId(id));
        return super.findOne(query);
    }

    public List<ScheduleJob> selectByModel(ScheduleJob scheduleJob) {
        Query<ScheduleJob> query = getNewQuery();
        if (scheduleJob.getStatus() != null) {
            query.field("status").equal(scheduleJob.getStatus());
        }
        if (scheduleJob.getStarted() != null) {
            query.field("started").equal(scheduleJob.getStarted());
        }
        query.order("-updateTime");

        return super.find(query).asList();
    }

    public ScheduleJob selectOneByModel(ScheduleJob scheduleJob) {
        Query<ScheduleJob> query = getNewQuery();
        if (scheduleJob.getStatus() != null) {
            query.field("status").equal(scheduleJob.getStatus());
        }

        return super.findOne(query);
    }

    public void updateByCondition(ScheduleJob condition,ScheduleJob model) {
        Query<ScheduleJob> query = getNewQuery();
        UpdateOperations<ScheduleJob> update = getNewUpdate();

        if (condition.getId() != null) {
            query.field("id").equal(new ObjectId(condition.getId()));
        }
        if (condition.getStatus() != null) {
            query.field("status").equal(condition.getStatus());
        }
        if (condition.getJobName() != null) {
            query.field("jobName").equal(condition.getJobName());
        }

        if (model.getJobName() != null) {
            update.set("jobName",model.getJobName());
        }
        if (model.getStatus() != null) {
            update.set("status",model.getStatus());
        }
        if (model.getCronExpression() != null) {
            update.set("cronExpression",model.getCronExpression());
        }
        if (model.getUrl() != null) {
            update.set("url",model.getUrl());
        }
        if (model.getDescription() != null) {
            update.set("description",model.getDescription());
        }
        if (model.getStarted() != null) {
            update.set("started",model.getStarted());
        }
        update.set("updateTime",new Date());

        super.update(query,update);
    }

    private Query<ScheduleJob> getNewQuery() {
        return super.getDs().createQuery(ScheduleJob.class);
    }

    private UpdateOperations<ScheduleJob> getNewUpdate() {
        return super.getDs().createUpdateOperations(ScheduleJob.class);
    }
}
