package ac.hansung.show.controller;

import java.net.URLEncoder;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ac.hansung.show.service.DiscountVO;
import ac.hansung.show.service.Page;
import ac.hansung.show.service.PerforInfoVO;
import ac.hansung.show.service.PerforListVO;
import ac.hansung.show.service.PlaceVO;
import ac.hansung.show.service.ShowService;

@Controller
public class ShowController {
	@Resource
	Page page;
	@Resource
	ShowService showService;

	// 테스트용 로그. Device의 정보를 받아와서 디바이스의 종류를 판독한다.
	private static final Logger logger = LoggerFactory.getLogger(ShowController.class);

	@RequestMapping("/")
	public ModelAndView home(Device device, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();

		String pc = "basic";
		request.setAttribute("pc", pc);
		
		// 인자로 받는 방법 대신 아래 코드를 추가하여 받을 수도 있다
		// device = DeviceUtils.getCurrentDevice(request);		
		mav.setViewName("redirect:/list.do");
		return mav;
	}

	// 모든 공연의 List를 출력하는 메소드
	@RequestMapping("/list.do")
	public ModelAndView showAllList(Device device, HttpServletRequest req)
			throws Exception {
		// 한 페이지에 보여질 공연 목록 수
		int rows = 16;
		// 페이지 블락 지정(1~10 페이지까지 하단에 보여짐)
		int pageBlock = 10;
		// 요청 주소에 따라 페이징을 달리하기 위한 변수
		String path = "list.do";
		String pageCode;
		// 현재 페이지
		String cPage = req.getParameter("cPage");
		ModelAndView mav = new ModelAndView();
			
		if (cPage == null)
			cPage = "1";

		// 현재 페이지에서 rows 만큼 공연 List를 가져오는 메소드
		List<PerforListVO> perForList = showService.showAllList(rows, cPage);

		// 페이징
		page.setPageInit(Integer.parseInt(cPage), showService.getListCount(),
				rows, pageBlock, path);
		// 페이징이 되어 하단에 넣기 위한 코드
		pageCode = page.getSb().toString();

		mav.addObject("perForList", perForList);
		mav.addObject("pageCode", pageCode);
		mav.addObject("cPage", cPage);
		
		String pc="basic";
		if(req.getParameter("pc")!=null)
			pc = req.getParameter("pc");
		if (device.isMobile()) {
			logger.info("list  mobile");
			if(pc.equals("pc")){
				logger.info("isPCVersion");
				mav.setViewName("showList");
				return mav;
			}
			else{
				mav.setViewName("m.showList");
			}
		} else {
			logger.info("list  desktop");
			mav.setViewName("showList");
		}
		return mav;
	}


	// 카테고리별로 공연 목록을 보여주기 위한 메소드
	@RequestMapping("/catShow.do")
	public ModelAndView showCatList(Device device, HttpServletRequest req) throws Exception {
		int rows = 16;
		int pageBlock = 10;
		// 카테고리 코드를 알아오기 위한 변수
		String catVal = req.getParameter("catVal");
		// xml에 카테고리명을 추가 하기 위한 변수
		String realmCode = null;
		String path = "catShow.do";
		String pageCode;
		String cPage = req.getParameter("cPage");
		ModelAndView mav = new ModelAndView();

		if (cPage == null)
			cPage = "1";

		// 카테고리 코드가 있으면 session에 카테고리 코드를 심음(다른 페이지 요청시 카테고리 코드를 계속 유지하기 위함)
		if (catVal != null) {
			req.getSession().setAttribute("catVal", catVal);
		}

		// 카테고리 코드가 session에 있으면(카테고리 별로 출력하여 다른 페이지가 요청되면) xml에 카테고리명을 추가 하기 위해
		// encoding 함 : 한글 처리를 위한
		if (req.getSession().getAttribute("catVal") != null) {
			catVal = (String) req.getSession().getAttribute("catVal");
			realmCode = "&realmCode=" + URLEncoder.encode(catVal, "utf-8");
		}

		// 카테고리에 따른 공연 List를 가져오는 메소드
		List<PerforListVO> perForList = showService.showCatList(rows, cPage,realmCode);

		page.setPageInit(Integer.parseInt(cPage), showService.getCatCount(catVal), rows, pageBlock, path);
		pageCode = page.getSb().toString();

		mav.addObject("perForList", perForList);
		mav.addObject("pageCode", pageCode);
		mav.addObject("cPage", cPage);

		

		String pc="basic";
		if(req.getParameter("pc")!=null)
			pc = req.getParameter("pc");
		if (device.isMobile()) {
			logger.info("catShow  mobile");
			if(pc.equals("pc")){
				mav.setViewName("showList");
			}
			else{
				logger.info("isPCVersion");
				mav.setViewName("m.showList");
			}
		} else {
			logger.info("catShow  desktop");
			mav.setViewName("showList");
		//	mav.setViewName("m.showList");
		}
		
		return mav;
	}

	// 할인되는 공연 목록을 보여주기 위한 메소드
	@RequestMapping("/discountList.do")
	public ModelAndView discountList(Device device, HttpServletRequest req) throws Exception {
		int rows = 16;
		int pageBlock = 10;
		String path = "discountList.do";
		String pageCode;
		String cPage = req.getParameter("cPage");
		ModelAndView mav = new ModelAndView();

		if (cPage == null)
			cPage = "1";

		// 할인 되는 공연 List를 가져오는 메소드
		List<DiscountVO> discountList = showService.discountList(rows, cPage);

		page.setPageInit(Integer.parseInt(cPage),
				showService.getDiscountCount(), rows, pageBlock, path);
		pageCode = page.getSb().toString();

		mav.addObject("discountList", discountList);
		mav.addObject("pageCode", pageCode);
		mav.addObject("cPage", cPage);


		String pc="basic";
		if(req.getParameter("pc")!=null)
			pc = req.getParameter("pc");
		if (device.isMobile()) {
			logger.info("discountList  mobile");
			if(pc.equals("pc")){
				mav.setViewName("discountList");
			}
			else{
				logger.info("isPCVersion");
				mav.setViewName("m.discountList");
			}
		} else {
			logger.info("discountList  desktop");
			mav.setViewName("discountList");
		}
		return mav;
	}

	// 검색 keyword에 따른 공연 목록을 보여주기 위한 메소드
	@RequestMapping("/search.do")
	public ModelAndView searchList(Device device, HttpServletRequest req) throws Exception {
		int rows = 16;
		int pageBlock = 10;
		String path = "search.do";
		String pageCode;
		// xml에 keyword 값을 추가 하기 위한 변수
		String keyword = null;
		// 검색창에 입력한 문자열을 받아오는 변수
		String search = req.getParameter("search");
		String cPage = req.getParameter("cPage");
		ModelAndView mav = new ModelAndView();

		if (cPage == null)
			cPage = "1";

		// 검색창에 검색 keyword를 입력하면 session에 검색 keyword를 심음(다른 페이지 요청시 keyword값을 계속
		// 유지하기 위함)
		if (search != null) {
			req.getSession().setAttribute("search", search);
		}

		// 검색 kewyord가 session에 있으면(검색 keyword가 입력되고 다른 페이지가 요청되면) xml에 검색
		// keyword를 추가 하기 위해 encoding 함 : 한글 처리를 위한
		if (req.getSession().getAttribute("search") != null) {
			search = (String) req.getSession().getAttribute("search");
			keyword = "&keyword=" + URLEncoder.encode(search, "utf-8");
		}

		// 검색 keyword에 따른 공연 List를 가져오기 위한 메소드
		List<PerforListVO> perForList = showService.searchList(rows, cPage,
				keyword);

		page.setPageInit(Integer.parseInt(cPage),
				showService.getSearchCount(keyword), rows, pageBlock, path);
		pageCode = page.getSb().toString();

		mav.addObject("perForList", perForList);
		mav.addObject("pageCode", pageCode);
		mav.addObject("cPage", cPage);


		String pc="basic";
		if(req.getParameter("pc")!=null)
			pc = req.getParameter("pc");
		if (device.isMobile()) {
			logger.info("search  mobile");
			if(pc.equals("pc")){
				mav.setViewName("showList");
			}
			else{
				logger.info("isPCVersion");
				mav.setViewName("m.showList");
			}
		} else {
			logger.info("search  desktop");
			mav.setViewName("showList");
		//	mav.setViewName("m.discountList");
		}
		
		return mav;
	}

	// 공연의 정보를 보여주기 위한 메소드
	@RequestMapping("/read.do")
	public ModelAndView showInfo(Device device, HttpServletRequest req) throws Exception {
		ModelAndView mav = new ModelAndView();
		String address = null;
		String keyword = null;
		// 공연의 정보를 가져오기 위한 공연 code 값
		String seq = req.getParameter("seq");
		// 맵 정보를 가져오기 위한 공연 장소 값
		String mapPlace = req.getParameter("place");
		// 공연 제목에 따른 할인 정보를 가져 오기 위한 공연 제목 값
		String title = req.getParameter("title");

		// 공연 장소 값 변환
		mapPlace = new String(mapPlace.getBytes("8859_1"), "utf-8");
		// 공연 제목 값 변환
		title = new String(title.getBytes("8859_1"), "utf-8");

		// 공연 장소가 존재하면 xml에 공연 장소 값을 넘겨 주기 위해 encoding : 한글 처리를 위한
		if (mapPlace != null) {
			address = "&keyword=" + URLEncoder.encode(mapPlace, "utf-8");
		}

		// 공연 제목이 존재하면 xml에 공연 제목 값을 넘겨 주기 위해 encoding : 한글 처리를 위한
		if (title != null) {
			keyword = "&keyword=" + URLEncoder.encode(title, "utf-8");
		}

		//////////////////////////////////////////////////////////피드백 추가 
		String url = "http://220.76.235.230:8080/show/read.do?seq="+seq+"&place="+mapPlace+"&title="+title;
		
		
		url = URLEncoder.encode(url, "utf-8");
		/////////////////////////////////////////////////////////////////
		
		// 맵 정보를 가져오기 위한 메소드
		PlaceVO pf = showService.mapView(address);
		// 공연 정보를 가져오기 위한 메소드
		PerforInfoVO pfi = showService.showInfoView(seq, keyword);

		mav.addObject("perForInfo", pfi);
		mav.addObject("place", pf);
		mav.addObject("url",url);
		mav.addObject("seq", seq);
		

		String pc="basic";
		if(req.getParameter("pc")!=null)
			pc = req.getParameter("pc");
		if (device.isMobile()) {
			logger.info("read  mobile");
			if(pc.equals("pc")){
				mav.setViewName("showInfo");
			}
			else{
				logger.info("isPCVersion");
				mav.setViewName("m.showInfo");
			}
		} else {
			logger.info("read  desktop");
			mav.setViewName("showInfo");
		//	mav.setViewName("m.showInfo");
		}
		
		return mav;
	}

	// 할인 되는 공연의 정보를 보여주기 위한 메소드
	@RequestMapping("/discountRead.do")
	public ModelAndView discountShowInfo(Device device, HttpServletRequest req)
			throws Exception {
		ModelAndView mav = new ModelAndView();
		// 공연 제목을 받아와 공연 제목의 해당하는 seq 값을 가져오기 위한 변수
		String title = req.getParameter("title");
		String mapPlace = req.getParameter("place");
		// xml에 공연 제목을 넘겨주기 위한 변수
		String keyword = null;
		String address = null;
		;

		title = new String(title.getBytes("8859_1"), "utf-8");
		mapPlace = new String(mapPlace.getBytes("8859_1"), "utf-8");

		if (title != null) {
			keyword = "&keyword=" + URLEncoder.encode(title, "utf-8");
		}

		if (mapPlace != null) {
			address = "&keyword=" + URLEncoder.encode(mapPlace, "utf-8");
		}

		// 할인 되는 공연 제목에 따라 그 공연의 seq값을 가져오기 위한 메소드
		String seq = showService.getShowSeq(keyword);
		// 할인 되는 seq값을 통해 할인 되는 공연의 정보를 가져오기 위한 메소드
		PerforInfoVO pfi = showService.showInfoView(seq, keyword);
		PlaceVO pf = showService.mapView(address);

		mav.addObject("perForInfo", pfi);
		mav.addObject("place", pf);

		String pc="basic";
		if(req.getParameter("pc")!=null)
			pc = req.getParameter("pc");
		if (device.isMobile()) {
			logger.info("discountRead  mobile");
			if(pc.equals("pc")){
				mav.setViewName("showInfo");
			}
			else{
				logger.info("isPCVersion");
				mav.setViewName("m.showInfo");
			}
		} else {
			logger.info("discountRead  desktop");
			mav.setViewName("showInfo");
		}
		
		return mav;
	}
}
