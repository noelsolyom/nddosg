package hu.soros.nddosg.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import hu.soros.nddosg.dto.MainCounterDto;
import hu.soros.nddosg.service.RequestService;

@Controller
public class MainCounterWSController implements ApplicationListener<SessionSubscribeEvent> {

	@Autowired
	private SimpMessagingTemplate brokerMessagingTemplate;

	@Autowired
	private RequestService requestService;

	@Override
	public void onApplicationEvent(SessionSubscribeEvent event) {
		Message<byte[]> message = event.getMessage();
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		StompCommand command = accessor.getCommand();
		if (command.equals(StompCommand.SUBSCRIBE)) {
			String destination = accessor.getDestination();
			MainCounterDto result = new MainCounterDto();
			try {
				result = requestService.getRequest();
			} catch (Exception e) {
				e.printStackTrace();
			}
			brokerMessagingTemplate.convertAndSend(destination, result);

		}

	}

}
