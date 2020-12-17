package debtstock;

import java.io.IOException;

import com.github.tusharepro.core.TusharePro;
import com.github.tusharepro.core.TushareProService;
import com.github.tusharepro.core.entity.CbBasicEntity;
import com.github.tusharepro.core.entity.MarginDetailEntity;
import com.github.tusharepro.core.http.Request;

public class DebtStock {
	public static void main(String[] args) {
		final TusharePro.Builder builder = new TusharePro.Builder()
		        .setToken(Hidden.token);  // 你的token

		TusharePro.setGlobal(builder.build());  // 设置全局配置
		
		try {
			TushareProService.marginDetail(new Request<MarginDetailEntity>() {}  // 使用全局配置
			        .allFields()  // 所有字段
			        ).stream()  
			        .forEach(System.out::println);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			TushareProService.cbBasic(new Request<CbBasicEntity>() {}  // 使用全局配置
			        .allFields()  // 所有字段
			        ).stream()  
			        .forEach(System.out::println);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
