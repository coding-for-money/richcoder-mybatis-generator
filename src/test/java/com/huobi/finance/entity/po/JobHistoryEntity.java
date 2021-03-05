package com.huobi.finance.entity.po;

    import java.util.Date;

import lombok.Data;
import java.io.Serializable;
import org.apache.ibatis.type.Alias;

/** 
auto generator ,don't modify!!!
 * @author lilinjun
 * @version V1.0
 * @date 2021 03-03 17:54.
 */
@Alias("JobHistoryEntity")
@Data
public class JobHistoryEntity implements Serializable {

private static final long serialVersionUID = -7248683359532752498L;

    private Long id;

    /**
	 * 调度任务名称
	 */
	private String taskName;

    /**
	 * 任务执行备注
	 */
	private String jobDetail;

    /**
	 * 任务执行结果
	 */
	private String jobState;

    private Date createdAt;


}
