package com.mercadolibre.bootcamp.projeto_integrador.service;

import com.mercadolibre.bootcamp.projeto_integrador.exceptions.ManagerNotFoundException;
import com.mercadolibre.bootcamp.projeto_integrador.model.Manager;
import com.mercadolibre.bootcamp.projeto_integrador.repository.IManagerRepository;
import com.mercadolibre.bootcamp.projeto_integrador.service.interfaces.IManagerService;
import org.springframework.stereotype.Service;

@Service
public class ManagerService implements IManagerService {
    private final IManagerRepository managerRepository;

    public ManagerService(IManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    @Override
    public Manager findById(long managerId) {
        return managerRepository.findById(managerId).orElseThrow(() -> new ManagerNotFoundException(managerId));
    }
}
