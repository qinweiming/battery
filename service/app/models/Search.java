package models;

import java.util.Date;

/**
 * 保存搜索信息
 * Created by xudongmei on 2017/1/4.
 */
public class Search {
    public String companyName;
    public Date startDate;
    public Date endDate;
    /**
     * 源厂商ID（卖家ID）
     */
    public String fromId;
    /**
     * 目的厂商ID（买家ID）
     */
    public String toId;
    /**
     * 起始产品流水号
     */
    public String startProductId;
    /**
     * 截止产品流水号
     */
    public String endProductId;
}
