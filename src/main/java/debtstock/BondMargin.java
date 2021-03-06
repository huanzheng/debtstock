package debtstock;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.tusharepro.core.TushareProService;
import com.github.tusharepro.core.entity.CbBasicEntity;
import com.github.tusharepro.core.entity.CbDailyEntity;
import com.github.tusharepro.core.entity.DailyEntity;
import com.github.tusharepro.core.entity.MarginDetailEntity;
import com.github.tusharepro.core.http.Request;

import lombok.Data;

@Data
public class BondMargin {
	public CbBasicEntity cb = null;
	public MarginDetailEntity margin = null;
	public CbDailyEntity cbPrice = null;
	public DailyEntity stockPrice = null;
	
	public Double yjlv;
	
	public static String FORMAT_INPUTDATE = "yyyyMMdd";

	public static List<BondMargin> getMatchesOldFashion() {
		ArrayList<BondMargin> ret = new ArrayList<BondMargin>();
		List<MarginDetailEntity> margins = null;
		List<CbBasicEntity> bonds = null;
		
		try {
			margins = TushareProService.marginDetail(new Request<MarginDetailEntity>() {}  // 使用全局配置
			        .allFields()  // 所有字段
			        );
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			bonds = TushareProService.cbBasic(new Request<CbBasicEntity>() {}  // 使用全局配置
			        .allFields()  // 所有字段
			        );
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		LocalDate now = LocalDate.now();
		if (margins != null && bonds != null) {
			for (CbBasicEntity bond: bonds) {
				for (MarginDetailEntity margin: margins) {
					if ((bond.getDelistDate() == null || now.isBefore(bond.getDelistDate()))
							&& margin.getTsCode().equals(bond.getStkCode())) {
						BondMargin match = new BondMargin();
						match.cb = bond;
						match.margin = margin;
						ret.add(match);
					}
				}
			}
		}
		return ret.stream()
				.sorted((l,r) -> r.cb.getTsCode().compareTo(l.cb.getTsCode()))
				.collect(Collectors.toList());
	}
	
	public static List<BondMargin> getMatches() {
		List<BondMargin> ret = null;
		try {
			final List<MarginDetailEntity> margins = TushareProService.marginDetail(new Request<MarginDetailEntity>() {}  // 使用全局配置
			        .allFields()  // 所有字段
			        );
		
			LocalDate now = LocalDate.now();
			return TushareProService.cbBasic(new Request<CbBasicEntity>() {}  // 使用全局配置
			        .allFields()  // 所有字段
			        ).stream()
					.filter(cb -> (cb.getDelistDate() == null || now.isBefore(cb.getDelistDate()))
									&& 
									margins.stream().anyMatch(margin -> margin.getTsCode().equals(cb.getStkCode())))
					.map(cb -> {
						BondMargin match = new BondMargin();
						match.cb = cb;
						match.margin = margins.stream().filter(margin -> margin.getTsCode().equals(cb.getStkCode())).findFirst().get();
						return match;
					})
					.sorted((l,r) -> r.cb.getTsCode().compareTo(l.cb.getTsCode()))
					.collect(Collectors.toList());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return ret;
	}
	
	public static void fillPrices(List<BondMargin> bondMargins, LocalDate tradeDate) {
		String dateStr = tradeDate.format(DateTimeFormatter.ofPattern(BondMargin.FORMAT_INPUTDATE));
		try {
			List<DailyEntity> stockPrices = TushareProService.daily(new Request<DailyEntity>() {}
												.param("trade_date", dateStr)
												)
												.stream()
												.sorted((l,r) -> r.getTsCode().compareTo(l.getTsCode()))
										        .collect(Collectors.toList());
			System.out.println("Got " + stockPrices.size() + " stockPrices");
			
			List<CbDailyEntity> cbPrices = TushareProService.cbDaily(new Request<CbDailyEntity>() {}
												.param("trade_date", dateStr)
										        ).stream()  
												.sorted((l,r) -> r.getTsCode().compareTo(l.getTsCode()))
										        .collect(Collectors.toList());
			System.out.println("Got " + cbPrices.size() + " bondPrices");
			
			bondMargins.stream()
						.forEach(bondMargin -> {
							Optional<CbDailyEntity> cbDaily = cbPrices.stream().filter(cbPrice -> cbPrice.getTsCode().equals(bondMargin.cb.getTsCode()))
													.findFirst();
							if (cbDaily.isPresent()) {
								bondMargin.cbPrice = cbDaily.get();
							} else {
								System.out.println("Cannot find bond trade info for bond " + bondMargin.cb.getTsCode() + " " + bondMargin.cb.getBondFullName());
							}
							Optional<DailyEntity> stockDaily = stockPrices.stream().filter(stockPrice -> stockPrice.getTsCode().equals(bondMargin.cb.getStkCode()))
													.findFirst();
							if (stockDaily.isPresent()) {
								bondMargin.stockPrice = stockDaily.get();
							} else {
								System.out.println("Cannot find stock trade info for bond " + bondMargin.cb.getTsCode() + " " + bondMargin.cb.getBondFullName());
							}
						});
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	/**
	  * Method calculates last working day for last day of month as input
	  * @param lastDayOfMonth
	  * @return LocalDate instance containing last working day
	  */
	public static LocalDate getLastWorkingDayOfMonth(LocalDate lastDayOfMonth) {
	    LocalDate lastWorkingDayofMonth;
	    int hourOfDay = LocalDateTime.now().getHour();
	    switch (DayOfWeek.of(lastDayOfMonth.get(ChronoField.DAY_OF_WEEK))) {
	   		case MONDAY:
	   			if (hourOfDay < 15) {
	   				lastWorkingDayofMonth = lastDayOfMonth.minusDays(3);
	   			} else {
	   				lastWorkingDayofMonth = lastDayOfMonth;
	   			}
	   			break;
	   		case SATURDAY:
	   			lastWorkingDayofMonth = lastDayOfMonth.minusDays(1);
	   			break;
	   		case SUNDAY:
	   			lastWorkingDayofMonth = lastDayOfMonth.minusDays(2);
	   			break;
	   		default:
	   			if (hourOfDay < 15) {
	   				lastWorkingDayofMonth = lastDayOfMonth.minusDays(1);
	   			} else {
	   				lastWorkingDayofMonth = lastDayOfMonth;
	   			}
	    }
	    return lastWorkingDayofMonth;
	}
	
	public static List<BondMargin> calAndSort(List<BondMargin> bondMargins) {
		List<BondMargin> ret = bondMargins.stream()
				.filter(bondMargin -> bondMargin.cb != null && bondMargin.cbPrice != null && bondMargin.stockPrice != null)
				.collect(Collectors.toList());
		ret.stream().forEach(bondMargin -> bondMargin.calYjl());
		return ret.stream().sorted((l,r) -> l.yjlv.compareTo(r.yjlv))
				.collect(Collectors.toList());
	}

	public void calYjl() {
		if (cb != null && cbPrice != null && stockPrice != null) {
			Double cbShares = cb.getPar()/cb.getConvPrice();
			Double cbSharePrice = cbPrice.getClose()/cbShares;
			yjlv = (cbSharePrice/stockPrice.getClose() - 1)*100;
		} else {
			yjlv = 10000.0;
			System.out.println("Missing information");
		}
	}
	
	public static List<BondMargin> getYjlvHistory(String tsCode, String startDateStr, String endDateStr) {
		List<BondMargin> ret = null;
		try {
				CbBasicEntity cb = TushareProService.cbBasic(new Request<CbBasicEntity>() {}
					.param("ts_code", tsCode)
			        .allFields()  // 所有字段
			        ).stream()
					.collect(Collectors.toList()).get(0);
				
				List<DailyEntity> stockPrices = TushareProService.daily(new Request<DailyEntity>() {}
					.param("start_date", startDateStr)
					.param("end_date", endDateStr)
					.param("ts_code", cb.getStkCode())
					)
					.stream()
					.collect(Collectors.toList());
				System.out.println("Got " + stockPrices.size() + " stockPrices");

				List<CbDailyEntity> cbPrices = TushareProService.cbDaily(new Request<CbDailyEntity>() {}
					.param("start_date", startDateStr)
					.param("end_date", endDateStr)
					.param("ts_code", cb.getTsCode())
			        )
					.stream()  
					.collect(Collectors.toList());
				System.out.println("Got " + cbPrices.size() + " bondPrices");
				
				ret = stockPrices.stream().map(stockPrice -> {
					BondMargin match = new BondMargin();
					match.cb = cb;
					match.stockPrice = stockPrice;
					Optional<CbDailyEntity> cbDaily = cbPrices.stream().filter(cbPrice -> cbPrice.getTradeDate().equals(stockPrice.getTradeDate())).findFirst();
					if (cbDaily.isPresent()) {
						match.cbPrice = cbDaily.get();
					} else {
						System.out.println("Missing bond trade info " + stockPrice.getTradeDate());
						match = null;
					}
					return match;
				}).filter(it -> it != null)
				  .collect(Collectors.toList());
				
				ret.stream().forEach(it -> it.calYjl());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return ret;	
	}
	
	public static List<BondMargin> getYjlvHistory(String tsCode, LocalDate endDate, int daysBefore) {
		LocalDate startDate = endDate.minusDays(daysBefore);
		String startDateStr = startDate.format(DateTimeFormatter.ofPattern(BondMargin.FORMAT_INPUTDATE));
		String endDateStr = endDate.format(DateTimeFormatter.ofPattern(BondMargin.FORMAT_INPUTDATE));
		return getYjlvHistory(tsCode, startDateStr, endDateStr);
	}
}
