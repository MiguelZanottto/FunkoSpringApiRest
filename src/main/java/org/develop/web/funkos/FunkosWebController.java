package org.develop.web.funkos;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.develop.categorias.models.Categoria;
import org.develop.categorias.services.CategoriasService;
import org.develop.funkos.dto.FunkoCreateDto;
import org.develop.funkos.dto.FunkoUpdateDto;
import org.develop.funkos.models.Funko;
import org.develop.funkos.repositories.FunkosRepository;
import org.develop.funkos.services.FunkosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/funkos")
@Slf4j
public class FunkosWebController {
    private final FunkosService funkoService;
    private final CategoriasService categoriaService;

    @Autowired
    public FunkosWebController(FunkosService funkoService, CategoriasService categoriaService) {
        this.funkoService = funkoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping(path = {"", "/", "/index", "/list"})
    public String index(
                        Model model,
                        @RequestParam(value = "search", required = false) Optional<String> search,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "3") int size,
                        @RequestParam(defaultValue = "id") String sortBy,
                        @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("Index GET con parámetros search: " + search + ", page: " + page + ", size: " + size);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        var funkosPage = funkoService.findAll(search,  Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        model.addAttribute("funkosPage", funkosPage);
        model.addAttribute("search", search.orElse(""));
        return "funkos/index";
    }

    @GetMapping("/create")
    public String createForm(Model model){
        var categorias = categoriaService.findAll(null).stream().map(Categoria::getNombre).toList();
        var funko = FunkoCreateDto.builder()
                .imagen(Funko.IMAGE_DEFAULT)
                .precio(0.0)
                .cantidad(0)
                .build();

        model.addAttribute("funko", funko);
        model.addAttribute("categorias", categorias);
        return "funkos/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("funko") FunkoCreateDto funkoCreateDto,
                         BindingResult result,
                         Model model) {
        log.info("Create POST");
        if (result.hasErrors()) {
            var categorias = categoriaService.findAll(null).stream().map(Categoria::getNombre).toList();
            model.addAttribute("categorias", categorias);
            return "funkos/create";
        }
        var funko = funkoService.save(funkoCreateDto);
        return "redirect:/funkos";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable("id") Long id, Model model) {
        var categorias = categoriaService.findAll(null).stream().map(Categoria::getNombre).toList();
        Funko funko = funkoService.findById(id);
        FunkoUpdateDto funkoUpdateDto = FunkoUpdateDto.builder()
                .nombre(funko.getNombre())
                .categoria(funko.getCategoria().getNombre())
                .precio(funko.getPrecio())
                .imagen(funko.getImagen())
                .cantidad(funko.getCantidad())
                .isActivo(funko.getIsActivo())
                .build();
        model.addAttribute("funko", funkoUpdateDto);
        model.addAttribute("categorias", categorias);
        return "funkos/update";
    }

    @PostMapping("/update/{id}")
    public String updateFunk(@PathVariable("id") Long id, @ModelAttribute FunkoUpdateDto funkoUpdateDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            var categorias = categoriaService.findAll(null).stream().map(Categoria::getNombre).toList();
            model.addAttribute("categorias", categorias);
            return "funkos/update";
        }
        System.out.println(funkoUpdateDto);
        log.info("Update POST");
        var res = funkoService.update(id, funkoUpdateDto);
        System.out.println(res);
        return "redirect:/funkos";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        funkoService.deleteById(id);
        return "redirect:/funkos";
    }

    @GetMapping("/update-image/{id}")
    public String showUpdateImageForm(@PathVariable("id") Long funkoId, Model model) {
        Funko funko = funkoService.findById(funkoId);
        model.addAttribute("funko", funko);
        return "funkos/update-image";
    }

    @PostMapping("/update-image/{id}")
    public String updateProductImage(@PathVariable("id") Long funkoId, @RequestParam("imagen") MultipartFile imagen) {
        log.info("Update POST con imagen");
        funkoService.updateImage(funkoId, imagen);
        return "redirect:/funkos";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Long id, Model model) {
        log.info("Details GET");
        Funko funko = funkoService.findById(id);
        model.addAttribute("funko", funko);
        return "funkos/details";
    }
}
