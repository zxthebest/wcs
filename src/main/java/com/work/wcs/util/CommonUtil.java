package com.work.wcs.util;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.oss.OSS;

public class CommonUtil {

	private static Logger log = LoggerFactory.getLogger(CommonUtil.class);

	/**
	 * 
	 * <p>
	 * 获取Ip
	 * </p>
	 * 
	 * @param request
	 * @return
	 * @author 熊荡(13125181082)
	 * @date 2017年6月30日 上午11:51:40
	 */
	public static String getHostIP(HttpServletRequest request) {
		String ip = "";
		// try {
		// // 获取主机IP
		// ip = InetAddress.getLocalHost().getHostAddress();
		// } catch (UnknownHostException nknownHostE) {
		// log.error("获取服务器ip失败！", nknownHostE);
		// nknownHostE.printStackTrace();
		// 从配置文件读取IP
		// 解读constant.xlsx文件，获取ip
		String fileUrl = request.getSession().getServletContext().getRealPath("/") + "constant.xlsx";

		try {
			ip = getIP(fileUrl);
		} catch (Exception e) {
			log.error("读取constant.xlsx文件出错！", e);
		}

		// }
		return ip;
	}

	/**
	 * 
	 * <p>
	 * 获取定制excel(constant文件)中内容
	 * </p>
	 * 
	 * @param fileUrl
	 * @return
	 * @author 周欣(13667212859)
	 * @date 2017年7月18日 下午5:15:05
	 */
	public static List<Map<String, Object>> getExcel(String fileUrl) {
		List<Map<String, Object>> list = PoiExcelClass.getWorkbook(fileUrl);
		return list;
	}

	/**
	 * 
	 * <p>
	 * 获取constant文件中记录的上传文件存储目录
	 * </p>
	 * 
	 * @param list
	 * @return
	 * @author 周欣(13667212859)
	 * @date 2017年7月18日 下午5:25:12
	 */
	public static String getSavePath(String url) {
		List<Map<String, Object>> list = PoiExcelClass.getWorkbook(url);
		String fileUrl = "";
		for (Map<String, Object> map : list) {

			if (!map.get("savePath").toString().equals("BLANK")) {
				// System.out.println((map.get("savePath").toString()));
				fileUrl = (map.get("savePath").toString());
			}
		}
		return fileUrl;
	}

	/**
	 * 
	 * <p>
	 * 获取constant文件中记录的ip地址
	 * </p>
	 * 
	 * @param list
	 * @return
	 * @author 周欣(13667212859)
	 * @date 2017年7月18日 下午5:28:45
	 */
	public static String getIP(String url) {
		List<Map<String, Object>> list = PoiExcelClass.getWorkbook(url);
		String ip = "";
		for (Map<String, Object> map : list) {
			if (!map.get("ip").toString().equals("BLANK")) {
				// System.out.println((map.get("ip").toString()));
				ip = (map.get("ip").toString());
			}
		}
		return ip;
	}
	
	public static String getUrl(OSS oss, String url){
		if (url.contains("/Resource")) {
			return Constant.appIp + url;
		}else{
			url = AlyunOss.getUrl(oss, url);
			return url;
		}
	}

	/*public static String getHostFileUrl1(HttpServletRequest request){
		String ip = getHostIP(request);
		return "http://" + ip + "NetShare1";
	}
	
	public static String getHostFileUrl(HttpServletRequest request){
		String ip = getHostIP(request);
		return "http://" + ip + "NetShare";
	}*/
	
	
}
