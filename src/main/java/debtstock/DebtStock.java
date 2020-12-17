package debtstock;

import java.util.ArrayList;
import java.util.List;

import com.github.tusharepro.core.TusharePro;


public class DebtStock {
	public static void main(String[] args) {
		final TusharePro.Builder builder = new TusharePro.Builder()
		        .setToken(Hidden.token);  // 你的token

		TusharePro.setGlobal(builder.build());  // 设置全局配置
		
		List<BondMargin> matches = BondMargin.getMatches2();
		
		System.out.println("Totally found " + matches.size() + " bond with margins");
		int i = 0;
//		for(BondMargin match : matches) {
//			System.out.println(" " + i + " -------------------------------------------------- ");
//			System.out.println(match.cb);
//			System.out.println(match.margin);
//			System.out.println(" ------------------------------------------------------------ ");
//			i++;
//		}
		for(BondMargin match : matches) {
			System.out.println(match.cb.getStkCode() + " " + match.cb.getStkShortName() + " " + match.cb.getTsCode() + " " + match.cb.getBondFullName());
		}
	}
}
