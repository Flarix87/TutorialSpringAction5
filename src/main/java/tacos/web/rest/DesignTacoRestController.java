package tacos.web.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import tacos.Ingredient;
import tacos.Ingredient.Type;
import tacos.Order;
import tacos.Taco;
import tacos.data.IngredientRepository;
import tacos.data.TacoRepository;

@RestController
@RequestMapping(path = "/rest/taco", produces = {"application/json", "text/xml"})
@CrossOrigin(origins = "*")    
public class DesignTacoRestController {
	
	private final IngredientRepository ingredientRepo;
	private  TacoRepository designRepo;
	
	/*@Autowired
	EntityLinks entityLinks;*/
	
	@ModelAttribute(name = "order")
	public Order order() {
		return new Order();
	}

	@ModelAttribute(name = "taco")
	public Taco taco() {
		return new Taco();
	}

	@Autowired
	public DesignTacoRestController(IngredientRepository ingredientRepo, TacoRepository designRepo) {
		this.ingredientRepo = ingredientRepo;
		this.designRepo = designRepo;
	}

	@GetMapping
	public String showDesignForm(Model model) {
		List<Ingredient> ingredients = new ArrayList<>();
		ingredientRepo.findAll().forEach(i -> ingredients.add(i));
		Type[] types = Ingredient.Type.values();
		for (Type type : types) {
			model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));
		}
		model.addAttribute("design", new Taco());
		return "design";
	}

	private List<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {
		return ingredients.stream().filter(x -> x.getType().equals(type)).collect(Collectors.toList());
	}

	@PostMapping
	public String processDesign(@Valid Taco design, Errors errors, @ModelAttribute Order order, Model model) {
		if (errors.hasErrors())
			return "design";
		Taco saved = designRepo.save(design);
		order.addDesign(saved);
		return "redirect:/orders/current";
	}
	
	@GetMapping("/recent")
	public Iterable<Taco> recentTacos() {
		PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
		return designRepo.findAll(/*page*/);
	}
	
	@GetMapping("/get/{id}")
	public ResponseEntity<Taco> tacoById(@PathVariable("id") Long id) {
		Optional<Taco> optTaco = designRepo.findById(id);
		if (optTaco.isPresent()) {
			return new ResponseEntity<>(optTaco.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
	}
	
	@PostMapping(path="/add",consumes = "application/json")
	@ResponseStatus(HttpStatus.CREATED)//Dice lo que hace no es obligatorio pero es + descritivo que http 200
	public Taco postTaco(@RequestBody Taco taco) {
		return designRepo.save(taco);
	}	
	
	@PutMapping("updatePut/{tacoId}")
	public Taco putOrder(@RequestBody Taco taco) {
		return designRepo.save(taco);
	}
	
	@PatchMapping(path = "updatePatch/{tacoId}")
	public Taco patchOrder(@PathVariable("tacoId") Long tacoId, @RequestBody Taco patch) {
		Taco tacoBD = designRepo.findById(tacoId).get();
		if (patch.getCreatedAt() != null)
			tacoBD.setCreatedAt(patch.getCreatedAt());
		if (patch.getName() != null)
			tacoBD.setName(patch.getName());
		if (patch.getIngredients() != null)
			tacoBD.setIngredients(patch.getIngredients());
		return designRepo.save(tacoBD);
	}
	
	@DeleteMapping("delete/{orderId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public ResponseEntity deleteOrder(@PathVariable("orderId") Long orderId) {
		try {
			designRepo.deleteById(orderId);
		} catch (EmptyResultDataAccessException e) {
			return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
}