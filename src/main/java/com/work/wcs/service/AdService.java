package com.work.wcs.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 
 * <P>广告Service</P>
 * @author 周欣(13667212859)
 * @date 2017年6月6日 下午4:54:17
 */
import org.springframework.transaction.annotation.Transactional;

import com.adplatform.bo.AdTempBo;
import com.adplatform.command.ADCommand;
import com.adplatform.command.ADSchemeCommand;
import com.adplatform.command.AdReceiverCommand;
import com.adplatform.mapper.AdCurrentMapper;
import com.adplatform.mapper.AdHistoryMapper;
import com.adplatform.mapper.AdReceiverMapper;
import com.adplatform.mapper.AdSchemeFileMapper;
import com.adplatform.mapper.AdTempMapper;
import com.adplatform.model.AdCurrent;
import com.adplatform.model.AdHistory;
import com.adplatform.model.AdReceiver;
import com.adplatform.model.AdSchemeFile;
import com.adplatform.model.AdTemp;
import com.adplatform.model.AdToBeCheck;
import com.adplatform.result.AdUpdateRate;

/**
 * 
 * <P>
 * 广告Service
 * </P>
 * 
 * @author 周欣(13667212859)
 * @date 2017年6月8日 上午10:07:09
 */
@Service
public class AdService {
	@Autowired
	private AdTempMapper adTempMapper;

	@Autowired
	private AdCurrentMapper adCurrentMapper;

	@Autowired
	private AdHistoryMapper adHistoryMapper;

	@Autowired
	private AdReceiverMapper adReceiverMapper;

	@Autowired
	private AdSchemeFileMapper adSchemeFileMapper;
	
	// 新增AdTemp
	public int addAdTemp(AdTemp adTemp) {
		return adTempMapper.insertSelective(adTemp);
	}

	// 新增AdCurrent
	public int addAdCurrent(AdCurrent adCurrent) {
		return adCurrentMapper.insertSelective(adCurrent);
	}

	// 修改AdTemp
	public int editAdTemp(AdTemp adTemp) {
		return adTempMapper.updateByPrimaryKeySelective(adTemp);
	}

	// 根据ADCommand查找AdTemp
	public AdTemp findAdTemp(ADCommand adCommand) {
		return adTempMapper.selectByADCommand(adCommand);
	}

	// 根据主键查找AdTemp
	public AdTemp findAdTempByPK(String adtAdid) {
		return adTempMapper.selectByPrimaryKey(adtAdid);
	}

	// 根据ADSchemeCommand 查找AdCurrent
	public AdCurrent findAdCurrent(ADSchemeCommand adSchemeCommand) {
		return adCurrentMapper.selectByADSchemeCommand(adSchemeCommand);
	}

	// 查询所有待审批广告
	public List<AdToBeCheck> findAllToBeCheck() {
		return adTempMapper.selectAllToBeCheck();
	}

	// 统计待审批广告数量
	public int countAdToBeCheck() {
		return adTempMapper.countAdToBeCheck();
	}

	// 根据主键查找AdTempBo
	public AdTempBo findAdTempBoByPrimaryKey(String adtAdid) {
		return adTempMapper.selectAdTempBoByPrimaryKey(adtAdid);
	}

	// 根据广告方案id查询广告
	public List<AdTemp> selectByAdSchemeId(String adSchemeId) {
		return adTempMapper.selectByAdSchemeId(adSchemeId);
	}

	// 根据广告方案id和广告类型查询广告
	public List<AdTemp> findAdTempByAdSchemeIdAndAdType(ADSchemeCommand adSchemeCommand) {
		return adTempMapper.selectByAdSchemeIdAndAdType(adSchemeCommand);
	}

	// 根据广告方案id和广告类型查询广告
	public List<AdCurrent> findAdCurrentByAdSchemeIdAndAdType(ADSchemeCommand adSchemeCommand) {
		return adCurrentMapper.selectByAdSchemeIdAndAdType(adSchemeCommand);
	}

	// adCurrent不存在，审核通过AdTemp
	@Transactional(rollbackFor = Exception.class)
	public void checkPassFirst(AdTemp adTemp, AdCurrent adCurrent) {
		adTempMapper.updateByPrimaryKeySelective(adTemp);
		adCurrentMapper.insertSelective(adCurrent);
	}

	// adCurrent已存在，审核通过AdTemp
	@Transactional(rollbackFor = Exception.class)
	public void checkPass(AdTemp adTemp, AdCurrent adCurrent) {
		adTempMapper.updateByPrimaryKeySelective(adTemp);
		adCurrentMapper.updateByPrimaryKeySelective(adCurrent);
	}

	// 根据广告方案id、组织id和广告类型
	public List<AdTemp> selectBySchemeIdAndTypeAndOrgID(ADCommand adCommand) {
		return adTempMapper.selectBySchemeIdAndTypeAndOrgID(adCommand);
	}

	public void setTempVideoFlagToZ(AdTemp adTemp) {
		adTempMapper.setVideoFlagToZ(adTemp);
	}

	public void setTempVideoFlagToO(AdTemp adTemp) {
		adTempMapper.setVideoFlagToO(adTemp);
	}

	public void setCurrentVideoFlagToZ(AdTemp adTemp) {
		adCurrentMapper.setVideoFlagToZ(adTemp);
	}

	public void setCurrentVideoFlagToO(AdTemp adTemp) {
		adCurrentMapper.setVideoFlagToO(adTemp);
	}

	// 根据广告方案id、组织id、广告类型、广告Index插入和更新广告
	@Transactional(rollbackFor = Exception.class)
	public int insertAdTempBySGTI(List<AdTemp> adTemps, List<AdSchemeFile> adSchemeFiles){
//		adTempMapper.updateAdTempVideoFlag(adTemps.get(0));
		adSchemeFileMapper.insertAdSchemeFileBatch(adSchemeFiles);
		return adTempMapper.insertAdTempBySGTI(adTemps);
		
	}

	// 根据广告方案id、组织id、广告类型、广告Index插入和更新广告
	@Transactional(rollbackFor = Exception.class)
	public void insertAdBySGTI(List<AdTemp> adTemps, List<AdCurrent> adCurrents, List<AdHistory> adHistories, List<AdSchemeFile> adSchemeFiles){
		if (adSchemeFiles.size()>0) {
			adSchemeFileMapper.insertAdSchemeFileBatch(adSchemeFiles);
		}
		adTempMapper.insertAdTempBySGTI(adTemps);
		adCurrentMapper.insertAdCurrentBySGTI(adCurrents);
		adHistoryMapper.insertAdHistoryBySGTI(adHistories);
	}
	
	
	public void insertAdSchemeFileBatch(List<AdSchemeFile> adSchemeFiles){
		adSchemeFileMapper.insertAdSchemeFileBatch(adSchemeFiles);
	}

	// 根据广告方案id、组织id、广告类型查询历史广告时间节点
	public List<Date> selectAdHistoryUpdateTimeBySGT(AdHistory adHistory) {
		return adHistoryMapper.selectUpdateTimeBySGT(adHistory);
	}

	// 根据广告方案id、组织id、广告类型、更新时间查询历史广告时间域
	public List<AdHistory> selectTimeDomainBySGTU(AdHistory adHistory) {
		return adHistoryMapper.selectTimeDomainBySGTU(adHistory);
	}

	// 根据广告方案id、组织id、广告类型、开始时间、结束时间查询广告历史
	public List<AdHistory> selectAdHistoyBySGTSE(AdHistory adHistory) {
		return adHistoryMapper.selectAdHistoyBySGTSE(adHistory);
	}

	// 根据广告方案id, 组织id, 广告类型, 广告Index查询广告
	public AdTemp selectAdTempBySGTI(AdTemp adTemp) {
		return adTempMapper.selectAdTempBySGTI(adTemp);
	}

	// 根据组织id、方案id和广告类型获取广告
	public AdCurrent selectAdcByGSTI(AdCurrent adCommand) {
		return adCurrentMapper.selectAdcByGSTI(adCommand);
	}

	// 根据组织id、方案id和广告类型获取最新的广告
	public List<AdCurrent> selectMergeAdcByGST(AdCurrent adCommand) {
		return adCurrentMapper.selectMergeAdcByGST(adCommand);
	}

	// 根据最新的广告获取对应的整套广告
	public List<AdCurrent> selectAdcListByGST(AdCurrent adCurrent) {
		return adCurrentMapper.selectAdcListByGST(adCurrent);
	}

	// 根据组织id、方案id和广告类型获取最新的广告
	public AdTemp selectNewestAdtByGST(AdTemp adCommand) {
		return adTempMapper.selectNewestAdtByGST(adCommand);
	}

	// 根据最新的广告获取对应的整套广告
	public List<AdTemp> selectAdtListByGST(AdTemp adTemp) {
		return adTempMapper.selectAdtListByGST(adTemp);
	}

	public AdReceiver selectAdReceiverByDevidAndType(AdReceiver record) {
		return adReceiverMapper.selectAdReceiverByDevidAndType(record);
	}

	@Transactional(rollbackFor = Exception.class)
	public int addAdReceiverSelective(AdReceiver record) {
		return adReceiverMapper.insertSelective(record);
	}

	// 根据组织id和广告类型统计更新情况
	public List<AdUpdateRate> selectUpdateRateByOrgAndType(ADSchemeCommand adSchemeCommand){
		return adReceiverMapper.selectUpdateRateByOrgAndType(adSchemeCommand);
	}

	/*// 把组织下的所有设备的广告更新状态变为0
	@Transactional(rollbackFor = Exception.class)
	public int updateAdReceiver(AdTemp adTemp) {
		return adReceiverMapper.updateAdReceiver(adTemp);
	}
	// 把组织下的所有设备的屏保广告更新状态变为0
	public int updateSaverAdReceiver(AdTemp adTemp) {
		return adReceiverMapper.updateSaverAdReceiver(adTemp);
	}*/
	// 把组织下的所有设备的广告更新状态变为0
	public int updateAdReceiver(AdReceiverCommand adReceiver) {
		return adReceiverMapper.updateAdReceiver(adReceiver);
	}
	

	@Transactional(rollbackFor = Exception.class)
	public int updateAdReceiverByDevice(AdReceiver adReceiver) {
		return adReceiverMapper.updateAdReceiverByDevice(adReceiver);
	}

	public List<AdTemp> selectMergeAdtByGST(AdTemp adTemp) {
		return adTempMapper.selectMergeAdtByGST(adTemp);
	}

	// 查找开始广告历史记录节点
	public List<Date> selectAdBootHistoryUpdateTimeBySGT(AdHistory adHistory) {
		return adHistoryMapper.selectAdBootHistoryUpdateTimeBySGT(adHistory);
	}

	// 查找开机广告历史记录
	public AdHistory selectAdBootHistoy(AdHistory adHistory) {
		return adHistoryMapper.selectAdBootHistoy(adHistory);
	}

	// 把所有该组织下的子组织图片地址清空
	@Transactional(rollbackFor = Exception.class)
	public void updateVideoCurrent(AdTemp adTemp) {
		adTempMapper.updateVideoTemp(adTemp);
		adCurrentMapper.updateVideoCurrent(adTemp);
	}

	// 把所有该组织下的子组织图片地址清空
	public void updateVideoTemp(AdTemp adTemp) {
		adTempMapper.updateVideoTemp(adTemp);
	}

	// 把所有该组织下的子组织图片地址清空
	@Transactional(rollbackFor = Exception.class)
	public void updateVideoCurrentBoot(AdTemp adTemp) {
		adTempMapper.updateVideoTempBoot(adTemp);
		adCurrentMapper.updateVideoCurrentBoot(adTemp);
	}

	// 把所有该组织下的子组织图片地址清空
	public void updateVideoTempBoot(AdTemp adTemp) {
		adTempMapper.updateVideoTempBoot(adTemp);
	}

	// 插入广告更新状态
	public void insertAdReceiverByDevice(AdReceiver adReceiver) {
		adReceiverMapper.insertAdReceiverByDevice(adReceiver);
	}

	// 查找该组织下设备广告更新个数
	public int selectCountUpdated(AdReceiverCommand adReceiver) {
		return adReceiverMapper.selectCountUpdated(adReceiver);
	}

	// 查找文件id广告历史记录
	public AdHistory selectAdHistoyByFileID(String fileId) {
		return adHistoryMapper.selectAdHistoyByFileID(fileId);
	}

	// 查找最新的更新时间
	public Date selectNewsAdcUpdateTime(ADSchemeCommand adSchemeCommand) {
		return adCurrentMapper.selectNewsAdcUpdateTime(adSchemeCommand);
	}

	// 把所有该组织下的子组织视频地址清空
	public void updatePicCurrentBoot(AdTemp adTemp) {
		adTempMapper.updatePicTempBoot(adTemp);
		adCurrentMapper.updatePicCurrentBoot(adTemp);
	}
	public void updatePicTempBoot(AdTemp adTemp) {
		adTempMapper.updatePicTempBoot(adTemp);
	}
	public void updatePicTemp(AdTemp adTemp) {
		adTempMapper.updatePicTemp(adTemp);
	}
	public void updatePicCurrent(AdTemp adTemp) {
		adTempMapper.updatePicTemp(adTemp);
		adCurrentMapper.updatePicCurrent(adTemp);
	}

	// 获取时间节点下最新的视频上传时间
	public Date selectNewestVideoUpdateTime(AdHistory adHistory) {
		return adHistoryMapper.selectNewestVideoUpdateTime(adHistory);
	}

	// 获取开机广告时间节点下最新的视频上传时间
	public Date selectNewestVideoUpdateTimeBoot(AdHistory adHistory) {
		return adHistoryMapper.selectNewestVideoUpdateTimeBoot(adHistory);
	}

	// 获取所有当前组织和其子组织下该广告方案的历史
	public List<AdHistory> selectAllHistoryByGTSI(ADSchemeCommand schemeCommand) {
		return adHistoryMapper.selectAllHistoryByGTSI(schemeCommand);
	}

	// 查询开机广告第九和第十的历史
	public List<AdHistory> selectAllHistoryByGTSILasttwo(ADSchemeCommand adCommand) {
		return adHistoryMapper.selectAllHistoryByGTSILasttwo(adCommand);
	}

	// 根据组织id查询官网信息
	public List<AdTemp> selectOfficAdtByGST(String groupId) {
		return adTempMapper.selectOfficAdtByGST(groupId);
	}

}
