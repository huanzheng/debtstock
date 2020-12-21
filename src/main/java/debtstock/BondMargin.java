package debtstock;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.tusharepro.core.TushareProService;
import com.github.tusharepro.core.entity.CbBasicEntity;
import com.github.tusharepro.core.entity.MarginDetailEntity;
import com.github.tusharepro.core.http.Request;

public class BondMargin {
	public CbBasicEntity cb;
	public MarginDetailEntity margin;

	public static List<BondMargin> getMatches() {
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
				.sorted((l,r) -> l.cb.getTsCode().compareTo(r.cb.getTsCode()))
				.collect(Collectors.toList());
	}
	
	public static List<BondMargin> getMatches2() {
		List<BondMargin> ret = null;
		List<CbBasicEntity> bonds = null;
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
						match.margin = margins.stream().filter(margin -> margin.getTsCode().equals(cb.getStkCode())).findAny().get();
						return match;
					})
					.sorted((l,r) -> l.cb.getTsCode().compareTo(r.cb.getTsCode()))
					.collect(Collectors.toList());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return ret;
	}

}
