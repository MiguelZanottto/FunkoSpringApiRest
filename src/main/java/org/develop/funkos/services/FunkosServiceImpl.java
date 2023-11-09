package org.develop.funkos.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.develop.categorias.models.Categoria;
import org.develop.categorias.services.CategoriasService;
import org.develop.funkos.dto.FunkoCreateDto;
import org.develop.funkos.dto.FunkoUpdateDto;
import org.develop.funkos.exceptions.FunkoNotFound;
import org.develop.funkos.mappers.FunkoMapper;
import org.develop.funkos.models.Funko;
import org.develop.funkos.repositories.FunkosRepository;
import org.develop.notifications.config.WebSocketConfig;
import org.develop.notifications.config.WebSocketHandler;
import org.develop.notifications.dto.FunkoNotificationDto;
import org.develop.notifications.mappers.FunkoNotificationMapper;
import org.develop.notifications.models.Notificacion;
import org.develop.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class FunkosServiceImpl implements FunkosService {
    private final FunkosRepository funkosRepository;
    private final CategoriasService categoriasService;
    private final FunkoMapper funkoMapper;
    private final StorageService storageService;

    private final WebSocketConfig webSocketConfig;
    private final ObjectMapper mapper;
    private final FunkoNotificationMapper funkoNotificationMapper;
    private WebSocketHandler webSocketService;

    @Autowired
    public FunkosServiceImpl(FunkosRepository funkosRepository, CategoriasService categoriasService, FunkoMapper funkoMapper, StorageService storageService,  WebSocketConfig webSocketConfig, FunkoNotificationMapper funkoNotificationMapper) {
        this.funkosRepository = funkosRepository;
        this.categoriasService = categoriasService;
        this.funkoMapper = funkoMapper;
        this.storageService = storageService;
        this.webSocketConfig = webSocketConfig;
        webSocketService = webSocketConfig.webSocketFunkosHandler();
        this.mapper = new ObjectMapper();
        this.funkoNotificationMapper = funkoNotificationMapper;
    }


    @Override
    public List<Funko> findAll(String categoria) {
       if(categoria == null || categoria.isEmpty()){
           log.info("Buscado todos los funkos");
           return funkosRepository.findAll();
       }
       log.info("Buscando todos los funkos por categoria: " + categoria);
       return funkosRepository.findByCategoriaContainsIgnoreCase(categoria.toLowerCase());
    }

    @Override
    public Funko findById(Long id) {
        log.info("Buscando funko por id: " + id);
        return funkosRepository.findById(id).orElseThrow(() -> new FunkoNotFound(id));
    }

    @Override
    public Funko save(FunkoCreateDto funkoCreateDto) {
        log.info("Guardando funko: " + funkoCreateDto);
        Categoria categoria = categoriasService.findByNombre(funkoCreateDto.getCategoria());
        var funkoSaved = funkosRepository.save(funkoMapper.toFunko(funkoCreateDto, categoria));
        onChange(Notificacion.Tipo.CREATE, funkoSaved);
        return funkoSaved;
    }

    @Override
    public Funko update(Long id, FunkoUpdateDto funkoUpdateDto) {
        log.info("Actualizando funko por id: " + id);
        Funko funkoActual = this.findById(id);
        Categoria categoria = null;
        if(funkoUpdateDto.getCategoria() != null && !funkoUpdateDto.getCategoria().isEmpty()){
            categoria = categoriasService.findByNombre(funkoUpdateDto.getCategoria());
        } else {
            categoria = funkoActual.getCategoria();
        }
        var funkoUpdated = funkosRepository.save(funkoMapper.toFunko(funkoUpdateDto, funkoActual, categoria));

        onChange(Notificacion.Tipo.UPDATE, funkoUpdated);

        return funkoUpdated;
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Borrando funko por id: " + id);
        var funk = this.findById(id);
        funkosRepository.deleteById(id);

        if(funk.getImagen() != null && !funk.getImagen().equals(Funko.IMAGE_DEFAULT)){
            storageService.delete(funk.getImagen());
        }
        onChange(Notificacion.Tipo.DELETE, funk);
    }

    @Override
    public Funko updateImage(Long id, MultipartFile image) {
        log.info("Actualizando imagen de Funko por id: " + id);
        var funkoActual = this.findById(id);
        if (funkoActual.getImagen() != null && !funkoActual.getImagen().equals(Funko.IMAGE_DEFAULT)) {
            storageService.delete(funkoActual.getImagen());
        }
        String imageStored = storageService.store(image);
        String imageUrl = storageService.getUrl(imageStored); //storageService.getUrl(imageStored); // Si quiero la url completa
        // Clonamos el producto con la nueva imagen, porque inmutabilidad de los objetos
        var funkoActualizado = new Funko(
                funkoActual.getId(),
                funkoActual.getNombre(),
                funkoActual.getPrecio(),
                funkoActual.getCantidad(),
                imageUrl,
                funkoActual.getFechaCreacion(),
                LocalDateTime.now(),
                funkoActual.getIsActivo(),
                funkoActual.getCategoria()
        );
        // Lo guardamos en el repositorio
        var funkoUpdated = funkosRepository.save(funkoActualizado);
        // Enviamos la notificación a los clientes ws
        onChange(Notificacion.Tipo.UPDATE, funkoUpdated);
        // Devolvemos el producto actualizado
        return funkoUpdated;
    }

    void onChange(Notificacion.Tipo tipo, Funko data) {
        log.debug("Servicio de funkos onChange con tipo: " + tipo + " y datos: " + data);

        if (webSocketService == null) {
            log.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketFunkosHandler();
        }

        try {
            Notificacion<FunkoNotificationDto> notificacion = new Notificacion<>(
                    "FUNKOS",
                    tipo,
                    funkoNotificationMapper.toFunkoNotificationDto(data),
                    LocalDateTime.now().toString()
            );

            String json = mapper.writeValueAsString((notificacion));

            log.info("Enviando mensaje a los clientes ws");
            // Enviamos el mensaje a los clientes ws con un hilo, si hay muchos clientes, puede tardar
            // no bloqueamos el hilo principal que atiende las peticiones http
            Thread senderThread = new Thread(() -> {
                try {
                    webSocketService.sendMessage(json);
                } catch (Exception e) {
                    log.error("Error al enviar el mensaje a través del servicio WebSocket", e);
                }
            });
            senderThread.start();
        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }

    public void setWebSocketService(WebSocketHandler webSocketHandlerMock) {
        this.webSocketService = webSocketHandlerMock;
    }

}
