package com.github.tusharepro.core.bean;

import com.github.tusharepro.core.http.BaseRequestParam;

/**
 * 国际指数
 * https://tushare.pro/document/2?doc_id=211
 */
public interface IndexGlobal extends BaseBean {

    String API_NAME = "index_global";

    interface Fields {
        String ts_code = "ts_code";  // TS指数代码
        String trade_date = "trade_date";  // 交易日
        String open = "open";  // 开盘点位
        String close = "close";  // 收盘点位
        String high = "high";  // 最高点位
        String low = "low";  // 最低点位
        String pre_close = "pre_close";  // 昨日收盘点
        String change = "change";  // 涨跌点位
        String pct_chg = "pct_chg";  // 涨跌幅
        String vol = "vol";  // 成交量 （大部分无此项数据）
        String amount = "amount";  // 成交额 （大部分无此项数据）
    }

    interface Params {

        ts_code ts_code = new ts_code();  // TS指数代码，见下表
        class ts_code extends BaseRequestParam {
            {key = "ts_code";}
        }

        trade_date trade_date = new trade_date();  // 交易日期，YYYYMMDD格式，下同
        class trade_date extends BaseRequestParam {
            {key = "trade_date";}
        }

        start_date start_date = new start_date();  // 开始日期
        class start_date extends BaseRequestParam {
            {key = "start_date";}
        }

        end_date end_date = new end_date();  // 结束日期
        class end_date extends BaseRequestParam {
            {key = "end_date";}
        }
    }

}