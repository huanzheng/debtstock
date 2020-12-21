package debtstock;

import java.text.DecimalFormat;
import java.util.List;

import com.github.tusharepro.core.TusharePro;


public class DebtStock {
	public static void main(String[] args) {
		final TusharePro.Builder builder = new TusharePro.Builder()
		        .setToken(Hidden.token);  // 你的token

		TusharePro.setGlobal(builder.build());  // 设置全局配置

		List<BondMargin> matches = BondMargin.getMatches();
		System.out.println("Totally found " + matches.size() + " bond with margins");

		BondMargin.fillPrices(matches);
		matches = BondMargin.calAndSort(matches);
		
		DecimalFormat df = new DecimalFormat("##.##");
		for(BondMargin match : matches) {
			System.out.println(match.cb.getStkCode() + " " + match.cb.getStkShortName() 
						+ " " + match.cb.getTsCode() + " " + match.cb.getBondFullName()
						+ " " + df.format(match.yjlv) + "%"
						+ " " + match.cb.getPar()
						+ " " + match.cb.getConvPrice()
						+ " " + match.cbPrice.getClose()
						+ " " + match.stockPrice.getClose());
		}
	}
}
