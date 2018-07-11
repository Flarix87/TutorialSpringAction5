package tacos.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.jdbc.Sql;

import tacos.Ingredient;

//@Sql("classpath:data.sql")
public interface IngredientRepository extends CrudRepository<Ingredient, String> {

}