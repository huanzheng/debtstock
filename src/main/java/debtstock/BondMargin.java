package debtstock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.tusharepro.core.TushareProService;
import com.github.tusharepro.core.entity.CbBasicEntity;
import com.github.tusharepro.core.entity.MarginDetailEntity;
import com.github.tusharepro.core.http.Request;

public class BondMargin {
	public CbBasicEntity cb;
	public MarginDetailEntity margin;

	public static ArrayList<BondMargin> getMatches() {
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
		
		if (margins != null && bonds != null) {
			for (MarginDetailEntity margin: margins) {
				for (CbBasicEntity bond: bonds) {
					if (margin.getTsCode().equals(bond.getStkCode())) {
						BondMargin match = new BondMargin();
						match.cb = bond;
						match.margin = margin;
						ret.add(match);
					}
				}
			}
		}
		
		return ret;
	}

}
