package tacos.web.rest;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import tacos.Order;
import tacos.data.OrderRepository;

@RestController
@RequestMapping(path = "rest/orders", produces = {"application/json", "text/xml"})
@CrossOrigin(origins = "*")  
public class OrderRestController {
	private OrderRepository orderRepo;
	
	public OrderRestController(OrderRepository orderRepo) {
		this.orderRepo = orderRepo; 
	}
	
	//En el caso del put para update, no es conveniente porq es necesario pasar la order completa, 
	//en caso de faltar algun atributo lo sobrescria con null perdiendo los datos que habia.
	@PutMapping("updatePut/{orderId}")
	public Order putOrder(@RequestBody Order order) {
		return orderRepo.save(order);
	}
	
	//patch resuelve lo anterior visto, es el adecuado para actualziar una entidad, tambien se usa de update	
	@PatchMapping(path = "updatePatch/{orderId}", consumes = "application/json")
	public Order patchOrder(@PathVariable("orderId") Long orderId, @RequestBody Order patch) {
		Order order = orderRepo.findById(orderId).get();
		if (patch.getName() != null) {
			order.setName(patch.getName());
		}
		if (patch.getStreet() != null) {
			order.setStreet(patch.getStreet());
		}
		if (patch.getCity() != null) {
			order.setCity(patch.getCity());
		}
		if (patch.getState() != null) {
			order.setState(patch.getState());
		}
		if (patch.getCcNumber() != null) {
			order.setCcNumber(patch.getCcNumber());
		}
		if (patch.getCcExpiration() != null) {
			order.setCcExpiration(patch.getCcExpiration());
		}
		if (patch.getCcCVV() != null) {
			order.setCcCVV(patch.getCcCVV());
		}
		return orderRepo.save(order);
	}
	
	@DeleteMapping("/{orderId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public ResponseEntity deleteOrder(@PathVariable("orderId") Long orderId) {
		try {
			orderRepo.deleteById(orderId);
		} catch (EmptyResultDataAccessException e) {
			 return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		 return new ResponseEntity<>(null, HttpStatus.OK);
	}
}
