package com.nari.slsd.hms.hdu.common.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.nari.slsd.hd.clientproxy.ClientProxy;
import com.nari.slsd.hd.clientproxy.IGenHisDataServiceProxy;
import com.nari.slsd.hd.config.Configuration;
import com.nari.slsd.hd.dto.Chardata;
import com.nari.slsd.hd.param.FetchParam;
import com.nari.slsd.hd.param.Param;

public class HmsDataProvider {
	
	public static HashMap<Long, Chardata> getNcRealData(ArrayList<Long> ids){
		//构建查询参数
		FetchParam param = new FetchParam(Param.AppType.APP_Type_NC);
		param.idarrayLongs = ids.toArray(new Long[0]);
		param.rundatatype = Param.RunDataType.RUN_RT;
		param.valtype = Param.ValType.Special_RT;
		
		FetchParam[] queryParam = { param };
		//历史数据查询接口
		IGenHisDataServiceProxy genHisDataService = ClientProxy.getGenHisDataServiceProxy(
				Configuration.getCurrentPlantId());
		
		HashMap<Long, Chardata>[] ncDataMap = genHisDataService.getSpecialData(queryParam);
		if(ncDataMap.length <= 0){
			return null;
		}
		return ncDataMap[0];
	}

}
