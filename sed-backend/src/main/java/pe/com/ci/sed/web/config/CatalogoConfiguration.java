package pe.com.ci.sed.web.config;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.microsoft.azure.storage.StorageException;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.com.ci.sed.web.model.generic.HeaderRequest;
import pe.com.ci.sed.web.persistence.entity.Catalogo;
import pe.com.ci.sed.web.persistence.entity.DocumentosRequeridos;
import pe.com.ci.sed.web.persistence.entity.Usuario;
import pe.com.ci.sed.web.service.CatalogoService;
import pe.com.ci.sed.web.service.UsuarioService;

@Log4j2
@Configuration
@Order(3)
public class CatalogoConfiguration {

    ObjectMapper mapper = new ObjectMapper();
    private final CatalogoService catalogoService;

    public CatalogoConfiguration(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @Value("classpath:catalogo/tipodocumento.json")
    Resource tipodocumento;

    @Value("classpath:catalogo/beneficio.json")
    Resource beneficio;

    @Value("classpath:catalogo/servicio.json")
    Resource servicio;

    @Value("classpath:catalogo/tipodocumentousuario.json")
    Resource tipodocumentousuario;

    @Value("classpath:catalogo/garante.json")
    Resource garante;

    @Value("classpath:catalogo/sede.json")
    Resource sede;

    @Value("classpath:catalogo/mecanismofacturacion.json")
    Resource mecanismofacturacion;

    @Value("classpath:catalogo/modofacturacion.json")
    Resource modofactura;

    @Value("classpath:catalogo/DocumentosRequeridos.json")
    Resource documentorequerido;

    @Value("classpath:catalogo/privilegios.json")
    Resource listaprivilegios;

    @Value("classpath:catalogo/estado.json")
    Resource estado;

    @Value("classpath:catalogo/garantereportetotal.json")
    Resource garantereportetotal;

    @Bean
    CommandLineRunner runner() {
        return args -> {
            List<Catalogo> tipodocumentos = mapper.readValue(tipodocumento.getInputStream(), new TypeReference<>() {
            });
            List<Catalogo> beneficios = mapper.readValue(beneficio.getInputStream(), new TypeReference<>() {
            });
            List<Catalogo> servicios = mapper.readValue(servicio.getInputStream(), new TypeReference<>() {
            });
            List<Catalogo> tipodocumentousers = mapper.readValue(tipodocumentousuario.getInputStream(), new TypeReference<>() {
            });
            List<Catalogo> garantes = mapper.readValue(garante.getInputStream(), new TypeReference<>() {
            });
            List<Catalogo> estados = mapper.readValue(estado.getInputStream(), new TypeReference<>() {
            });
            List<Catalogo> garantesreporte = mapper.readValue(garantereportetotal.getInputStream(), new TypeReference<>() {
            });
            List<Catalogo> sedes = mapper.readValue(sede.getInputStream(), new TypeReference<>() {
            });
            List<Catalogo> mecanismofacturaciones = mapper.readValue(mecanismofacturacion.getInputStream(), new TypeReference<>() {
            });
            List<Catalogo> modofacturaciones = mapper.readValue(modofactura.getInputStream(), new TypeReference<>() {
            });
            tipodocumentos.addAll(beneficios);
            tipodocumentos.addAll(servicios);
            tipodocumentos.addAll(tipodocumentousers);
            tipodocumentos.addAll(modofacturaciones);
            tipodocumentos.addAll(mecanismofacturaciones);
            tipodocumentos.addAll(sedes);
            tipodocumentos.addAll(garantes);
            tipodocumentos.addAll(estados);
            tipodocumentos.addAll(garantesreporte);
            tipodocumentos.parallelStream().forEach(p -> {
                p.setPartitionKey(p.getCatalogo());
                p.setDescripcion(p.getDescripcion().toUpperCase());
            });
            List<List<Catalogo>> parts = Lists.partition(tipodocumentos, 100);
            //List<List<Catalogo>> parts = Lists.partition(tipodocumentos.stream().filter(distinctByKey(Catalogo::getDescripcion)).collect(Collectors.toList()), 100);
            parts.forEach(part -> {
                try {
                    catalogoService.registrarCatalogo(part);
                } catch (StorageException e) {
                    e.printStackTrace();
                }
            });
            log.info("{}", "termino registro de catálogos");
            log.info("{}", "inicio registro de documentos requeridos");
            List<DocumentosRequeridos> documentosrequeridos = mapper.readValue(documentorequerido.getInputStream(), new TypeReference<>() {
            });
            documentosrequeridos.parallelStream().forEach(p -> p.setDescripcion(p.getDescripcion().toUpperCase()));
            List<List<DocumentosRequeridos>> parts2 = Lists.partition(documentosrequeridos, 100);
            parts2.forEach(part -> {
                try {
                    catalogoService.registrarDocumentosRequeridos(part);
                } catch (StorageException e) {
                    e.printStackTrace();
                }
            });
            log.info("{}", "termino registro de documentos requeridos");

            log.info("{}", "inicio registro de privilegios");
            List<Catalogo> privilegios = mapper.readValue(listaprivilegios.getInputStream(), new TypeReference<>() {
            });
            privilegios.parallelStream().forEach(p->p.setDescripcion(p.getDescripcion().toUpperCase()));
            catalogoService.registrarCatalogoPrivilegio(privilegios);
            log.info("{}", "fin registro de privilegios");
        };
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}


