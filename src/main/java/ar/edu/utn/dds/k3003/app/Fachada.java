package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.*;
import ar.edu.utn.dds.k3003.model.Heladera;
import ar.edu.utn.dds.k3003.model.Temperatura;
import ar.edu.utn.dds.k3003.model.Vianda;
import ar.edu.utn.dds.k3003.repositories.HeladeraMapper;
import ar.edu.utn.dds.k3003.repositories.HeladeraRepository;
import ar.edu.utn.dds.k3003.repositories.TemperaturaMapper;
import lombok.Getter;
import lombok.Setter;


import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Transient;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;


@Setter
@Getter

public class Fachada implements ar.edu.utn.dds.k3003.facades.FachadaHeladeras {

    private final HeladeraRepository repoHeladera;
    private final HeladeraMapper heladeraMapper;
    private FachadaViandas fachadaViandas;
    private final TemperaturaMapper temperaturaMapper;
    private static AtomicLong seqId = new AtomicLong();


    public Fachada(TemperaturaMapper temperaturaMapper, HeladeraRepository repoHeladera, HeladeraMapper heladeraMapper) {
        this.temperaturaMapper = temperaturaMapper;
        this.repoHeladera = repoHeladera;
        this.heladeraMapper = heladeraMapper;
    }

    public Fachada(){
        this.repoHeladera = new HeladeraRepository();
        this.heladeraMapper = new HeladeraMapper();
        this.temperaturaMapper = new TemperaturaMapper();
    }


    public HeladeraDTO agregar(HeladeraDTO heladeraDTO) {
        Heladera heladera = new Heladera(heladeraDTO.getNombre());
        heladera.setId(heladeraDTO.getId());
        //System.out.println(heladera.getId());
        this.repoHeladera.guardar(heladera);
        //System.out.println(this.repoHeladera.heladeras.stream().map(h->h.getId()).toList());
        return heladeraMapper.map(heladera);
    }

    /**
     * "El colaborador que creo la vianda, ahora la deja
     * dentro de la heladera"
     * Primero, busca en el repo el id de la heladera,busca la vianda en el repo, después deposita la vianda y le cambia el estado con la fachada
     */
    @Override
    public void depositar(Integer integer, String s) throws NoSuchElementException {
        Heladera heladera = this.repoHeladera.findById(integer);
        ViandaDTO viandaDTO = this.fachadaViandas.buscarXQR(s);
        Vianda vianda = new Vianda(viandaDTO.getCodigoQR(), (long) viandaDTO.getHeladeraId(), viandaDTO.getEstado(), viandaDTO.getColaboradorId(), viandaDTO.getFechaElaboracion());
        heladera.guardarVianda(vianda);
        fachadaViandas.modificarEstado(s, EstadoViandaEnum.DEPOSITADA);
        repoHeladera.guardarVianda(vianda);
        repoHeladera.actualizar(heladera);
    }

    @Override
    public Integer cantidadViandas(Integer integer) throws NoSuchElementException {

        return repoHeladera.obtenerViandasDeHeladera(integer).size();
    }

    @Override
    public void retirar(RetiroDTO retiroDTO) throws NoSuchElementException {
        Heladera heladera = this.repoHeladera.findById(retiroDTO.getHeladeraId());
        ViandaDTO viandaDTO = this.fachadaViandas.buscarXQR(retiroDTO.getQrVianda());
        Vianda vianda = new Vianda(viandaDTO.getCodigoQR(), (long) viandaDTO.getHeladeraId(), viandaDTO.getEstado(), viandaDTO.getColaboradorId(), viandaDTO.getFechaElaboracion());
        heladera.eliminarVianda(vianda);
        fachadaViandas.modificarEstado(vianda.getQr(), EstadoViandaEnum.RETIRADA);
        repoHeladera.eliminarVianda(vianda);
        repoHeladera.actualizar(heladera);
    }

    @Override
    public void temperatura(TemperaturaDTO temperaturaDTO) {
        Temperatura temperatura = new Temperatura(temperaturaDTO.getHeladeraId(), temperaturaDTO.getFechaMedicion(), temperaturaDTO.getTemperatura());
        repoHeladera.guardarTemperatura(temperatura);
        Heladera heladera = repoHeladera.findById(temperatura.getHeladeraId());
        heladera.agregarTemperatura(temperatura);
        repoHeladera.actualizar(heladera);
    }

    @Override
    public List<TemperaturaDTO> obtenerTemperaturas(Integer integer) {
        List<TemperaturaDTO> temperaturas = repoHeladera.obtenerTemperaturasDeHeladera(integer).stream().map(t -> temperaturaMapper.map(t)).toList();
        return temperaturas.stream().sorted(Comparator.comparing(TemperaturaDTO::getFechaMedicion).reversed()).toList();
    }

    @Override
    public void setViandasProxy(FachadaViandas fachadaViandas) {
    this.fachadaViandas= fachadaViandas;
    }


}
