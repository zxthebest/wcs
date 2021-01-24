package com.work.wcs.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 
 * <P>
 * 登陆拦截器，确保所有操作需要登陆方可执行
 * </P>
 * 
 * @author 周欣(13667212859)
 * @date 2017年5月11日 下午4:25:37
 */
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 1.请求到登陆页面放行

		if (request.getServletPath().startsWith("/webpageLogin.form")) {
			return true;
		}
		// 2.用户已经登陆放行
		if (null != request.getSession().getAttribute("userID")) {

			return true;
		}
		// 3.意见反馈和获取最新软件信息接口访问放行
		if (request.getServletPath().startsWith("/suggestion.form")
				|| request.getServletPath().startsWith("/getNew.form")) {
			return true;
		}
		// 4.接口放行：根据设备编号和广告类型获取广告信息访问
		if (request.getServletPath().startsWith("/getAdCurrentByDevice.form")) {
			return true;
		}

		// 5.设备校验接口放行
		if (-1 != request.getServletPath().indexOf("/checkDevice.form")) {
			return true;
		}
		// 6.设备信息更新接口放行
		if (-1 != request.getServletPath().indexOf("/updateDevice.form")) {
			return true;
		}
		// 7.设备活跃信息更新接口放行
		if (-1 != request.getServletPath().indexOf("/updateDynamic.form")) {
			return true;
		}

		// 8.接口放行：用于向设备提供广告方案信息
		if (request.getServletPath().startsWith("/getAdScheme.form")) {
			return true;
		}

		// 9.接口放行：为设备绑定广告商
		if (request.getServletPath().startsWith("/deviceBandOrgAd.form")) {
			return true;
		}
		// 10.接口放行：根据设备编号获取广告文件
		if (request.getServletPath().startsWith("/getAdFileByDevice.form")) {
			return true;
		}

		// 11.专为测试使用
		if (-1 != request.getServletPath().indexOf("/test")) {
			return true;
		}
		// 12.接口放行：用于向设备提供广告商组织机构信息
		if (request.getServletPath().startsWith("/getOrgAd.form")) {
			return true;
		}

		// 13.接口放行：更新设备使用信息
		if (request.getServletPath().startsWith("/devUseInfo.form")) {
			return true;
		}

		// 14.接口放行：访问老系统数据库数据
		if (request.getServletPath().startsWith("/shareShow.form")) {
			return true;
		}

		// 15.短信放行
		if (request.getServletPath().startsWith("/SMS_")) {
			return true;
		}
		// 16.用户注册放行
		if (request.getServletPath().startsWith("/register.form")) {
			return true;
		}

		// 17.接口：根据传递的设备唯一标识符获取关联公司及其所有子公司
		if (request.getServletPath().startsWith("/getOrgAndDept.form")) {
			return true;
		}
		// 18.用户更换绑定设备放行
		if (request.getServletPath().startsWith("/bindDev.form")) {
			return true;
		}
		// 19.文件上传相关接口
		if (request.getServletPath().startsWith("/upload")) {
			return true;
		}
		// 20.接口放行：接收并更新文件使用情况
		if (request.getServletPath().startsWith("/checkFileOpen.form")) {
			return true;
		}
		// 21.接口放行：接收设备软件使用信息
		if (request.getServletPath().startsWith("/devAppUse.form")) {
			return true;
		}
		// 22.接口放行：接收盒子安装软件版本
		if (request.getServletPath().startsWith("/devAppVer.form")) {
			return true;
		}
		// 23.接口放行：接收学习文件播放记录
		if (request.getServletPath().startsWith("/studyFilePlayRecord.form")) {
			return true;
		}
		// 24.接口放行：接收客户拜访文件播放记录
		if (request.getServletPath().startsWith("/visitFilePlayRecord.form")) {
			return true;
		}
		// 25.接口放行：接收客户拜访记录
		if (request.getServletPath().startsWith("/visitRecord.form")) {
			return true;
		}
		// 26.临时测试放行：查询晨会音频文件
		if (request.getServletPath().startsWith("/selectCheckMorning.form")) {
			return true;
		}
		// 27.接口放行：解除设备绑定
		if (request.getServletPath().startsWith("/relieveDevBind.form")) {
			return true;
		}

		// 28.放行：判断session失效
		if (request.getServletPath().startsWith("/sessionFail.form")) {
			return true;
		}
		// 29.移动端接口放行
		if (request.getServletPath().startsWith("/changePhone.form")
				|| request.getServletPath().startsWith("/selectDevByPhone.form")
				|| request.getServletPath().startsWith("/mobileLogin.form")
				|| request.getServletPath().startsWith("/updateUserRigsterInfo.form")
				|| request.getServletPath().startsWith("/devAdPlayRecord.form")
				|| request.getServletPath().startsWith("/getOrgAndDeptByPhone.form")
				|| request.getServletPath().startsWith("/SMS_VerifiCode.form")
				|| request.getServletPath().startsWith("/uploadCheckMorningVoice.form")
				|| request.getServletPath().startsWith("/listVisitRecord.form")
				|| request.getServletPath().startsWith("/addTeam.form")
				|| request.getServletPath().startsWith("/delTeam.form")
				|| request.getServletPath().startsWith("/joinTeam.form")
				|| request.getServletPath().startsWith("/leftTeam.form")
				|| request.getServletPath().startsWith("/teamMember.form")
				|| request.getServletPath().startsWith("/getRegisterUserInfo.form")
				|| request.getServletPath().startsWith("/getRegisterUserInfo1.form")
				|| request.getServletPath().startsWith("/listCheckMorning.form")
				|| request.getServletPath().startsWith("/uploadVisitRecord.form")
				|| request.getServletPath().startsWith("/addCheckMorningVoice.form")) {
			return true;
		}
		// 30.移动端接口放行
		if (request.getServletPath().startsWith("/dev")
				|| request.getServletPath().startsWith("/getAdCurrentForDevice.form")
				|| request.getServletPath().startsWith("/getAdCurrentForShare.form")
				|| request.getServletPath().startsWith("/shareGetStudyAndVisit.form")
				|| request.getServletPath().startsWith("/shareVisitFileReceive.form")
				|| request.getServletPath().startsWith("/setAdReceiverByDevice.form")
				|| request.getServletPath().startsWith("/verifiCode.form")
				|| request.getServletPath().startsWith("/updateStudyFileReceive.form")
				|| request.getServletPath().startsWith("/updateVisitFileReceive.form")) {
			return true;
		}
		// 31.广告管理有单独的超时返回结果
		if (request.getServletPath().startsWith("/webpageGetAdHistory.form")
				|| request.getServletPath().startsWith("/webpageGetAdTemp.form")
				|| request.getServletPath().startsWith("/webpageGetUpdateRate.form")
				|| request.getServletPath().startsWith("/webpageSaveAdtemp.form")
				|| request.getServletPath().startsWith("/webpageSaveVideo.form")) {
			return true;
		}
		// response.sendRedirect("login.jsp");
		// 32.意见反馈
		if (request.getServletPath().startsWith("/addSugAdvice.form")) {
			return true;
		}
		// 33.类型查询
		if (request.getServletPath().startsWith("/findAllByType.form")) {
			return true;
		}

		response.sendRedirect("sessionFail.form");
		return false;
	}
}
