package debtstock;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.github.tusharepro.core.TusharePro;
import com.github.tusharepro.core.TushareProService;
import com.github.tusharepro.core.bean.FundNav;
import com.github.tusharepro.core.entity.CbDailyEntity;
import com.github.tusharepro.core.entity.FundBasicEntity;
import com.github.tusharepro.core.entity.FundNavEntity;
import com.github.tusharepro.core.http.Client;
import com.github.tusharepro.core.http.Request;


public class DebtStock {
	public static void main(String[] args) {
		final TusharePro.Builder builder = new TusharePro.Builder()
		        .setToken(Hidden.token);  // 你的token

		TusharePro.setGlobal(builder.build());  // 设置全局配置
		//sortCbWithMargins();
		getFundsNav();
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
		String tsCode = "127015.SZ";
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
	
	public static void getFunds() {
		try {
			List<FundBasicEntity> funds = TushareProService.fundBasic(new Request<FundBasicEntity>() {}
				
		        ).stream()
				.collect(Collectors.toList());
				funds.forEach(System.out::println);
				System.out.println("Got " + funds.size() + " funds");

			} catch (IOException e) {
				
			}
	}
	
	private static void getFundNav(String tsCode, String dateStr) {
		try {
			List<FundNavEntity> fundNavs = TushareProService.fundNav(new Request<FundNavEntity>() {}
				.param("end_date", dateStr)
				.param("ts_code",tsCode)
		        ).stream()
				.collect(Collectors.toList());
				
				if (fundNavs.size() == 1) {
					System.out.println(fundNavs.get(0).getTsCode() + " " + fundNavs.get(0).getUnitNav());
				} else {
					System.out.println("Failed to get fund nav for " + tsCode + " " + dateStr);
				}
				
			} catch (IOException e) {
				
			}
	}
	
	public static void getFundsNav() {
		LocalDate date = BondMargin.getLastWorkingDayOfMonth(LocalDate.now());
		String dateStr = date.format(DateTimeFormatter.ofPattern(BondMargin.FORMAT_INPUTDATE));
		dateStr = "20210329";
		
		String[] tsCodes = {"001714.OF", "001938.OF", "001216.OF", "110011.OF", "260108.OF", "110003.OF"};
		for(String tsCode : tsCodes)
			getFundNav(tsCode, dateStr);
		System.out.println();
		
		String[] tsCodes2 = {"519697.OF", "519732.OF", "270002.OF", "163402.SZ"};
		for(String tsCode : tsCodes2)
			getFundNav(tsCode, dateStr);
		System.out.println();
		
		String[] tsCodes3 = {"002227.OF", "001316.OF", "320021.OF", "050023.OF", "001289.OF"};
		for(String tsCode : tsCodes3)
			getFundNav(tsCode, dateStr);
		System.out.println();
		
		String[] tsCodes4 = {"217022.OF", "001868.OF", "000286.OF", "006947.OF"};
		for(String tsCode : tsCodes4)
			getFundNav(tsCode, dateStr);
		System.out.println();

	}
}
