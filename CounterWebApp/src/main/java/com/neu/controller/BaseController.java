package com.neu.controller;
/**
 * @author rajan
 *
 */
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.neu.controller.highlight.TikaSearchWord;
import com.neu.controller.highlight.IndividualDoc;

@Controller
public class BaseController {

	private static int counter = 0;
	private static final String VIEW_INDEX = "index";
	private static final String WORD_SEARCH = "wordSearch";
	private static final String NO_RESULTS = "noResults";
	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(BaseController.class);

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String welcome(ModelMap model, HttpServletRequest req) {
		System.out.println("in main");
		if(req.getSession() != null){
		HttpSession session = req.getSession();
		session.setAttribute("foundResults", null);
		}
		return VIEW_INDEX;

	}

	
	
	
	@RequestMapping(value = "/userSearch.htm", method = RequestMethod.GET)
	public String getSearchedWords(ModelMap model, HttpServletRequest req) throws Exception {
		
		ArrayList<IndividualDoc> resultsFetched = new ArrayList<IndividualDoc>();
		 
		ArrayList<IndividualDoc> displayData = TikaSearchWord.getDoc(req.getParameter("usearch"), resultsFetched);
	   
		
		HttpSession session = req.getSession();
		session.setAttribute("searchedData", displayData);
		
		return VIEW_INDEX;
		

	}

}