package org.develop.web.funkos;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.develop.rest.categorias.models.Categoria;
import org.develop.rest.categorias.services.CategoriasService;
import org.develop.rest.funkos.dto.FunkoCreateDto;
import org.develop.rest.funkos.dto.FunkoUpdateDto;
import org.develop.rest.funkos.models.Funko;
import org.develop.rest.funkos.services.FunkosService;
import org.develop.web.store.UserStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.Optional;


@Controller
@RequestMapping("/funkos")
@Slf4j
public class FunkosWebController {
    private final FunkosService funkoService;
    private final CategoriasService categoriaService;
    private final UserStore userSession;

    @Autowired
    public FunkosWebController(FunkosService funkoService, CategoriasService categoriaService, UserStore userSession) {
        this.funkoService = funkoService;
        this.categoriaService = categoriaService;
        this.userSession = userSession;
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        log.info("Login GET");
        if (isLoggedAndSessionIsActive(session)) {
            log.info("Si está logueado volvemos al index");
            return "redirect:/funkos";
        }
        return "funkos/login";
    }

    @PostMapping
    public String login(@RequestParam("password") String password, HttpSession session, Model model) {
        log.info("Login POST");
        if ("pass".equals(password)) {
            userSession.setLastLogin(new Date());
            userSession.setLogged(true);
            session.setAttribute("userSession", userSession);
            session.setMaxInactiveInterval(1800);
            return "redirect:/funkos";
        } else {
            return "funkos/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        log.info("Logout GET");
        session.invalidate();
        return "redirect:/funkos";
    }

    @GetMapping(path = {"", "/", "/index", "/list"})
    public String index(
                        HttpSession session, Model model,
                        @RequestParam(value = "search", required = false) Optional<String> search,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "3") int size,
                        @RequestParam(defaultValue = "id") String sortBy,
                        @RequestParam(defaultValue = "asc") String direction
    ) {
        // Comprobamos si está logueado
        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/funkos/login";
        }

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
    public String updateForm(@PathVariable("id") Long id, Model model, HttpSession session) {
        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/funkos/login";
        }

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
    public String updateFunk(@PathVariable("id") Long id,@Valid @ModelAttribute("funko") FunkoUpdateDto funkoUpdateDto, BindingResult result, Model model) {
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
    public String delete(@PathVariable("id") Long id, HttpSession session) {
        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/funkos/login";
        }
        funkoService.deleteById(id);
        return "redirect:/funkos";
    }

    @GetMapping("/update-image/{id}")
    public String showUpdateImageForm(@PathVariable("id") Long funkoId, Model model, HttpSession session) {

        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/funkos/login";
        }

        Funko funko = funkoService.findById(funkoId);
        model.addAttribute("funko", funko);
        return "funkos/update-image";
    }

    @PostMapping("/update-image/{id}")
    public String updateFunkImage(@PathVariable("id") Long funkoId, @RequestParam("imagen") MultipartFile imagen) {
        log.info("Update POST con imagen");
        funkoService.updateImage(funkoId, imagen);
        return "redirect:/funkos";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Long id, Model model, HttpSession session) {
        log.info("Details GET");

        if (!isLoggedAndSessionIsActive(session)) {
            log.info("No hay sesión o no está logueado volvemos al login");
            return "redirect:/funkos/login";
        }

        Funko funko = funkoService.findById(id);
        model.addAttribute("funko", funko);
        return "funkos/details";
    }


    private boolean isLoggedAndSessionIsActive(HttpSession session) {
        log.info("Comprobando si está logueado");
        UserStore sessionData = (UserStore) session.getAttribute("userSession");
        return sessionData != null && sessionData.isLogged();
    }
}
