import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController // Essa anotação indica que esta classe é um controlador REST, responsável por lidar com requisições HTTP e retornar respostas no formato JSON ou XML.
@RequestMapping ("/cafeteria") // Essa anotação define o caminho base para todas as rotas dentro deste controlador. Todas as rotas definidas aqui começarão com "/cafeteria".


public class CafeteriaController {

    private final CafeteriaRepository cafeteriaRepository;

    public CafeteriaController(CafeteriaRepository Repository) {
        this.cafeteriaRepository = cafeteriaRepository;
    }
    public List<Cafeteria> getAllCafeterias() {
        return cafeteriaRepository.findAll();
    }
    @PostMapping 
    @ResponseStatus (HttpStatus.CREATED)
    public Cafeteria createCafeteria(@RequestBody Cafeteria cafeteria) {
        return cafeteriaRepository.save(cafeteria);
    }


    
}
