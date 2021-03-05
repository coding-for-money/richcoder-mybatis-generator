package com.huobi.finance.entity.model;

    import java.util.Date;

import lombok.Data;
import java.io.Serializable;

/** 
auto generator ,don't modify!!!
 * @author lilinjun
 * @version V1.0
 * @date 2021 03-03 17:54.
 */
@Data
public class JobHistoryModel implements Serializable {

private static final long serialVersionUID = -7460958329226325335L;

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
