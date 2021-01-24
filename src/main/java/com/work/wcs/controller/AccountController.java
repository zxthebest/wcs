package com.work.wcs.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.adplatform.bo.SoftUpdateBo;
import com.adplatform.bo.UserSystemManufactureBo;
import com.adplatform.command.DevUseDimSearchCommand;
import com.adplatform.command.LoginCommand;
import com.adplatform.command.PlatformUserCommand;
import com.adplatform.model.DevBaseInfo;
import com.adplatform.model.GroupInfo;
import com.adplatform.model.GroupInfoApp;
import com.adplatform.model.TypeBaseInfo;
import com.adplatform.model.UserAuthority;
import com.adplatform.model.UserRegister;
import com.adplatform.model.UserRole;
import com.adplatform.model.UserSystem;
import com.adplatform.model.UserSystemLoginRecord;
import com.adplatform.result.UserRegisterManageResult;
import com.adplatform.result.UserRoleAuthorityResult;
import com.adplatform.result.UserSystemResult;
import com.adplatform.service.AccountService;
import com.adplatform.service.AdService;
import com.adplatform.service.DevBaseInfoService;
import com.adplatform.service.GroupInfoService;
import com.adplatform.service.RoleAuthorityService;
import com.adplatform.service.SoftUpdateService;
import com.adplatform.service.TypeService;
import com.adplatform.service.UserRegisterService;
import com.adplatform.service.UserSystemLoginRecordService;
import com.adplatform.util.AdvertiserUserHelper;
import com.adplatform.util.AlyunOss;
import com.adplatform.util.CommonResult;
import com.adplatform.util.CommonUtil;
import com.adplatform.util.Constant;
import com.adplatform.util.DesUtils;
import com.adplatform.util.Role;
import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSS;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * 
 * <P>
 * 平台用户Controller
 * </P>
 * 
 * @author 周欣(13667212859)
 * @date 2017年5月2日 下午2:37:41
 */
@Controller
@RequestMapping("/")
public class AccountController {
	@Autowired
	private AccountService accountService;
	@Autowired
	private SoftUpdateService softUpdateService;
	@Autowired
	private UserRegisterService userRegisterService;
	@Autowired
	private AdService adService;
	@Autowired
	private DevBaseInfoService devBaseInfoService;
	@Autowired
	private GroupInfoService groupInfoService;
	@Autowired
	private TypeService typeService;
	@Autowired
	private RoleAuthorityService roleAuthorityService;
	@Autowired
	private UserSystemLoginRecordService userSystemLoginRecordService;
	private Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * 
	 * <p>
	 * 账号登陆
	 * </p>
	 * 
	 * @param request
	 * @param loginCommand
	 * @return
	 * @author 周欣(13667212859)
	 * @date 2017年5月3日 下午3:09:02
	 */
	@RequestMapping(value = "/login")
	public ModelAndView login(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			LoginCommand loginCommand) {
		// 新建返回视图
		ModelAndView view = new ModelAndView("/");
		// if (null != session.getAttribute("relogin")) {
		// session.setAttribute("relogin", null);
		// return view;
		// }
		// csrf攻击校验
		// String token = (String) session.getAttribute("csrftoken");
		// System.out.println("csrftoken=" + csrftoken);
		// System.out.println("token=" + token);
		// if (null == csrftoken || null == token || !csrftoken.equals(token)) {
		//
		// return view;
		// }
		// 密码加密
		LoginCommand loginCommand2 = new LoginCommand();
		loginCommand2.setUsername(loginCommand.getUsername());
		loginCommand2.setPassword(loginCommand.getPassword());
		try {
			DesUtils des = new DesUtils("qmkj");
			String password = des.encrypt(loginCommand.getPassword());
			loginCommand.setPassword(password);

		} catch (Exception e1) {
			log.error("密钥加密失败", e1);
		}
		UserSystem userSystem = new UserSystem();
		try {
			userSystem = accountService.login(loginCommand);
		} catch (Exception e) {// 数据库连接断开则尝试再次连接
			e.printStackTrace();
			try {
				userSystem = accountService.login(loginCommand);
			} catch (Exception e1) {// 再次连接失败则返回登录界面
				request.setAttribute("error", "服务忙，请稍后再试！");
				return view;
			}
		}

		// System.out.println(request.getServletPath());
		// 账号验证成功进入后续操作界面
		if (null != userSystem) {
			session.setAttribute("csrftoken", null);
			token(session);
			session.setAttribute("userID", userSystem.getUsUserid());
			request.setAttribute("error", "");
			// // 获取平台基本信息
			// int operatorNum = accountService.countOperatorNum();
			// int adToBeCheck = adService.countAdToBeCheck();
			// int orgNum = groupInfoService.countOrgNum();
			// int deviceNum = devBaseInfoService.countDeviceNum();
			//
			// List<SoftUpdateBo> list =
			// softUpdateService.findAllSoftUpdateBo();
			//
			// switch (userSystem.getUsType()) {
			// case Role.Super:
			// view.setViewName("/WEB-INF/homepage/qmkj.jsp");
			// break;
			// case Role.Manger:
			// view.setViewName("/WEB-INF/homepage/qmkj.jsp");
			// break;
			// case Role.Operator:
			// view.setViewName("/WEB-INF/homepage/qmkj.jsp");
			// break;
			// case Role.ADGuest:
			// view.setViewName("/WEB-INF/ad/adDesign.jsp");
			// String ids = userSystem.getUsBindgroupid();
			// String userID = userSystem.getUsUserid();
			// // view.addObject("ids", ids);
			// // view.addObject("userID", userID);
			// break;
			// default:
			// break;
			// }
			// if (Role.ADGuest != userSystem.getUsType()) {
			// view.addObject("operatorNum", operatorNum);
			// view.addObject("adToBeCheck", adToBeCheck);
			// view.addObject("deviceNum", deviceNum);
			// view.addObject("orgNum", orgNum);
			// view.addObject("list", list);
			// }
			// view.setViewName("/index.html");
			view.setViewName("homepage.form");
			return view;
		} else {
			UserSystem us = accountService.selectByLoginName(loginCommand.getUsername());
			if (null != us && 1 == us.getUsForbidden()) {
				request.setAttribute("error", "账号已禁用！");
				return view;
			}
			request.setAttribute("error", "账号或密码错误！");
			return view;
		}
	}

	/**
	 * <p>
	 * 判断是否为老系统用户
	 * </p>
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @author 熊荡(13125181082)
	 * @date 2017年10月17日 下午4:01:43
	 */
	@RequestMapping("/shareShow")
	@ResponseBody
	public String shareShow(String username, String password) {
		AdvertiserUserHelper userHelper = new AdvertiserUserHelper();
		boolean bool = userHelper.login(username, password);
		if (bool) {
			return "isOld";
		} else {
			return "notOld";
		}
	}

	/**
	 * 
	 * <p>
	 * 返回主页
	 * </p>
	 * 
	 * @param session
	 * @return
	 * @author 周欣(13667212859)
	 * @date 2017年5月16日 上午10:22:24
	 */
	@RequestMapping("/homepage")
	public ModelAndView back(HttpSession session) {
		// 新建返回视图
		ModelAndView view = new ModelAndView();
		// 获取用户id
		String id = (String) session.getAttribute("userID");
		UserSystem userSystem = accountService.selectByPrimaryKey(id);
		// 获取平台基本信息
		int operatorNum = accountService.countOperatorNum();
		int adToBeCheck = adService.countAdToBeCheck();
		int orgNum = groupInfoService.countOrgNum();
		int deviceNum = devBaseInfoService.countDeviceNum();
		List<SoftUpdateBo> list = softUpdateService.findAllSoftUpdateBo();
		switch (userSystem.getUsType()) {
		case Role.Super:
			view.setViewName("/WEB-INF/homepage/qmkj.jsp");
			break;
		case Role.Manger:
			view.setViewName("/WEB-INF/homepage/qmkj.jsp");
			break;
		case Role.Operator:
			view.setViewName("/WEB-INF/homepage/qmkj.jsp");
			break;
		case Role.ADGuest:
			view.setViewName("/index.html");
			break;

		default:
			break;
		}
		if (Role.ADGuest != userSystem.getUsType()) {
			view.addObject("operatorNum", operatorNum);
			view.addObject("adToBeCheck", adToBeCheck);
			view.addObject("deviceNum", deviceNum);
			view.addObject("orgNum", orgNum);
			view.addObject("list", list);
		}
		return view;

	}

	/**
	 * 
	 * <p>
	 * 修改管理员信息
	 * </p>
	 * 
	 * @return
	 * @author 周欣(13667212859)
	 * @date 2017年5月17日 上午11:47:09
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/editManger")
	public ModelAndView editManger(Integer pageindex) {
		ModelAndView view = new ModelAndView("/WEB-INF/account/editManger.jsp");
		if (pageindex == null) {
			pageindex = 1;
		}

		// 启用分页
		PageHelper.startPage(pageindex, 10);
		// 排序,按照账号创建的先后顺序降序排列
		PageHelper.orderBy("US_CreateTime desc");
		List<UserSystem> list = accountService.selectByType(Role.Manger);
		DesUtils des;
		try {
			des = new DesUtils("qmkj");
			for (UserSystem userSystem : list) {
				userSystem.setUsPassword(des.decrypt(userSystem.getUsPassword()));
			}
		} catch (Exception e) {
			log.error("密钥解密失败", e);
		}
		view.addObject("list", list);
		// 获取总页数，并将总页数传递给视图
		// 分页时，实际返回的结果list类型是Page<E>，如果想取出分页信息，需要强制转换为Page<E>，
		// 或者使用PageInfo类（PageInfo page = new PageInfo(list);）
		int pagecount = ((Page) list).getPages();
		view.addObject("pagecount", pagecount);

		// 将当前页码传递给视图和session
		pageindex = ((Page) list).getPageNum();
		view.addObject("pageindex", pageindex);
		return view;
	}

	/**
	 * 
	 * <p>
	 * 保存账号
	 * </p>
	 * 
	 * @param request
	 * @param userSystem
	 * @param csrftoken
	 * @return
	 * @author 周欣(13667212859)
	 * @date 2017年8月3日 上午11:48:05
	 */
	@RequestMapping(value = "/saveUserSystem", method = RequestMethod.POST)
	@ResponseBody
	public String saveUserSystem(HttpServletRequest request, UserSystem userSystem, String csrftoken) {
		// csrf攻击校验
		String token = (String) request.getSession().getAttribute("csrftoken");
		// System.out.println("csrftoken=" + csrftoken);
		// System.out.println("token=" + token);
		if (null == csrftoken || null == token || !csrftoken.equals(token)) {
			log.error("非法操作：保存账号，可能是csrf攻击");
			return "保存失败";
		}
		// 密码加密
		String password = userSystem.getUsPassword();
		String pwd = "";
		try {
			DesUtils des = new DesUtils("qmkj");
			pwd = des.encrypt(password);

		} catch (Exception e1) {
			log.error("密钥加密失败", e1);
		}
		if ("".equals(pwd)) {
			return "密码修改失败";
		} else {
			userSystem.setUsPassword(pwd);
		}
		// 获取权限值
		String[] authority = request.getParameterValues("authority");
		List<UserAuthority> list = new ArrayList<>();
		// 判断是新增还是修改
		if (null == userSystem.getUsUserid()) {// 新增
			// 获取主键
			String usUserid = UUID.randomUUID().toString();
			if (!"".equals(userSystem.getUsLoginname()) && !"".equals(userSystem.getUsPassword())
					&& !"".equals(userSystem.getUsUsername())) {// 账号信息不能为空

				userSystem.setUsUserid(usUserid);
				if (userSystem.getUsType() == Role.Manger || userSystem.getUsType() == Role.Operator) {
					GroupInfo org = groupInfoService.findByGroupName("武汉启目科技有限公司");
					userSystem.setUsBindgroupid(org.getGiGroupid());
				}
				// 删除标志置为0
				userSystem.setUsDel(0);
			} else {
				return "请维护账号信息！";
			}
			// 为list集合赋值
			if (null != authority) {
				for (int j = 0; j < authority.length; j++) {
					UserAuthority ua = new UserAuthority();
					ua.setUuaUaid(UUID.randomUUID().toString());
					ua.setUuaAuthorityvalue(Integer.valueOf(authority[j]));
					ua.setUuaUserid(usUserid);
					ua.setUuaDel(0);
					list.add(ua);
				}
			} else {
				return "请分配权限！";
			}

			try {
				accountService.insertSelective(userSystem, list);

			} catch (DuplicateKeyException e) {// 违反数据完整性约束
				log.error("新增用户信息出错：用户帐号已存在，请勿重复添加！", e);
				return "帐号已存在，请勿重复添加！";
			} catch (Exception e) {
				log.error("新增用户信息出错:原因不明", e);
				return "新增失败";
			}
			return "账号新增成功！";
		} else {// 修改
			// 为list集合赋值,权限对象不能为空，且用户类型不是管理员。
			if (null != authority && userSystem.getUsType() != 2) {
				for (int j = 0; j < authority.length; j++) {
					UserAuthority ua = new UserAuthority();
					ua.setUuaUaid(UUID.randomUUID().toString());
					ua.setUuaAuthorityvalue(Integer.valueOf(authority[j]));
					ua.setUuaUserid(userSystem.getUsUserid());
					ua.setUuaDel(0);
					list.add(ua);
				}
			} else if (userSystem.getUsType() == 2) {

				try {// 管理员只更新账号信息，权限默认不用更新
					accountService.updateByPrimaryKeySelective(userSystem);
				} catch (Exception e) {
					log.error("修改用户信息出错:原因不明", e);
					return "修改失败";
				}
				return "账号修改成功！";
			} else {
				return "请分配权限！";
			}
			try {
				accountService.editUserSystem(userSystem, list);
			} catch (Exception e) {
				log.error("修改用户信息出错:原因不明", e);
				return "修改失败";
			}
			return "账号修改成功！";
		}
	}

	/**
	 * <p>
	 * 生产商修改界面
	 * </p>
	 * 
	 * @param session
	 * @return
	 * @author 熊荡(13125181082)
	 * @date 2017年5月16日 下午3:19:59
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/editManufacture")
	public ModelAndView editManufacture(HttpSession session, Integer pageindex) {
		// 新建返回视图
		ModelAndView view = new ModelAndView("/WEB-INF/account/editManufacture.jsp");
		if (pageindex == null) {
			pageindex = 1;
		}
		// 启用分页
		PageHelper.startPage(pageindex, 10);
		// 排序,按照账号创建的先后顺序降序排列
		// PageHelper.orderBy("US_CreateTime desc");
		List<UserSystemManufactureBo> list = accountService.selectManufacture();
		DesUtils des;
		try {
			des = new DesUtils("qmkj");
			for (UserSystemManufactureBo usb : list) {
				usb.setUsPassword(des.decrypt(usb.getUsPassword()));
			}
		} catch (Exception e) {
			log.error("密钥解密失败", e);
		}
		view.addObject("list", list);

		// 获取总页数，并将总页数传递给视图
		// 分页时，实际返回的结果list类型是Page<E>，如果想取出分页信息，需要强制转换为Page<E>，
		// 或者使用PageInfo类（PageInfo page = new PageInfo(list);）
		int pagecount = ((Page) list).getPages();
		view.addObject("pagecount", pagecount);

		// 将当前页码传递给视图和session
		pageindex = ((Page) list).getPageNum();
		view.addObject("pageindex", pageindex);
		// 传递生产商组织机构信息
		List<GroupInfo> orgList = groupInfoService.findOrgManufacture();
		view.addObject("orgList", orgList);
		// 传递设备类型信息
		List<TypeBaseInfo> devTypeList = typeService.findAllByType("devType");
		view.addObject("devTypeList", devTypeList);
		return view;
	}

	/**
	 * 
	 * <p>
	 * 保存生产商帐号
	 * </p>
	 * 
	 * @param session
	 * @param userSystem
	 * @param csrftoken
	 * @return
	 * @author 周欣(13667212859)
	 * @date 2017年8月3日 上午11:50:55
	 */
	@RequestMapping(value = "/saveManufacture", method = RequestMethod.POST)
	@ResponseBody
	public String saveManufacture(HttpSession session, UserSystem userSystem, String csrftoken) {
		// csrf攻击校验
		String token = (String) session.getAttribute("csrftoken");
		// System.out.println("csrftoken=" + csrftoken);
		// System.out.println("token=" + token);
		if (null == csrftoken || null == token || !csrftoken.equals(token)) {
			log.error("非法操作：保存生产商帐号，可能是csrf攻击");
			return "保存失败";
		}
		// 密码加密
		String password = userSystem.getUsPassword();
		String pwd = "";
		try {
			DesUtils des = new DesUtils("qmkj");
			pwd = des.encrypt(password);

		} catch (Exception e1) {
			log.error("密钥加密失败", e1);
		}
		if ("".equals(pwd)) {
			return "密码修改失败";
		} else {
			userSystem.setUsPassword(pwd);
		}
		// 判断是新增还是修改
		if (null == userSystem.getUsUserid()) {// 新增生产商帐号
			// 获取主键
			String usUserid = UUID.randomUUID().toString();
			userSystem.setUsUserid(usUserid);
			userSystem.setUsUsername("生产商");
			userSystem.setUsDel(0);
			String loginName = userSystem.getUsLoginname();
			UserSystem isExist = accountService.selectByLoginName(loginName);
			if (isExist == null) {
				accountService.insertSelective(userSystem);
				return "账号新增成功！";
			} else {
				return "帐号已存在，请勿重复添加！";
			}
		} else {// 修改生产商帐号
			try {
				accountService.updateByPrimaryKeySelective(userSystem);
				return "账号修改成功！";
			} catch (Exception e) {
				log.error("修改用户信息出错", e);
				return "帐号信息修改失败";
			}
		}
	}

	/**
	 * 
	 * <p>
	 * 删除用户
	 * </p>
	 * 
	 * @param session
	 * @param usUserid
	 * @param csrftoken
	 * @return
	 * @author 周欣(13667212859)
	 * @date 2017年8月3日 上午10:56:03
	 */
	@RequestMapping(value = "/delUserSystem", method = RequestMethod.POST)
	@ResponseBody
	public String delUserSystem(HttpSession session, String usUserid, String csrftoken) {
		// csrf攻击校验
		String token = (String) session.getAttribute("csrftoken");
		// System.out.println("csrftoken=" + csrftoken);
		// System.out.println("token=" + token);
		if (null == csrftoken || null == token || !csrftoken.equals(token)) {
			log.error("非法操作：删除用户信息，可能是csrf攻击");
			return "删除失败";
		}
		UserSystem userSystem = accountService.selectByPrimaryKey(usUserid);
		if (null != userSystem) {
			userSystem.setUsDel(1);
			try {
				accountService.delUserSystem(userSystem);
			} catch (Exception e) {
				log.error("删除用户信息出错:accountService.delUserSystem(userSystem);", e);
				return "删除失败";
			}
			return "账号删除成功！";
		}
		return "账号不存在！";
	}

	/**
	 * 
	 * <p>
	 * token避免csrf攻击
	 * </p>
	 * 
	 * @param session
	 * @return
	 * @author 周欣(13667212859)
	 * @date 2017年8月2日 下午3:36:47
	 */
	@RequestMapping(value = "/csrftoken", method = RequestMethod.POST)
	@ResponseBody
	public String token(HttpSession session) {
		if (null != session.getAttribute("csrftoken")) {
			return (String) session.getAttribute("csrftoken");
		}
		session.setAttribute("csrftoken", UUID.randomUUID().toString());
		return (String) session.getAttribute("csrftoken");
	}

	/**
	 * <p>
	 * 页面接口:获取帐号所属组织的所有上级公司(包含本公司)
	 * </p>
	 * 
	 * @param session
	 * @param orgId
	 *            组织id
	 * @return
	 * @author 熊荡(13125181082)
	 * @date 2017年12月15日 下午3:32:19
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/webpageGetBelongOrgs")
	@ResponseBody
	public Object webpageGetBelongOrgs(String orgId, HttpSession session) {
		CommonResult cr = new CommonResult();
		try {
			if (null == orgId || "".equals(orgId) || "null".equals(orgId)) {
				String userId = (String) session.getAttribute("userID");
				UserSystem userSystem = accountService.selectByPrimaryKey(userId);
				orgId = userSystem.getUsBindgroupid();
			}
			List<GroupInfoApp> groupInfos = groupInfoService.selectParentOrgs(orgId);
			cr.setDatas(groupInfos);
			cr.setReturnCode(1);
			cr.setSTATUS(CommonResult.getSuccesscode());
			cr.setMessage("获取帐号所属组织的所有上级公司(包含本公司)成功");
			return cr;
		} catch (Exception e) {
			log.error("页面接口:获取帐号所属组织的所有上级公司(包含本公司)", e);
			cr.setReturnCode(-1);
			cr.setMessage("获取帐号所属组织的所有上级公司(包含本公司)失败");
			return cr;
		}
	}

	/**
	 * <p>
	 * 页面接口:获取本公司和其直属下级的帐号
	 * </p>
	 * 
	 * @param session
	 * @param orgId
	 *            组织id
	 * @return
	 * @author 熊荡(13125181082)
	 * @date 2017年12月16日 下午2:26:41
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/webpageGetAccountsToManage")
	@ResponseBody
	public Object webpageGetAccountsToManage(String orgId, String vague, Integer pageindex, HttpSession session) {
		CommonResult commonResult = new CommonResult();
		try {
			if (null == orgId || "".equals(orgId) || "null".equals(orgId)) {
				String userId = (String) session.getAttribute("userID");
				UserSystem userSystem = accountService.selectByPrimaryKey(userId);
				orgId = userSystem.getUsBindgroupid();
			}

			// 获取用户所在组织下所有直属分公司和部门信息(不包含更下级单位)
			// List<GroupInfo> listDown =
			// groupInfoService.selectDirectGroup(orgId);
			// AdEditOrgListResult ar = new AdEditOrgListResult();
			if (null == pageindex) {
				pageindex = 1;
			}
			List<UserSystemResult> userSystemResults = new ArrayList<UserSystemResult>();
			PageHelper.startPage(pageindex, 10);
			if (null == vague || "".equals(vague) || "null".equals(vague)) {
				userSystemResults = accountService.selectSubordinateUser(orgId);

			} else {
				DevUseDimSearchCommand userCommand = new DevUseDimSearchCommand();
				userCommand.setOrgId(orgId);
				userCommand.setInput("%" + vague + "%");
				userSystemResults = accountService.selectSubordinateUserDim(userCommand);
			}
			for (UserSystemResult result : userSystemResults) {
				String updateTime = result.getUsUpdateTime();
				if (null != updateTime) {
					int i = updateTime.indexOf(".");
					if (i != -1) {
						updateTime = updateTime.substring(0, i);
						result.setUsUpdateTime(updateTime);
					}
				}
			}
			int pagecount = 0;
			long count = 0;
			if (userSystemResults.size() > 0) {
				count = ((Page) userSystemResults).getTotal();
				pagecount = ((Page) userSystemResults).getPages();
				pageindex = ((Page) userSystemResults).getPageNum();
			}
			commonResult.setSTATUS(CommonResult.getSuccesscode());
			Map<String, Object> map = new HashMap<>();
			map.put("count", count);
			map.put("pagecount", pagecount);
			map.put("pageindex", pageindex);
			map.put("userSystemResults", userSystemResults);
			commonResult.setReturnCode(1);
			commonResult.setData(map);
			commonResult.setMessage("获取管理员帐号成功");
			return commonResult;
		} catch (Exception e) {
			log.error("页面接口:获取本公司和其直属下级的帐号", e);
			commonResult.setReturnCode(-1);
			commonResult.setMessage("获取管理员帐号失败");
			return commonResult;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/webpageGetAccountsToPlatform")
	@ResponseBody
	public Object webpageGetAccountsToPlatform(String orgId, String vague, Integer pageindex, HttpSession session) {
		CommonResult commonResult = new CommonResult();
		try {
			if (null == pageindex) {
				pageindex = 1;
			}
			List<UserSystemResult> userSystemResults = new ArrayList<UserSystemResult>();
			DevUseDimSearchCommand userCommand = new DevUseDimSearchCommand();
			userCommand.setOrgId(orgId);
			if (null == vague || "".equals(vague) || "null".equals(vague)) {
				vague = null;
			} else {
				userCommand.setInput("%" + vague + "%");
			}
			PageHelper.startPage(pageindex, 10);
			userSystemResults = accountService.selectParentGroupUserDim(userCommand);
			for (UserSystemResult result : userSystemResults) {
				String updateTime = result.getUsUpdateTime();
				if (null != updateTime) {
					int i = updateTime.indexOf(".");
					if (i != -1) {
						updateTime = updateTime.substring(0, i);
						result.setUsUpdateTime(updateTime);
					}
				}
				result.setGiSuperiorgroup(result.getGiGroupName());
				result.setUsSuperiorgroupid(result.getUsBindgroupid());
				result.setGiGroupName(null);
				result.setUsBindgroupid(null);
			}
			int pagecount = 0;
			long count = 0;
			if (userSystemResults.size() > 0) {
				count = ((Page) userSystemResults).getTotal();
				pagecount = ((Page) userSystemResults).getPages();
				pageindex = ((Page) userSystemResults).getPageNum();
			}
			commonResult.setSTATUS(CommonResult.getSuccesscode());
			Map<String, Object> map = new HashMap<>();
			map.put("count", count);
			map.put("pagecount", pagecount);
			map.put("pageindex", pageindex);
			map.put("userSystemResults", userSystemResults);
			commonResult.setReturnCode(1);
			commonResult.setData(map);
			commonResult.setMessage("获取管理员帐号成功");
			return commonResult;
		} catch (Exception e) {
			log.error("页面接口:获取本公司和其直属下级的帐号", e);
			commonResult.setReturnCode(-1);
			commonResult.setMessage("获取管理员帐号失败");
			return commonResult;
		}
	}

	/**
	 * <p>
	 * 页面接口:禁用/解禁帐号
	 * </p>
	 * 
	 * @param userId
	 *            修改人帐号id
	 * @param usUserid
	 *            要修改的帐号帐号id
	 * @param forbidden
	 *            禁用标记
	 * @return
	 * @author 熊荡(13125181082)
	 * @date 2017年12月16日 下午2:47:18
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/webpageForbiddenAccount")
	@ResponseBody
	public Object webpageForbiddenAccount(String userId, String usUserid, Integer forbidden, HttpSession session) {
		CommonResult cr = new CommonResult();
		try {
			if (null == userId || "".equals(userId) || "null".equals(userId)) {
				userId = (String) session.getAttribute("userID");
			}
			UserSystem userSystem = new UserSystem();
			userSystem.setUsUserid(usUserid);
			userSystem.setUsForbidden(forbidden);
			userSystem.setUsEditUserId(userId);
			int i = accountService.updateByPrimaryKeySelective(userSystem);
			System.out.println(i);
			cr.setSTATUS(CommonResult.getSuccesscode());
			cr.setReturnCode(1);
			cr.setMessage("禁用/解禁管理员帐号成功");
			return cr;
		} catch (Exception e) {
			log.error("管理员帐号禁用", e);
			cr.setReturnCode(-1);
			cr.setMessage("禁用/解禁管理员帐号失败");
			return cr;
		}
	}

	/**
	 * <p>
	 * 页面接口:获取创建新帐号时的选择信息
	 * </p>
	 * 
	 * @param session
	 * @param orgId
	 *            组织id
	 * @return
	 * @author 熊荡(13125181082)
	 * @date 2017年12月16日 下午4:22:46
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/webpageGetRegisterSelect")
	@ResponseBody
	public Object webpageGetRegisterSelect(String orgId, HttpSession session) {
		CommonResult commonResult = new CommonResult();
		try {
			if (null == orgId || "".equals(orgId) || "null".equals(orgId)) {
				String userId = (String) session.getAttribute("userID");
				UserSystem userSystem = accountService.selectByPrimaryKey(userId);
				orgId = userSystem.getUsBindgroupid();
			}
			GroupInfo belongGroup = groupInfoService.selectByPrimaryKey(orgId);
			List<GroupInfo> subordinateGroup = new ArrayList<GroupInfo>();
			int select = 0; // 可选
			if (belongGroup.getGiType() == 32) {
				select = 1; // 不可选
				subordinateGroup.add(belongGroup);
				belongGroup = groupInfoService.selectByPrimaryKey(belongGroup.getGiParentgroupid());
			} else {
				subordinateGroup = groupInfoService.selectDirectGroupToSelect(orgId);
			}
			List<UserRole> userRoles = roleAuthorityService.selectListUserRoleAvailable(orgId);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("belongGroup", belongGroup);
			map.put("select", select);
			map.put("subordinateGroup", subordinateGroup);
			map.put("userRole", userRoles);
			commonResult.setReturnCode(1);
			commonResult.setSTATUS(CommonResult.getSuccesscode());
			commonResult.setMessage("获取创建新帐号时的选择信息成功");
			commonResult.setData(map);
			return commonResult;
		} catch (Exception e) {
			// TODO: handle exception
			commonResult.setReturnCode(-1);
			commonResult.setMessage("获取创建新帐号时的选择信息失败");
			return commonResult;
		}
	}

	/**
	 * <p>
	 * 新增组织管理帐号
	 * </p>
	 * 
	 * @param userSystem
	 *            (usLoginname: 注册账户, usPassword: 密码, usRoleId: 角色id,
	 *            usSuperiorgroupid：所属组织) (usBindgroupid: 所属子组织, usUsername:
	 *            真实姓名, usPhone: 手机号, usJob: 职务)
	 * @param session
	 * @return
	 * @author 熊荡(13125181082)
	 * @date 2017年12月19日 上午10:43:07
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/webpageAddAccount")
	@ResponseBody
	public Object webpageAddAccount(String userId, UserSystem userSystem, HttpSession session) {
		if (null == userId || "".equals(userId) || "null".equals(userId)) {
			userId = (String) session.getAttribute("userID");
		}
		UserSystem userSystem2 = accountService.selectByPrimaryKey(userId);
		CommonResult cr = new CommonResult();
		try {
			String usLoginname = userSystem.getUsLoginname();
			UserSystem system = accountService.selectByLoginName(usLoginname);
			if (null != system) {
				cr.setSTATUS(CommonResult.getSuccesscode());
				cr.setReturnCode(2);
				cr.setMessage("帐号已存在");
				return cr;
			}
			String password = userSystem.getUsPassword();
			// TODO 加密密码
			DesUtils des = new DesUtils("qmkj");
			password = des.encrypt(password);
			userSystem.setUsPassword(password);
			userSystem.setUsUserid(UUID.randomUUID().toString());
			String bingGroupId = userSystem.getUsBindgroupid();
			if (userSystem.getUsForbidden() == null) {
				userSystem.setUsForbidden(0);
			}
			if (null == bingGroupId || "".equals(bingGroupId) || "null".equals(bingGroupId)) {
				bingGroupId = userSystem2.getUsBindgroupid();
				userSystem.setUsBindgroupid(bingGroupId);
			}
			GroupInfoApp superGroup = groupInfoService.selectParentOrg(bingGroupId);
			userSystem.setUsType(superGroup.getGiType());
			String usCreateUserId = userSystem.getUsCreateUserId();// 创建者id
			if (null == usCreateUserId || "".equals(usCreateUserId) || "null".equals(usCreateUserId)) {
				userSystem.setUsCreateUserId(userId);
			}
			String usEditUserId = userSystem.getUsEditUserId(); // 修改者id
			if (null == usEditUserId || "".equals(usEditUserId) || "null".equals(usEditUserId)) {
				userSystem.setUsEditUserId(userId);
			}
			accountService.insertSelective(userSystem);
			cr.setSTATUS(CommonResult.getSuccesscode());
			cr.setReturnCode(0);
			cr.setMessage("新建组织管理帐号成功");
			return cr;
		} catch (Exception e) {
			cr.setReturnCode(-1);
			cr.setMessage("新建组织管理帐号失败");
			return cr;
		}
	}

	/**
	 * <p>
	 * 修改组织管理帐号
	 * </p>
	 * 
	 * @param userSystem(usUserid:待修改帐号id,
	 *            usUsername:真实姓名, usPhone:手机号, usJob:职务, usBindgroupid:所属单位id,
	 *            usRoleId:角色id)
	 * @param session
	 * @return
	 * @author 熊荡(13125181082)
	 * @date 2017年12月19日 上午10:43:07
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/webpageAlterAccount")
	@ResponseBody
	public Object webpageAlterAccount(UserSystem userSystem, HttpSession session) {
		String errString = JSON.toJSONString(userSystem);
		CommonResult cr = new CommonResult();
		try {
			String userId = (String) session.getAttribute("userID");
			UserSystem system = accountService.selectByPrimaryKey(userId);
			String usEditUserId = userSystem.getUsEditUserId(); // 修改者id
			if (null == usEditUserId || "".equals(usEditUserId) || "null".equals(usEditUserId)) {
				userSystem.setUsEditUserId(userId);
			}
			String usBindGroupId = userSystem.getUsBindgroupid();
			if (null == usBindGroupId || "".equals(usBindGroupId) || "null".equals(usBindGroupId)) {
				userSystem.setUsBindgroupid(system.getUsBindgroupid());
			}
			if (null != userSystem) {
				accountService.updateByPrimaryKeySelective(userSystem);
			}
			cr.setSTATUS(CommonResult.getSuccesscode());
			cr.setReturnCode(1);
			cr.setMessage("修改组织管理帐号成功");
			return cr;
		} catch (Exception e) {
			log.error("新建组织管理帐号" + errString, e);
			cr.setReturnCode(-1);
			cr.setMessage("修改组织管理帐号失败");
			return cr;
		}
	}

	/**
	 * <p>
	 * 修改组织管理帐号密码
	 * </p>
	 * 
	 * @param userId
	 *            用户帐号id
	 * @param password
	 *            用户密码
	 * @param session
	 * @return
	 * @author 熊荡(13125181082)
	 * @date 2017年12月19日 上午10:43:07
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/webpageAlterPassword")
	@ResponseBody
	public Object webpageAlterPassword(String editId, String userId, String password, HttpSession session) {
		String errString = "editId" + editId + "userId" + userId + "password" + password;
		CommonResult cr = new CommonResult();
		try {
			// 获取当前账号id(修改人id)
			editId = (String) session.getAttribute("userID");
			UserSystem userSystem = new UserSystem();
			userSystem.setUsUserid(userId);
			DesUtils des = new DesUtils("qmkj");
			password = des.encrypt(password);
			userSystem.setUsPassword(password);
			userSystem.setUsEditUserId(editId);
			accountService.updateByPrimaryKeySelective(userSystem);
			cr.setSTATUS(CommonResult.getSuccesscode());
			cr.setReturnCode(1);
			cr.setMessage("修改组织管理帐号密码成功");
			return cr;
		} catch (Exception e) {
			log.error("修改组织管理帐号密码" + errString, e);
			cr.setReturnCode(-1);
			cr.setMessage("修改组织管理帐号密码失败");
			return cr;
		}
	}

	/**
	 * <p>
	 * 获取帐号自身的信息
	 * </p>
	 * 
	 * @param userId
	 *            用户id
	 * @param session
	 * @return
	 * @author 熊荡(13125181082)
	 * @date 2017年12月19日 上午11:14:42
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/webpageGetSelfInfo")
	@ResponseBody
	public Object webpageGetSelfInfo(String userId, HttpSession session, HttpServletRequest request) {
		CommonResult cr = new CommonResult();
		try {
			if (null == userId || "".equals(userId) || "null".equals(userId)) {
				userId = (String) session.getAttribute("userID");
			}
			UserSystem system = accountService.selectByPrimaryKey(userId);
			UserSystem userSystem = accountService.selectSelfInfo(userId);
			GroupInfo groupInfo = groupInfoService.selectByPrimaryKey(system.getUsBindgroupid());
			if (groupInfo.getGiType() != Constant.OrgType.US && groupInfo.getGiLevel() == 1) {
				String bindGroupId = userSystem.getUsBindgroupid();
				userSystem.setUsSuperiorgroupid(bindGroupId);
				userSystem.setUsBindgroupid(null);
			}
			if (groupInfo.getGiType() == Constant.OrgType.US && groupInfo.getGiLevel() == 1) {
				String bindGroupId = userSystem.getUsBindgroupid();
				userSystem.setUsSuperiorgroupid(bindGroupId);
				userSystem.setUsBindgroupid(null);
			}
			String portrait = userSystem.getUsPortrait();
			if (null == portrait || "".equals(portrait) || "null".equals(portrait)) {
				userSystem.setUsPortrait(Constant.appIp + "/NetShare1/img/userLogo.png");
			} else {
				OSS client = AlyunOss.getClient();
				String url = AlyunOss.getUrl(client, portrait);
				userSystem.setUsPortrait(url);
				AlyunOss.closeClient(client);
			}
			cr.setReturnCode(1);
			cr.setMessage("获取自身帐号信息成功");
			cr.setSTATUS(CommonResult.getSuccesscode());
			cr.setData(userSystem);
			return cr;
		} catch (Exception e) {
			log.error("获取自身帐号信息成功", e);
			cr.setReturnCode(-1);
			cr.setMessage("获取自身帐号信息失败");
			return cr;
		}
	}

	/**
	 * <p>
	 * 页面接口：个人中心修改帐号信息
	 * </p>
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @author 熊荡(13125181082)
	 * @date 2017年12月19日 下午4:09:04
	 */
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping("/webpageAlterAccountSelf")
	@ResponseBody
	public Object webpageAlterAccountSelf(String usUsername, String usPhone, String usJob, String usPortrait,
			HttpSession session) {
		CommonResult cr = new CommonResult();
		try {
			String userId = (String) session.getAttribute("userID");
			UserSystem record = new UserSystem();
			record.setUsUserid(userId);
			record.setUsUsername(usUsername);
			record.setUsPhone(usPhone);
			record.setUsJob(usJob);
			/*
			 * Map<String, String> map = new HashMap<>(); map =
			 * JSON.parseObject(usPortrait, map.getClass()); if (map != null) {
			 * Set<String> set = map.keySet(); Iterator<String> iter =
			 * set.iterator(); if (iter.hasNext()) { usPortrait = iter.next();
			 */
			record.setUsPortrait(usPortrait);
			/*
			 * } }
			 */
			accountService.updateByPrimaryKeySelective(record);
			cr.setSTATUS(CommonResult.getSuccesscode());
			cr.setReturnCode(1);
			cr.setMessage("个人中心修改帐号信息成功");
			return cr;
		} catch (Exception e) {
			log.error("个人中心修改帐号信息", e);
			cr.setReturnCode(-1);
			cr.setMessage("个人中心修改帐号信息失败");
			return cr;
		}
	}

	/**
	 * <p>
	 * 页面接口：个人中心修改密码
	 * </p>
	 * 
	 * @param userId
	 *            用户id
	 * @param password
	 *            原密码
	 * @param newPassword
	 *            新密码
	 * @param session
	 * @return
	 * @author 熊荡(13125181082)
	 * @date 2017年12月19日 下午3:01:09
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/webpageAlertPasswordSelf")
	@ResponseBody
	public Object webpageAlertPasswordSelf(String userId, String password, String newPassword, HttpSession session) {
		CommonResult cr = new CommonResult();
		try {
			if (null == userId || "".equals(userId) || "null".equals(userId)) {
				userId = (String) session.getAttribute("userID");
			}
			DesUtils des = new DesUtils("qmkj");
			password = des.encrypt(password);
			UserSystem userSystem = accountService.selectByPrimaryKey(userId);
			if (password.equals(userSystem.getUsPassword())) {
				newPassword = des.encrypt(newPassword);
				userSystem.setUsPassword(newPassword);
				accountService.updateByPrimaryKeySelective(userSystem);
			} else {
				cr.setReturnCode(0);
				cr.setMessage("原密码输入错误");
				return cr;
			}
		} catch (Exception e) {
			log.error("个人中心修改密码", e);
			cr.setReturnCode(-1);
			cr.setMessage("个人中心修改密码失败");
			return cr;
		}
		cr.setSTATUS(CommonResult.getSuccesscode());
		cr.setReturnCode(1);
		cr.setMessage("个人中心修改密码成功");
		return cr;
	}

	/**
	 * <p>
	 * 页面接口：登录
	 * </p>
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param loginCommand
	 *            (username: 用户名, password: 密码)
	 * @return
	 * @author 熊荡(13125181082)
	 * @date 2017年12月20日 上午9:42:00
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/webpageLogin")
	@ResponseBody
	public Object webpageLogin(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			LoginCommand loginCommand) {
		CommonResult cr = new CommonResult();
		// 密码加密
		LoginCommand loginCommand2 = new LoginCommand();
		loginCommand2.setUsername(loginCommand.getUsername());
		loginCommand2.setPassword(loginCommand.getPassword());
		try {
			DesUtils des = new DesUtils("qmkj");
			String password = des.encrypt(loginCommand.getPassword());
			loginCommand.setPassword(password);

		} catch (Exception e1) {
			log.error("密钥加密失败", e1);
			cr.setReturnCode(-1);
			cr.setMessage("密钥加密失败");
			return cr;
		}
		UserSystem userSystem = new UserSystem();
		try {
			userSystem = accountService.login(loginCommand);
		} catch (Exception e) {// 数据库连接断开则尝试再次连接
			e.printStackTrace();
			try {
				userSystem = accountService.login(loginCommand);
			} catch (Exception e1) {// 再次连接失败则返回登录界面
				request.setAttribute("error", "服务忙，请稍后再试！");
				cr.setReturnCode(0);
				cr.setMessage("服务忙，请稍后再试！");
				return cr;
			}
		}

		// System.out.println(request.getServletPath());
		// 账号验证成功进入后续操作界面
		if (null != userSystem) {
			GroupInfo groupInfo = groupInfoService.selectByPrimaryKey(userSystem.getUsBindgroupid());
			if (groupInfo.getGiForbidden() == 1) {
				cr.setReturnCode(3);
				cr.setMessage("账号已禁用！");
				return cr;
			}
			session.setAttribute("csrftoken", null);
			token(session);
			session.setAttribute("userID", userSystem.getUsUserid());
			request.setAttribute("error", "");
			if (userSystem.getUsType() == Constant.OrgType.CLIENT_GROUP) {
				cr.setReturnCode(1);
				cr.setData(userSystem);
			} else if (userSystem.getUsType() == Constant.OrgType.US) {
				cr.setReturnCode(5);
				cr.setData(userSystem);
			} else {
				cr.setReturnCode(2);
				cr.setMessage("不是平台帐号");
				return cr;
			}
			String portraitUrl = userSystem.getUsPortrait();
			String moduleString = Constant.appIp + "/NetShare1";
			if (null != portraitUrl && !"".equals(portraitUrl) && !"null".equals(portraitUrl)) {
				OSS client = AlyunOss.getClient();
				String url = CommonUtil.getUrl(client, portraitUrl);
				AlyunOss.closeClient(client);
				userSystem.setUsPortrait(url);
			} else {
				userSystem.setUsPortrait(moduleString + "/img/userLogo.png");
			}
			cr.setMessage("成功");
			cr.setSTATUS(CommonResult.successCode);
			List<UserRoleAuthorityResult> list = roleAuthorityService
					.selectUserRoleAuthorityBoByUserID(userSystem.getUsUserid());
			for (UserRoleAuthorityResult result : list) {
				if ("A".equals(result.getModuleValue())) {
					result.setModulePic(moduleString + "/img/title_ContentManagement.png");
				} else if ("B".equals(result.getModuleValue())) {
					result.setModulePic(moduleString + "/img/title_BehaviorManagement.png");
				} else if ("C".equals(result.getModuleValue())) {
					result.setModulePic(moduleString + "/img/title_TrainingManagement.png");
				} else if ("D".equals(result.getModuleValue())) {
					result.setModulePic(moduleString + "/img/title_DeviceStatistics.png");
				} else if ("E".equals(result.getModuleValue())) {
					result.setModulePic(moduleString + "/img/title_ContentStatistics.png");
				} else if ("F".equals(result.getModuleValue())) {
					result.setModulePic(moduleString + "/img/title_GroupManagement.png");
				} else if ("G".equals(result.getModuleValue())) {
					result.setModulePic(moduleString + "/img/title_RoleAuthority.png");
				} else if ("H".equals(result.getModuleValue())) {
					result.setModulePic(moduleString + "/img/title_UserManagement.png");
				} else if ("I".equals(result.getModuleValue())) {
					result.setModulePic(moduleString + "/img/title_SchemeManagement.png");
				} else if ("J".equals(result.getModuleValue())) {
					result.setModulePic(moduleString + "/img/title_MoldboardManagement.png");
				} else if ("K".equals(result.getModuleValue())) {
					result.setModulePic(moduleString + "/img/title_SystemSetting.png");
				}
			}
			cr.setDatas(list);
			session.setAttribute("userID", userSystem.getUsUserid());

			try {
				UserSystemLoginRecord uslr = new UserSystemLoginRecord();
				uslr.setUslrId(UUID.randomUUID().toString());
				uslr.setUslrUserid(userSystem.getUsUserid());
				userSystemLoginRecordService.insertSelective(uslr);
			} catch (Exception e) {
				log.error("插入平台帐号登录信息记录失败：userId=" + userSystem.getUsUserid(), e);
			}

			return cr;
		} else {
			UserSystem us = accountService.selectByLoginName(loginCommand.getUsername());
			if (null != us) {
				if (1 == us.getUsForbidden()) {
					request.setAttribute("error", "账号已禁用！");
					cr.setReturnCode(3);
					cr.setMessage("账号已禁用！");
					return cr;
				} else {
					cr.setReturnCode(6);
					cr.setMessage("密码错误！");
					return cr;
				}
			}
			cr.setReturnCode(4);
			cr.setMessage("账号不存在！");
			return cr;
		}
	}

	/**
	 * <p>
	 * 页面接口:获取设备用户
	 * </p>
	 * 
	 * @param orgId
	 * @param session
	 * @return
	 * @author 熊荡(13125181082)
	 * @date 2017年12月25日 下午4:37:10
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/webpageGetUserRegister")
	@ResponseBody
	public Object webpageGetUserRegister(String orgId, Integer pageindex, String vague, HttpSession session) {
		CommonResult commonResult = new CommonResult();
		try {
			if (null == orgId || "".equals(orgId) || "null".equals(orgId)) {
				String userId = (String) session.getAttribute("userID");
				UserSystem userSystem = accountService.selectByPrimaryKey(userId);
				orgId = userSystem.getUsBindgroupid();
			}
			GroupInfo groupInfo = groupInfoService.selectByPrimaryKey(orgId);
			List<UserRegisterManageResult> userRegisters = new ArrayList<UserRegisterManageResult>();
			if (null == pageindex) {
				pageindex = 1;
			}
			PageHelper.startPage(pageindex, 10);
			if (groupInfo.getGiType() == 32) {
				if (null == vague || "".equals(vague) || "null".equals(vague)) {
					userRegisters = userRegisterService.selectUserRegisterByDeptId(orgId);
				} else {
					DevUseDimSearchCommand userCommand = new DevUseDimSearchCommand();
					userCommand.setOrgId(orgId);
					userCommand.setInput("%" + vague + "%");
					userRegisters = userRegisterService.selectUserRegisterByDeptIdDim(userCommand);
				}
			} else {
				if (null == vague || "".equals(vague) || "null".equals(vague)) {
					userRegisters = userRegisterService.selectUserRegisterByGroupId(orgId);
				} else {
					DevUseDimSearchCommand userCommand = new DevUseDimSearchCommand();
					userCommand.setOrgId(orgId);
					userCommand.setInput("%" + vague + "%");
					userRegisters = userRegisterService.selectUserRegisterByGroupIdDim(userCommand);
				}
			}
			for (UserRegisterManageResult result : userRegisters) {
				String updateTime = result.getUrCreatetime();
				if (updateTime != null) {
					int i = updateTime.indexOf('.');
					if (i != -1) {
						updateTime = updateTime.substring(0, i);
					}
				}
				result.setUrCreatetime(updateTime);
			}
			int pagecount = 0;
			long count = 0;
			if (userRegisters.size() > 0) {
				count = ((Page) userRegisters).getTotal();
				pagecount = ((Page) userRegisters).getPages();
				pageindex = ((Page) userRegisters).getPageNum();
			}
			commonResult.setReturnCode(1);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("count", count);
			map.put("pagecount", pagecount);
			map.put("pageindex", pageindex);
			commonResult.setData(map);
			commonResult.setDatas(userRegisters);
			commonResult.setSTATUS(CommonResult.getSuccesscode());
			commonResult.setMessage("设备用户列表获取成功");
			return commonResult;
		} catch (Exception e) {
			log.error("页面接口：获取设备用户信息", e);
			commonResult.setReturnCode(0);
			commonResult.setMessage("设备用户列表获取失败");
			return commonResult;
		}
	}

	/**
	 * <p>
	 * 页面接口：禁用/解禁 设备用户帐号
	 * </p>
	 * 
	 * @param userId
	 * @param urUserid
	 * @param forbidden
	 * @param session
	 * @return
	 * @author 熊荡(13125181082)
	 * @date 2017年12月25日 下午4:59:38
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/webpageForbiddenUserRegister")
	@ResponseBody
	public Object webpageForbiddenUserRegister(String userId, String urUserid, Integer forbidden, HttpSession session) {
		CommonResult commonResult = new CommonResult();
		try {
			/*
			 * if (null==userId || "".equals(userId) || "null".equals(userId)) {
			 * userId = (String) session.getAttribute("userID"); }
			 */
			UserRegister userRegister = new UserRegister();
			userRegister.setUrId(urUserid);
			userRegister.setUrForbidden(forbidden);
			userRegisterService.updateByPrimaryKeySelective(userRegister);
			commonResult.setReturnCode(1);
			commonResult.setSTATUS(CommonResult.getSuccesscode());
			if (forbidden == 1)
				commonResult.setMessage("设备用户禁用成功");
			else
				commonResult.setMessage("设备用户解禁成功");
			return commonResult;
		} catch (Exception e) {
			log.error("设备帐号禁用", e);
			commonResult.setReturnCode(-1);
			commonResult.setMessage("设备用户解禁失败");
			return commonResult;
		}
	}

	/**
	 * <p>
	 * 页面接口：获取设备用户导入/修改的选择信息
	 * </p>
	 * 
	 * @param orgId
	 * @param session
	 * @return
	 * @author 熊荡(13125181082)
	 * @date 2017年12月25日 下午7:02:34
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/webpageGetDevUserSelect")
	@ResponseBody
	public Object webpageGetDevUserSelect(String orgId, HttpSession session) {
		CommonResult commonResult = new CommonResult();
		try {
			if (null == orgId || "".equals(orgId) || "null".equals(orgId)) {
				String userId = (String) session.getAttribute("userID");
				UserSystem userSystem = accountService.selectByPrimaryKey(userId);
				orgId = userSystem.getUsBindgroupid();
			}
			GroupInfo userGroup = groupInfoService.selectByPrimaryKey(orgId);
			List<GroupInfoApp> group = new ArrayList<GroupInfoApp>(); // groupInfoService.selectChildrenOrg(orgId);
			List<GroupInfo> dept = new ArrayList<GroupInfo>();
			int select = 0; // 可选
			if (userGroup.getGiType() == 32) {
				select = 1; // 不可选
				dept.add(userGroup);
				userGroup = groupInfoService.selectByPrimaryKey(userGroup.getGiParentgroupid());
				GroupInfoApp groupInfoApp = new GroupInfoApp();
				groupInfoApp.setGiGroupid(userGroup.getGiGroupid());
				groupInfoApp.setGiGroupname(userGroup.getGiGroupname());
				groupInfoApp.setGiLevel(userGroup.getGiLevel());
				groupInfoApp.setGiParentgroupid(userGroup.getGiParentgroupid());
				groupInfoApp.setGiType(userGroup.getGiType());
				group.add(groupInfoApp);
			} else {
				group = groupInfoService.selectChildrenCompanies(orgId);
				// dept = groupInfoService.selectChildrenDepts(orgId);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("group", group);
			map.put("select", select);
			map.put("dept", dept);
			commonResult.setReturnCode(1);
			commonResult.setSTATUS(CommonResult.getSuccesscode());
			commonResult.setData(map);
			commonResult.setMessage("获取设备用户导入/修改的选择信息成功");
			return commonResult;
		} catch (Exception e) {
			log.error("获取设备用户导入/修改的选择信息", e);
			commonResult.setReturnCode(0);
			commonResult.setMessage("获取设备用户导入/修改的选择信息失败");
			return commonResult;
		}
	}

	/**
	 * <p>
	 * 页面接口: 获取设备用户导入部门信息
	 * </p>
	 * 
	 * @param orgId
	 *            组织机构id
	 * @return
	 * @author 熊荡(13125181082)
	 * @date 2018年1月12日 上午11:21:08
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/webpageGetDevUserDept")
	@ResponseBody
	public Object webpageGetDevUserDept(String orgId) {
		CommonResult commonResult = new CommonResult();
		try {
			List<GroupInfo> depts = groupInfoService.selectChildrenDepts(orgId);
			if (null == orgId || "".equals(orgId) || "null".equals(orgId)) {
				commonResult.setMessage("请选择公司");
				commonResult.setReturnCode(0);
				return commonResult;
			}
			commonResult.setDatas(depts);
			commonResult.setMessage("获取部门成功");
			commonResult.setReturnCode(1);
			commonResult.setSTATUS(CommonResult.getSuccesscode());
			return commonResult;
		} catch (Exception e) {
			log.error("设备用户导入获取部门信息列表+ orgId=" + orgId, e);
			commonResult.setMessage("获取部门成功失败");
			commonResult.setReturnCode(-1);
			return commonResult;
		}
	}

	/**
	 * <p>
	 * 页面接口：修改设备用户
	 * </p>
	 * 
	 * @param registerId
	 * @param userName
	 * @param phone
	 * @param groupId
	 * @param deptId
	 * @return
	 * @author 熊荡(13125181082)
	 * @param commonResult
	 * @date 2017年12月25日 下午7:16:21
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/webpageAlertUserRegister")
	@ResponseBody
	public Object webpageAlertUserRegister(String registerId, String userName, String phone, String groupId,
			String deptId, Integer responsible) {
		CommonResult commonResult = new CommonResult();
		try {
			UserRegister register = userRegisterService.selectByPrimaryKey(registerId);
			String oldPhone = register.getUrPhone();
			List<DevBaseInfo> devBaseInfos = devBaseInfoService.findDeviceByPhone(oldPhone);
			UserRegister userRegister = new UserRegister();
			userRegister.setUrId(registerId);
			userRegister.setUrName(userName);
			userRegister.setUrPhone(phone);
			userRegister.setUrOrgid(groupId);
			if (responsible == null || responsible == 0) {
				userRegister.setUrResponsible(0);
			} else if (responsible == 1) {
				UserRegister registerExist = userRegisterService.findRegisterIdByDeptAndResponsible(deptId);
				if (null != registerExist && !registerId.equals(registerExist.getUrId())) {
					commonResult.setReturnCode(2);
					commonResult.setMessage("已经存在部门负责人");
					commonResult.setData(registerExist);
					return commonResult;
				}
				userRegister.setUrResponsible(1);
			}
			if (null == deptId || "".equals(deptId) || "null".equals(deptId)) {
				userRegister.setUrDeptid(null);
				for (DevBaseInfo baseInfo : devBaseInfos) {
					baseInfo.setDbiOwnerid(groupId);
					baseInfo.setDbiPhone(phone);
				}
			} else {
				userRegister.setUrDeptid(deptId);
				for (DevBaseInfo baseInfo : devBaseInfos) {
					baseInfo.setDbiOwnerid(deptId);
					baseInfo.setDbiPhone(phone);
				}
			}

			userRegisterService.updateByPrimaryKeyManual(userRegister, devBaseInfos);
			commonResult.setReturnCode(1);
			commonResult.setSTATUS(CommonResult.getSuccesscode());
			commonResult.setMessage("修改设备用户成功");
			return commonResult;
		} catch (Exception e) {
			log.error("网页修改设备用户信息", e);
			commonResult.setReturnCode(0);
			commonResult.setMessage("修改设备用户失败");
			return commonResult;
		}
	}

	/**
	 * <p>
	 * 页面接口：获取平台帐号
	 * </p>
	 * 
	 * @param orgId
	 * @param checkOrgId
	 * @param roleId
	 * @param pageindex
	 * @param vague
	 * @param session
	 * @return
	 * @author 熊荡(13125181082)
	 * @date 2018年1月29日 上午10:51:34
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/webpageGetPlatformUserList")
	@ResponseBody
	public Object webpageGetPlatformUserList(String orgId, ArrayList<String> checkOrgId, String roleId,
			Integer pageindex, String vague, HttpSession session) {
		CommonResult commonResult = new CommonResult();
		try {
			if (null == orgId || "".equals(orgId) || "null".equals(orgId)) {
				String userId = (String) session.getAttribute("userID");
				UserSystem userSystem = accountService.selectByPrimaryKey(userId);
				orgId = userSystem.getUsBindgroupid();
			}
			/*
			 * if (null == checkOrgId || "".equals(checkOrgId) ||
			 * "null".equals(checkOrgId)) { checkOrgId = null; }
			 */
			if (null == roleId || "".equals(roleId) || "null".equals(roleId)) {
				roleId = null;
			}
			if (null == vague || "".equals(vague) || "null".equals(vague)) {
				vague = null;
				checkOrgId = null;
			} else {
				vague = "%" + vague + "%";
				checkOrgId = groupInfoService.selectOrgIdsByGroupNameDim(vague);
				if (checkOrgId.size() == 0) {
					checkOrgId = null;
				}
			}
			if (null == pageindex) {
				pageindex = 1;
			}
			PlatformUserCommand pUserCommand = new PlatformUserCommand();
			pUserCommand.setOrgId(orgId);
			pUserCommand.setCheckOrgId(checkOrgId);
			pUserCommand.setRoleId(roleId);
			pUserCommand.setVague(vague);
			List<UserSystemResult> userSystemResults = new ArrayList<UserSystemResult>();
			PageHelper.startPage(pageindex, 10);
			userSystemResults = accountService.selectPlatformUserDim(pUserCommand);
			for (UserSystemResult result : userSystemResults) {
				String updateTime = result.getUsUpdateTime();
				if (null != updateTime) {
					int i = updateTime.indexOf(".");
					if (i != -1) {
						updateTime = updateTime.substring(0, i);
						result.setUsUpdateTime(updateTime);
					}
				}
				Integer usType = result.getUsType();
				switch (usType) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
					String bindGroupId = result.getUsBindgroupid();
					String bindGroup = result.getGiGroupName();
					result.setUsSuperiorgroupid(bindGroupId);
					result.setGiSuperiorgroup(bindGroup);
					result.setUsBindgroupid(null);
					result.setGiGroupName(null);
					break;
				}
			}
			int pagecount = 0;
			long count = 0;
			if (userSystemResults.size() > 0) {
				count = ((Page) userSystemResults).getTotal();
				pagecount = ((Page) userSystemResults).getPages();
				pageindex = ((Page) userSystemResults).getPageNum();
			}
			commonResult.setSTATUS(CommonResult.getSuccesscode());
			Map<String, Object> map = new HashMap<>();
			map.put("count", count);
			map.put("pagecount", pagecount);
			map.put("pageindex", pageindex);
			map.put("userSystemResults", userSystemResults);
			commonResult.setReturnCode(1);
			commonResult.setData(map);
			commonResult.setMessage("获取管理员帐号成功");
			return commonResult;
		} catch (Exception e) {
			log.error("页面接口:获取本公司和其直属下级的帐号", e);
			commonResult.setReturnCode(-1);
			commonResult.setMessage("获取管理员帐号失败");
			return commonResult;
		}
	}

	/**
	 * <p>
	 * 页面接口：获取平台角色列表
	 * </p>
	 * 
	 * @param orgId
	 * @return
	 * @author 熊荡(13125181082)
	 * @date 2018年1月29日 上午11:59:37
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/webpageGetPlatformRole")
	@ResponseBody
	public Object webpageGetPlatformRole(String orgId, HttpSession session) {
		CommonResult cr = new CommonResult();
		try {
			if (null == orgId || "".equals(orgId) || "null".equals(orgId)) {
				String userId = (String) session.getAttribute("userID");
				UserSystem userSystem = accountService.selectByPrimaryKey(userId);
				orgId = userSystem.getUsBindgroupid();
			}
			List<UserRole> roles = roleAuthorityService.selectListUserRoleAvailable(orgId);
			cr.setSTATUS(CommonResult.getSuccesscode());
			cr.setReturnCode(1);
			cr.setDatas(roles);
			cr.setMessage("获取平台角色成功！");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("获取平台角色：webpageGetPlatformUserList：" + orgId);
			cr.setReturnCode(-1);
			cr.setMessage("获取平台角色失败！");
		}
		return cr;
	}

	/**
	 * <p>
	 * 页面接口：获取创建平台帐号时可选择的子组织
	 * </p>
	 * 
	 * @param orgId
	 * @param session
	 * @return
	 * @author 熊荡(13125181082)
	 * @date 2018年1月30日 上午9:13:24
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/webpageGetPlatChildGroup")
	@ResponseBody
	public Object webpageGetPlatChildGroup(String orgId, HttpSession session) {
		CommonResult cr = new CommonResult();
		try {
			GroupInfo groupInfo = groupInfoService.selectByPrimaryKey(orgId);
			int select = 0;
			List<GroupInfo> groupInfos = new ArrayList<GroupInfo>();
			if (groupInfo.getGiType() == Constant.OrgType.US) {
				groupInfos = groupInfoService.selectDirectGroupToSelect(orgId);
			} else {
				select = 1;
			}
			cr.setSTATUS(CommonResult.getSuccesscode());
			cr.setReturnCode(1);
			cr.setDatas(groupInfos);
			cr.setData(select);
			cr.setMessage("获取平台下级组织成功！");
		} catch (Exception e) {
			log.error("获取平台下级组织异常：" + orgId);
			cr.setReturnCode(-1);
			cr.setMessage("获取平台下级组织失败！");
		}
		return cr;
	}

	/**
	 * <p>
	 * 页面接口：新增平台帐号
	 * </p>
	 * 
	 * @param userId
	 * @param userSystem
	 * @param session
	 * @return
	 * @author 熊荡(13125181082)
	 * @date 2018年1月30日 上午9:19:35
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/webpageAddPlatformUser")
	@ResponseBody
	public Object webpageAddPlatformUser(String userId, UserSystem userSystem, HttpSession session) {
		if (null == userId || "".equals(userId) || "null".equals(userId)) {
			userId = (String) session.getAttribute("userID");
		}
		UserSystem userSystem2 = accountService.selectByPrimaryKey(userId);
		CommonResult cr = new CommonResult();
		try {
			String usLoginname = userSystem.getUsLoginname();
			UserSystem system = accountService.selectByLoginName(usLoginname);
			if (null != system) {
				cr.setSTATUS(CommonResult.getSuccesscode());
				cr.setReturnCode(2);
				cr.setMessage("帐号已存在");
				return cr;
			}
			String password = userSystem.getUsPassword();
			// TODO 加密密码
			DesUtils des = new DesUtils("qmkj");
			password = des.encrypt(password);
			userSystem.setUsPassword(password);
			userSystem.setUsUserid(UUID.randomUUID().toString());
			String usSuperiorgroupid = userSystem.getUsSuperiorgroupid();
			GroupInfo groupInfo = groupInfoService.selectByPrimaryKey(usSuperiorgroupid);
			String bingGroupId = userSystem.getUsBindgroupid();
			if (groupInfo.getGiType() == Constant.OrgType.US) {
				if (null == bingGroupId || "".equals(bingGroupId) || "null".equals(bingGroupId)) {
					bingGroupId = userSystem2.getUsBindgroupid();
					userSystem.setUsBindgroupid(bingGroupId);
				}
			} else {
				bingGroupId = usSuperiorgroupid;
				userSystem.setUsBindgroupid(bingGroupId);
				usSuperiorgroupid = userSystem2.getUsBindgroupid();
				userSystem.setUsSuperiorgroupid(usSuperiorgroupid);
			}
			GroupInfoApp superGroup = groupInfoService.selectParentOrg(bingGroupId);
			userSystem.setUsType(superGroup.getGiType());
			String usCreateUserId = userSystem.getUsCreateUserId();// 创建者id
			if (null == usCreateUserId || "".equals(usCreateUserId) || "null".equals(usCreateUserId)) {
				userSystem.setUsCreateUserId(userId);
			}
			String usEditUserId = userSystem.getUsEditUserId(); // 修改者id
			if (null == usEditUserId || "".equals(usEditUserId) || "null".equals(usEditUserId)) {
				userSystem.setUsEditUserId(userId);
			}
			accountService.insertSelective(userSystem);
			cr.setSTATUS(CommonResult.getSuccesscode());
			cr.setReturnCode(1);
			cr.setMessage("新建平台管理帐号成功");
			return cr;
		} catch (Exception e) {
			cr.setReturnCode(-1);
			cr.setMessage("新建平台管理帐号失败");
			return cr;
		}
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping("/webpageAlertPlatformUser")
	@ResponseBody
	public Object webpageAlertPlatformUser(String userId, UserSystem userSystem, HttpSession session) {
		String errString = JSON.toJSONString(userSystem);
		CommonResult cr = new CommonResult();
		try {
			userId = (String) session.getAttribute("userID");
			UserSystem system = accountService.selectByPrimaryKey(userId);
			String usEditUserId = userSystem.getUsEditUserId(); // 修改者id
			if (null == usEditUserId || "".equals(usEditUserId) || "null".equals(usEditUserId)) {
				userSystem.setUsEditUserId(userId);
			}
			String usSuperiorgroupid = userSystem.getUsSuperiorgroupid();
			GroupInfo groupInfo = groupInfoService.selectByPrimaryKey(usSuperiorgroupid);
			String bingGroupId = userSystem.getUsBindgroupid();
			userSystem.setUsType(groupInfo.getGiType());
			if (groupInfo.getGiType() == Constant.OrgType.US) {
				if (null == bingGroupId || "".equals(bingGroupId) || "null".equals(bingGroupId)) {
					bingGroupId = system.getUsBindgroupid();
					userSystem.setUsBindgroupid(bingGroupId);
				}
			} else {
				bingGroupId = usSuperiorgroupid;
				userSystem.setUsBindgroupid(bingGroupId);
				usSuperiorgroupid = system.getUsBindgroupid();
				userSystem.setUsSuperiorgroupid(usSuperiorgroupid);
			}

			if (null != userSystem) {
				accountService.updatePlatformUserByPrimaryKey(userSystem);
			}
			cr.setSTATUS(CommonResult.getSuccesscode());
			cr.setReturnCode(1);
			cr.setMessage("修改平台管理帐号成功");
			return cr;
		} catch (Exception e) {
			log.error("修改平台管理帐号" + errString, e);
			cr.setReturnCode(-1);
			cr.setMessage("修改平台管理帐号失败");
			return cr;
		}
	}

	/**
	 * 
	 * <p>
	 * session失效返回标志
	 * </p>
	 * 
	 * @return
	 * @author 周欣(13667212859)
	 * @date 2018年1月18日 下午5:51:11
	 */
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping(value = "/sessionFail")
	@ResponseBody
	public Object sessionFail() {
		CommonResult cr = new CommonResult();
		cr.setReturnCode(101);
		cr.setMessage("登录已超时，请重新登录！");
		return cr;
	}

	/**
	 * <p>
	 * 页面接口：获取创建平台帐号时可选择的公司
	 * </p>
	 * 
	 * @param orgId
	 * @param session
	 * @return
	 * @author 熊荡(13125181082)
	 * @date 2018年1月30日 上午9:12:21
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/webpageGetPlatformGroupSelect")
	@ResponseBody
	public Object webpageGetPlatformGroupSelect(String orgId, HttpSession session) {
		CommonResult cr = new CommonResult();
		try {
			if (null == orgId || "".equals(orgId) || "null".equals(orgId)) {
				String userId = (String) session.getAttribute("userID");
				UserSystem userSystem = accountService.selectByPrimaryKey(userId);
				orgId = userSystem.getUsBindgroupid();
			}
			GroupInfo groupInfo = groupInfoService.selectByPrimaryKey(orgId);
			List<GroupInfo> groupInfos = groupInfoService.selectAllParentGroups();
			groupInfos.add(0, groupInfo);
			cr.setSTATUS(CommonResult.getSuccesscode());
			cr.setDatas(groupInfos);
			cr.setReturnCode(1);
			cr.setMessage("获取平台组织选择列表成功");
		} catch (Exception e) {
			log.error("获取平台组织选择列表失败");
			cr.setReturnCode(-1);
			cr.setMessage("获取平台组织选择列表失败");
		}
		return cr;
	}

	/**
	 * 
	 * <p>
	 * U盘检测
	 * </p>
	 * 
	 * @return
	 * @author 周欣(13667212859)
	 * @date 2018年5月7日 上午9:51:56
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/webUTest")
	@ResponseBody
	public Object webUTest(HttpServletRequest request) {
		CommonResult cr = new CommonResult();
		final String text = "QMKJ-U盘检测";
		byte[] textByte;
		try {
			textByte = text.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			cr.setReturnCode(-1);
			cr.setMessage("编码失败");
			return cr;
		}
		final Base64.Encoder encoder = Base64.getEncoder();
		final String encodedText = encoder.encodeToString(textByte);
		// 获取项目根目录
		String baseUrl = request.getSession().getServletContext().getRealPath("/");
		File file = new File(baseUrl + File.separator + "SmartKey.txt");
		try (PrintStream ps = new PrintStream(new FileOutputStream(file));) {
			ps.print(encodedText);
		} catch (FileNotFoundException e1) {
			cr.setReturnCode(-2);
			cr.setMessage("文件写入失败");
			return cr;
		}
		String url = Constant.appIp + "/ns/SmartKey.txt";
		cr.setSTATUS(true);
		cr.setMessage("成功");
		cr.setData(url);
		cr.setReturnCode(1);
		return cr;
	}

}
