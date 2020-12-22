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
		int daysBefore = 200;
		String tsCode = "113008.SH";
		List<BondMargin> history = BondMargin.getYjlvHistory(tsCode, endDate, daysBefore);
		BondMargin match1 = history.get(0);
		System.out.println(match1.cb);

		DecimalFormat df = new DecimalFormat("##.##");
		System.out.printf(
				"%-12s %-8s %-12s %-28s %-8s %-8s %-12s %-12s %-12s\n",
				"股票代码",
				"股票名称",
				"可转债代码",
				"可转债名称",
				"债券面值",
				"转股价格",
				"转股起始日",
				"转股截至日",
				"到期时间");
		System.out.printf(
				"%-12s %-8s %-12s %-28s %-8s %-8s %-12s %-12s %-12s\n",
				match1.cb.getStkCode(),
				match1.cb.getStkShortName(), 
				match1.cb.getTsCode(),
				match1.cb.getBondFullName(),
				match1.cb.getPar(),
				match1.cb.getConvPrice(),
				match1.cb.getConvStartDate(),
				match1.cb.getConvEndDate(),
				match1.cb.getMaturityDate());
		
		
		System.out.printf("%-9s %-6s %-5s %-6s\n", "交易日期","溢价率","债券价格","正股价格");
		for(BondMargin match : history) {
			System.out.printf(
					"%-12s %-8s %-8s %-8s\n",
					match.cbPrice.getTradeDate()
						, df.format(match.yjlv) + "%"
						, df.format(match.cbPrice.getClose())
						, df.format(match.stockPrice.getClose())
						);
		}
	}
}
