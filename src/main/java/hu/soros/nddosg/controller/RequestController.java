package hu.soros.nddosg.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.soros.nddosg.service.RequestService;

@RestController
@RequestMapping(value = "/")
public class RequestController {

	@Autowired
	private RequestService requestService;

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestController.class);

	@GetMapping(value = "/request", produces = "application/json")
	public ResponseEntity<String> getRequest(HttpServletRequest request) {
		LOGGER.trace("Request arrived.");
		try {
			requestService.getRequest(request);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}
}
