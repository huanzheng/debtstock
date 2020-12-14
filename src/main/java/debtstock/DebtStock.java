package debtstock;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.tusharepro.core.TusharePro;
import com.github.tusharepro.core.TushareProService;
import com.github.tusharepro.core.bean.StockBasic;
import com.github.tusharepro.core.common.KeyValue;
import com.github.tusharepro.core.entity.StockBasicEntity;
import com.github.tusharepro.core.http.Request;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

public class DebtStock {
	public static void main(String[] args) {
		System.out.println("fasas");
		final TusharePro.Builder builder = new TusharePro.Builder()
		        .setToken("39850d3cf74840e6789a42d99881c8138b17bea8f3bdbdd9c242d61c");  // 你的token

		TusharePro.setGlobal(builder.build());  // 设置全局配置

		final KeyValue<String, String> list_status = StockBasic.Params.list_status.value("L");

		// 打印 上海交易所所有上市的沪股通股票 信息
//		try {
//			TushareProService.stockBasic(new Request<StockBasicEntity>() {}  // 使用全局配置
//			        .allFields()  // 所有字段
//			        .param(StockBasic.Params.exchange.value("SSE"))  // 参数
//			        .param(list_status)  // 参数
//			        .param("is_hs", "H"))  // 参数
//			        .forEach(System.out::println);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		System.out.println("fasas2");
		final OkHttpClient defaultHttpClient = new OkHttpClient.Builder()
		        .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE))
		        .connectTimeout(42, TimeUnit.SECONDS)
		        .writeTimeout(42, TimeUnit.SECONDS)
		        .readTimeout(42, TimeUnit.SECONDS)
		        .build();

		// 一个完整的例子
		try {
			TushareProService.stockBasic(
			        new Request<StockBasicEntity>(builder.copy()  // 将配置拷贝
			                .setMaxRetries(5)  // 设置重试次数, 默认为3
			                .setRetrySleepTimeUnit(TimeUnit.SECONDS)  // 设置重试sleep单位, 默认毫秒
			                .setRetrySleepTimeOut(60L)  // 设置重试sleep时间, 默认为0
			                .setRequestExecutor(Executors.newSingleThreadExecutor((r -> {
			                    Thread thread = new Thread(r);
			                    thread.setDaemon(true);
			                    return thread;
			                })))  // 设置请求线程池, 默认CachedThreadPool
			                .setHttpFunction(requestBytes -> {  // requestBytes -> function -> responseBytes 请使用阻塞的方式
			                    try {
			                        okhttp3.Request request = new okhttp3.Request.Builder()
			                                .url("http://api.waditu.com")
			                                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBytes))
			                                .build();

			                        return defaultHttpClient.newCall(request).execute().body().bytes();
			                    }
			                    catch (IOException e) {
			                        e.printStackTrace();
			                    }
			                    return null;
			                })
			                .build()){}
			        .allFields()
			        .param(StockBasic.Params.exchange.value("SZSE"))
			        .param(list_status))
			        .stream()
			        .filter(x -> "银行".equals(x.getIndustry()))
			        .forEach(System.out::println);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("fasas3");
	}
	 
}
