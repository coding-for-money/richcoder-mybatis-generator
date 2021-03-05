package com.huobi.finance.entity.dao;

import java.util.List;

import com.huobi.finance.entity.po.JobHistoryEntity;
import org.springframework.stereotype.Repository;

/** 
auto generator ,don't modify!!!
 * @author lilinjun
 * @version V1.0
 * @date 2021 03-03 17:54.
 */
@Repository
public interface JobHistoryMapper {

Integer insertSelective(JobHistoryEntity jobHistoryEntity);

Integer updateByPrimaryKeySelective(JobHistoryEntity jobHistoryEntity);

JobHistoryEntity selectByPrimaryKeySelective(JobHistoryEntity jobHistoryEntity);

List<JobHistoryEntity> queryList(JobHistoryEntity jobHistoryEntity);

List<JobHistoryEntity> listPageSelective(JobHistoryEntity jobHistoryEntity);

Integer countSelective(JobHistoryEntity jobHistoryEntity);

Integer deleteByPrimaryKeySelective(JobHistoryEntity jobHistoryEntity);

}
