package com.work.wcs.mapper;

import java.util.Date;
import java.util.List;

import com.adplatform.command.ADCommand;
import com.adplatform.command.ADSchemeCommand;
import com.adplatform.model.AdCurrent;
import com.adplatform.model.AdTemp;

public interface AdCurrentMapper {
	int deleteByPrimaryKey(String adcAdid);

	int insert(AdCurrent record);

	int insertSelective(AdCurrent record);

	AdCurrent selectByPrimaryKey(String adcAdid);

	int updateByPrimaryKeySelective(AdCurrent record);

	int updateByPrimaryKey(AdCurrent record);

	// 根据ADSchemeCommand 查找AdCurrent
	AdCurrent selectByADSchemeCommand(ADSchemeCommand adSchemeCommand);

	// 根据广告方案id和广告类型查询广告
	List<AdCurrent> selectByAdSchemeIdAndAdType(ADSchemeCommand adSchemeCommand);

	void setVideoFlagToZ(AdTemp adTemp);

	void setVideoFlagToO(AdTemp adTemp);
	
	int insertAdCurrentBySGTI(List<AdCurrent> adCurrents);
	
	// 根据组织id、方案id和广告类型获取最新的广告
	List<AdCurrent> selectMergeAdcByGST(AdCurrent adCommand);
	
	// 根据最新的广告获取对应的整套广告
	List<AdCurrent> selectAdcListByGST(AdCurrent adCurrent);

	AdCurrent selectAdcByGSTI(AdCurrent adCommand);

	// 把所有该组织下的子组织图片地址清空
	void updateVideoCurrent(AdTemp adTemp);

	// 把所有该组织下的子组织图片地址清空
	void updateVideoCurrentBoot(AdTemp adTemp);

	// 查找最新的更新时间
	Date selectNewsAdcUpdateTime(ADSchemeCommand adSchemeCommand);

	// 把所有该组织下的子组织视频地址清空
	void updatePicCurrentBoot(AdTemp adTemp);

	// 把所有该组织下的子组织视频地址清空
	void updatePicCurrent(AdTemp adTemp);

	void updateAdCurrentBatchByGSTI(List<ADSchemeCommand> updataList);

	void updateTheLasttwoIndex(ADSchemeCommand schemeCommand);

}