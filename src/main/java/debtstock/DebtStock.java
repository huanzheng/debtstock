package debtstock;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

import com.github.tusharepro.core.TusharePro;


public class DebtStock {
	public static void main(String[] args) {
		final TusharePro.Builder builder = new TusharePro.Builder()
		        .setToken(Hidden.token);  // 你的token

		TusharePro.setGlobal(builder.build());  // 设置全局配置
		//sortCbWithMargins();
		getYiJiaLvHistory();
	}
	
	public static void sortCbWithMargins() {
		List<BondMargin> matches = BondMargin.getMatches();
		System.out.println("Totally found " + matches.size() + " bond with margins");

		BondMargin.fillPrices(matches, BondMargin.getLastWorkingDayOfMonth(LocalDate.now()));
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
	
	public static void getYiJiaLvHistory() {
		LocalDate endDate = BondMargin.getLastWorkingDayOfMonth(LocalDate.now());
		int daysBefore = 30;
		String tsCode = "113008.SH";
		List<BondMargin> history = BondMargin.getYjlvHistory(tsCode, endDate, daysBefore);
		DecimalFormat df = new DecimalFormat("##.##");
		System.out.println("股票代码" + " 股票名称" + " 可转债代码" + " 可转债名称" + " 溢价率" + " 交易日期" + " 债券价格" + " 正股价格" + " 债券面值" + " 转股价格");

		for(BondMargin match : history) {
			System.out.println(match.cb.getStkCode() + " " + match.cb.getStkShortName() 
						+ " " + match.cb.getTsCode() + " " + match.cb.getBondFullName()
						+ " " + df.format(match.yjlv) + "%"
						+ " " + match.cbPrice.getTradeDate()
						+ " " + match.cbPrice.getClose()
						+ " " + match.stockPrice.getClose()
						+ " " + match.cb.getPar()
						+ " " + match.cb.getConvPrice()
						);
		}
	}
}
